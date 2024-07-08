internal protocol LocalTrack: MediaTrackProvider {
    func start()
    func stop()
    func enabled() -> Bool
    func setEnabled(_ enabled: Bool)
    func trackId() -> String
}
