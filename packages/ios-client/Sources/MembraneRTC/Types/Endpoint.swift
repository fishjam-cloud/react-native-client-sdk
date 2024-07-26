public struct Endpoint: Codable {
    public let id: String
    public let type: EndpointType
    public let metadata: Metadata
    public let tracks: [String: TrackData]?

    public init(id: String, type: EndpointType, metadata: Metadata? = nil, tracks: [String: TrackData]? = nil) {
        self.id = id
        self.type = type
        self.metadata = metadata ?? Metadata()
        self.tracks = tracks
    }

    public func with(
        id: String? = nil, type: EndpointType? = nil, metadata: Metadata? = nil, tracks: [String: TrackData]? = nil
    ) -> Self {
        return Endpoint(
            id: id ?? self.id,
            type: type ?? self.type,
            metadata: metadata ?? self.metadata,
            tracks: tracks ?? self.tracks
        )
    }

    static func empty() -> Endpoint {
        return Endpoint(id: "", type: .WEBRTC)
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
        self.type = try container.decode(EndpointType.self, forKey: .type)
        self.metadata = try container.decodeIfPresent(Metadata.self, forKey: .metadata) ?? Metadata()
        self.tracks = try container.decodeIfPresent([String: TrackData].self, forKey: .tracks)
    }
}

public enum EndpointType: Codable {
    case WEBRTC

    public init(from string: String) {
        switch string.lowercased() {
        case "webrtc":
            self = .WEBRTC
        default:
            self = .WEBRTC
        }
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let str = try container.decode(String.self)
        self = EndpointType(from: str)
    }

    public func encode(to encoder: Swift.Encoder) throws {
        var container = encoder.singleValueContainer()

        switch self {
        case .WEBRTC:
            try container.encode("WEBRTC")
        }
    }
}
