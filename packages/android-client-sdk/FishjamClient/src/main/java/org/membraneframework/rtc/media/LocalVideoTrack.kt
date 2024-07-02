package org.membraneframework.rtc.media

import android.content.Context
import org.webrtc.*
import timber.log.Timber
import java.util.*

/**
 * A class representing a local video track.
 *
 * Internally it wraps a WebRTC <strong>VideoTrack</strong>.
 */
class LocalVideoTrack(
    mediaTrack: org.webrtc.VideoTrack,
    private val capturer: Capturer,
    eglBase: EglBase,
    val videoParameters: VideoParameters
) : VideoTrack(mediaTrack, eglBase.eglBaseContext), LocalTrack {
    data class CaptureDevice(val deviceName: String, val isFrontFacing: Boolean, val isBackFacing: Boolean)

    companion object {
        fun create(
            context: Context,
            factory: PeerConnectionFactory,
            eglBase: EglBase,
            videoParameters: VideoParameters,
            cameraName: String? = null
        ): LocalVideoTrack {
            val source = factory.createVideoSource(false)
            val track = factory.createVideoTrack(UUID.randomUUID().toString(), source)

            val capturer =
                CameraCapturer(
                    context = context,
                    source = source,
                    rootEglBase = eglBase,
                    videoParameters = videoParameters,
                    cameraName
                )

            return LocalVideoTrack(track, capturer, eglBase, videoParameters)
        }

        fun getCaptureDevices(context: Context): List<CaptureDevice> {
            val enumerator =
                if (Camera2Enumerator.isSupported(context)) {
                    Camera2Enumerator(context)
                } else {
                    Camera1Enumerator(true)
                }
            return enumerator.deviceNames.map { name ->
                CaptureDevice(
                    name,
                    enumerator.isFrontFacing(name),
                    enumerator.isBackFacing(name)
                )
            }
        }
    }

    override fun start() {
        capturer.startCapture()
    }

    override fun stop() {
        capturer.stopCapture()
    }

    override fun enabled(): Boolean {
        return videoTrack.enabled()
    }

    override fun setEnabled(enabled: Boolean) {
        videoTrack.setEnabled(enabled)
    }

    fun flipCamera() {
        (capturer as? CameraCapturer)?.flipCamera()
    }

    fun switchCamera(deviceName: String) {
        (capturer as? CameraCapturer)?.switchCamera(deviceName)
    }
}

interface Capturer {
    fun capturer(): VideoCapturer

    fun startCapture()

    fun stopCapture()
}

class CameraCapturer(
    private val context: Context,
    private val source: VideoSource,
    private val rootEglBase: EglBase,
    private val videoParameters: VideoParameters,
    cameraName: String?
) : Capturer, CameraVideoCapturer.CameraSwitchHandler {
    private lateinit var cameraCapturer: CameraVideoCapturer
    private lateinit var size: Size
    private var isCapturing = false

    init {
        createCapturer(cameraName)
    }

    override fun capturer(): VideoCapturer {
        return cameraCapturer
    }

    override fun startCapture() {
        isCapturing = true
        cameraCapturer.startCapture(size.width, size.height, videoParameters.maxFps)
    }

    override fun stopCapture() {
        isCapturing = false
        cameraCapturer.stopCapture()
        cameraCapturer.dispose()
    }

    fun flipCamera() {
        cameraCapturer.switchCamera(this)
    }

    fun switchCamera(deviceName: String) {
        cameraCapturer.switchCamera(this, deviceName)
    }

    private fun createCapturer(providedDeviceName: String?) {
        val enumerator =
            if (Camera2Enumerator.isSupported(context)) {
                Camera2Enumerator(context)
            } else {
                Camera1Enumerator(true)
            }

        var deviceName = providedDeviceName

        if (deviceName == null) {
            for (name in enumerator.deviceNames) {
                if (enumerator.isFrontFacing(name)) {
                    deviceName = name
                    break
                }
            }
        }

        this.cameraCapturer = enumerator.createCapturer(deviceName, null)

        this.cameraCapturer.initialize(
            SurfaceTextureHelper.create("CameraCaptureThread", rootEglBase.eglBaseContext),
            context,
            source.capturerObserver
        )

        val sizes =
            enumerator.getSupportedFormats(deviceName)
                ?.map { Size(it.width, it.height) }
                ?: emptyList()

        this.size =
            CameraEnumerationAndroid.getClosestSupportedSize(
                sizes,
                videoParameters.dimensions.width,
                videoParameters.dimensions.height
            )
    }

    override fun onCameraSwitchDone(isFrontCamera: Boolean) {
    }

    override fun onCameraSwitchError(errorDescription: String?) {
        // FIXME flipCamera() should probably return a promise or something
        Timber.e("Failed to switch camera: $errorDescription")
    }
}
