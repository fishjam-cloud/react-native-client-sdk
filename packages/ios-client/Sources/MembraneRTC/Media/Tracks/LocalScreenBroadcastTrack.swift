import WebRTC

public protocol LocalScreenBroadcastTrackDelegate: AnyObject {
    func started()
    func stopped()
    func paused()
    func resumed()
}

/// Utility wrapper around a local `RTCVideoTrack` also managing a `BroadcastScreenCapturer`.
public class LocalScreenBroadcastTrack: LocalVideoTrack, ScreenBroadcastCapturerDelegate {
    private let appGroup: String
    public weak var delegate: LocalScreenBroadcastTrackDelegate?

    internal init(
        appGroup: String, videoParameters: VideoParameters,
        delegate _: LocalScreenBroadcastTrackDelegate? = nil, peerConnectionFactoryWrapper: PeerConnectionFactoryWrapper
    ) {
        self.appGroup = appGroup
        super.init(parameters: videoParameters, peerConnectionFactoryWrapper: peerConnectionFactoryWrapper)
    }

    internal func started() {
        delegate?.started()
    }

    internal func stopped() {
        delegate?.stopped()
    }

    public func paused() {
        delegate?.paused()
    }

    public func resumed() {
        delegate?.resumed()
    }

    override func createCapturer(videoSource _: RTCVideoSource) -> VideoCapturer {
        let capturer = ScreenBroadcastCapturer(
            videoSource, appGroup: appGroup, videoParameters: videoParameters)
        capturer.capturerDelegate = self
        return capturer
    }
}
