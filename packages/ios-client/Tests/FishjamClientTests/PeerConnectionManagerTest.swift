import WebRTC
import XCTest

@testable import FishjamClient

class PeerConnectionManagerTest: XCTestCase {
    var manager: PeerConnectionManager!
    var peerConnectionFactory: PeerConnectionFactoryWrapper!
    var peerConnection: RTCPeerConnection!

    override func setUp() {
        class PeerConnectionFactoryMock: PeerConnectionFactoryWrapper {
            let test: PeerConnectionManagerTest
            init(encoder: Encoder, test: PeerConnectionManagerTest) {
                self.test = test
                super.init(encoder: encoder)
            }
            override func createPeerConnection(_ configuration: RTCConfiguration, constraints: RTCMediaConstraints)
                -> RTCPeerConnection?
            {
                test.peerConnection = super.createPeerConnection(configuration, constraints: constraints)
                return test.peerConnection
            }
        }
        peerConnectionFactory = PeerConnectionFactoryMock(encoder: .DEFAULT, test: self)
        let config = RTCConfiguration()

        class ListenerImpl: PeerConnectionListener {
            func onAddTrack(trackId: String, track: RTCMediaStreamTrack) {}
            func onLocalIceCandidate(candidate: RTCIceCandidate) {}
            func onPeerConnectionStateChange(newState: RTCIceConnectionState) {}
        }

        let listener = ListenerImpl()
        manager = PeerConnectionManager(
            config: config, peerConnectionFactory: peerConnectionFactory, peerConnectionListener: listener)

        let expectation = XCTestExpectation(description: "Create sdp offer.")

        manager.getSdpOffer(integratedTurnServers: [], tracksTypes: [:], localTracks: []) { sdp, midToTrackId, error in
            XCTAssertNotNil(sdp, "Sdp offer wasn't created")
            expectation.fulfill()
        }
        wait(for: [expectation], timeout: 2.0)
    }

    func testCreatesOffer() throws {
    }

    func testAddAudioTrack() throws {
        let audioTrack = LocalAudioTrack(peerConnectionFactoryWrapper: peerConnectionFactory)

        manager.addTrack(track: audioTrack, localStreamId: "id")
        XCTAssertFalse(peerConnection.transceivers.isEmpty, "No track added")
    }

    func testAddVideoTrack() throws {
        let videoTrack = LocalVideoTrack.create(
            for: .camera, videoParameters: .presetHD43, peerConnectionFactoryWrapper: peerConnectionFactory)

        manager.addTrack(track: videoTrack, localStreamId: "id")
        XCTAssertFalse(peerConnection.transceivers.isEmpty, "No track added")
    }

    func testSimulcastConfig() throws {
        let preset: VideoParameters = .presetHD43
        let videoParameters = VideoParameters(
            dimensions: preset.dimensions, simulcastConfig: SimulcastConfig(enabled: true, activeEncodings: [.h, .l]))
        let videoTrack = LocalVideoTrack.create(
            for: .camera, videoParameters: videoParameters, peerConnectionFactoryWrapper: peerConnectionFactory)

        manager.addTrack(track: videoTrack, localStreamId: "id")
        let encodings = peerConnection.transceivers[0].sender.parameters.encodings
        XCTAssertEqual(3, encodings.count, "There should be 3 encodings")

        XCTAssertEqual("l", encodings[0].rid, "first encoding should have rid=l")
        XCTAssertTrue(encodings[0].isActive, "l encoding should be active")
        XCTAssertEqual(4, encodings[0].scaleResolutionDownBy, "l layer should be 4x smaller")

        XCTAssertEqual("m", encodings[1].rid, "second encoding should have rid=m")
        XCTAssertFalse(encodings[1].isActive, "m encoding should not be active")
        XCTAssertEqual(2, encodings[1].scaleResolutionDownBy, "m layer should be 2x smaller")

        XCTAssertEqual("h", encodings[2].rid, "third encoding should have rid=h")
        XCTAssertTrue(encodings[2].isActive, "h encoding should be active")
        XCTAssertEqual(1, encodings[2].scaleResolutionDownBy, "h layer should have original size")
    }

    func testSetTrackBandwidth() throws {
        let preset: VideoParameters = .presetHD43
        let videoParameters = VideoParameters(
            dimensions: preset.dimensions,
            maxBandwidth: .BandwidthLimit(1000),
            simulcastConfig: SimulcastConfig(enabled: true, activeEncodings: [.h, .m, .l])
        )
        let videoTrack = LocalVideoTrack.create(
            for: .camera, videoParameters: videoParameters, peerConnectionFactoryWrapper: peerConnectionFactory)

        manager.addTrack(track: videoTrack, localStreamId: "id")

        let encodings = peerConnection.transceivers[0].sender.parameters.encodings
        XCTAssertEqual(780190, encodings[2].maxBitrateBps, "h layer should have correct maxBitrateBps")
        XCTAssertEqual(195047, encodings[1].maxBitrateBps, "m layer should have correct maxBitrateBps")
        XCTAssertEqual(48761, encodings[0].maxBitrateBps, "l layer should have correct maxBitrateBps")
    }

    func testSetTrackBndwidthWithAMap() throws {
        let preset: VideoParameters = .presetHD43
        let videoParameters = VideoParameters(
            dimensions: preset.dimensions,
            maxBandwidth: .SimulcastBandwidthLimit(["h": 1500, "m": 500, "l": 150]),
            simulcastConfig: SimulcastConfig(enabled: true, activeEncodings: [.h, .m, .l])
        )
        let videoTrack = LocalVideoTrack.create(
            for: .camera, videoParameters: videoParameters, peerConnectionFactoryWrapper: peerConnectionFactory)

        manager.addTrack(track: videoTrack, localStreamId: "id")

        let encodings = peerConnection.transceivers[0].sender.parameters.encodings
        XCTAssertEqual(1_536_000, encodings[2].maxBitrateBps, "h layer should have correct maxBitrateBps")
        XCTAssertEqual(512000, encodings[1].maxBitrateBps, "m layer should have correct maxBitrateBps")
        XCTAssertEqual(153600, encodings[0].maxBitrateBps, "l layer should have correct maxBitrateBps")
    }
}
