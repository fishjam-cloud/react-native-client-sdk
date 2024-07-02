package org.membraneframework.reactnative

import expo.modules.kotlin.Promise
import expo.modules.kotlin.functions.Coroutine
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SimulcastConfig : Record {
  @Field
  val enabled: Boolean = false

  @Field
  val activeEncodings: List<String> = emptyList()
}

class CameraConfig : Record {
  @Field
  val quality: String = "VGA169"

  @Field
  val flipVideo: Boolean = false

  @Field
  val videoTrackMetadata: Map<String, Any> = emptyMap()

  @Field
  val simulcastConfig: SimulcastConfig = SimulcastConfig()

  // expo-modules on Android don't support Either type
  @Field
  val maxBandwidthMap: Map<String, Int> = emptyMap()

  @Field
  val maxBandwidthInt: Int = 0

  @Field
  val cameraEnabled: Boolean = true

  @Field
  val captureDeviceId: String? = null
}

class MicrophoneConfig : Record {
  @Field
  val audioTrackMetadata: Map<String, Any> = emptyMap()

  @Field
  val microphoneEnabled: Boolean = true
}

class ScreencastOptions : Record {
  @Field
  val quality: String = "HD15"

  @Field
  val screencastMetadata: Map<String, Any> = emptyMap()

  @Field
  val simulcastConfig: SimulcastConfig = SimulcastConfig()

  @Field
  val maxBandwidthMap: Map<String, Int> = emptyMap()

  @Field
  val maxBandwidthInt: Int = 0
}

class RNFishjamClientModule : Module() {
  override fun definition() =
    ModuleDefinition {
      Name("RNFishjamClient")

      Events(
        "IsCameraOn",
        "IsMicrophoneOn",
        "IsScreencastOn",
        "SimulcastConfigUpdate",
        "EndpointsUpdate",
        "AudioDeviceUpdate",
        "SendMediaEvent",
        "BandwidthEstimation"
      )

      val rnFishjamClient =
        RNFishjamClient { name: String, data: Map<String, Any?> ->
          sendEvent(name, data)
        }

      OnCreate {
        rnFishjamClient.onModuleCreate(appContext)
      }

      OnDestroy {
        rnFishjamClient.onModuleDestroy()
      }

      OnActivityDestroys {
        rnFishjamClient.cleanUp()
      }

      OnActivityResult { _, result ->
        rnFishjamClient.onActivityResult(result.requestCode, result.resultCode, result.data)
      }

      AsyncFunction("connect") { url: String, peerToken: String, promise: Promise ->
        CoroutineScope(Dispatchers.Main).launch {
          rnFishjamClient.create()
          rnFishjamClient.connect(url, peerToken, promise)
        }
      }

      AsyncFunction("joinRoom") { peerMetadata: Map<String, Any>, promise: Promise ->
        CoroutineScope(Dispatchers.Main).launch {
          rnFishjamClient.joinRoom(peerMetadata, promise)
        }
      }

      AsyncFunction("leaveRoom") { ->
        CoroutineScope(Dispatchers.Main).launch {
          rnFishjamClient.leaveRoom()
        }
      }

      AsyncFunction("cleanUp") Coroutine { ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.cleanUp()
        }
      }

      AsyncFunction("startCamera") Coroutine { config: CameraConfig ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.startCamera(config)
        }
      }

      AsyncFunction("startMicrophone") Coroutine { config: MicrophoneConfig ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.startMicrophone(config)
        }
      }

      Property("isMicrophoneOn") {
        return@Property rnFishjamClient.isMicrophoneOn
      }

      AsyncFunction("toggleMicrophone") Coroutine { ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.toggleMicrophone()
        }
      }

      Property("isCameraOn") {
        return@Property rnFishjamClient.isCameraOn
      }

      AsyncFunction("toggleCamera") Coroutine { ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.toggleCamera()
        }
      }

      AsyncFunction("flipCamera") Coroutine { ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.flipCamera()
        }
      }

      AsyncFunction("switchCamera") Coroutine { captureDeviceId: String ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.switchCamera(captureDeviceId)
        }
      }

      AsyncFunction("getCaptureDevices") Coroutine { ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.getCaptureDevices()
        }
      }

      AsyncFunction("toggleScreencast") { screencastOptions: ScreencastOptions, promise: Promise ->
        CoroutineScope(Dispatchers.Main).launch {
          rnFishjamClient.toggleScreencast(screencastOptions, promise)
        }
      }

      Property("isScreencastOn") {
        return@Property rnFishjamClient.isScreencastOn
      }

      AsyncFunction("getEndpoints") Coroutine { ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.getEndpoints()
        }
      }

      AsyncFunction("updateEndpointMetadata") Coroutine { metadata: Map<String, Any> ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.updateEndpointMetadata(metadata)
        }
      }

      AsyncFunction("updateVideoTrackMetadata") Coroutine { metadata: Map<String, Any> ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.updateLocalVideoTrackMetadata(metadata)
        }
      }

      AsyncFunction("updateAudioTrackMetadata") Coroutine { metadata: Map<String, Any> ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.updateLocalAudioTrackMetadata(metadata)
        }
      }

      AsyncFunction("updateScreencastTrackMetadata") Coroutine { metadata: Map<String, Any> ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.updateLocalScreencastTrackMetadata(metadata)
        }
      }

      AsyncFunction("setOutputAudioDevice") { audioDevice: String ->
        rnFishjamClient.setOutputAudioDevice(audioDevice)
      }

      AsyncFunction("startAudioSwitcher") {
        rnFishjamClient.startAudioSwitcher()
      }

      AsyncFunction("stopAudioSwitcher") {
        rnFishjamClient.stopAudioSwitcher()
      }

      AsyncFunction("toggleScreencastTrackEncoding") Coroutine { encoding: String ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.toggleScreencastTrackEncoding(encoding)
        }
      }

      AsyncFunction("setScreencastTrackBandwidth") Coroutine { bandwidth: Int ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.setScreencastTrackBandwidth(bandwidth)
        }
      }

      AsyncFunction("setScreencastTrackEncodingBandwidth") Coroutine { encoding: String, bandwidth: Int ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.setScreencastTrackEncodingBandwidth(encoding, bandwidth)
        }
      }

      AsyncFunction("setTargetTrackEncoding") Coroutine { trackId: String, encoding: String ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.setTargetTrackEncoding(trackId, encoding)
        }
      }

      AsyncFunction("toggleVideoTrackEncoding") Coroutine { encoding: String ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.toggleVideoTrackEncoding(encoding)
        }
      }

      AsyncFunction("setVideoTrackEncodingBandwidth") Coroutine { encoding: String, bandwidth: Int ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.setVideoTrackEncodingBandwidth(encoding, bandwidth)
        }
      }

      AsyncFunction("setVideoTrackBandwidth") Coroutine { bandwidth: Int ->
        withContext(Dispatchers.Main) {
          rnFishjamClient.setVideoTrackBandwidth(bandwidth)
        }
      }

      AsyncFunction("changeWebRTCLoggingSeverity") Coroutine { severity: String ->
        CoroutineScope(Dispatchers.Main).launch {
          rnFishjamClient.changeWebRTCLoggingSeverity(severity)
        }
      }

      AsyncFunction("getStatistics") { ->
        rnFishjamClient.getStatistics()
      }
    }
}
