package com.fishjamdev.client.media

import com.fishjamdev.client.models.Metadata
import org.webrtc.AudioTrack

class LocalAudioTrack(
  mediaTrack: AudioTrack,
  endpointId: String,
  metadata: Metadata
) : Track(mediaTrack, endpointId, null, metadata),
  LocalTrack {
  override fun start() {
  }

  override fun stop() {
  }
}
