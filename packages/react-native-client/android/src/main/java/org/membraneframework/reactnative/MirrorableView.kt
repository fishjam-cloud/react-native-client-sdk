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
  private var mirrorVideo: Boolean = false
  private var isFrontCamera: Boolean = false
  var isInitialized: Boolean = false
  val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
  abstract val fadeAnimation: ValueAnimator
  abstract val videoView: VideoTextureViewRenderer?

  fun setMirrorVideo(
    mirrorVideo: Boolean?,
    isFrontCamera: Boolean?
  ) {
    if (this.mirrorVideo == mirrorVideo && this.isFrontCamera == isFrontCamera) return
    this.mirrorVideo = mirrorVideo ?: this.mirrorVideo
    this.isFrontCamera = isFrontCamera ?: this.isFrontCamera
    if (!isInitialized) return

    coroutineScope.launch {
      fadeAnimation.start()
      delay(200)
      videoView?.setMirror(this@MirrorableView.mirrorVideo xor this@MirrorableView.isFrontCamera)
      delay(800)
      fadeAnimation.reverse()
    }
  }

  fun initialSetMirrorVideo(isFrontCamera: Boolean) {
    this.isFrontCamera = isFrontCamera
    coroutineScope.launch {
      videoView?.setMirror(this@MirrorableView.mirrorVideo xor this@MirrorableView.isFrontCamera)
      delay(800)
      fadeAnimation.reverse()
    }
  }
}
