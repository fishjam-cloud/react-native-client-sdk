import WebRTC

/// Utility wrapper around a remote `RTCAudioTrack`.
public class RemoteAudioTrack: AudioTrack, RemoteTrack {
    public let track: RTCAudioTrack

    init(track: RTCAudioTrack) {
        self.track = track

        super.init()
    }

    public func enabled() -> Bool {
        return track.isEnabled
    }

    public func setEnabled(_ enabled: Bool) {
        track.isEnabled = enabled
    }

    /// Sets a volume for given remote track, should be in range [0, 1]
    public func setVolume(_ volume: Double) {
        guard volume >= 0.0, volume <= 1.0 else { return }

        // from WebRTC internal documentation this volume is in range 0-10 so just multiply it
        track.source.volume = volume * 10.0
    }

    override func rtcTrack() -> RTCMediaStreamTrack {
        return track
    }

    public func trackId() -> String {
        return track.trackId
    }
}
