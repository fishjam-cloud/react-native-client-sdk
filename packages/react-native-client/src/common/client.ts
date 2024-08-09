import { Metadata } from '../types';
import RNFishjamClientModule from '../RNFishjamClientModule';
import { setConnectionStatus } from './state';
import { startMicrophone } from '../hooks/useMicrophone';

export async function connect(
  url: string,
  peerToken: string,
  peerMetadata: Metadata,
) {
  await RNFishjamClientModule.connect(url, peerToken, peerMetadata);

  setConnectionStatus(true);

  await startMicrophone();
}

export async function leaveRoom() {
  await RNFishjamClientModule.leaveRoom();
  setConnectionStatus(false);
}
