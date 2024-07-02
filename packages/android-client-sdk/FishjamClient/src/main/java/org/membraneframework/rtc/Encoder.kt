package org.membraneframework.rtc

/**
 * Enum describing possible encoder types
 * `"SOFTWARE"` - use software encoder, in most cases vp8
 * `"HARDWARE"` - use hardware encoder, depends on the device
 *
 * TODO: for the current webrtc version simulcast doesn't work with hardware encoders
 */
enum class EncoderType {
    SOFTWARE,
    HARDWARE
}

/**
 * Class containing encoder options
 */
data class EncoderOptions(
    /**
     * SOFTWARE / HARDWARE encoder, default: HARDWARE
     */
    val encoderType: EncoderType = EncoderType.HARDWARE,
    /**
     * whether to enable Intel's VP8 encoder, default: true
     */
    val enableIntelVp8Encoder: Boolean = true,
    /**
     * whether to enable H264 High Profile, default: false
     */
    val enableH264HighProfile: Boolean = false
)
