import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';
import { View, ViewStyle } from 'react-native';

import { VideoLayout } from '../RNFishjamClient.types';
import { isJest } from '../utils';

export type VideoRendererProps = {
  /**
   * id of the video track which you want to render.
   */
  trackId: string;
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
};

const NativeView: React.ComponentType<VideoRendererProps> = isJest()
  ? () => <View />
  : requireNativeViewManager('VideoRendererViewModule');

export const VideoRendererView = React.forwardRef<
  React.ComponentType<VideoRendererProps>,
  VideoRendererProps
>((props, ref) => (
  // @ts-ignore
  <NativeView {...props} ref={ref} />
));
