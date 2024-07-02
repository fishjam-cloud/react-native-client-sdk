package org.membraneframework.rtc.media

interface OnSoundDetectedListener {
    fun onSoundDetected(isDetected: Boolean)

    fun onSoundVolumeChanged(volume: Int)
}
