import { Endpoint, Metadata } from './RNFishjamClient.types';
import { updateEndpointMetadata } from './common/metadata';
import { useEndpoints } from './hooks/useEndpoints';

export { useAudioSettings } from './hooks/useAudioSettings';
export { useBandwidthEstimation } from './hooks/useBandwidthEstimation';
export { useCamera } from './hooks/useCamera';
export { useMicrophone } from './hooks/useMicrophone';
export { useRTCStatistics } from './hooks/useRTCStatistics';
export { useScreencast } from './hooks/useScreencast';

export {
  updateAudioTrackMetadata,
  updateEndpointMetadata,
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

// below are aliases used by 'old' rn-client-sdk. They should be removed
export type Peer<
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
> = Endpoint<MetadataType, VideoTrackMetadataType, AudioTrackMetadataType>;

/**
 * This hook provides live updates of room peers.
 * @returns An array of room peers.
 */
export const usePeers: <
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
>() => Peer<MetadataType, VideoTrackMetadataType, AudioTrackMetadataType>[] =
  useEndpoints;

/**
 * Function that updates peer's metadata on the server.
 * @param metadata a map `string -> any` containing user's metadata to be sent to the server.
 */
export const updatePeerMetadata = updateEndpointMetadata;
