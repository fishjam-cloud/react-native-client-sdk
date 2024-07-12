package com.fishjamdev.client.models

enum class EndpointType {
  WEBRTC;

  companion object {
    fun fromString(type: String): EndpointType {
      return EndpointType.valueOf(type.uppercase())
    }
  }
}
