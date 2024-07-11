export { usePeers } from './hooks/usePeers';
export type { Peer } from './hooks/usePeers';

export { useAudioSettings } from './hooks/useAudioSettings';
export { useBandwidthEstimation } from './hooks/useBandwidthEstimation';
export { useCamera } from './hooks/useCamera';
export { useMicrophone } from './hooks/useMicrophone';
export { useRTCStatistics } from './hooks/useRTCStatistics';
export { useScreencast } from './hooks/useScreencast';

export {
  updateAudioTrackMetadata,
  updatePeerMetadata,
  updateVideoTrackMetadata,
} from './common/metadata';
export {
  changeWebRTCLoggingSeverity,
  setTargetTrackEncoding,
} from './common/webRTC';
export { connect, leaveRoom } from './common/client';

export { VideoPreviewView } from './components/VideoPreviewView';
export type { VideoPreviewViewProps } from './components/VideoPreviewView';
export { VideoRendererView } from './components/VideoRendererView';
export type { VideoRendererProps } from './components/VideoRendererView';

export * from './RNFishjamClient.types';
