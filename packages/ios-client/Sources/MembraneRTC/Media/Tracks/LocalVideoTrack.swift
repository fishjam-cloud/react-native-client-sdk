//DONE ?

import WebRTC

/// Utility wrapper around a local `RTCVideoTrack` also managing an instance of `VideoCapturer`
public class LocalVideoTrack: VideoTrack, LocalTrack {
    internal var capturer: CameraCapturer
    internal var videoParameters: VideoParameters

    //    public enum Capturer {
    //        case camera, file
    //    }

    internal init(
        mediaTrack: RTCVideoTrack, endpointId: String, metadata: Metadata = Metadata(),
        videoParameters: VideoParameters, capturer: CameraCapturer
    ) {
        self.videoParameters = videoParameters
        self.capturer = capturer
        super.init(mediaTrack: mediaTrack, endpointId: endpointId, rtcEngineId: nil, metadata: metadata)
    }

    public func start() {
        capturer.startCapture()
    }

    public func stop() {
        capturer.stopCapture()
    }

    public func flipCamera() {
        capturer.switchCamera()
    }

    public func switchCamera(deviceId: String) {
        capturer.switchCamera(deviceId: deviceId)
    }
}

//public class LocalCameraVideoTrack: LocalVideoTrack {
//    override internal func createCapturer(videoSource: RTCVideoSource) -> VideoCapturer {
//        return CameraCapturer(videoParameters: videoParameters, delegate: videoSource)
//    }
//
//    internal var mirrorVideo: (_ shouldMirror: Bool) -> Void = { _ in } {
//        didSet {
//            if let cap = capturer as? CameraCapturer {
//                cap.mirrorVideo = mirrorVideo
//            }
//        }
//    }
//
//    public func switchCamera() {
//        guard let capturer = capturer as? CameraCapturer else {
//            return
//        }
//
//        capturer.switchCamera()
//    }
//
//    public func switchCamera(deviceId: String) {
//        guard let capturer = capturer as? CameraCapturer else {
//            return
//        }
//
//        capturer.switchCamera(deviceId: deviceId)
//
//    }
//
//    public static func getCaptureDevices() -> [AVCaptureDevice] {
//        return RTCCameraVideoCapturer.captureDevices()
//    }
//}
//
//public class LocalFileVideoTrack: LocalVideoTrack {
//    override internal func createCapturer(videoSource: RTCVideoSource) -> VideoCapturer {
//        return FileCapturer(videoSource)
//    }
//}
