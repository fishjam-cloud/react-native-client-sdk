package com.fishjamcloud.client.webrtc

import org.webrtc.IceCandidate
import org.webrtc.MediaStreamTrack

internal interface PeerConnectionListener {
  fun onAddTrack(
    trackId: String,
    track: MediaStreamTrack
  )

  fun onLocalIceCandidate(candidate: IceCandidate)
}
