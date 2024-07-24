package org.membraneframework.reactnative

import android.animation.ValueAnimator
import android.content.Context
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
  protected abstract val fadeAnimation: ValueAnimator
  protected abstract val videoView: VideoTextureViewRenderer?

  fun setMirrorVideo(
    isFrontCamera: Boolean?,
    isInitialCall: Boolean
  ) {
    if (this.isFrontCamera == isFrontCamera) return
    this.isFrontCamera = isFrontCamera ?: this.isFrontCamera
    if (!(isInitialCall || isInitialized)) return

    coroutineScope.launch {
      if (!isInitialCall) {
        fadeAnimation.start()
        delay(200)
      }
      videoView?.setMirror(this@MirrorableView.isFrontCamera)
      delay(800)
      fadeAnimation.reverse()
    }
  }
}
