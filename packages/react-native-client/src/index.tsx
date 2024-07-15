export type { Peer, Track } from './hooks/usePeers';
export {
  usePeers,
  TrackType,
  VadStatus,
  EncodingReason,
} from './hooks/usePeers';

export type { AudioOutputDevice } from './hooks/useAudioSettings';
export {
  useAudioSettings,
  AudioOutputDeviceType,
  AudioSessionMode,
} from './hooks/useAudioSettings';

export type { BandwidthEstimationEvent } from './hooks/useBandwidthEstimation';
export { useBandwidthEstimation } from './hooks/useBandwidthEstimation';

export type { CaptureDevice, CameraConfig } from './hooks/useCamera';
export { useCamera, VideoQuality } from './hooks/useCamera';

export type {
  IsMicrophoneOnEvent,
  MicrophoneConfig,
} from './hooks/useMicrophone';
export { useMicrophone } from './hooks/useMicrophone';

export { useRTCStatistics } from './stats/useRTCStatistics';

export type {
  ScreencastOptions,
  ScreencastQuality,
} from './hooks/useScreencast';
export { useScreencast } from './hooks/useScreencast';

export {
  updateAudioTrackMetadata,
  updatePeerMetadata,
  updateVideoTrackMetadata,
} from './common/metadata';

export type { LoggingSeverity } from './common/webRTC';
export {
  changeWebRTCLoggingSeverity,
  setTargetTrackEncoding,
} from './common/webRTC';

export { connect, leaveRoom } from './common/client';

export type { VideoPreviewViewProps } from './components/VideoPreviewView';
export { VideoPreviewView } from './components/VideoPreviewView';

export type { VideoRendererProps } from './components/VideoRendererView';
export { VideoRendererView } from './components/VideoRendererView';

export type {
  Metadata,
  TrackBandwidthLimit,
  TrackEncoding,
  SimulcastBandwidthLimit,
  BandwidthLimit,
  SimulcastConfig,
} from './types';
export { VideoLayout } from './types';
