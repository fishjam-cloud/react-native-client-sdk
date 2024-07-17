package org.membraneframework.reactnative

import android.content.Context
import com.fishjamcloud.client.media.VideoTrack
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ExpoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.RendererCommon

class VideoRendererView(
  context: Context,
  appContext: AppContext
) : ExpoView(context, appContext),
  RNFishjamClient.OnTrackUpdateListener {
  var activeVideoTrack: VideoTrack? = null
  var trackId: String? = null

  private val videoView =
    RNFishjamClient.fishjamClient.createVideoViewRenderer().also {
      addView(it)
      RNFishjamClient.onTracksUpdateListeners.add(this)
    }

  private fun setupTrack(videoTrack: VideoTrack) {
    if (activeVideoTrack == videoTrack) return

    activeVideoTrack?.removeRenderer(videoView)
    videoTrack.addRenderer(videoView)
    activeVideoTrack = videoTrack
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
    videoView.setMirror(mirrorVideo)
  }
}
