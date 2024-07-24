package org.membraneframework.reactnative

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.fishjamcloud.client.ui.VideoTextureViewRenderer
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ExpoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class MirrorableView(
  context: Context,
  appContext: AppContext
) : ExpoView(context, appContext) {
  protected var isFrontCamera: Boolean = false
  var isInitialized: Boolean = false
  val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
  private val fadeAnimation: ValueAnimator =  getVideoViewFadeAnimator { color ->
    foreground = ColorDrawable(color)
  }

  protected abstract val videoView: VideoTextureViewRenderer?

  fun setMirrorVideo(
    isFrontCamera: Boolean,
    isInitialCall: Boolean
  ) {
    if (this.isFrontCamera == isFrontCamera && !isInitialCall) return
    this.isFrontCamera = isFrontCamera
    if (!isInitialCall && !isInitialized) return

    coroutineScope.launch {
      if (!isInitialCall) {
        fadeAnimation.start()
        delay(200)
      }
      videoView?.setMirror(this@MirrorableView.isFrontCamera)
      delay(600)
      fadeAnimation.reverse()
    }
  }
}
