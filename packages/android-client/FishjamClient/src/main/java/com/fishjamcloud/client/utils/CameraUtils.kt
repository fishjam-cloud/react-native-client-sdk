package com.fishjamcloud.client.utils

import android.content.Context
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator

fun getEnumerator(context: Context): CameraEnumerator =
  if (Camera2Enumerator.isSupported(context)) {
    Camera2Enumerator(context)
  } else {
    Camera1Enumerator(true)
  }
