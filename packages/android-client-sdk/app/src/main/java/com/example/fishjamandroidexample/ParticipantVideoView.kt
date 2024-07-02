package com.example.fishjamandroidexample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.membraneframework.rtc.media.VideoTrack
import org.membraneframework.rtc.ui.VideoTextureViewRenderer
import org.webrtc.RendererCommon

enum class VideoViewLayout {
    FIT,
    FILL
    ;

    internal fun toScalingType(): RendererCommon.ScalingType {
        return when (this) {
            FIT -> RendererCommon.ScalingType.SCALE_ASPECT_FIT
            FILL -> RendererCommon.ScalingType.SCALE_ASPECT_FILL
        }
    }
}

@Composable
fun ParticipantVideoView(
    participant: Participant,
    videoViewLayout: VideoViewLayout,
    modifier: Modifier = Modifier
) {
    var activeVideoTrack by remember { mutableStateOf<VideoTrack?>(null) }
    var view: VideoTextureViewRenderer? by remember { mutableStateOf(null) }

    fun setupTrack(
        videoTrack: VideoTrack,
        view: VideoTextureViewRenderer
    ) {
        if (activeVideoTrack == videoTrack) return

        activeVideoTrack?.removeRenderer(view)
        videoTrack.addRenderer(view)
        activeVideoTrack = videoTrack
    }

    DisposableEffect(participant.videoTrack) {
        onDispose {
            view?.let {
                participant.videoTrack!!.removeRenderer(it)
            }
        }
    }

    DisposableEffect(currentCompositeKeyHash.toString()) {
        onDispose {
            view?.release()
        }
    }

    AndroidView(
        factory = { context ->
            VideoTextureViewRenderer(context).apply {
                this.init(participant.videoTrack!!.eglContext, null)

                this.setScalingType(videoViewLayout.toScalingType())
                this.setEnableHardwareScaler(true)

                setupTrack(participant.videoTrack, this)

                view = this
            }
        },
        update = { updatedView ->
            setupTrack(participant.videoTrack!!, updatedView)
        },
        modifier = modifier
    )
}
