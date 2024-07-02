package org.membraneframework.rtc.models

/**
 * Type describing Voice Activity Detection statuses.
 *
 * - SPEECH - voice activity has been detected
 * - SILENCE - lack of voice activity has been detected
 */
enum class VadStatus(val value: String) {
    SPEECH("speech"),
    SILENCE("silence");

    companion object {
        fun fromString(s: String): VadStatus? {
            return values().find { it.value == s }
        }
    }
}
