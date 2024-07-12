package com.fishjamdev.client.media

import android.media.projection.MediaProjection
import com.fishjamdev.client.models.VideoParameters
import com.fishjamdev.client.models.Metadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.fishjamdev.client.utils.ClosableCoroutineScope
import org.webrtc.ScreenCapturerAndroid
import java.util.ArrayList

class LocalScreencastTrack(
  internal val videoTrack: org.webrtc.VideoTrack,
  endpointId: String,
  metadata: Metadata,
  private val capturer: ScreenCapturerAndroid,
  val videoParameters: VideoParameters
) : LocalTrack, Track(videoTrack, endpointId, null, metadata) {
  private val mutex = Mutex()
  private val coroutineScope: CoroutineScope =
    ClosableCoroutineScope(SupervisorJob())
  private var isStopped = false

  override fun start() {
    coroutineScope.launch {
      mutex.withLock {
        if (!isStopped) {
          capturer.startCapture(
            videoParameters.dimensions.width,
            videoParameters.dimensions.height,
            videoParameters.maxFps
          )
        }
      }
    }
  }

  override fun stop() {
    coroutineScope.launch {
      mutex.withLock {
        isStopped = true
        capturer.stopCapture()
        capturer.dispose()
        videoTrack.dispose()
      }
    }
  }

  /*
      MediaProjection callback wrapper holding several callbacks that
      will be invoked once the media projections stops.
   */
  class ProjectionCallback : MediaProjection.Callback() {
    var callbacks: ArrayList<() -> Unit> = arrayListOf()

    override fun onStop() {
      callbacks.forEach {
        it.invoke()
      }

      callbacks.clear()
    }

    fun addCallback(callback: () -> Unit) {
      callbacks.add(callback)
    }
  }


}
