package org.membraneframework.reactnative

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.fishjamcloud.client.media.CameraCapturer
import com.fishjamcloud.client.media.LocalVideoTrack
import com.fishjamcloud.client.ui.VideoTextureViewRenderer
import com.fishjamcloud.client.utils.getEnumerator
import expo.modules.kotlin.AppContext
import kotlinx.coroutines.cancel
import org.webrtc.RendererCommon

class VideoPreviewView(
  context: Context,
  appContext: AppContext
) : MirrorableView(context, appContext) {
  private var localVideoTrack: LocalVideoTrack? = null
  override var videoView: VideoTextureViewRenderer? = null
  override val fadeAnimation: ValueAnimator =
    getVideoViewFadeAnimator { color ->
      foreground = ColorDrawable(color)
    }

  private fun initialize() {
    if (isInitialized) return
    isInitialized = true
    foreground = ColorDrawable(Color.BLACK)
    videoView =
      RNFishjamClient.fishjamClient.createVideoViewRenderer().also {
        addView(it)
      }

    localVideoTrack =
      RNFishjamClient.fishjamClient.getLocalEndpoint().tracks.values.firstOrNull {
          track ->
        track is LocalVideoTrack
      } as? LocalVideoTrack?
    if (localVideoTrack?.capturer is CameraCapturer) {
      (localVideoTrack!!.capturer as CameraCapturer).setMirrorVideo = { isFrontCamera ->
        setMirrorVideo(isFrontCamera = isFrontCamera, isInitialCall = false)
      }
      setMirrorVideo(
        isFrontCamera = getEnumerator(context).isFrontFacing((localVideoTrack!!.capturer as CameraCapturer).cameraName),
        isInitialCall = true
      )
    }

    videoView?.let { localVideoTrack?.addRenderer(it) }
  }

  fun setVideoLayout(videoLayout: String) {
    val scalingType =
      when (videoLayout) {
        "FILL" -> RendererCommon.ScalingType.SCALE_ASPECT_FILL
        "FIT" -> RendererCommon.ScalingType.SCALE_ASPECT_FIT
        else -> RendererCommon.ScalingType.SCALE_ASPECT_FILL
      }
    videoView?.setScalingType(scalingType)
    videoView?.setEnableHardwareScaler(true)
  }

  private fun dispose() {
    videoView?.let { localVideoTrack?.removeRenderer(it) }
    videoView?.release()
    isInitialized = false
    coroutineScope.cancel()
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    dispose()
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    initialize()
  }
}
