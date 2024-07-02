import WebRTC

public class VideoTrack: MediaTrackProvider, Equatable {
    func rtcTrack() -> RTCMediaStreamTrack {
        fatalError("forbidden rtcTrack() call on a plain VideoTrack")
    }

    public static func == (lhs: VideoTrack, rhs: VideoTrack) -> Bool {
        return lhs.rtcTrack() == rhs.rtcTrack()
    }
}
