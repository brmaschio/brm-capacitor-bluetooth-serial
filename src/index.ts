import { registerPlugin } from '@capacitor/core';

import type { BrMCapacitorBluetoothSerialPlugin } from './definitions';

const BrMCapacitorBluetoothSerial = registerPlugin<BrMCapacitorBluetoothSerialPlugin>('BrMCapacitorBluetoothSerial', {
  web: () => import('./web').then((m) => new m.BrMCapacitorBluetoothSerialWeb()),
});

export * from './definitions';
export { BrMCapacitorBluetoothSerial };
