import Foundation
import Starscream
import WebRTC

internal class FishjamClientInternal: MembraneRTCDelegate, WebSocketDelegate, PeerConnectionListener {
    var webrtcClient: MembraneRTC?
    private var peerConnectionFactoryWrapper: PeerConnectionFactoryWrapper
    private var peerConnectionManager: PeerConnectionManager

    private var localEndpoint: Endpoint = Endpoint.empty()
    private var remoteEndpoints: [String: Endpoint] = [:]

    private var config: Config?
    private var webSocket: FishjamWebsocket?
    private var listener: FishjamClientListener
    private var websocketFactory: (String) -> FishjamWebsocket

    private var commandsQueue: CommandsQueue = CommandsQueue()
    private var isAuthenticated: Bool = false

    public init(listener: FishjamClientListener, websocketFactory: @escaping (String) -> FishjamWebsocket) {
        self.listener = listener
        self.websocketFactory = websocketFactory
    }

    //    private func getTrackWithRtcEngineId(trackId: String): Track? =
    //      localEndpoint.tracks.values.firstOrNull { track -> track.webrtcId() == trackId }
    //        ?: remoteEndpoints.values
    //          .firstOrNull { endpoint ->
    //            endpoint.tracks.values.firstOrNull { track -> track.getRTCEngineId() == trackId } !=
    //              null
    //          }?.tracks
    //          ?.values
    //          ?.firstOrNull { track -> track.getRTCEngineId() == trackId }

    func connect(config: Config) {
        commandsQueue.addCommand(
            Command(commandName: .connect, clientStateAfterCommand: .connected) {
                self.config = config
                self.webSocket = self.websocketFactory(config.websocketUrl)
                self.webSocket?.delegate = self
                self.webSocket?.connect()
            })
    }

    func join(peerMetadata: Metadata) {
        commandsQueue.addCommand(
            Command(commandName: .join, clientStateAfterCommand: .joined) {
                self.localEndpoint = self.localEndpoint.with(metadata: peerMetadata)
                self.webrtcClient?.connect(metadata: peerMetadata)
            })
    }

    func leave() {
        webrtcClient?.disconnect()
        localEndpoint.tracks?.values.forEach { ($0 as? LocalTrack)?.stop() }
        localEndpoint = Endpoint.empty()
        webrtcClient?.removeObserver(<#T##observer: NSObject##NSObject#>, forKeyPath: <#T##String#>)
        isAuthenticated = false
    }

    //    fun leave() {
    //      coroutineScope.launch {
    //        rtcEngineCommunication.disconnect()
    //        localEndpoint.tracks.values.forEach { (it as? LocalTrack)?.stop() }
    //        peerConnectionManager.close()
    //        localEndpoint = Endpoint(id = "", type = EndpointType.WEBRTC)
    //        remoteEndpoints = mutableMapOf()
    //        peerConnectionManager.removeListener(this@FishjamClientInternal)
    //        rtcEngineCommunication.removeListener(this@FishjamClientInternal)
    //        webSocket?.close(1000, null)
    //        webSocket = null
    //        commandsQueue.clear("Client disconnected")
    //      }
    //    }

    func cleanUp() {
        webrtcClient?.disconnect()
        isAuthenticated = false
        webSocket?.disconnect()
        webSocket = nil
        onDisconnected()
    }

    func didReceive(event: Starscream.WebSocketEvent, client: any Starscream.WebSocketClient) {
        switch event {
        case .connected(_):  //done
            websocketDidConnect()
        case .disconnected(let reason, let code):  //done
            onSocketClose(code: code, reason: reason)
        case .text(let message):
            websocketDidReceiveMessage(text: message)
        case .binary(let data):
            websocketDidReceiveData(data: data)
        case .ping(_):
            break
        case .pong(_):
            break
        case .viabilityChanged(_):
            break
        case .reconnectSuggested(_):
            break
        case .cancelled:  //done
            onDisconnected()
        case .error(_):  // done
            onSocketError()
        default:
            break
        }
    }

    func websocketDidConnect() {
        onSocketOpen()
        let authRequest = Fishjam_PeerMessage.with({
            $0.authRequest = Fishjam_PeerMessage.AuthRequest.with({
                $0.token = self.config?.token ?? ""
            })
        })

        guard let serializedData = try? authRequest.serializedData() else {
            return
        }
        sendEvent(peerMessage: serializedData)
        commandsQueue.finishCommand()
    }

    func websocketDidReceiveData(data: Data) {
        do {
            let peerMessage = try Fishjam_PeerMessage(serializedData: data)
            if case .authenticated(_) = peerMessage.content {
                isAuthenticated = true
                commandsQueue.finishCommand()
                onAuthSuccess()
            } else if case .mediaEvent(_) = peerMessage.content {
                receiveEvent(event: peerMessage.mediaEvent.data)
            } else {
                print("Received unexpected websocket message: \(peerMessage)")
            }
        } catch {
            print("Unexpected error: \(error).")
        }
    }

    func websocketDidReceiveMessage(text: String) {
        print("Unsupported socket callback 'websocketDidReceiveMessage' was called.")
        onSocketError()
    }

    private func sendEvent(peerMessage: Data) {
        self.webSocket?.write(data: peerMessage)
    }

    private func receiveEvent(event: SerializedMediaEvent) {
        webrtcClient?.receiveMediaEvent(mediaEvent: event)
    }

    func onEndpointAdded(endpoint: Endpoint) {
        listener.onPeerJoined(endpoint: endpoint)
    }

    func onEndpointRemoved(endpoint: Endpoint) {
        listener.onPeerLeft(endpoint: endpoint)
    }

    func onEndpointUpdated(endpoint: Endpoint) {
        listener.onPeerUpdated(endpoint: endpoint)
    }

    func onSendMediaEvent(event: SerializedMediaEvent) {
        if !isAuthenticated {
            print("Tried to send media event: \(event) before authentication")
            return
        }
        let mediaEvent =
            Fishjam_PeerMessage.with({
                $0.mediaEvent = Fishjam_PeerMessage.MediaEvent.with({
                    $0.data = event
                })
            })

        guard let serialzedData = try? mediaEvent.serializedData() else {
            return
        }
        sendEvent(peerMessage: serialzedData)
    }

    func onTrackAdded(ctx: TrackContext) {
        listener.onTrackAdded(ctx: ctx)
    }

    func onTrackReady(ctx: TrackContext) {
        listener.onTrackReady(ctx: ctx)
    }

    func onTrackRemoved(ctx: TrackContext) {
        listener.onTrackRemoved(ctx: ctx)
    }

    func onTrackUpdated(ctx: TrackContext) {
        listener.onTrackUpdated(ctx: ctx)
    }

    func onBandwidthEstimationChanged(estimation: Int) {
        listener.onBandwidthEstimationChanged(estimation: estimation)
    }

    func onSocketClose(code: UInt16, reason: String) {
        listener.onSocketClose(code: code, reason: reason)
        commandsQueue.clear(cause: "Websocket was closed")
    }

    func onSocketError() {
        isAuthenticated = false
        listener.onSocketError()
        commandsQueue.clear(cause: "Socket error")
    }

    func onSocketOpen() {
        listener.onSocketOpen()
    }

    func onAuthSuccess() {
        listener.onAuthSuccess()
    }

    func onAuthError() {
        listener.onAuthError()
    }

    func onDisconnected() {
        isAuthenticated = false
        listener.onDisconnected()
        commandsQueue.clear(cause: "Websocket was closed")
    }

    func onConnected(endpointId: String, otherEndpoints: [Endpoint]) {
        listener.onJoined(peerID: endpointId, peersInRoom: otherEndpoints)
    }

    func onConnectionError(metadata: Any) {
        listener.onJoinError(metadata: metadata)
    }

    func onTrackEncodingChanged(endpointId: String, trackId: String, encoding: String) {
    }

    //Peer Conection listener
    func onAddTrack(trackId: String, track: RTCMediaStreamTrack) {
        return
    }

    //    override fun onAddTrack(
    //      rtcEngineTrackId: String,
    //      webrtcTrack: MediaStreamTrack
    //    ) {
    //      var track =
    //        getTrackWithRtcEngineId(rtcEngineTrackId) ?: run {
    //          Timber.e("onAddTrack: Track context with trackId=$rtcEngineTrackId not found")
    //          return
    //        }
    //
    //      val trackId = track.id()
    //
    //      track =
    //        when (webrtcTrack) {
    //          is VideoTrack ->
    //            RemoteVideoTrack(
    //              webrtcTrack,
    //              track.endpointId,
    //              track.getRTCEngineId(),
    //              track.metadata,
    //              trackId
    //            )
    //
    //          is AudioTrack ->
    //            RemoteAudioTrack(
    //              webrtcTrack,
    //              track.endpointId,
    //              track.getRTCEngineId(),
    //              track.metadata,
    //              trackId
    //            )
    //
    //          else ->
    //            throw IllegalStateException("invalid type of incoming track")
    //        }
    //
    //      remoteEndpoints[track.endpointId] = remoteEndpoints[track.endpointId]!!.addOrReplaceTrack(track)
    //      listener.onTrackReady(track)
    //    }

    func onLocalIceCandidate(candidate: RTCIceCandidate) {
        return
    }

    func onPeerConnectionStateChange(newState: RTCIceConnectionState) {
        return
    }
}
