import WebRTC

/// Utility wrapper around a remote `RTCVideoTrack`.
public class RemoteVideoTrack: VideoTrack, RemoteTrack {
    public let track: RTCVideoTrack

    init(track: RTCVideoTrack) {
        self.track = track
    }

    public func enabled() -> Bool {
        return track.isEnabled
    }

    public func setEnabled(_ enabled: Bool) {
        track.isEnabled = enabled
    }

    override func rtcTrack() -> RTCMediaStreamTrack {
        return track
    }

    public func trackId() -> String {
        return track.trackId
    }
}
