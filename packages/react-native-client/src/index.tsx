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
export * from './common/client';

export { VideoPreviewView } from './VideoPreviewView';
export type { VideoPreviewViewProps } from './VideoPreviewView';
export { VideoRendererView } from './VideoRendererView';
export type { VideoRendererProps } from './VideoRendererView';

export * from './RNFishjamClient.types';
