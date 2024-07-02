package org.membraneframework.rtc.transport

import kotlinx.coroutines.*
import org.membraneframework.rtc.utils.ClosableCoroutineScope
import org.membraneframework.rtc.utils.SerializedMediaEvent
import org.membraneframework.rtc.utils.SocketChannelParams
import org.membraneframework.rtc.utils.SocketConnectionParams
import org.phoenixframework.Channel
import org.phoenixframework.Socket
import timber.log.Timber

sealed class PhoenixTransportError : Exception() {
    data class Unauthorized(val reason: String) : PhoenixTransportError()

    data class ConnectionError(val reason: String) : PhoenixTransportError()

    data class Unexpected(val reason: String) : PhoenixTransportError()

    override fun toString(): String {
        return when (this) {
            is Unauthorized ->
                "User is unauthorized to use the transport: ${this.reason}"
            is ConnectionError ->
                "Failed to connect with the remote side: ${this.reason}"
            is Unexpected ->
                "Encountered unexpected error: ${this.reason}"
        }
    }
}

/**
 * An interface defining a listener to a <strong>PhoenixTransport</strong>.
 */
interface PhoenixTransportListener {
    fun onEvent(event: SerializedMediaEvent)

    fun onError(error: PhoenixTransportError)

    fun onClose()
}

class PhoenixTransport(
    private val url: String,
    private val topic: String,
    private val ioDispatcher: CoroutineDispatcher,
    private val params: SocketConnectionParams? = emptyMap(),
    private val socketChannelParams: SocketChannelParams = emptyMap()
) {
    private lateinit var coroutineScope: CoroutineScope
    private var socket: Socket? = null
    private var channel: Channel? = null
    private var listener: PhoenixTransportListener? = null

    private var joinContinuation: CancellableContinuation<Unit>? = null

    suspend fun connect(listener: PhoenixTransportListener) {
        Timber.i("Starting connection...")
        this.listener = listener

        coroutineScope = ClosableCoroutineScope(SupervisorJob() + ioDispatcher)

        socket = Socket(url, params)
        socket!!.connect()

        var socketRefs: Array<String> = emptyArray()

        suspendCancellableCoroutine<Unit> { continuation ->
            val openRef =
                socket!!.onOpen {
                    continuation.resumeWith(Result.success(Unit))
                }

            val errorRef =
                socket!!.onError { error, _ ->
                    continuation.cancel(PhoenixTransportError.ConnectionError(error.toString()))
                }

            val closeRef =
                socket!!.onClose {
                    continuation.cancel(PhoenixTransportError.ConnectionError("closed"))
                }

            socketRefs += openRef
            socketRefs += errorRef
            socketRefs += closeRef
        }

        socket!!.off(socketRefs.toList())

        socket!!.onError { error, _ ->
            this.listener?.onError(PhoenixTransportError.ConnectionError(error.toString()))
        }

        socket!!.onClose {
            this.listener?.onClose()
        }

        channel = socket!!.channel(topic, socketChannelParams)

        channel?.join(timeout = 3000L)
            ?.receive("ok") { _ ->
                joinContinuation?.resumeWith(Result.success(Unit))
            }
            ?.receive("error") { _ ->
                joinContinuation?.resumeWith(
                    Result.failure(PhoenixTransportError.Unauthorized("couldn't join phoenix channel"))
                )
            }

        channel?.on("mediaEvent") { message ->
            val data = message.payload["data"] as String
            listener.onEvent(data)
        }

        return suspendCancellableCoroutine {
            joinContinuation = it
        }
    }

    fun disconnect() {
        if (channel != null) {
            channel
                ?.leave()
                ?.receive("ok") {
                    socket?.disconnect()
                }
        } else {
            socket?.disconnect()
        }
    }

    fun send(event: SerializedMediaEvent) {
        val payload =
            mapOf(
                "data" to event
            )
        channel?.push("mediaEvent", payload)
    }
}
