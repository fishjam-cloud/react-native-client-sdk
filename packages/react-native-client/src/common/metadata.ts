import { Metadata } from '../types';
import RNFishjamClientModule from '../FishjamClient';

/**
 * a function that updates endpoints's metadata on the server
 * @param metadata a map `string -> any` containing user's track metadata to be sent to the server
 */
export async function updatePeerMetadata<EndpointMetadataType extends Metadata>(
  metadata: EndpointMetadataType,
) {
  await RNFishjamClientModule.updateEndpointMetadata(metadata);
}

/**
 * a function that updates video metadata on the server.
 * @param metadata a map string -> any containing video track metadata to be sent to the server
 */
export async function updateVideoTrackMetadata<
  VideoTrackMetadataType extends Metadata,
>(metadata: VideoTrackMetadataType) {
  await RNFishjamClientModule.updateVideoTrackMetadata(metadata);
}

/**
 * a function that updates audio metadata on the server
 * @param metadata a map `string -> any` containing audio track metadata to be sent to the server
 */
export async function updateAudioTrackMetadata<
  AudioTrackMetadataType extends Metadata,
>(metadata: AudioTrackMetadataType) {
  await RNFishjamClientModule.updateAudioTrackMetadata(metadata);
}
