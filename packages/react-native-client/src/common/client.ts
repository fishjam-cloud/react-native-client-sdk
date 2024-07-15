import { Metadata } from '../types';
import RNFishjamClientModule from '../FishjamClient';

export const connect = (
  url: string,
  peerToken: string,
  peerMetadata: Metadata,
) => {
  return RNFishjamClientModule.connect(url, peerToken, peerMetadata);
};

export const leaveRoom = () => {
  return RNFishjamClientModule.leaveRoom();
};
