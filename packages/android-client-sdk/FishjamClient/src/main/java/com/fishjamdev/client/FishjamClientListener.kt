package com.fishjamdev.client

import okhttp3.Response
import org.membraneframework.rtc.models.TrackContext
import timber.log.Timber

interface FishjamClientListener {
    /**
     * Emitted when the websocket connection is closed
     */
    fun onSocketClose(
        code: Int,
        reason: String
    ) {
        Timber.i("Socket was closed with code $code reason: $reason")
    }

    /**
     * Emitted when occurs an error in the websocket connection
     */
    fun onSocketError(
        t: Throwable,
        response: Response?
    ) {
        Timber.w("Socket error:", t, response)
    }

    /**
     * Emitted when the websocket connection is opened
     */
    fun onSocketOpen() {
        Timber.i("Socket opened")
    }

    /**
     * Emitted when authentication is successful
     */
    fun onAuthSuccess()

    /**
     * Emitted when authentication fails
     */
    fun onAuthError()

    /**
     * Emitted when local user is connected to fishjam.
     */
    fun onJoined(
        peerID: String,
        peersInRoom: List<Peer>
    )

    /**
     * Emitted when there was an error while connecting to the fishjam.
     */
    fun onJoinError(metadata: Any)

    /**
     * Emitted when the connection is closed
     */
    fun onDisconnected() {
        Timber.i("Client disconnected")
    }

    /**
     * Called each time new peer joins the room.
     */
    fun onPeerJoined(peer: Peer)

    /**
     * Called each time peer leaves the room.
     */
    fun onPeerLeft(peer: Peer)

    /**
     * Called each time peer has its metadata updated.
     */
    fun onPeerUpdated(peer: Peer)

    /**
     * Called when data in a new track arrives.
     *
     * This callback is always called after {@link FishjamClientListener.onTrackAdded}.
     * It informs user that data related to the given track arrives and can be played or displayed.
     */
    fun onTrackReady(ctx: TrackContext)

    /**
     * Called each time the peer which was already in the room, adds new track. Fields track and stream will be set to null.
     * These fields will be set to non-null value in {@link FishjamClientListener.onTrackReady}
     */
    fun onTrackAdded(ctx: TrackContext) {
        Timber.i("Track ${ctx.trackId} added")
    }

    /**
     * Called when some track will no longer be sent.
     *
     * It will also be called before {@link FishjamClientListener.onPeerLeft} for each track of this peer.
     */
    fun onTrackRemoved(ctx: TrackContext)

    /**
     * Called each time peer has its track metadata updated.
     */
    fun onTrackUpdated(ctx: TrackContext) {
        Timber.i("Track ${ctx.trackId} updated")
    }

    /**
     * Called every time the server estimates client's bandwidth.
     *
     * @param estimation - client's available incoming bitrate estimated
     * by the server. It's measured in bits per second.
     */
    fun onBandwidthEstimationChanged(estimation: Long) {
        Timber.i("Bandwidth estimation changed: $estimation")
    }
}
