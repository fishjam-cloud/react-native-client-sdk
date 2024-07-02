import Foundation
import Promises
import SwiftPhoenixClient

public class PhoenixTransport {
    enum ConnectionState {
        case uninitialized, connecting, connected, closed, error
    }

    /// Channel's topic
    let topic: String
    let channelParams: [String: Any]

    let socket: Socket
    var channel: Channel?
    var connectionState: ConnectionState = .uninitialized

    weak var delegate: PhoenixTransportDelegate?

    let queue = DispatchQueue(label: "membrane.rtc.transport", qos: .background)

    public init(url: String, topic: String, params: [String: Any], channelParams: [String: Any] = [:]) {
        self.topic = topic
        self.channelParams = channelParams

        socket = Socket(
            endPoint: url, transport: { URLSessionTransport(url: $0) }, paramsClosure: { params })
    }

    public func connect(delegate: PhoenixTransportDelegate) -> Promise<Void> {
        return Promise(on: queue) { resolve, fail in
            guard case .uninitialized = self.connectionState else {
                fail(PhoenixTransportError.unexpected(reason: "Tried to connect on a pending socket"))
                return
            }

            self.connectionState = .connecting

            self.delegate = delegate

            self.socket.connect()

            self.socket.onOpen { self.onOpen() }
            self.socket.onClose { self.onClose() }
            self.socket.onError { error in self.onError() }

            let channel = self.socket.channel(self.topic, params: self.channelParams)

            channel.join(timeout: 3.0)
                .receive(
                    "ok",
                    callback: { _ in
                        self.connectionState = .connected
                        resolve(())
                    }
                ).receive(
                    "error",
                    callback: { _ in
                        self.connectionState = .error
                        fail(PhoenixTransportError.connectionError)
                    })

            self.channel = channel

            /// listen for media events
            self.channel!.on(
                "mediaEvent",
                callback: { message in
                    self.delegate?.didReceive(event: message.payload["data"] as! SerializedMediaEvent)
                })
        }
    }

    public func disconnect() {
        if let channel = channel {
            channel.leave()

            self.channel = nil
        }

        socket.disconnect()

        connectionState = .closed
    }

    public func send(event: SerializedMediaEvent) {
        guard connectionState == .connected,
            let channel = channel
        else {
            sdkLogger.error("PhoenixEventTransport tried sending a message on a closed socket")
            return
        }

        channel.push("mediaEvent", payload: ["data": event])
    }
}

extension PhoenixTransport {
    func onOpen() {
        connectionState = .connected
    }

    func onClose() {
        connectionState = .closed
    }

    func onError() {
        connectionState = .closed
        delegate?.didReceive(error: PhoenixTransportError.connectionError)
    }
}
