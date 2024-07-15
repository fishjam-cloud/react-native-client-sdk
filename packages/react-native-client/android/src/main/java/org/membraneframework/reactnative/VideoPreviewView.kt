package org.membraneframework.reactnative

import android.content.Context
import com.fishjamcloud.client.media.LocalVideoTrack
import com.fishjamcloud.client.ui.VideoTextureViewRenderer
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ExpoView
import org.webrtc.RendererCommon

class VideoPreviewView(
  context: Context,
  appContext: AppContext
) : ExpoView(context, appContext) {
  private var localVideoTrack: LocalVideoTrack? = null
  private var isInitialized: Boolean = false

  private var videoView: VideoTextureViewRenderer? = null

  private fun initialize() {
    if (isInitialized) return
    isInitialized = true

    videoView =
      RNFishjamClient.fishjamClient!!.createVideoViewRenderer().also {
        addView(it)
      }

    localVideoTrack =
      RNFishjamClient.fishjamClient?.getLocalEndpoint()?.tracks?.values?.firstOrNull {
          track ->
        track is LocalVideoTrack
      } as? LocalVideoTrack?
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
    videoView?.setMirror(mirrorVideo)
  }

  private fun dispose() {
    videoView?.let { localVideoTrack?.removeRenderer(it) }
    videoView?.release()
    isInitialized = false
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
