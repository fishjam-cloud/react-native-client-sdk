import { useEffect, useState } from 'react';

import { Metadata, SimulcastConfig, TrackEncoding } from '../types';
import RNFishjamClientModule from '../RNFishjamClientModule';
import { ReceivableEvents, eventEmitter } from '../common/eventEmitter';

export enum TrackType {
  Audio = 'Audio',
  Video = 'Video',
}

/**
 * Type describing Voice Activity Detection statuses.
 *
 * SPEECH - voice activity has been detected
 * SILENCE - lack of voice activity has been detected
 */
export enum VadStatus {
  Silence = 'silence',
  Speech = 'speech',
}

export type Track<MetadataType extends Metadata> = {
  id: string;
  type: TrackType;
  metadata: MetadataType;
  vadStatus: VadStatus;
  // Encoding that is currently received. Only present for remote tracks.
  encoding: TrackEncoding | null;
  // Information about simulcast, if null simulcast is not enabled
  simulcastConfig: SimulcastConfig | null;
  // The reason of currently selected encoding. Only present for remote tracks.
  encodingReason: EncodingReason | null;
};

/**
 * Type describing possible reasons of currently selected encoding.
 *
 * - OTHER - the exact reason couldn't be determined
 * - ENCODING_INACTIVE - previously selected encoding became inactive
 * - LOW_BANDWIDTH - there is no longer enough bandwidth to maintain previously selected encoding
 */
export enum EncodingReason {
  Other = 'other',
  EncodingInactive = 'encodingInactive',
  LowBandwidth = 'lowBandwidth',
}

export type EndpointsUpdateEvent<
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
> = {
  EndpointsUpdate: Endpoint<
    MetadataType,
    VideoTrackMetadataType,
    AudioTrackMetadataType
  >[];
};

export type Endpoint<
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
> = {
  /**
   *  id used to identify an endpoint
   */
  id: string;
  /**
   * used to indicate endpoint type.
   */
  type: string;
  /**
   * whether the endpoint is local or remote
   */
  isLocal: boolean;
  /**
   * a map `string -> any` containing endpoint metadata from the server
   */
  metadata: MetadataType;
  /**
   * a list of endpoints's video and audio tracks
   */
  tracks: Track<VideoTrackMetadataType | AudioTrackMetadataType>[];
};

export type Peer<
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
> = Endpoint<MetadataType, VideoTrackMetadataType, AudioTrackMetadataType>;

/**
 * This hook provides live updates of room peers.
 * @returns An array of room peers.
 */
export function usePeers<
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
>() {
  const [peers, setPeers] = useState<
    Peer<MetadataType, VideoTrackMetadataType, AudioTrackMetadataType>[]
  >([]);

  useEffect(() => {
    async function updateEndpoints() {
      const endpoints = await RNFishjamClientModule.getEndpoints<
        MetadataType,
        VideoTrackMetadataType,
        AudioTrackMetadataType
      >();
      setPeers(endpoints);
    }

    const eventListener = eventEmitter.addListener<
      EndpointsUpdateEvent<
        MetadataType,
        VideoTrackMetadataType,
        AudioTrackMetadataType
      >
    >(ReceivableEvents.EndpointsUpdate, (event) => {
      setPeers(event.EndpointsUpdate);
    });

    updateEndpoints();
    return () => eventListener.remove();
  }, []);

  return peers;
}
