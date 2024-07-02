import WebRTC

public class AudioTrack: MediaTrackProvider, Equatable {
    func rtcTrack() -> RTCMediaStreamTrack {
        fatalError("forbidden rtcTrack() call on a plain AudioTrack")
    }

    public static func == (lhs: AudioTrack, rhs: AudioTrack) -> Bool {
        return lhs.rtcTrack() == rhs.rtcTrack()
    }
}
