import { WebPlugin } from '@capacitor/core';

import type { BrMCapacitorBluetoothSerialPlugin } from './definitions';

export class BrMCapacitorBluetoothSerialWeb extends WebPlugin implements BrMCapacitorBluetoothSerialPlugin {

  private msg = 'Not supported att web';

  async requestPermissions(): Promise<any> {
    this.showWarn();
  }

  async hasPermitions(): Promise<any> {
    this.showWarn();
  }

  async listPairedDevices(): Promise<any> {
    this.showWarn();
  }

  async connect(): Promise<any> {
    this.showWarn();
  }

  async disconnect(): Promise<any> {
    this.showWarn();
  }
  
  async isEnabled(): Promise<any> {
    this.showWarn();
  }

  async isConnected(): Promise<any> {
    this.showWarn();
  }

  async write(): Promise<any> {
    this.showWarn();
  }

  async read(): Promise<any> {
    this.showWarn();
  }

  private showWarn() {
    console.warn(this.msg);
  }

}
