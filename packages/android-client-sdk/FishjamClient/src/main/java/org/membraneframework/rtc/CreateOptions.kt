package org.membraneframework.rtc

/**
 * A set of options used by the <strong>MembraneRTC</strong> when creating the client.
 * @property encoderOptions The encoder options used to encode video
 */
data class CreateOptions(
    val encoderOptions: EncoderOptions = EncoderOptions()
)
