package org.membraneframework.rtc

import android.content.Context
import org.webrtc.EglBase
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import timber.log.Timber

internal object RTCModule {
    fun eglBase(): EglBase {
        return EglBase.create()
    }

    fun audioDeviceModule(appContext: Context): AudioDeviceModule {
        val audioRecordErrorCallback =
            object : JavaAudioDeviceModule.AudioRecordErrorCallback {
                override fun onWebRtcAudioRecordInitError(errorMessage: String?) {
                    Timber.e("onWebRtcAudioRecordInitError: $errorMessage")
                }

                override fun onWebRtcAudioRecordStartError(
                    errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode?,
                    errorMessage: String?
                ) {
                    Timber.e("onWebRtcAudioRecordStartError: $errorCode. $errorMessage")
                }

                override fun onWebRtcAudioRecordError(errorMessage: String?) {
                    Timber.e("onWebRtcAudioRecordError: $errorMessage")
                }
            }

        val audioTrackErrorCallback =
            object : JavaAudioDeviceModule.AudioTrackErrorCallback {
                override fun onWebRtcAudioTrackInitError(errorMessage: String?) {
                    Timber.e("onWebRtcAudioTrackInitError: $errorMessage")
                }

                override fun onWebRtcAudioTrackStartError(
                    errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode?,
                    errorMessage: String?
                ) {
                    Timber.e("onWebRtcAudioTrackStartError: $errorCode. $errorMessage")
                }

                override fun onWebRtcAudioTrackError(errorMessage: String?) {
                    Timber.e("onWebRtcAudioTrackError: $errorMessage")
                }
            }
        val audioRecordStateCallback: JavaAudioDeviceModule.AudioRecordStateCallback =
            object :
                JavaAudioDeviceModule.AudioRecordStateCallback {
                override fun onWebRtcAudioRecordStart() {
                    Timber.i("Audio recording starts")
                }

                override fun onWebRtcAudioRecordStop() {
                    Timber.i("Audio recording stops")
                }
            }

        // Set audio track state callbacks.
        val audioTrackStateCallback: JavaAudioDeviceModule.AudioTrackStateCallback =
            object :
                JavaAudioDeviceModule.AudioTrackStateCallback {
                override fun onWebRtcAudioTrackStart() {
                    Timber.i("Audio playout starts")
                }

                override fun onWebRtcAudioTrackStop() {
                    Timber.i("Audio playout stops")
                }
            }

        return JavaAudioDeviceModule.builder(appContext)
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(true)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .setAudioRecordStateCallback(audioRecordStateCallback)
            .setAudioTrackStateCallback(audioTrackStateCallback)
            .createAudioDeviceModule()
    }
}
