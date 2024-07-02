import WebRTC

// a utility protocol that allows to hide WebRTC internals from the package's user
// and still alowing the `MembraneRTC` to operate on WebRTC structures
internal protocol MediaTrackProvider {
    func rtcTrack() -> RTCMediaStreamTrack
}
