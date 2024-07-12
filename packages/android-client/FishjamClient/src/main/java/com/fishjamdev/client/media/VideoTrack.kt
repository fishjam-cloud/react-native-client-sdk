package com.fishjamdev.client.media

import org.webrtc.VideoSink
import com.fishjamdev.client.models.Metadata
import java.util.UUID

open class VideoTrack(
  internal val videoTrack: org.webrtc.VideoTrack,
  endpointId: String,  rtcEngineId: String?, metadata: Metadata, id: String = UUID.randomUUID().toString()
) : Track(
  videoTrack,
  endpointId,
  rtcEngineId,
  metadata,
  id
) {
  fun addRenderer(renderer: VideoSink) {
    this.videoTrack.addSink(renderer)
  }

  fun removeRenderer(renderer: VideoSink) {
    this.videoTrack.removeSink(renderer)
  }
}
