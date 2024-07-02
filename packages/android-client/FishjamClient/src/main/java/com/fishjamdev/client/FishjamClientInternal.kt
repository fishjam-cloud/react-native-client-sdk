package com.fishjamdev.client

import android.content.Context
import fishjam.PeerNotifications.PeerMessage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import org.membraneframework.rtc.MembraneRTC
import org.membraneframework.rtc.MembraneRTCListener
import org.membraneframework.rtc.models.Endpoint
import org.membraneframework.rtc.models.TrackContext
import org.membraneframework.rtc.utils.SerializedMediaEvent
import org.membraneframework.rtc.utils.TimberDebugTree
import timber.log.Timber

typealias Peer = Endpoint

internal class FishjamClientInternal(
    appContext: Context,
    private val listener: FishjamClientListener
) :
    MembraneRTCListener {
    private var webSocket: WebSocket? = null
    val webrtcClient = MembraneRTC.create(appContext, this)
    private var isAuthenticated = false

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(TimberDebugTree())
        }
    }

    fun connect(config: Config) {
        val request = Request.Builder().url(config.websocketUrl).build()
        val webSocket =
            OkHttpClient().newWebSocket(
                request,
                object : WebSocketListener() {
                    override fun onClosed(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String
                    ) {
                        listener.onSocketClose(code, reason)
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        bytes: ByteString
                    ) {
                        try {
                            val peerMessage = PeerMessage.parseFrom(bytes.toByteArray())
                            if (peerMessage.hasAuthenticated()) {
                                isAuthenticated = true
                                listener.onAuthSuccess()
                            } else if (peerMessage.hasMediaEvent()) {
                                receiveEvent(peerMessage.mediaEvent.data)
                            } else {
                                Timber.w("Received unexpected websocket message: $peerMessage")
                            }
                        } catch (e: Exception) {
                            Timber.e("Received invalid websocket message", e)
                        }
                    }

                    override fun onOpen(
                        webSocket: WebSocket,
                        response: Response
                    ) {
                        listener.onSocketOpen()
                        val authRequest =
                            PeerMessage
                                .newBuilder()
                                .setAuthRequest(PeerMessage.AuthRequest.newBuilder().setToken(config.token))
                                .build()
                        sendEvent(authRequest)
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?
                    ) {
                        listener.onSocketError(t, response)
                    }
                }
            )

        this.webSocket = webSocket
    }

    fun leave() {
        webrtcClient.disconnect()
        isAuthenticated = false
    }

    fun cleanUp() {
        webrtcClient.disconnect()
        isAuthenticated = false
        webSocket?.close(1000, null)
        webSocket = null
        listener.onDisconnected()
    }

    private fun sendEvent(peerMessage: PeerMessage) {
        webSocket?.send(peerMessage.toByteArray().toByteString())
    }

    private fun receiveEvent(event: SerializedMediaEvent) {
        webrtcClient.receiveMediaEvent(event)
    }

    override fun onEndpointAdded(peer: Peer) {
        listener.onPeerJoined(peer)
    }

    override fun onEndpointRemoved(peer: Peer) {
        listener.onPeerLeft(peer)
    }

    override fun onEndpointUpdated(peer: Peer) {
        listener.onPeerUpdated(peer)
    }

    override fun onSendMediaEvent(event: SerializedMediaEvent) {
        if (!isAuthenticated) {
            Timber.e("Tried to send media event: $event before authentication")
            return
        }
        val mediaEvent =
            PeerMessage
                .newBuilder()
                .setMediaEvent(PeerMessage.MediaEvent.newBuilder().setData(event))
                .build()
        sendEvent(mediaEvent)
    }

    override fun onTrackAdded(ctx: TrackContext) {
        listener.onTrackAdded(ctx)
    }

    override fun onTrackReady(ctx: TrackContext) {
        listener.onTrackReady(ctx)
    }

    override fun onTrackRemoved(ctx: TrackContext) {
        listener.onTrackRemoved(ctx)
    }

    override fun onTrackUpdated(ctx: TrackContext) {
        listener.onTrackUpdated(ctx)
    }

    override fun onBandwidthEstimationChanged(estimation: Long) {
        listener.onBandwidthEstimationChanged(estimation)
    }

    override fun onConnectError(metadata: Any) {
        listener.onJoinError(metadata)
    }

    override fun onConnected(
        peerID: String,
        peersInRoom: List<Peer>
    ) {
        listener.onJoined(peerID, peersInRoom)
    }

    override fun onDisconnected() {
        listener.onDisconnected()
    }
}
