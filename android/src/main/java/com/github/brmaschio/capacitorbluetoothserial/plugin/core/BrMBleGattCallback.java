package com.github.brmaschio.capacitorbluetoothserial.plugin.core;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;

import com.github.brmaschio.capacitorbluetoothserial.plugin.connection.BluetoothLeConnection;

public class BrMBleGattCallback extends BluetoothGattCallback {

    private final BluetoothLeConnection parentConnection;

    public BrMBleGattCallback(BluetoothLeConnection parentConnection) {
        this.parentConnection = parentConnection;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                parentConnection.connected = true;
                if (parentConnection.context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt.discoverServices();
                } else {
                    parentConnection.disconnectAndCleanUp(gatt);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                parentConnection.disconnectAndCleanUp(gatt);
            }
        } else {
            parentConnection.disconnectAndCleanUp(gatt);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            try {
                parentConnection.findCharacteristicsAndEnableNotifications(gatt);
            } catch (BluetoothPermissionException e) {
                parentConnection.disconnectAndCleanUp(gatt);
            }
        } else {
            parentConnection.disconnectAndCleanUp(gatt);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        if (!parentConnection.running) return;

        super.onCharacteristicRead(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().equals(parentConnection.readerUuid)) {
            parentConnection.readBuffer.offer(characteristic.getValue());
//            byte[] value = characteristic.getValue();
//            try {
//                parentConnection.readBuffer.put(value);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        if (!parentConnection.running) return;

        super.onCharacteristicChanged(gatt, characteristic);
        if (characteristic.getUuid().equals(parentConnection.readerUuid)) {
            byte[] value = characteristic.getValue();
            parentConnection.readBuffer.offer(value);
//            try {
//                parentConnection.readBuffer.put(value);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

}
