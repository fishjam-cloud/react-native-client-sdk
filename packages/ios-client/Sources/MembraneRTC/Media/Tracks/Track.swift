import WebRTC

open class Track {
    internal var mediaTrack: RTCMediaStreamTrack?
    let endpointId: String
    private var _rtcEngineId: String?
    var metadata: [String: Any]
    private let _id: String = UUID().uuidString

    /**
    * Every track can have two IDs:
    * - the one from RTC engine in the format <peerid>:<trackid>
    * - the one from WebRTC library in the form of a UUID v4 string (different than the one from the engine)
    * It's often confusing when to use which one, and a mapping is typically needed.
    * Moreover, a track may sometimes be missing one of the IDs:
    * - RTC engine ID is missing when a local track is created without an established connection
    * - WebRTC ID is missing when a track is received from an RTC engine but has not been negotiated
    * As a solution, there is a third ID created by our client. It's always present, never changes,
    * and is always used unless communicating specifically with the RTC engine or WebRTC. This ID is what
    * the user sees, unless they need to debug something.
    */

    init(mediaTrack: RTCMediaStreamTrack?, endpointId: String, rtcEngineId: String?, metadata: [String: Any] = [:]) {
        self.mediaTrack = mediaTrack
        self.endpointId = endpointId
        self._rtcEngineId = rtcEngineId
        self.metadata = metadata
    }

    var id: String {
        return _id
    }

    internal var webrtcId: String {
        return mediaTrack?.trackId ?? ""
    }

    internal var enabled: Bool {
        get {
            return mediaTrack?.isEnabled ?? false
        }
        set {
            mediaTrack?.isEnabled = newValue
        }
    }

    internal var rtcEngineId: String? {
        get {
            return _rtcEngineId
        }
        set {
            _rtcEngineId = newValue
        }
    }

}
