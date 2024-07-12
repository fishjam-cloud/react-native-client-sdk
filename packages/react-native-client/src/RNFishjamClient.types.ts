import type { RTCStats } from './stats/types';
import type { MicrophoneConfig } from './hooks/useMicrophone';

export enum TrackType {
  Audio = 'Audio',
  Video = 'Video',
}
/**
 * Type describing Voice Activity Detection statuses.
 *
 * SPEECH - voice activity has been detected
 * SILENCE - lack of voice activity has been detected
 */
export enum VadStatus {
  Silence = 'silence',
  Speech = 'speech',
}

/**
 * Type describing possible reasons of currently selected encoding.
 *
 * - OTHER - the exact reason couldn't be determined
 * - ENCODING_INACTIVE - previously selected encoding became inactive
 * - LOW_BANDWIDTH - there is no longer enough bandwidth to maintain previously selected encoding
 */
export enum EncodingReason {
  Other = 'other',
  EncodingInactive = 'encodingInactive',
  LowBandwidth = 'lowBandwidth',
}

export type Track<MetadataType extends Metadata> = {
  id: string;
  type: TrackType;
  metadata: MetadataType;
  vadStatus: VadStatus;
  // Encoding that is currently received. Only present for remote tracks.
  encoding: TrackEncoding | null;
  // Information about simulcast, if null simulcast is not enabled
  simulcastConfig: SimulcastConfig | null;
  // The reason of currently selected encoding. Only present for remote tracks.
  encodingReason: EncodingReason | null;
};
// global
export type Metadata = { [key: string]: any };
export type SocketConnectionParams = { [key: string]: any };
export type SocketChannelParams = { [key: string]: any };

export type Endpoint<
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
> = {
  /**
   *  id used to identify an endpoint
   */
  id: string;
  /**
   * used to indicate endpoint type.
   */
  type: string;
  /**
   * whether the endpoint is local or remote
   */
  isLocal: boolean;
  /**
   * a map `string -> any` containing endpoint metadata from the server
   */
  metadata: MetadataType;
  /**
   * a list of endpoints's video and audio tracks
   */
  tracks: Track<VideoTrackMetadataType | AudioTrackMetadataType>[];
};

export enum VideoLayout {
  FILL = 'FILL',
  FIT = 'FIT',
}

export enum VideoQuality {
  QVGA_169 = 'QVGA169',
  VGA_169 = 'VGA169',
  QHD_169 = 'QHD169',
  HD_169 = 'HD169',
  FHD_169 = 'FHD169',
  QVGA_43 = 'QVGA43',
  VGA_43 = 'VGA43',
  QHD_43 = 'QHD43',
  HD_43 = 'HD43',
  FHD_43 = 'FHD43',
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

export type ConnectionOptions<MetadataType extends Metadata> = {
  /**
   * a map `string -> any` containing user metadata to be sent to the server. Use it to send for example user display name or other options.
   */
  endpointMetadata: MetadataType;

  /**
   *  a map `string -> any` containing connection params passed to the socket.
   */
  connectionParams: SocketConnectionParams;
  /**
   * a map `string -> any` containing params passed to the socket channel.
   */
  socketChannelParams: SocketChannelParams;
};

export type CameraConfig<MetadataType extends Metadata> = {
  /**
   * resolution + aspect ratio of local video track, one of: `QVGA_169`, `VGA_169`, `QHD_169`, `HD_169`,
   * `FHD_169`, `QVGA_43`, `VGA_43`, `QHD_43`, `HD_43`, `FHD_43`. Note that quality might be worse than
   * specified due to device capabilities, internet connection etc.
   * @default `VGA_169`
   */
  quality: VideoQuality;
  /**
   * whether to flip the dimensions of the video, that is whether to film in vertical orientation.
   * @default `true`
   */
  flipVideo: boolean;
  /**
   * a map `string -> any` containing video track metadata to be sent to the server.
   */
  videoTrackMetadata: MetadataType;
  /**
   *  SimulcastConfig of a video track. By default simulcast is disabled.
   */
  simulcastConfig: SimulcastConfig;
  /**
   *  bandwidth limit of a video track. By default there is no bandwidth limit.
   */
  maxBandwidth: TrackBandwidthLimit;
  /**
   * whether the camera track is initially enabled, you can toggle it on/off later with toggleCamera method
   * @default `true`
   */
  cameraEnabled: boolean;
  /**
   * id of the camera to start capture with. Get available cameras with `getCaptureDevices()`.
   * You can switch the cameras later with `flipCamera`/`switchCamera` functions.
   * @default the first front camera
   */
  captureDeviceId: string;
};

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

export type CaptureDevice = {
  id: string;
  name: string;
  isFrontFacing: boolean;
  isBackFacing: boolean;
};

export enum LoggingSeverity {
  Verbose = 'verbose',
  Info = 'info',
  Warning = 'warning',
  Error = 'error',
  None = 'none',
}

export type EndpointsUpdateEvent<
  MetadataType extends Metadata,
  VideoTrackMetadataType extends Metadata,
  AudioTrackMetadataType extends Metadata,
> = {
  EndpointsUpdate: Endpoint<
    MetadataType,
    VideoTrackMetadataType,
    AudioTrackMetadataType
  >[];
};

export type IsCameraOnEvent = { IsCameraOn: boolean };

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
