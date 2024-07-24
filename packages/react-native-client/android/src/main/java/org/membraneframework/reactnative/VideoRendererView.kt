package org.membraneframework.reactnative

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.fishjamcloud.client.media.CameraCapturer
import com.fishjamcloud.client.media.LocalVideoTrack
import com.fishjamcloud.client.media.VideoTrack
import com.fishjamcloud.client.utils.getEnumerator
import expo.modules.kotlin.AppContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.webrtc.RendererCommon

class VideoRendererView(
  context: Context,
  appContext: AppContext
) : MirrorableView(context, appContext),
  RNFishjamClient.OnTrackUpdateListener {
  private var activeVideoTrack: VideoTrack? = null
  private var trackId: String? = null
  override val fadeAnimation: ValueAnimator =
    getVideoViewFadeAnimator { color ->
      foreground = ColorDrawable(color)
    }

  override val videoView =
    RNFishjamClient.fishjamClient.createVideoViewRenderer().also {
      addView(it)
      RNFishjamClient.onTracksUpdateListeners.add(this)
    }

  private fun setupTrack(videoTrack: VideoTrack) {
    if (activeVideoTrack == videoTrack) return
    foreground = ColorDrawable(Color.BLACK)

    activeVideoTrack?.removeRenderer(videoView)
    activeVideoTrack = videoTrack
    if (videoTrack is LocalVideoTrack && videoTrack.capturer is CameraCapturer) {
      foreground = ColorDrawable(Color.BLACK)
      (videoTrack.capturer as CameraCapturer).setMirrorVideo = { isFrontCamera ->
        setMirrorVideo(isFrontCamera = isFrontCamera, isInitialCall = false)
      }
      setMirrorVideo(
        isFrontCamera = getEnumerator(context).isFrontFacing((videoTrack.capturer as CameraCapturer).cameraName),
        isInitialCall = true
      )
    }

    videoTrack.addRenderer(videoView)
  }

  private fun update() {
    CoroutineScope(Dispatchers.Main).launch {
      val peers = RNFishjamClient.getAllPeers()
      val endpoint = peers.firstOrNull { it.tracks[trackId] != null } ?: return@launch
      val videoTrack = endpoint.tracks[trackId] as? VideoTrack ?: return@launch
      setupTrack(videoTrack)
    }
  }

  fun init(trackId: String) {
    this.trackId = trackId
    isInitialized = false
    update()
    isInitialized = true
  }

  fun dispose() {
    activeVideoTrack?.removeRenderer(videoView)
    videoView.release()
    RNFishjamClient.onTracksUpdateListeners.remove(this)
    coroutineScope.cancel()
  }

  override fun onTracksUpdate() {
    update()
  }

  fun setVideoLayout(videoLayout: String) {
    val scalingType =
      when (videoLayout) {
        "FILL" -> RendererCommon.ScalingType.SCALE_ASPECT_FILL
        "FIT" -> RendererCommon.ScalingType.SCALE_ASPECT_FIT
        else -> RendererCommon.ScalingType.SCALE_ASPECT_FILL
      }
    videoView.setScalingType(scalingType)
    videoView.setEnableHardwareScaler(true)
  }
}
