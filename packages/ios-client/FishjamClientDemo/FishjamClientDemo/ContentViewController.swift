//
//  ContentViewController.swift
//  FishjamClientDemo
//
//  Created by Karol Sygiet on 02/06/2023.
//

import FishjamClient
import Foundation
import UIKit

struct Participant {
    let id: String
    let displayName: String
    var isAudioTrackActive: Bool
}

class ParticipantVideo: Identifiable, ObservableObject {
    let id: String

    @Published var participant: Participant
    @Published var isActive: Bool
    @Published var videoTrack: VideoTrack?
    @Published var mirror: Bool
    @Published var vadStatus: VadStatus

    init(
        id: String, participant: Participant, videoTrack: VideoTrack? = nil,
        isActive: Bool = false,
        mirror: Bool = false
    ) {
        self.id = id
        self.participant = participant
        self.videoTrack = videoTrack
        self.isActive = isActive
        self.mirror = mirror
        self.vadStatus = VadStatus.silence
    }
}

class ContentViewController: ObservableObject {
    private var fishjamClient: FishjamClient?

    @Published var participants: [String: Participant]
    @Published var participantVideos: [ParticipantVideo]
    @Published var connected: Bool

    var localParticipantId: String
    var localVideoTrack: LocalVideoTrack?
    var localAudioTrack: LocalAudioTrack?

    @Published var errorMessage: String?

    public init() {
        self.participants = [:]
        self.participantVideos = []
        self.localParticipantId = "local"
        self.connected = false

        self.fishjamClient = FishjamClient(listener: self)
    }

    public func connect(peerToken: String) {
        let conf = Config(
            websocketUrl: (Bundle.main.infoDictionary?["fishjam_url"] as! String),
            token: peerToken
        )

        let videoTrackMetadata =
            [
                "user_id": "UNKNOWN", "active": true, "type": "camera",
            ] as [String: Any]
        let audioTrackMetadata =
            [
                "user_id": "UNKNOWN", "active": true, "type": "audio",
            ] as [String: Any]

        let preset = VideoParameters.presetHD43
        let videoParameters = VideoParameters(
            dimensions: preset.dimensions.flip(),
            simulcastConfig: SimulcastConfig(enabled: false)
        )

        fishjamClient?.connect(config: conf)

        self.localVideoTrack = fishjamClient?.createVideoTrack(
            videoParameters: videoParameters, metadata: .init(videoTrackMetadata))
        self.localAudioTrack = fishjamClient?.createAudioTrack(metadata: .init(audioTrackMetadata))

    }

    public func disconnect() {
        fishjamClient?.cleanUp()
    }
}

extension ContentViewController: FishjamClientListener {
    func onBandwidthEstimationChanged(estimation: Int) {

    }

    func findParticipantVideo(id: String) -> ParticipantVideo? {
        return participantVideos.first(where: { $0.id == id })
    }

    func add(video: ParticipantVideo) {
        DispatchQueue.main.async {
            guard self.findParticipantVideo(id: video.id) == nil else {
                print("Controller tried to add already existing ParticipantVideo")
                return
            }

            self.participantVideos.append(video)
        }
    }

    func remove(video: ParticipantVideo) {
        DispatchQueue.main.async {
            guard let idx = self.participantVideos.firstIndex(where: { $0.id == video.id }) else {
                return
            }

            self.participantVideos.remove(at: idx)
        }
    }

    func findParticipantVideoByOwner(participantId: String) -> ParticipantVideo? {
        return self.participantVideos.first(where: {
            $0.participant.id == participantId
        })
    }

    func onSendMediaEvent(event: SerializedMediaEvent) {}

    func onJoined(peerID: String, peersInRoom: [Endpoint]) {
        self.localParticipantId = peerID

        let localParticipant = Participant(id: peerID, displayName: "Me", isAudioTrackActive: true)

        let participants = peersInRoom.map { endpoint in
            Participant(
                id: endpoint.id, displayName: endpoint.metadata["displayName"] as? String ?? "",
                isAudioTrackActive: false)
        }

        DispatchQueue.main.async {
            self.participantVideos = participants.map { p in
                ParticipantVideo(id: p.id, participant: p, videoTrack: nil, isActive: false)
            }

            guard let videoTrack = self.localVideoTrack else {
                fatalError("failed to setup local video")
            }

            self.participants[localParticipant.id] = localParticipant
            self.participantVideos.append(
                ParticipantVideo(
                    id: self.localParticipantId, participant: localParticipant, videoTrack: videoTrack, isActive: true))
            participants.forEach { participant in self.participants[participant.id] = participant }
        }
    }

    func onJoinError(metadata _: Any) {
        errorMessage = "Failed to join a room"
    }

    func onTrackReady(ctx: TrackContext) {
        guard var participant = participants[ctx.endpoint.id] else {
            return
        }

        guard let videoTrack = ctx.track as? VideoTrack else {
            DispatchQueue.main.async {
                participant.isAudioTrackActive = ctx.metadata["active"] as? Bool == true
                self.participants[ctx.endpoint.id] = participant
                let pv = self.findParticipantVideoByOwner(participantId: ctx.endpoint.id)
                pv?.participant = participant
            }

            return
        }

        // there can be a situation where we simply need to replace `videoTrack` for
        // already existing video, happens when dynamically adding new local track
        if let participantVideo = participantVideos.first(where: { $0.id == ctx.trackId }) {
            DispatchQueue.main.async {
                participantVideo.videoTrack = videoTrack
            }

            return
        }

        // track is seen for the first time so initialize the participant's video
        let video = ParticipantVideo(
            id: ctx.trackId, participant: participant, videoTrack: videoTrack,
            isActive: ctx.metadata["active"] as? Bool == true)

        guard let existingVideo = self.findParticipantVideoByOwner(participantId: ctx.endpoint.id) else {
            add(video: video)
            return
        }

        guard let idx = self.participantVideos.firstIndex(where: { $0.id == existingVideo.id }) else {
            return
        }
        DispatchQueue.main.async {
            self.participantVideos[idx] = video
        }
    }

    func onTrackAdded(ctx _: TrackContext) {}

    func onTrackRemoved(ctx: TrackContext) {
        if let video = participantVideos.first(where: { $0.id == ctx.trackId }) {
            remove(video: video)
        }
    }

    func onTrackUpdated(ctx: TrackContext) {
        let isActive = ctx.metadata["active"] as? Bool ?? false

        if ctx.metadata["type"] as? String == "camera" {
            DispatchQueue.main.async {
                self.participantVideos.first(where: { $0.participant.id == ctx.endpoint.id })?.isActive =
                    isActive
            }
        } else {
            DispatchQueue.main.async {
                guard var p = self.participants[ctx.endpoint.id] else {
                    return
                }
                p.isAudioTrackActive = isActive
                self.participants[ctx.endpoint.id] = p
                self.participantVideos.first(where: { $0.participant.id == ctx.endpoint.id })?.participant = p
            }

        }
    }

    func onPeerJoined(endpoint: Endpoint) {
        self.participants[endpoint.id] = Participant(
            id: endpoint.id, displayName: endpoint.metadata["displayName"] as? String ?? "", isAudioTrackActive: false)
        let pv =
            ParticipantVideo(id: endpoint.id, participant: participants[endpoint.id]!, videoTrack: nil, isActive: false)
        add(video: pv)
    }

    func onPeerLeft(endpoint: Endpoint) {
        DispatchQueue.main.async {
            self.participants.removeValue(forKey: endpoint.id)
            self.participantVideos = self.participantVideos.filter({ $0.participant.id != endpoint.id })
        }
    }

    func onPeerUpdated(endpoint _: Endpoint) {}

    func onSocketClose(code: UInt16, reason: String) {
        if code != 1000 || reason == "invalid token" {
            self.errorMessage = setErrorMessage(code: code, message: reason)
        }
    }

    func onSocketError() {}

    func onSocketOpen() {
        self.errorMessage = ""
    }

    func onAuthSuccess() {
        fishjamClient?.join(peerMetadata: .init(["displayName": "iphoneUser"]))
    }

    func onAuthError() {}

    func onDisconnected() {}

    private func setErrorMessage(code: UInt16, message: String) -> String {
        return String(code) + ": " + message
    }
}
