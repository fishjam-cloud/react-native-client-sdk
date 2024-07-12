package com.fishjamdev.client.models

/**
 * Type describing possible reasons of currently selected encoding.
 * - OTHER - the exact reason couldn't be determined
 * - ENCODING_INACTIVE - previously selected encoding became inactive
 * - LOW_BANDWIDTH - there is no longer enough bandwidth to maintain previously selected encoding
 */
enum class EncodingReason(
  val value: String
) {
  OTHER("other"),
  ENCODING_INACTIVE("encoding_inactive"),
  LOW_BANDWIDTH("low_bandwidth");

  companion object {
    fun fromString(s: String): EncodingReason? = values().find { it.value == s }
  }
}
