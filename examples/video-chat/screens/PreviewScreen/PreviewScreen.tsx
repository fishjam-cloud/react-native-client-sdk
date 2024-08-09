import {
  CaptureDevice,
  TrackEncoding,
  useCamera,
  useMicrophone,
  connect,
  VideoPreviewView,
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
import {
  InCallButton,
  NoCameraView,
  LetterButton,
  SoundOutputDevicesBottomSheet,
} from '../../components';
import { usePreventBackButton } from '../../hooks/usePreventBackButton';
import type { AppRootStackParamList } from '../../navigators/AppNavigator';
import { previewScreenLabels } from '../../types/ComponentLabels';
import { BrandColors } from '../../utils/Colors';
import {
  displayIosSimulatorCameraAlert,
  isIosSimulator,
} from '../../utils/deviceUtils';
import { useToggleCamera } from '../../hooks/useToggleCamera';

type Props = NativeStackScreenProps<AppRootStackParamList, 'Preview'>;
type BottomSheetRef = Props & {
  bottomSheetRef: React.RefObject<BottomSheet>;
};

const { JOIN_BUTTON, TOGGLE_MICROPHONE_BUTTON } = previewScreenLabels;

function PreviewScreen({
  navigation,
  route,
  bottomSheetRef,
}: Props & BottomSheetRef) {
  const availableCameras = useRef<CaptureDevice[]>([]);
  const [currentCamera, setCurrentCamera] = useState<CaptureDevice | null>(
    null,
  );
  const {
    startCamera,
    getCaptureDevices,
    isCameraOn,
    simulcastConfig,
    toggleVideoTrackEncoding,
    switchCamera,
  } = useCamera();
  const { isMicrophoneOn, toggleMicrophone } = useMicrophone();

  const encodings: Record<string, TrackEncoding[]> = {
    ios: ['l', 'h'],
    android: ['l', 'm', 'h'],
  };

  const toggleSwitchCamera = () => {
    const camera =
      availableCameras.current.find(
        (device) => device.isFrontFacing !== currentCamera?.isFrontFacing,
      ) || null;
    if (camera) {
      switchCamera(camera.id);
      setCurrentCamera(camera);
    }
  };

  useEffect(() => {
    async function setupCamera() {
      const devices = await getCaptureDevices();
      availableCameras.current = devices;

      const captureDevice = devices.find((device) => device.isFrontFacing);

      startCamera({
        simulcastConfig: {
          enabled: true,
          activeEncodings:
            // iOS has a limit of 3 hardware encoders
            // 3 simulcast layers + 1 screencast layer = 4, which is too much
            // so we limit simulcast layers to 2
            Platform.OS === 'android' ? ['l', 'm', 'h'] : ['l', 'h'],
        },
        quality: 'HD169',
        maxBandwidth: { l: 150, m: 500, h: 1500 },
        videoTrackMetadata: { active: true, type: 'camera' },
        captureDeviceId: captureDevice?.id,
        cameraEnabled: true,
      });

      setCurrentCamera(captureDevice || null);
    }

    setupCamera();
  }, [getCaptureDevices, startCamera]);

  const onJoinPressed = async () => {
    await connect(route.params.fishjamUrl, route.params.peerToken, {
      name: 'RN mobile',
    });
    navigation.navigate('Room', {
      isCameraOn,
      userName: route?.params?.userName,
    });
  };

  useEffect(() => {
    if (isIosSimulator) {
      displayIosSimulatorCameraAlert();
    }
  }, []);

  const { toggleCamera } = useToggleCamera();

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.cameraPreview}>
        {!isIosSimulator && isCameraOn ? (
          <VideoPreviewView style={styles.cameraPreviewView} />
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
}

export default function PreviewScreenWrapper({ navigation, route }: Props) {
  const bottomSheetRef = useRef<BottomSheet>(null);
  usePreventBackButton();

  if (Platform.OS === 'android') {
    return (
      <TouchableWithoutFeedback onPress={() => bottomSheetRef.current?.close()}>
        <PreviewScreen
          navigation={navigation}
          route={route}
          bottomSheetRef={bottomSheetRef}
        />
      </TouchableWithoutFeedback>
    );
  }
  return (
    <PreviewScreen
      navigation={navigation}
      route={route}
      bottomSheetRef={bottomSheetRef}
    />
  );
}

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
  cameraPreviewView: {
    width: '100%',
    height: '100%',
  },
});
