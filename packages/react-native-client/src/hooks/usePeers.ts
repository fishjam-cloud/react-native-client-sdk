import { useEffect, useState } from 'react';

import {
  Endpoint,
  EndpointsUpdateEvent,
  Metadata,
} from '../RNFishjamClient.types';
import RNFishjamClientModule from '../RNFishjamClientModule';
import { ReceivableEvents, eventEmitter } from '../common/eventEmitter';

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
    const eventListener = eventEmitter.addListener<
      EndpointsUpdateEvent<
        MetadataType,
        VideoTrackMetadataType,
        AudioTrackMetadataType
      >
    >(ReceivableEvents.EndpointsUpdate, (event) => {
      setPeers(event.EndpointsUpdate);
    });
    RNFishjamClientModule.getEndpoints<
      MetadataType,
      VideoTrackMetadataType,
      AudioTrackMetadataType
    >().then((endpoints) => {
      setPeers(endpoints);
    });
    return () => eventListener.remove();
  }, []);

  return peers;
}
