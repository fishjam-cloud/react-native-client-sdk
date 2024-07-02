package org.membraneframework.rtc.media

class RemoteAudioTrack(
    mediaTrack: org.webrtc.AudioTrack
) : AudioTrack(mediaTrack), RemoteTrack {
    override fun enabled(): Boolean {
        return this.audioTrack.enabled()
    }

    override fun setEnabled(enabled: Boolean) {
        this.audioTrack.setEnabled(enabled)
    }
}
