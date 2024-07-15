import {
  CaptureDevice,
  TrackEncoding,
  useCamera,
  useMicrophone,
} from '@fishjam-cloud/react-native-client';
import BottomSheet from '@gorhom/bottom-sheet';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useEffect, useRef, useState } from 'react';
import {
  Button,
  Platform,
  SafeAreaView,
  StyleSheet,
  TouchableWithoutFeedback,
  View,
} from 'react-native';

import { SwitchCameraButton } from './SwitchCameraButton';
import { SwitchOutputDeviceButton } from './SwitchOutputDeviceButton';
import { ToggleCameraButton } from './ToggleCameraButton';
import { InCallButton } from '../../components';
import LetterButton from '../../components/LetterButton';
import { NoCameraView } from '../../components/NoCameraView';
import { SoundOutputDevicesBottomSheet } from '../../components/SoundOutputDevicesBottomSheet';
import VideoPreview from '../../components/VideoPreview';
import { usePreventBackButton } from '../../hooks/usePreventBackButton';
import type { AppRootStackParamList } from '../../navigators/AppNavigator';
import { previewScreenLabels } from '../../types/ComponentLabels';
import { BrandColors } from '../../utils/Colors';
import {
  displayIosSimulatorCameraAlert,
  isIosSimulator,
} from '../../utils/deviceUtils';

type Props = NativeStackScreenProps<AppRootStackParamList, 'Preview'>;
const { JOIN_BUTTON, TOGGLE_MICROPHONE_BUTTON } = previewScreenLabels;

const PreviewScreen = ({ navigation, route }: Props) => {
  const bottomSheetRef = useRef<BottomSheet>(null);
  usePreventBackButton();

  const availableCameras = useRef<CaptureDevice[]>([]);
  const [currentCamera, setCurrentCamera] = useState<CaptureDevice | null>(
    null,
  );
  const {
    getCaptureDevices,
    isCameraOn: isCameraAvailable,
    simulcastConfig,
    toggleVideoTrackEncoding,
  } = useCamera();
  const { isMicrophoneOn: isMicrophoneAvailable } = useMicrophone();
  const [isMicrophoneOn, setIsMicrophoneOn] = useState<boolean>(
    isMicrophoneAvailable,
  );
  const [isCameraOn, setIsCameraOn] = useState<boolean>(isCameraAvailable);

  const encodings: Record<string, TrackEncoding[]> = {
    ios: ['l', 'h'],
    android: ['l', 'm', 'h'],
  };

  const toggleMicrophone = () => {
    setIsMicrophoneOn(!isMicrophoneOn);
  };

  const toggleCamera = () => {
    setIsCameraOn(!isCameraOn);
  };

  const toggleSwitchCamera = () => {
    setCurrentCamera(
      availableCameras.current.find(
        (device) => device.isFrontFacing !== currentCamera?.isFrontFacing,
      ) || null,
    );
  };

  useEffect(() => {
    getCaptureDevices().then((devices) => {
      availableCameras.current = devices;
      setCurrentCamera(devices.find((device) => device.isFrontFacing) || null);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const onJoinPressed = async () => {
    navigation.navigate('Room', {
      isCameraOn,
      isMicrophoneOn,
      userName: route?.params?.userName,
    });
  };

  useEffect(() => {
    if (isIosSimulator) {
      displayIosSimulatorCameraAlert();
    }
  }, []);

  const body = (
    <SafeAreaView style={styles.container}>
      <View style={styles.cameraPreview}>
        {!isIosSimulator && isCameraOn ? (
          <VideoPreview currentCamera={currentCamera} />
        ) : (
          <NoCameraView username={route?.params?.userName || 'RN Mobile'} />
        )}
      </View>
      <View style={styles.mediaButtonsWrapper}>
        <InCallButton
          iconName={isMicrophoneOn ? 'microphone' : 'microphone-off'}
          onPress={toggleMicrophone}
          accessibilityLabel={TOGGLE_MICROPHONE_BUTTON}
        />
        <ToggleCameraButton
          toggleCamera={toggleCamera}
          isCameraOn={isCameraOn}
        />
        <SwitchCameraButton switchCamera={toggleSwitchCamera} />
        <SwitchOutputDeviceButton bottomSheetRef={bottomSheetRef} />
      </View>
      <View style={styles.simulcastButtonsWrapper}>
        {encodings[Platform.OS].map((val) => (
          <LetterButton
            trackEncoding={val}
            key={`encoding-${val}`}
            selected={simulcastConfig.activeEncodings.includes(val)}
            onPress={() => toggleVideoTrackEncoding(val)}
          />
        ))}
      </View>
      <View style={styles.joinButton}>
        <Button
          title="Join Room"
          onPress={onJoinPressed}
          accessibilityLabel={JOIN_BUTTON}
        />
      </View>

      {Platform.OS === 'android' && (
        <SoundOutputDevicesBottomSheet bottomSheetRef={bottomSheetRef} />
      )}
    </SafeAreaView>
  );

  if (Platform.OS === 'android') {
    return (
      <TouchableWithoutFeedback onPress={() => bottomSheetRef.current?.close()}>
        {body}
      </TouchableWithoutFeedback>
    );
  }
  return body;
};

export default PreviewScreen;

const styles = StyleSheet.create({
  callView: { display: 'flex', flexDirection: 'row', gap: 20 },
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F1FAFE',
    padding: 24,
  },
  cameraPreview: {
    flex: 6,
    margin: 24,
    alignSelf: 'stretch',
    alignItems: 'center',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: BrandColors.darkBlue80,
    overflow: 'hidden',
  },
  mediaButtonsWrapper: {
    display: 'flex',
    flexDirection: 'row',
    gap: 20,
    flex: 1,
  },
  simulcastButtonsWrapper: {
    display: 'flex',
    flexDirection: 'row',
    gap: 20,
    flex: 1,
  },
  joinButton: {
    flex: 1,
  },
});
