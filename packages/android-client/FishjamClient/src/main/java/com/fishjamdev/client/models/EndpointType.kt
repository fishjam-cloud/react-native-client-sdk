package com.fishjamdev.client.models

enum class EndpointType {
  WEBRTC;

  companion object {
    fun fromString(type: String): EndpointType = EndpointType.valueOf(type.uppercase())
  }
}
