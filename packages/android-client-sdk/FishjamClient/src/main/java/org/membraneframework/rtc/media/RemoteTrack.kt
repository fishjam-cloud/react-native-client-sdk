package org.membraneframework.rtc.media

interface RemoteTrack {
    fun enabled(): Boolean

    fun setEnabled(enabled: Boolean)
}
