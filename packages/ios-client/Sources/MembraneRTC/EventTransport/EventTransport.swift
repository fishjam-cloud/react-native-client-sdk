import Foundation
import Promises

public enum PhoenixTransportError: Error {
    /// Thrown when user is not authorized to join the session
    case unauthorized

    /// Thrown when transport fails to connect
    case connectionError

    /// Thrown when  the transport encountered unknown/unspecified error
    case unexpected(reason: String)
}

extension PhoenixTransportError: CustomStringConvertible {
    public var description: String {
        switch self {
        case .unauthorized:
            return "User is unauthorized to use the transport"
        case .connectionError:
            return "Failed to connect with the remote side"
        case .unexpected(let reason):
            return "Encountered unexpected error: \(reason)"
        }
    }
}

/// Protocol for a delegate listening for messages received by the `PhoenixTransport`
public protocol PhoenixTransportDelegate: AnyObject {
    func didReceive(event: SerializedMediaEvent)
    func didReceive(error: PhoenixTransportError)
}
