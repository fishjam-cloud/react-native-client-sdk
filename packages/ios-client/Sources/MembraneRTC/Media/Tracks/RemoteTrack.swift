/// Protocol representing an available shared functionality of remote tracks.
public protocol RemoteTrack {
    func enabled() -> Bool
    func setEnabled(_ enabled: Bool)
    func trackId() -> String
}
