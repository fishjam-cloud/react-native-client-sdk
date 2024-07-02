import Starscream
import WebRTC

internal protocol FishjamMembraneRTC {
    func disconnect()
    func receiveMediaEvent(mediaEvent: SerializedMediaEvent)
}

extension MembraneRTC: FishjamMembraneRTC {}
