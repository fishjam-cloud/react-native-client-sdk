package org.membraneframework.rtc.models

import org.membraneframework.rtc.utils.Metadata

data class Endpoint(
    val id: String,
    val type: String,
    val metadata: Metadata? = mapOf(),
    val tracks: Map<String, TrackData> = mapOf()
) {
    fun withTrack(
        trackId: String,
        metadata: Metadata?
    ): Endpoint {
        val tracks = this.tracks.toMutableMap()
        val trackData = tracks[trackId]
        tracks[trackId] = TrackData(metadata = metadata, simulcastConfig = trackData?.simulcastConfig)
        return this.copy(tracks = tracks)
    }

    fun withoutTrack(trackId: String): Endpoint {
        val tracks = this.tracks.toMutableMap()
        tracks.remove(trackId)
        return this.copy(tracks = tracks)
    }
}
