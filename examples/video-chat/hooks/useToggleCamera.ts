import {
  updateVideoTrackMetadata,
  useCamera,
} from '@fishjam-cloud/react-native-client';
import { useCallback } from 'react';

import {
  displayIosSimulatorCameraAlert,
  isIosSimulator,
} from '../utils/deviceUtils';

export function useToggleCamera() {
  const { isCameraOn, toggleCamera: membraneToggleCamera } = useCamera();

  const toggleCamera = useCallback(async () => {
    if (isIosSimulator) {
      displayIosSimulatorCameraAlert();
      return;
    }

    await membraneToggleCamera();
    await updateVideoTrackMetadata({ active: !isCameraOn, type: 'camera' });
  }, [isCameraOn, membraneToggleCamera]);

  return { toggleCamera };
}
