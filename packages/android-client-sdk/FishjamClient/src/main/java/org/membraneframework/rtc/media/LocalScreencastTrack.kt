package org.membraneframework.rtc.media

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.membraneframework.rtc.media.screencast.ScreencastServiceConnector
import org.membraneframework.rtc.utils.ClosableCoroutineScope
import org.webrtc.*
import java.util.*

/**
 * A class representing a local screencast track.
 * <p>
 * It is responsible for managing an instance of <strong>ScreenCapturerAndroid</strong>
 * that is responsible for capturing the entire device's screen and passing it to the WebRTC
 * <strong>VideoTrack</strong>.
 */
class LocalScreencastTrack(
    private val source: VideoSource,
    mediaTrack: org.webrtc.VideoTrack,
    context: Context,
    eglBase: EglBase,
    private val capturer: ScreenCapturerAndroid,
    val videoParameters: VideoParameters,
    callback: ProjectionCallback
) : VideoTrack(mediaTrack, eglBase.eglBaseContext), LocalTrack {
    private val screencastConnection = ScreencastServiceConnector(context)
    private val mutex = Mutex()
    private val coroutineScope: CoroutineScope =
        ClosableCoroutineScope(SupervisorJob())
    private var isStopped = false

    suspend fun startForegroundService(
        notificationId: Int?,
        notification: Notification?
    ) {
        mutex.withLock {
            if (!isStopped) {
                screencastConnection.connect()
                screencastConnection.start(notificationId, notification)
            }
        }
    }

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
                screencastConnection.stop()
                capturer.stopCapture()
                capturer.dispose()
                videoTrack.dispose()
                source.dispose()
            }
        }
    }

    override fun enabled(): Boolean {
        return videoTrack.enabled()
    }

    override fun setEnabled(enabled: Boolean) {
        videoTrack.setEnabled(enabled)
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

    companion object {
        /**
         * Creates a screencast track.
         *
         * @param context: context of the current application
         * @param factory: an instance of <strong>PeerConnectionFactory</strong> used for creating video sources and tracks
         * @param simulcastConfig: simulcast configuration. By default simulcast is disabled.
         * @param eglBase: an instance of <strong>EglBase</strong> used for rendering the video
         */
        fun create(
            context: Context,
            factory: PeerConnectionFactory,
            eglBase: EglBase,
            mediaProjectionPermission: Intent,
            videoParameters: VideoParameters,
            onStopped: (track: LocalScreencastTrack) -> Unit
        ): LocalScreencastTrack {
            val source = factory.createVideoSource(true)

            val track = factory.createVideoTrack(UUID.randomUUID().toString(), source)

            val callback = ProjectionCallback()

            val capturer = ScreenCapturerAndroid(mediaProjectionPermission, callback)

            capturer.initialize(
                SurfaceTextureHelper.create("ScreenVideoCaptureThread", eglBase.eglBaseContext),
                context,
                source.capturerObserver
            )

            val localScreencastTrack =
                LocalScreencastTrack(
                    source,
                    track,
                    context,
                    eglBase,
                    capturer,
                    videoParameters,
                    callback
                )
            callback.addCallback {
                onStopped(localScreencastTrack)
            }

            return localScreencastTrack
        }
    }
}
