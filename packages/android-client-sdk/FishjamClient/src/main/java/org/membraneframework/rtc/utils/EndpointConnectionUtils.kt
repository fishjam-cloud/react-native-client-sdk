package org.membraneframework.rtc.utils

import org.webrtc.*
import org.webrtc.RtpTransceiver.RtpTransceiverInit

internal fun PeerConnection.addTransceiver(
    mediaTrack: MediaStreamTrack,
    direction: RtpTransceiver.RtpTransceiverDirection,
    streamIds: List<String>,
    sendEncodings: List<RtpParameters.Encoding>
) {
    val transceiverInit = RtpTransceiverInit(direction, streamIds, sendEncodings)
    this.addTransceiver(mediaTrack, transceiverInit)
}

internal fun RtpParameters.getEncodings(): List<RtpParameters.Encoding> {
    return this.encodings
}
