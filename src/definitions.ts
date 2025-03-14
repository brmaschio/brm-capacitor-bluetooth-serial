export interface BrMCapacitorBluetoothSerialPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
