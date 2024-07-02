/// Delegate responsible for receiving notification from `MembraneRTC` client.
public protocol MembraneRTCDelegate {
    /// Called each time MembraneWebRTC need to send some data to the server.
    func onSendMediaEvent(event: SerializedMediaEvent)

    /// Callback invoked when the client has been approved to participate in session.
    func onConnected(endpointId: String, otherEndpoints: [Endpoint])

    /// Callback invoked when client has been denied access to enter the room.
    func onConnectionError(metadata: Any)

    /// Callback invoked a track is ready to be played.
    func onTrackReady(ctx: TrackContext)

    /// Callback invoked a peer already present in a room adds a new track.
    func onTrackAdded(ctx: TrackContext)

    /// Callback invoked when a track will no longer receive any data and should get removed.
    func onTrackRemoved(ctx: TrackContext)

    /// Callback invoked when track's metadata gets updated.
    func onTrackUpdated(ctx: TrackContext)

    /// Callback invoked when a new endpoint is added..
    func onEndpointAdded(endpoint: Endpoint)

    /// Callback invoked when an endpoint is removed from the room.
    ///
    /// When an endpoint with active track leaves the room, `onTrackRemoved` will be called for all endpoint's tracks beforehand.
    func onEndpointRemoved(endpoint: Endpoint)

    /// Callback invoked when endpoint's metadata gets updated.
    func onEndpointUpdated(endpoint: Endpoint)

    ///Called every time the server estimates client's bandwidth.
    ///estimation - client's available incoming bitrate estimated
    ///by the server. It's measured in bits per second.
    func onBandwidthEstimationChanged(estimation: Int)
}

extension MembraneRTCDelegate {
    public func onBandwidthEstimationChanged(estimation: Int) {
        sdkLogger.info("Bandwidth estimation changed \(estimation)")
    }
}
