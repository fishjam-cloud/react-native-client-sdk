import { useMicrophone } from '@fishjam-dev/react-native-client';
import notifee, {
  AndroidImportance,
  AndroidColor,
} from '@notifee/react-native';
import { useCallback } from 'react';
import { Platform } from 'react-native';

interface Props {
  isMicrophoneAvailable: boolean;
}

async function startForegroundService() {
  if (Platform.OS === 'android') {
    const channelId = await notifee.createChannel({
      id: 'video_call',
      name: 'Video call',
      lights: false,
      vibration: false,
      importance: AndroidImportance.DEFAULT,
    });

    await notifee.displayNotification({
      title: 'Your video call is ongoing',
      body: 'Tap to return to the call.',
      android: {
        channelId,
        asForegroundService: true,
        ongoing: true,
        color: AndroidColor.BLUE,
        colorized: true,
        pressAction: {
          id: 'default',
        },
      },
    });
  }
}

export function useJoinRoom({ isMicrophoneAvailable }: Props) {
  const { startMicrophone } = useMicrophone();

  const joinRoom = useCallback(async () => {
    await startForegroundService();

    await startMicrophone({
      audioTrackMetadata: { active: isMicrophoneAvailable, type: 'audio' },
      microphoneEnabled: isMicrophoneAvailable,
    });
  }, [isMicrophoneAvailable, startMicrophone]);

  return { joinRoom };
}
