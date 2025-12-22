export enum EditorMode{
  TEXT = "TEXT", HEX = "HEX"
}

export interface Device {
  name: string;
  address: string;
}

export interface BrMCapacitorBluetoothSerialPlugin {

  requestPermissions(): Promise<{ hasPermitions: boolean }>;
  hasPermitions(): Promise<{ hasPermitions: boolean }>;
  isEnabled(): Promise<{ isEnabled: boolean }>;
  listPairedDevices(): Promise<{ devices: Device[] }>;
  scanBleDevices(timeout: number): Promise<{ devices: Device[] }>;
  connect(options: { address: string, mode: EditorMode }): Promise<{ connected: boolean }>;
  connectBle(options: { address: string, mode: EditorMode }): Promise<{ connected: boolean }>;
  disconnect(options: { address: string }): Promise<{ disconnected: boolean }>;
  disconnectBle(options: { address: string }): Promise<{ disconnected: boolean }>;
  isConnected(options: { address: string }): Promise<{ isConnected: boolean }>;
  isConnectedBle(options: { address: string }): Promise<{ isConnected: boolean }>;
  write(options: { address: string, command: string }): Promise<void>;
  writeBle(options: { address: string, command: string }): Promise<void>;
  read(options: { address: string }): Promise<{ data: boolean }>;
  readBle(options: { address: string }): Promise<{ data: boolean }>;

}
