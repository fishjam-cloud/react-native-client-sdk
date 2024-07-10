import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';
import { View, ViewStyle } from 'react-native';

import { VideoLayout } from './RNFishjamClient.types';
import { isJest } from './utils';

export type VideoPreviewViewProps = {
  /**
   * `FILL` or `FIT` - it works just like RN Image component. `FILL` fills the whole view
   * with video and it may cut some parts of the video. `FIT` scales the video so the whole
   * video is visible, but it may leave some empty space in the view.
   * @default `FILL`
   */
  videoLayout?: VideoLayout;
  /**
   * whether to mirror video
   * @default false
   */
  mirrorVideo?: boolean;
  style?: ViewStyle;
  /**
   * Id of the camera used for preview. Get available cameras with `getCaptureDevices()` function.
   * @default the first front camera
   */
  captureDeviceId?: string;
};

const NativeView: React.ComponentType<VideoPreviewViewProps> = isJest()
  ? () => <View />
  : requireNativeViewManager('VideoPreviewViewModule');

export const VideoPreviewView = React.forwardRef<
  React.ComponentType<VideoPreviewViewProps>,
  VideoPreviewViewProps
>((props, ref) => (
  // @ts-ignore
  <NativeView {...props} ref={ref} />
));
