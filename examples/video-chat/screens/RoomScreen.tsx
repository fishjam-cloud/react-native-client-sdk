import {
  leaveRoom,
  usePeers,
  useScreencast,
  useCamera,
  useMicrophone,
  useAudioSettings,
} from '@fishjam-cloud/react-native-client';
import BottomSheet from '@gorhom/bottom-sheet';
import notifee from '@notifee/react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useCallback, useEffect, useMemo, useRef } from 'react';
import { Platform, SafeAreaView, StyleSheet, View } from 'react-native';

import { InCallButton, VideosGrid } from '../components';
import { NoCameraView } from '../components/NoCameraView';
import { SoundOutputDevicesBottomSheet } from '../components/SoundOutputDevicesBottomSheet';
import { useJoinRoom } from '../hooks/useJoinRoom';
import { usePreventBackButton } from '../hooks/usePreventBackButton';
import { useToggleCamera } from '../hooks/useToggleCamera';
import { useToggleMicrophone } from '../hooks/useToggleMicrophone';
import type { AppRootStackParamList } from '../navigators/AppNavigator';
import { roomScreenLabels } from '../types/ComponentLabels';

type Props = NativeStackScreenProps<AppRootStackParamList, 'Room'>;
const {
  DISCONNECT_BUTTON,
  TOGGLE_CAMERA_BUTTON,
  SWITCH_CAMERA_BUTTON,
  SHARE_SCREEN_BUTTON,
  TOGGLE_MICROPHONE_BUTTON,
  NO_CAMERA_VIEW,
} = roomScreenLabels;

const RoomScreen = ({ navigation, route }: Props) => {
  const {
    isCameraOn: isCameraAvailable,
    isMicrophoneOn: isMicrophoneAvailable,
    userName,
  } = route?.params ?? {};
  usePreventBackButton();
  const audioSettings = useAudioSettings();

  const { joinRoom } = useJoinRoom({
    isCameraAvailable,
    isMicrophoneAvailable,
  });
  const { isCameraOn, flipCamera } = useCamera();
  const { toggleCamera } = useToggleCamera();
  const { isMicrophoneOn } = useMicrophone();
  const { toggleMicrophone } = useToggleMicrophone();

  useEffect(() => {
    joinRoom();
  }, [joinRoom]);

  const { peers } = usePeers();

  const tracks = useMemo(
    () =>
      peers.flatMap((peer) =>
        peer.tracks.filter(
          (t) => t.metadata.type !== 'audio' && (t.metadata.active ?? true),
        ),
      ),
    [peers],
  );

  const { toggleScreencast, isScreencastOn } = useScreencast();

  const onDisconnectPress = useCallback(() => {
    leaveRoom();
    navigation.navigate('Home');
  }, [navigation]);

  const onToggleScreenCast = useCallback(() => {
    toggleScreencast({
      screencastMetadata: {
        displayName: 'presenting',
        type: 'screensharing',
        active: !isScreencastOn,
      },
      quality: 'HD15',
    });
  }, [isScreencastOn, toggleScreencast]);

  const bottomSheetRef = useRef<BottomSheet>(null);

  const toggleOutputSoundDevice = useCallback(async () => {
    if (Platform.OS === 'ios') {
      await audioSettings.showAudioRoutePicker();
    } else if (Platform.OS === 'android') {
      bottomSheetRef.current?.expand();
    }
  }, [audioSettings]);

  useEffect(() => {
    return () => {
      notifee.stopForegroundService();
    };
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      {tracks.length > 0 ? (
        <VideosGrid tracks={tracks} />
      ) : (
        <NoCameraView
          username={userName || 'username'}
          accessibilityLabel={NO_CAMERA_VIEW}
        />
      )}

      <View style={styles.callView}>
        <InCallButton
          type="disconnect"
          iconName="phone-hangup"
          onPress={onDisconnectPress}
          accessibilityLabel={DISCONNECT_BUTTON}
        />
        <InCallButton
          iconName={isMicrophoneOn ? 'microphone' : 'microphone-off'}
          onPress={toggleMicrophone}
          accessibilityLabel={TOGGLE_MICROPHONE_BUTTON}
        />
        <InCallButton
          iconName={isCameraOn ? 'camera' : 'camera-off'}
          onPress={toggleCamera}
          accessibilityLabel={TOGGLE_CAMERA_BUTTON}
        />
        <InCallButton
          iconName="camera-switch"
          onPress={flipCamera}
          accessibilityLabel={SWITCH_CAMERA_BUTTON}
        />
        <InCallButton
          iconName={isScreencastOn ? 'share' : 'share-off'}
          onPress={onToggleScreenCast}
          accessibilityLabel={SHARE_SCREEN_BUTTON}
        />
        <InCallButton
          iconName="volume-high"
          onPress={toggleOutputSoundDevice}
        />
      </View>
      {Platform.OS === 'android' ? (
        <SoundOutputDevicesBottomSheet bottomSheetRef={bottomSheetRef} />
      ) : null}
    </SafeAreaView>
  );
};

export default RoomScreen;

const styles = StyleSheet.create({
  callView: { display: 'flex', flexDirection: 'row', gap: 10 },
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#F1FAFE',
    padding: 24,
  },
});
