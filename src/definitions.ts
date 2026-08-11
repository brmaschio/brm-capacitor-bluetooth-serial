import type { PluginListenerHandle } from '@capacitor/core';

export enum EditorMode {
  TEXT = "TEXT", 
  HEX = "HEX"
}

export enum ReadMode {
  RAW = "RAW", 
  CR = "CR", 
  LF = "LF", 
  CRLF = "CRLF", 
  STX_ETX = "STX_ETX"
}

export enum WriteMode {
  RAW = "RAW", 
  CR = "CR", 
  LF = "LF", 
  CRLF = "CRLF"
}

export interface Device {
  name: string;
  address: string;
}

export interface ConnectionStatusEvent {
  address: string;
  connected: boolean;
}

export interface BluetoothDataEvent {
  address: string;
  data: string;
}

export interface BrMCapacitorBluetoothSerialPlugin {

  requestPermissions(): Promise<{ hasPermitions: boolean }>;
  hasPermitions(): Promise<{ hasPermitions: boolean }>;
  hasPermitionsBle(): Promise<{ hasPermitions: boolean }>;
  isEnabled(): Promise<{ isEnabled: boolean }>;
  listPairedDevices(): Promise<{ devices: Device[] }>;
  scanBleDevices(options: { timeout: number }): Promise<{ devices: Device[] }>;
  connect(options: { address: string, mode?: EditorMode, readMode?: ReadMode }): Promise<{ connected: boolean }>;
  connectBle(options: { address: string, mode?: EditorMode, readMode?: ReadMode }): Promise<{ connected: boolean }>;
  disconnect(options: { address: string }): Promise<{ disconnected: boolean }>;
  disconnectBle(options: { address: string }): Promise<{ disconnected: boolean }>;
  isConnected(options: { address: string }): Promise<{ isConnected: boolean }>;
  isConnectedBle(options: { address: string }): Promise<{ isConnected: boolean }>;
  write(options: { address: string, command: string, writeMode?: WriteMode }): Promise<void>;
  writeBle(options: { address: string, command: string, writeMode?: WriteMode }): Promise<void>;
  read(options: { address: string }): Promise<{ data: boolean }>;
  readBle(options: { address: string }): Promise<{ data: boolean }>;
  addListener(eventName: 'dataReceived', listenerFunc: (event: BluetoothDataEvent) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
  addListener(eventName: 'connectionStatusChange', listenerFunc: (event: ConnectionStatusEvent) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
  removeAllListeners(): Promise<void>;

}
