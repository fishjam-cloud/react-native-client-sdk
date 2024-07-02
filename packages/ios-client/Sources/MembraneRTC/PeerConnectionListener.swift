import WebRTC

internal protocol PeerConnectionListener {
    func onAddTrack(trackId: String, track: RTCMediaStreamTrack)
    func onLocalIceCandidate(candidate: RTCIceCandidate)
    func onPeerConnectionStateChange(newState: RTCIceConnectionState)
}
