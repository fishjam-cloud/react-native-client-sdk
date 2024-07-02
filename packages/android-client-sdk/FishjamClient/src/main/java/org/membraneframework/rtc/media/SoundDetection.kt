package org.membraneframework.rtc.media

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import timber.log.Timber
import java.util.*
import kotlin.math.abs
import kotlin.math.log10

/**
 * Class responsible for sound detection using the device's microphone.
 */
class SoundDetection {
    private var timer: Timer? = null
    private var audioRecord: AudioRecord? = null
    private var onSoundDetectedListener: OnSoundDetectedListener? = null
    private var bufferSize = 0
    var isRecording = false
        private set

    /**
     * Starts the sound detection process with the specified monitor interval, sampling rate, and volume threshold.
     *
     * @param monitorInterval The time interval (in milliseconds) between sound detection checks.
     * @param samplingRate The audio sampling rate in Hz.
     * @param volumeThreshold The threshold value in decibels (dB) above which a sound is considered detected.
     */
    fun start(
        monitorInterval: Int = 50,
        samplingRate: Int = 22050,
        volumeThreshold: Int = -60
    ) {
        if (isRecording) {
            Timber.w("Sound detection is already in progress. Ignoring the start request.")
            return
        }
        bufferSize =
            AudioRecord.getMinBufferSize(
                samplingRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        audioRecord =
            try {
                AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    samplingRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )
            } catch (e: SecurityException) {
                throw SecurityException(
                    "Unable to initialize the AudioRecord." +
                        " Ensure that the recording permission is granted."
                )
            }

        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            audioRecord?.startRecording()
            isRecording = true
            startTimer(monitorInterval, volumeThreshold)
        } else {
            Timber.e("COULDNT_PREPARE_RECORDING AudioRecord couldn't be initialized.")
            throw IllegalStateException("AudioRecord couldn't be initialized.")
        }
    }

    /**
     * Stops the sound detection process and releases the resources used by the AudioRecord.
     */
    fun stop() {
        if (!isRecording) {
            Timber.e("INVALID_STATE Please call start before stopping recording")
            return
        }
        stopTimer()
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: RuntimeException) {
            Timber.e(
                "RUNTIME_EXCEPTION No valid audio data received. You may be using a device that can't record audio."
            )
        } finally {
            audioRecord = null
        }
    }

    /**
     * Sets a listener to receive sound detection events.
     *
     * @param listener The listener to be notified when a sound is detected.
     */
    fun setSoundDetectionListener(listener: OnSoundDetectedListener?) {
        onSoundDetectedListener = listener
    }

    /**
     * Sets the sound detection status and notifies the listener if available.
     *
     * @param detectionResult The result of the sound detection (true if sound detected, false otherwise).
     */
    private fun setIsSoundDetected(detectionResult: Boolean) {
        onSoundDetectedListener?.onSoundDetected(detectionResult)
    }

    /**
     * Sets the sound level value and notifies the listener if available.
     *
     * @param soundVolume The new sound level value to be set.
     */
    private fun setIsSoundVolumeChanged(soundVolume: Int) {
        onSoundDetectedListener?.onSoundVolumeChanged(soundVolume)
    }

    /**
     * Evaluates whether the provided `volumeValue` surpasses the given `volumeThreshold`,
     * indicating sound detection. Updates the sound detection status using
     * `setIsSoundDetected` and informs listeners of sound level change via
     * `setIsSoundVolumeChanged` with the provided `volumeValue`.
     *
     * @param volumeThreshold The threshold value in decibels (dB) above which a sound is considered detected.
     * @param volumeValue The current volume value in decibels (dB).
     */
    private fun detectSound(
        volumeThreshold: Int,
        volumeValue: Int
    ) {
        setIsSoundDetected(volumeValue > volumeThreshold)
        setIsSoundVolumeChanged(volumeValue)
    }

    /**
     * Calculates the maximum amplitude value from the audio buffer.
     *
     * @param buffer The audio buffer.
     * @param bytesRead The number of bytes read from the audio buffer.
     * @return The maximum amplitude value from the buffer.
     */
    private fun getMaxAmplitude(
        buffer: ShortArray,
        bytesRead: Int
    ): Int {
        return buffer.take(bytesRead).maxOfOrNull { abs(it.toInt()) } ?: 0
    }

    /**
     * Calculates the sound level value in decibels (dB) based on the given maxAmplitude.
     *
     * If maxAmplitude is non-positive (<= 0), returns a default value of -160 dB.
     * Otherwise, computes the value using the formula: 20 * log10(maxAmplitude / 32767),
     * where 32767 is the maximum value for a 16-bit PCM audio sample.
     * The calculated dB value represents the loudness of the audio signal.
     *
     * @param maxAmplitude The maximum amplitude value from the audio buffer.
     * @return The sound level value in decibels (dB).
     */
    private fun calculateValue(maxAmplitude: Int): Int {
        if (maxAmplitude <= 0) {
            return -160
        }
        return (20 * log10(maxAmplitude.toDouble() / 32767)).toInt()
    }

    /**
     * Reads audio data from the audio record and fills the buffer.
     *
     * @param buffer The audio buffer to be filled.
     * @return The number of bytes read from the audio record.
     */
    private fun readAudioData(buffer: ShortArray): Int {
        return audioRecord?.read(buffer, 0, bufferSize) ?: 0
    }

    /**
     * Starts the timer to periodically perform sound detection.
     *
     * @param monitorInterval The time interval (in milliseconds) between sound detection checks.
     * @param volumeThreshold The threshold value in decibels (dB) above which a sound is considered detected.
     */
    private fun startTimer(
        monitorInterval: Int,
        volumeThreshold: Int
    ) {
        timer = Timer()
        timer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    val buffer = ShortArray(bufferSize)
                    val bytesRead = readAudioData(buffer)
                    if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION || bytesRead == AudioRecord.ERROR_BAD_VALUE) {
                        Timber.w("Invalid bytesRead value: $bytesRead")
                        return
                    }
                    val amplitude = getMaxAmplitude(buffer, bytesRead)
                    val value = calculateValue(amplitude)
                    detectSound(volumeThreshold, value)
                }
            },
            0,
            monitorInterval.toLong()
        )
    }

    /**
     * Stops the timer used for sound detection.
     */
    private fun stopTimer() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }
}
