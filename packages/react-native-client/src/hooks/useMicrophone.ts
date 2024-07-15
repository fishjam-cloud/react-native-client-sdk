import { useCallback, useEffect, useState } from 'react';

import { Metadata } from '../types';
import RNFishjamClientModule from '../RNFishjamClientModule';
import { ReceivableEvents, eventEmitter } from '../common/eventEmitter';

export type MicrophoneConfig<MetadataType extends Metadata> = {
  /**
   * a map `string -> any` containing audio track metadata to be sent to the server.
   */
  audioTrackMetadata: MetadataType;
  /**
   * whether the microphone is initially enabled, you can toggle it on/off later with toggleMicrophone method
   * @default `true`
   */
  microphoneEnabled: boolean;
};
export type IsMicrophoneOnEvent = { IsMicrophoneOn: boolean };

/**
 * This hook can toggle microphone on/off and provides current microphone state.
 */
export function useMicrophone() {
  const [isMicrophoneOn, setIsMicrophoneOn] = useState<boolean>(
    RNFishjamClientModule.isMicrophoneOn,
  );

  useEffect(() => {
    const eventListener = eventEmitter.addListener<IsMicrophoneOnEvent>(
      ReceivableEvents.IsMicrophoneOn,
      (event) => setIsMicrophoneOn(event.IsMicrophoneOn),
    );
    setIsMicrophoneOn(RNFishjamClientModule.isMicrophoneOn);
    return () => eventListener.remove();
  }, []);

  /**
   * Function to toggle microphone on/off
   */
  const toggleMicrophone = useCallback(async () => {
    const state = await RNFishjamClientModule.toggleMicrophone();
    setIsMicrophoneOn(state);
  }, []);

  /**
   * Starts local microphone capturing.
   * @param config configuration of the microphone capture
   * @returns A promise that resolves when microphone capturing is started.
   */
  const startMicrophone = useCallback(
    async <MicrophoneConfigMetadataType extends Metadata>(
      config: Partial<MicrophoneConfig<MicrophoneConfigMetadataType>> = {},
    ) => {
      await RNFishjamClientModule.startMicrophone(config);
    },
    [],
  );

  return { isMicrophoneOn, toggleMicrophone, startMicrophone };
}
