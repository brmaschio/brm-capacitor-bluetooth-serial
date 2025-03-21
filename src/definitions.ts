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
  listPairedDevices(): Promise<{ devices: Device[] }>;
  connect(options: { address: string, mode: EditorMode }): Promise<{ connected: boolean }>;
  disconnect(options: { address: string }): Promise<{ disconnected: boolean }>;
  isEnabled(): Promise<{ isEnabled: boolean }>;
  isConnected(options: { address: string }): Promise<{ isConnected: boolean }>;
  write(options: { address: string, command: string }): Promise<void>;
  read(options: { address: string }): Promise<{ data: boolean }>;

}
