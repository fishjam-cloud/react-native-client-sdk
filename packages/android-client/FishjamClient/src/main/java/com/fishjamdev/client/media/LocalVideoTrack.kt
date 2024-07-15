package com.fishjamdev.client.media

import android.content.Context
import com.fishjamdev.client.models.Metadata
import com.fishjamdev.client.models.VideoParameters
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerationAndroid
import org.webrtc.CameraVideoCapturer
import org.webrtc.EglBase
import org.webrtc.Size
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import timber.log.Timber

class LocalVideoTrack(
  mediaTrack: org.webrtc.VideoTrack,
  endpointId: String,
  metadata: Metadata,
  private val capturer: Capturer,
  val videoParameters: VideoParameters
) : VideoTrack(mediaTrack, endpointId, rtcEngineId = null, metadata),
  LocalTrack {
  data class CaptureDevice(
    val deviceName: String,
    val isFrontFacing: Boolean,
    val isBackFacing: Boolean
  )

  companion object {
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
) : Capturer,
  CameraVideoCapturer.CameraSwitchHandler {
  private lateinit var cameraCapturer: CameraVideoCapturer
  private lateinit var size: Size
  private var isCapturing = false

  init {
    createCapturer(cameraName)
  }

  override fun capturer(): VideoCapturer = cameraCapturer

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
      enumerator
        .getSupportedFormats(deviceName)
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
