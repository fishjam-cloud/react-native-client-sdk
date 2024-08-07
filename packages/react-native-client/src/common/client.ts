import { Metadata } from '../types';
import RNFishjamClientModule from '../RNFishjamClientModule';

export type ConnectionConfig = {
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
  config: ConnectionConfig = {},
) {
  await RNFishjamClientModule.connect(url, peerToken, peerMetadata, config);
}

export async function leaveRoom() {
  await RNFishjamClientModule.leaveRoom();
}
