package org.membraneframework.reactnative

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.animation.LinearInterpolator
import com.fishjamcloud.client.media.CameraCapturer
import com.fishjamcloud.client.media.LocalVideoTrack
import com.fishjamcloud.client.media.VideoTrack
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

class VideoRendererView(
  context: Context,
  appContext: AppContext
) : ExpoView(context, appContext),
  RNFishjamClient.OnTrackUpdateListener {
  var activeVideoTrack: VideoTrack? = null
  var trackId: String? = null
  private var mirrorVideo: Boolean = false
  private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

  private val videoView =
    RNFishjamClient.fishjamClient.createVideoViewRenderer().also {
      addView(it)
      RNFishjamClient.onTracksUpdateListeners.add(this)
    }

  private var fadeAnimation: ValueAnimator =
    ValueAnimator.ofArgb(Color.TRANSPARENT, Color.BLACK).apply {
      duration = 100
      interpolator = LinearInterpolator()
      addUpdateListener {
        val colorValue = it.animatedValue as Int
        foreground = ColorDrawable(colorValue)
      }
    }

  private fun setupTrack(videoTrack: VideoTrack) {
    if (activeVideoTrack == videoTrack) return

    activeVideoTrack?.removeRenderer(videoView)
    activeVideoTrack = videoTrack

    if (videoTrack is LocalVideoTrack && videoTrack.capturer is CameraCapturer) {
      (videoTrack.capturer as CameraCapturer).setMirrorVideo = { isFrontCamera ->
        setMirrorVideo(isFrontCamera)
      }
      setMirrorVideo(getEnumerator(context).isFrontFacing((videoTrack.capturer as CameraCapturer).cameraName))
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
    update()
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

  fun setMirrorVideo(mirrorVideo: Boolean) {
    if (this.mirrorVideo == mirrorVideo) return
    this.mirrorVideo = mirrorVideo
    coroutineScope.launch {
      fadeAnimation.start()
      delay(200)
      videoView.setMirror(mirrorVideo)
      delay(200)
      fadeAnimation.reverse()
    }
  }
}
