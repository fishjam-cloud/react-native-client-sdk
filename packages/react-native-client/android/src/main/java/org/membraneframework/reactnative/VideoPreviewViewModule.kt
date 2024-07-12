package org.membraneframework.reactnative

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class VideoPreviewViewModule : Module() {
  override fun definition() =
    ModuleDefinition {
      Name("VideoPreviewViewModule")

      View(VideoPreviewView::class) {
        Prop("videoLayout") { view: VideoPreviewView, videoLayout: String ->
          view.setVideoLayout(videoLayout)
        }

        Prop("mirrorVideo") { view: VideoPreviewView, mirrorVideo: Boolean ->
          view.setMirrorVideo(mirrorVideo)
        }
      }
    }
}
