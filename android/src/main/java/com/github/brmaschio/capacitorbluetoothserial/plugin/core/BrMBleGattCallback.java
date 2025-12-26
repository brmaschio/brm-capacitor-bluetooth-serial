package com.github.brmaschio.capacitorbluetoothserial.plugin.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import com.github.brmaschio.capacitorbluetoothserial.plugin.connection.BluetoothLeConnection;

public class BrMBleGattCallback extends BluetoothGattCallback {

    private final BluetoothLeConnection parentConnection;

    @Override
    @SuppressLint("MissingPermission")
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                parentConnection.disconnectAndCleanUp(gatt);
            }
        } else {
            parentConnection.disconnectAndCleanUp(gatt);
        }
    }

    public BrMBleGattCallback(BluetoothLeConnection parentConnection) {
        this.parentConnection = parentConnection;
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            try {
                parentConnection.findCharacteristicsAndEnableNotifications(gatt);
                parentConnection.notifyConnectionSuccess();
            } catch (BluetoothPermissionException e) {
                parentConnection.notifyConnectionFailure(e.getMessage());
                parentConnection.disconnectAndCleanUp(gatt);
            }
        } else {
            parentConnection.notifyConnectionFailure("Service discovery failed");
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
//            parentConnection.readBuffer.offer(value);
            parentConnection.onDataReceived(value);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

}
