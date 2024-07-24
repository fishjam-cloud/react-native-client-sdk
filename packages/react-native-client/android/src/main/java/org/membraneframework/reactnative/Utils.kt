package org.membraneframework.reactnative

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.animation.LinearInterpolator
import com.fishjamcloud.client.models.TrackEncoding

internal fun String.toTrackEncoding(): TrackEncoding =
  when (this) {
    "l" -> TrackEncoding.L
    "m" -> TrackEncoding.M
    "h" -> TrackEncoding.H
    else -> throw IllegalArgumentException("Invalid encoding specified: $this")
  }

internal fun getVideoViewFadeAnimator(updateColor: (Int) -> Unit): ValueAnimator =
  ValueAnimator.ofArgb(Color.TRANSPARENT, Color.BLACK).apply {
    duration = 200
    interpolator = LinearInterpolator()
    addUpdateListener {
      val colorValue = it.animatedValue as Int
      updateColor(colorValue)
    }
  }
