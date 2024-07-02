package org.membraneframework.rtc

import org.membraneframework.rtc.events.OfferData
import org.membraneframework.rtc.models.Endpoint
import org.membraneframework.rtc.models.TrackData
import org.membraneframework.rtc.utils.Metadata
import org.membraneframework.rtc.utils.SerializedMediaEvent

internal interface RTCEngineListener {
    fun onConnected(
        endpointID: String,
        otherEndpoints: List<Endpoint>
    )

    fun onSendMediaEvent(event: SerializedMediaEvent)

    fun onEndpointAdded(endpoint: Endpoint)

    fun onEndpointRemoved(endpointId: String)

    fun onEndpointUpdated(
        endpointId: String,
        endpointMetadata: Metadata? = mapOf()
    )

    fun onOfferData(
        integratedTurnServers: List<OfferData.TurnServer>,
        tracksTypes: Map<String, Int>
    )

    fun onSdpAnswer(
        type: String,
        sdp: String,
        midToTrackId: Map<String, String>
    )

    fun onRemoteCandidate(
        candidate: String,
        sdpMLineIndex: Int,
        sdpMid: String?
    )

    fun onTracksAdded(
        endpointId: String,
        tracks: Map<String, TrackData>
    )

    fun onTracksRemoved(
        endpointId: String,
        trackIds: List<String>
    )

    fun onTrackUpdated(
        endpointId: String,
        trackId: String,
        metadata: Metadata? = mapOf()
    )

    fun onTrackEncodingChanged(
        endpointId: String,
        trackId: String,
        encoding: String,
        encodingReason: String
    )

    fun onVadNotification(
        trackId: String,
        status: String
    )

    fun onBandwidthEstimation(estimation: Long)
}
