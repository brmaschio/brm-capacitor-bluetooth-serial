// import type { PluginListenerHandle } from "@capacitor/core";

export enum EditorMode{
  TEXT = "TEXT", HEX = "HEX"
}

export interface BrMCapacitorBluetoothSerialPlugin {

  requestPermissions(): void;
  hasPermitions(): Promise<{ hasPermitions: boolean }>;
  listPairedDevices(): Promise<{ devices: any[] }>;
  connect(options: { address: string, mode: EditorMode }): Promise<{ connected: boolean }>;
  disconnect(options: { address: string }): Promise<{ disconnected: boolean }>;
  isEnabled(): Promise<{ isEnabled: boolean }>;
  isConnected(options: { address: string }): Promise<{ isConnected: boolean }>;
  write(options: { address: string, command: string }): Promise<void>;
  read(options: { address: string }): Promise<{ data: boolean }>;

  // addListener(
  //   eventName: 'bluetoothLog',
  //   listenerFunc: (data: { message: string }) => void
  // ): Promise<PluginListenerHandle>;

}
