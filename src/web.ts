import { WebPlugin } from '@capacitor/core';

import type { BrMCapacitorBluetoothSerialPlugin } from './definitions';

export class BrMCapacitorBluetoothSerialWeb extends WebPlugin implements BrMCapacitorBluetoothSerialPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
