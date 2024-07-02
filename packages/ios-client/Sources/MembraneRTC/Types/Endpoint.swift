public struct Endpoint: Codable {
    public let id: String
    public let type: String
    public let metadata: Metadata
    public let tracks: [String: TrackData]?

    public init(id: String, type: String, metadata: Metadata?, tracks: [String: TrackData]? = nil) {
        self.id = id
        self.type = type
        self.metadata = metadata ?? Metadata()
        self.tracks = tracks
    }

    public func with(
        id: String? = nil, type: String? = nil, metadata: Metadata? = nil, tracks: [String: TrackData]? = nil
    ) -> Self {
        return Endpoint(
            id: id ?? self.id,
            type: type ?? self.type,
            metadata: metadata ?? self.metadata,
            tracks: tracks ?? self.tracks
        )
    }

    public func withTrack(trackId: String, metadata: Metadata?, simulcastConfig: SimulcastConfig?) -> Self {
        var newTracks = self.tracks
        let oldSimulcastConfig = newTracks?[trackId]?.simulcastConfig
        newTracks?[trackId] = TrackData(
            metadata: metadata ?? Metadata(), simulcastConfig: simulcastConfig ?? oldSimulcastConfig)

        return Endpoint(id: self.id, type: self.type, metadata: self.metadata, tracks: newTracks)
    }

    public func withoutTrack(trackId: String) -> Self {
        var newTracks = self.tracks
        newTracks?.removeValue(forKey: trackId)

        return Endpoint(id: self.id, type: self.type, metadata: self.metadata, tracks: newTracks)
    }

    enum CodingKeys: String, CodingKey {
        case id, type, metadata, tracks
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.id = try container.decode(String.self, forKey: .id)
        self.type = try container.decode(String.self, forKey: .type)
        self.metadata = try container.decodeIfPresent(Metadata.self, forKey: .metadata) ?? Metadata()
        self.tracks = try container.decodeIfPresent([String: TrackData].self, forKey: .tracks)
    }
}
