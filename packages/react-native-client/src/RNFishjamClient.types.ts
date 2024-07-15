import type { RTCStats } from './stats/types';
import type { MicrophoneConfig } from './hooks/useMicrophone';
import type { Endpoint } from './hooks/usePeers';
import type { CameraConfig, CaptureDevice } from './hooks/useCamera';
// global
export type Metadata = { [key: string]: any };

export enum VideoLayout {
  FILL = 'FILL',
  FIT = 'FIT',
}

export enum ScreencastQuality {
  VGA = 'VGA',
  HD5 = 'HD5',
  HD15 = 'HD15',
  FHD15 = 'FHD15',
  FHD30 = 'FHD30',
}

export type TrackEncoding = 'l' | 'm' | 'h';

/**
 * A type describing simulcast configuration.
 *
 * At the moment, simulcast track is initialized in three versions - low, medium and high.
 * High resolution is the original track resolution, while medium and low resolutions are
 * the original track resolution scaled down by 2 and 4 respectively.
 */
export type SimulcastConfig = {
  /**
   * whether to simulcast track or not. By default simulcast is disabled.
   */
  enabled: boolean;
  /**
   *  list of active encodings. Encoding can be one of `"h"` (original encoding), `"m"` (scaled down x2), `"l"` (scaled down x4).
   */
  activeEncodings: TrackEncoding[];
};

/**
 * Type describing maximal bandwidth that can be used, in kbps. 0 is interpreted as unlimited bandwidth.
 */
export type BandwidthLimit = number;

/**
 * Type describing bandwidth limit for simulcast track. It is a mapping `encoding -> BandwidthLimit`. If encoding isn't present in this mapping,
 * it will be assumed that this particular encoding shouldn't have any bandwidth limit.
 */
export type SimulcastBandwidthLimit = Record<TrackEncoding, BandwidthLimit>;

/**
 * A type describing bandwidth limitation of a track, including simulcast and non-simulcast tracks. Can be `BandwidthLimit` or `SimulcastBandwidthLimit`.
 */
export type TrackBandwidthLimit = BandwidthLimit | SimulcastBandwidthLimit;

export type ScreencastOptions<MetadataType extends Metadata> = {
  /**
   * Resolution + fps of screencast track, one of: `VGA`, `HD5`, `HD15`, `FHD15`, `FHD30`.
   * Note that quality might be worse than specified due to device capabilities, internet
   * connection etc.
   * @default `HD15``
   */
  quality: ScreencastQuality;
  /**
   * a map `string -> any` containing screencast track metadata to be sent to the server
   */
  screencastMetadata: MetadataType;
  /**
   * SimulcastConfig of a screencast track. By default simulcast is disabled.
   */
  simulcastConfig: SimulcastConfig;
  /**
   *  bandwidth limit of a screencast track. By default there is no bandwidth limit.
   */
  maxBandwidth: TrackBandwidthLimit;
};

export type IsScreencastOnEvent = { IsScreencastOn: boolean };

export type SimulcastConfigUpdateEvent = SimulcastConfig;

export type RNFishjamClient = {
  connect: (
    url: string,
    peerToken: string,
    peerMetadata: Metadata,
  ) => Promise<void>;
  leaveRoom: () => Promise<void>;
  startCamera: <MetadataType extends Metadata>(
    config: Partial<CameraConfig<MetadataType>>,
  ) => Promise<void>;
  startMicrophone: <MetadataType extends Metadata>(
    config: Partial<MicrophoneConfig<MetadataType>>,
  ) => Promise<void>;
  isMicrophoneOn: boolean;
  toggleMicrophone: () => Promise<boolean>;
  isCameraOn: boolean;
  toggleCamera: () => Promise<boolean>;
  flipCamera: () => Promise<void>;
  switchCamera: (captureDeviceId: string) => Promise<void>;
  getCaptureDevices: () => Promise<CaptureDevice[]>;
  toggleScreencast: <MetadataType extends Metadata>(
    screencastOptions: Partial<ScreencastOptions<MetadataType>>,
  ) => Promise<void>;
  isScreencastOn: boolean;
  getEndpoints: <
    EndpointMetadataType extends Metadata,
    VideoTrackMetadataType extends Metadata,
    AudioTrackMetadataType extends Metadata,
  >() => Promise<
    Endpoint<
      EndpointMetadataType,
      VideoTrackMetadataType,
      AudioTrackMetadataType
    >[]
  >;
  updateEndpointMetadata: <MetadataType extends Metadata>(
    metadata: MetadataType,
  ) => Promise<void>;
  updateVideoTrackMetadata: <MetadataType extends Metadata>(
    metadata: MetadataType,
  ) => Promise<void>;
  updateAudioTrackMetadata: <MetadataType extends Metadata>(
    metadata: MetadataType,
  ) => Promise<void>;
  updateScreencastTrackMetadata: <MetadataType extends Metadata>(
    metadata: MetadataType,
  ) => Promise<void>;
  setOutputAudioDevice: (audioDevice: string) => Promise<void>;
  startAudioSwitcher: () => Promise<void>;
  stopAudioSwitcher: () => Promise<void>;
  selectAudioSessionMode: (sessionMode: string) => Promise<void>;
  showAudioRoutePicker: () => Promise<void>;
  toggleScreencastTrackEncoding: (encoding: string) => Promise<SimulcastConfig>;
  setScreencastTrackBandwidth: (bandwidth: number) => Promise<void>;
  setScreencastTrackEncodingBandwidth: (
    encoding: string,
    bandwidth: number,
  ) => Promise<void>;
  setTargetTrackEncoding: (trackId: string, encoding: string) => Promise<void>;
  toggleVideoTrackEncoding: (encoding: string) => Promise<SimulcastConfig>;
  setVideoTrackEncodingBandwidth: (
    encoding: string,
    bandwidth: number,
  ) => Promise<void>;
  setVideoTrackBandwidth: (bandwidth: number) => Promise<void>;
  changeWebRTCLoggingSeverity: (severity: string) => Promise<void>;
  getStatistics: () => Promise<RTCStats>;
};
