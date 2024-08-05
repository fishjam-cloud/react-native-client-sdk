// hopefully temporary file with global state

let isClientConnected = false;
let isMicrophoneEnabled = false;

export function isConnected() {
  return isClientConnected;
}
export function setConnectionStatus(connected: boolean) {
  isClientConnected = connected;
}

export function isMicrophoneSetToOn() {
  return isMicrophoneEnabled;
}
export function setMicrophoneStatus(connected: boolean) {
  isMicrophoneEnabled = connected;
}
