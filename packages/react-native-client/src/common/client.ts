import { Metadata } from '../types';
import RNFishjamClientModule from '../RNFishjamClientModule';
import { setConnectionStatus, isMicrophoneSetToOn } from './state';

export async function connect(
  url: string,
  peerToken: string,
  peerMetadata: Metadata,
) {
  await RNFishjamClientModule.connect(url, peerToken, peerMetadata);

  setConnectionStatus(true);

  await RNFishjamClientModule.startMicrophone({
    audioTrackMetadata: { active: isMicrophoneSetToOn(), type: 'audio' },
    microphoneEnabled: isMicrophoneSetToOn(),
  });
}

export async function leaveRoom() {
  await RNFishjamClientModule.leaveRoom();
  setConnectionStatus(false);
}
