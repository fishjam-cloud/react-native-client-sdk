package org.membraneframework.rtc.media

import org.webrtc.EglBase
import org.webrtc.MediaStreamTrack
import org.webrtc.VideoSink

open class VideoTrack(
  protected val videoTrack: org.webrtc.VideoTrack,
  val eglContext: EglBase.Context
) : MediaTrackProvider {
  override fun id(): String = videoTrack.id()

  override fun rtcTrack(): MediaStreamTrack = videoTrack

  fun addRenderer(renderer: VideoSink) {
    this.videoTrack.addSink(renderer)
  }

  fun removeRenderer(renderer: VideoSink) {
    this.videoTrack.removeSink(renderer)
  }
}
