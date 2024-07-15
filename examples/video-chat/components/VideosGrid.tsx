import {
  Track,
  VideoRendererView,
  setTargetTrackEncoding,
  Metadata,
} from '@fishjam-cloud/react-native-client';
import React from 'react';
import { Dimensions, StyleSheet, View } from 'react-native';
import Animated, {
  FadeInDown,
  LinearTransition,
} from 'react-native-reanimated';

import LetterButton from './LetterButton';
import { roomScreenLabels } from '../types/ComponentLabels';

type Props = {
  tracks: Track<Metadata>[];
};

const { width } = Dimensions.get('window');
const { VIDEO_CELL } = roomScreenLabels;
const AnimatedVideoRenderer =
  Animated.createAnimatedComponent(VideoRendererView);

export function VideosGrid({ tracks }: Props) {
  const videoWidth = (width - 40) / 2;

  return (
    <View
      style={
        tracks.length > 3 ? styles.videosContainer2 : styles.videosContainer1
      }>
      {tracks.map((v, idx) => (
        <Animated.View
          accessibilityLabel={VIDEO_CELL + idx}
          entering={FadeInDown.duration(200)}
          layout={LinearTransition.duration(150)}
          style={
            tracks.length > 3
              ? [styles.video2, { width: videoWidth, height: videoWidth }]
              : [styles.video1, { maxWidth: width - 20 }]
          }
          key={v.id}>
          <AnimatedVideoRenderer
            trackId={v.id}
            entering={FadeInDown.duration(200)}
            layout={LinearTransition.duration(150)}
            style={styles.animatedView}
          />
          {(v.simulcastConfig?.enabled ?? false) && (
            <View style={styles.buttons}>
              {v.simulcastConfig?.activeEncodings.map((e) => (
                <LetterButton
                  key={e}
                  trackEncoding={e}
                  selected={v.encoding === e}
                  onPress={() => setTargetTrackEncoding(v.id, e)}
                />
              ))}
            </View>
          )}
        </Animated.View>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  animatedView: { flex: 1 },
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  videosContainer1: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  videosContainer2: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    alignContent: 'center',
  },
  video1: {
    flex: 1,
    margin: 10,
    aspectRatio: 1,
    borderRadius: 8,
    overflow: 'hidden',
    backgroundColor: 'black',
  },
  video2: {
    margin: 10,
    aspectRatio: 1,
    borderRadius: 8,
    overflow: 'hidden',
    backgroundColor: 'black',
  },
  border: {
    borderRadius: 8,
    overflow: 'hidden',
  },
  video: {
    flex: 1,
  },
  buttons: {
    flexDirection: 'row',
    gap: 8,
    padding: 4,
    borderRadius: 8,
    position: 'absolute',
    opacity: 0.5,
    backgroundColor: 'white',
    right: 0,
  },
});

export default VideosGrid;
