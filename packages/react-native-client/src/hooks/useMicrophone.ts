import { useCallback, useEffect, useState } from 'react';

import { Metadata } from '../types';
import RNFishjamClientModule from '../RNFishjamClientModule';
import { ReceivableEvents, eventEmitter } from '../common/eventEmitter';
import { isConnected, setMicrophoneStatus } from '../common/state';

export type MicrophoneConfig<MetadataType extends Metadata> = {
  /**
   * a map `string -> any` containing audio track metadata to be sent to the server.
   */
  audioTrackMetadata?: MetadataType;
  /**
   * whether the microphone is initially enabled, you can toggle it on/off later with toggleMicrophone method
   * @default `true`
   */
  microphoneEnabled?: boolean;
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
    setMicrophoneStatus(isMicrophoneOn);
  }, [isMicrophoneOn]);

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
    if (isConnected()) {
      const status = await RNFishjamClientModule.toggleMicrophone();
      await RNFishjamClientModule.updateAudioTrackMetadata({
        active: status,
        type: 'audio',
      });
      setIsMicrophoneOn(status);
    } else {
      setIsMicrophoneOn((state) => !state);
    }
  }, []);

  return { isMicrophoneOn, toggleMicrophone };
}
