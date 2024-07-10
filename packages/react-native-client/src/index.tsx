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

export { default as VideoPreviewView } from './VideoPreviewView';
export { default as VideoRendererView } from './VideoRendererView';
export * from './RNFishjamClient.types';
