package org.membraneframework.reactnative

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.animation.LinearInterpolator
import com.fishjamcloud.client.media.CameraCapturer
import com.fishjamcloud.client.media.LocalVideoTrack
import com.fishjamcloud.client.ui.VideoTextureViewRenderer
import com.fishjamcloud.client.utils.getEnumerator
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ExpoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.webrtc.RendererCommon

class VideoPreviewView(
  context: Context,
  appContext: AppContext
) : ExpoView(context, appContext) {
  private var localVideoTrack: LocalVideoTrack? = null
  private var isInitialized: Boolean = false
  private var mirrorVideo: Boolean = false
  private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

  private var videoView: VideoTextureViewRenderer? = null

  private var fadeAnimation: ValueAnimator =
    ValueAnimator.ofArgb(Color.TRANSPARENT, Color.BLACK).apply {
      duration = 100
      interpolator = LinearInterpolator()
      addUpdateListener {
        val colorValue = it.animatedValue as Int
        foreground = ColorDrawable(colorValue)
      }
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
        setMirrorVideo(isFrontCamera)
      }
      initialSetMirrorVideo(getEnumerator(context).isFrontFacing((localVideoTrack!!.capturer as CameraCapturer).cameraName))
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

  fun setMirrorVideo(mirrorVideo: Boolean) {
    if (this.mirrorVideo == mirrorVideo) return
    this.mirrorVideo = mirrorVideo
    coroutineScope.launch {
      fadeAnimation.start()
      delay(200)
      videoView?.setMirror(mirrorVideo)
      delay(200)
      fadeAnimation.reverse()
    }
  }

  private fun initialSetMirrorVideo(mirrorVideo: Boolean) {
    this.mirrorVideo = mirrorVideo
    coroutineScope.launch {
      videoView?.setMirror(mirrorVideo)
      delay(200)
      fadeAnimation.reverse()
    }
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
