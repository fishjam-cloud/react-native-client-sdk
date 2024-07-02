package org.membraneframework.rtc

import org.membraneframework.rtc.models.Endpoint
import org.membraneframework.rtc.models.TrackContext
import org.membraneframework.rtc.utils.SerializedMediaEvent
import timber.log.Timber

interface MembraneRTCListener {
    // Called each time MembraneWebRTC need to send some data to the server.
    fun onSendMediaEvent(event: SerializedMediaEvent)

    // Callback invoked when the client has been approved to participate in media exchange.
    fun onConnected(
        endpointID: String,
        otherEndpoints: List<Endpoint>
    )

    // Called when endpoint of this MembraneRTC instance was removed
    fun onDisconnected()

    // Called in case of errors related to multimedia session e.g. ICE connection.
    fun onConnectError(metadata: Any)

    // Callback invoked a track is ready to be played.
    fun onTrackReady(ctx: TrackContext)

    // Callback invoked a endpoint already present in a room adds a new track.
    fun onTrackAdded(ctx: TrackContext)

    // Callback invoked when a track will no longer receive any data.
    fun onTrackRemoved(ctx: TrackContext)

    // Callback invoked when track's metadata gets updated
    fun onTrackUpdated(ctx: TrackContext)

    // Callback invoked when a new endpoint joins the room.
    fun onEndpointAdded(endpoint: Endpoint)

    // Called each time endpoint is removed, called only for other endpoints.
    fun onEndpointRemoved(endpoint: Endpoint)

    // Called each time endpoint has its metadata updated.
    fun onEndpointUpdated(endpoint: Endpoint)

    // Called every time the server estimates client's bandwidth.
    // estimation - client's available incoming bitrate estimated
    // by the server. It's measured in bits per second.
    fun onBandwidthEstimationChanged(estimation: Long) {
        Timber.i("Bandwidth estimation changed $estimation")
    }
}
