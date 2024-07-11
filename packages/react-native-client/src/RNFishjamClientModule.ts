import { requireNativeModule } from 'expo-modules-core';
import { NativeModule } from 'react-native';

import { RNFishjamClient } from './RNFishjamClient.types';

const nativeModule = requireNativeModule('RNFishjamClient');

export default nativeModule as RNFishjamClient & NativeModule;
