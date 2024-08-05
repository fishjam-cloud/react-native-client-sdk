import { Metadata } from '../types';
import RNFishjamClientModule from '../RNFishjamClientModule';

export type Config = {
  reconnectConfig?: {
    maxAttempts?: number;
    initialDelayMs?: number;
    delayMs?: number;
  };
};

export async function connect(
  url: string,
  peerToken: string,
  peerMetadata: Metadata,
  config: Config = {},
) {
  await RNFishjamClientModule.connect(url, peerToken, peerMetadata, config);
}

export async function leaveRoom() {
  await RNFishjamClientModule.leaveRoom();
}
