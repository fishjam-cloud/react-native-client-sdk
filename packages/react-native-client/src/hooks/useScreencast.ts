import { useCallback, useEffect, useState } from 'react';

import {
  BandwidthLimit,
  Metadata,
  SimulcastConfig,
  TrackBandwidthLimit,
  TrackEncoding,
} from '../types';
import RNFishjamClientModule from '../FishjamClient';
import { ReceivableEvents, eventEmitter } from '../common/eventEmitter';

type IsScreencastOnEvent = { IsScreencastOn: boolean };

export enum ScreencastQuality {
  VGA = 'VGA',
  HD5 = 'HD5',
  HD15 = 'HD15',
  FHD15 = 'FHD15',
  FHD30 = 'FHD30',
}

export type ScreencastOptions<MetadataType extends Metadata> = {
  /**
   * Resolution + fps of screencast track, one of: `VGA`, `HD5`, `HD15`, `FHD15`, `FHD30`.
   * Note that quality might be worse than specified due to device capabilities, internet
   * connection etc.
   * @default `HD15``
   */
  quality: ScreencastQuality;
  /**
   * a map `string -> any` containing screencast track metadata to be sent to the server
   */
  screencastMetadata: MetadataType;
  /**
   * SimulcastConfig of a screencast track. By default simulcast is disabled.
   */
  simulcastConfig: SimulcastConfig;
  /**
   *  bandwidth limit of a screencast track. By default there is no bandwidth limit.
   */
  maxBandwidth: TrackBandwidthLimit;
};

const defaultSimulcastConfig = () => ({
  enabled: false,
  activeEncodings: [],
});

let screencastSimulcastConfig: SimulcastConfig = defaultSimulcastConfig();

/**
 * This hook can toggle screen sharing on/off and provides current screencast state.
 * @returns An object with functions to manage screencast.
 */
export function useScreencast() {
  const [isScreencastOn, setIsScreencastOn] = useState<boolean>(
    RNFishjamClientModule.isScreencastOn,
  );
  const [simulcastConfig, setSimulcastConfig] = useState<SimulcastConfig>(
    screencastSimulcastConfig,
  );
  useEffect(() => {
    const eventListener = eventEmitter.addListener<IsScreencastOnEvent>(
      ReceivableEvents.IsScreencastOn,
      (event) => setIsScreencastOn(event.IsScreencastOn),
    );
    setIsScreencastOn(RNFishjamClientModule.isScreencastOn);
    return () => eventListener.remove();
  }, []);

  /**
   * Toggles the screencast on/off
   */
  const toggleScreencast = useCallback(
    async <ScreencastOptionsMetadataType extends Metadata>(
      screencastOptions: Partial<
        ScreencastOptions<ScreencastOptionsMetadataType>
      > = {},
    ) => {
      await RNFishjamClientModule.toggleScreencast(screencastOptions);
      screencastSimulcastConfig =
        screencastOptions.simulcastConfig || defaultSimulcastConfig();
      setSimulcastConfig(screencastSimulcastConfig);
    },
    [],
  );

  /**
   * a function that updates screencast track metadata on the server
   * @param metadata a map `string -> any` containing screencast track metadata to be sent to the server
   */
  const updateScreencastTrackMetadata = useCallback(
    async <ScreencastMetadataType extends Metadata>(
      metadata: ScreencastMetadataType,
    ) => {
      await RNFishjamClientModule.updateScreencastTrackMetadata(metadata);
    },
    [],
  );

  /**
   * Toggles simulcast encoding of a screencast track on/off
   * @param encoding encoding to toggle
   */
  const toggleScreencastTrackEncoding = useCallback(
    async (encoding: TrackEncoding) => {
      screencastSimulcastConfig =
        await RNFishjamClientModule.toggleScreencastTrackEncoding(encoding);
      setSimulcastConfig(screencastSimulcastConfig);
    },
    [],
  );

  /**
   * updates maximum bandwidth for the given simulcast encoding of the screencast track
   * @param encoding encoding to update
   * @param bandwidth BandwidthLimit to set
   */
  const setScreencastTrackEncodingBandwidth = useCallback(
    async (encoding: TrackEncoding, bandwidth: BandwidthLimit) => {
      await RNFishjamClientModule.setScreencastTrackEncodingBandwidth(
        encoding,
        bandwidth,
      );
    },
    [],
  );

  /**
   * updates maximum bandwidth for the screencast track. This value directly translates
   * to quality of the stream and the amount of RTP packets being sent. In case simulcast
   * is enabled bandwidth is split between all of the variant streams proportionally to
   * their resolution
   * @param bandwidth BandwidthLimit to set
   */
  const setScreencastTrackBandwidth = useCallback(
    async (bandwidth: BandwidthLimit) => {
      await RNFishjamClientModule.setScreencastTrackBandwidth(bandwidth);
    },
    [],
  );

  return {
    isScreencastOn,
    toggleScreencast,
    updateScreencastTrackMetadata,
    toggleScreencastTrackEncoding,
    simulcastConfig,
    setScreencastTrackEncodingBandwidth,
    setScreencastTrackBandwidth,
  };
}
