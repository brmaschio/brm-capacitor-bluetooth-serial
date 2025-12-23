package com.github.brmaschio.capacitorbluetoothserial.plugin.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.pm.PackageManager;

import com.getcapacitor.JSArray;
import com.github.brmaschio.capacitorbluetoothserial.plugin.connection.BluetoothLeConnection;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BluetoothPermissionException;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BrMBleScanCallback;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.EditorMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.Helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BluetoothLeService {

    private final Context context;
    private final Activity activity;
    private final BluetoothService bluetoothService;
    private final BluetoothAdapter bluetoothAdapter;

    private final Map<String, BluetoothLeConnection> connectionsBleInstances = new ConcurrentHashMap<>();

    public BluetoothLeService(Context context, Activity activity, BluetoothService bluetoothService) {
        // context
        this.context = context;
        this.activity = activity;
        // service
        this.bluetoothService = bluetoothService;
        // bluetooth core
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @SuppressLint("MissingPermission")
    public JSArray scanBleDevices(Integer timeout) throws BluetoothPermissionException {

        final JSArray devices = new JSArray();
        final Set<String> discoveredAddresses = new HashSet<>();

        BrMBleScanCallback scanCallback = new BrMBleScanCallback(devices, discoveredAddresses);
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            throw new BluetoothPermissionException("Scanner BLE not supported");
        }
        bluetoothLeScanner.startScan(scanCallback);

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BluetoothPermissionException("BLE scan interrupted due to error.");
        } finally {
            bluetoothLeScanner.stopScan(scanCallback);
        }

        return devices;

    }

    @SuppressLint("MissingPermission")
    public boolean connectBle(String address, String serviceUuidStr, String readCharacteristicUuidStr,
                              String writeCharacteristicUuidStr, EditorMode editorMode) throws BluetoothPermissionException {

        checksBleScannerPermission();

        BluetoothLeConnection existingConnection = connectionsBleInstances.get(address);
        if (existingConnection != null && existingConnection.isConnected()) {
            return true;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            throw new BluetoothPermissionException("BLE device not found with address: " + address);
        }

        UUID serviceUuid = (serviceUuidStr != null && !serviceUuidStr.isEmpty()) ? UUID.fromString(serviceUuidStr) : null;
        UUID readCharacteristicUuid = (readCharacteristicUuidStr != null && !readCharacteristicUuidStr.isEmpty()) ? UUID.fromString(readCharacteristicUuidStr) : null;
        UUID writeCharacteristicUuid = (writeCharacteristicUuidStr != null && !writeCharacteristicUuidStr.isEmpty()) ? UUID.fromString(writeCharacteristicUuidStr) : null;

        try {
            BluetoothLeConnection bleConnection = new BluetoothLeConnection(
                    context, device, editorMode, serviceUuid, readCharacteristicUuid, writeCharacteristicUuid
            );
            connectionsBleInstances.put(address, bleConnection);
            bleConnection.start();
            return true;
        } catch (IllegalArgumentException e) {
            throw new BluetoothPermissionException("Invalid UUID provided: " + e.getMessage());
        } catch (BluetoothPermissionException e) {
            connectionsBleInstances.remove(address);
            throw e;
        }
    }

    @SuppressLint("MissingPermission")
    public boolean disconnectBle(String address) throws BluetoothPermissionException {

        if (!bluetoothService.hasPermitions()) {
            throw new BluetoothPermissionException("Bluetooth LE disconnect permissions not granted.");
        }

        BluetoothLeConnection connection = connectionsBleInstances.get(address);
        if (connection == null) {
            return true;
        }

        connection.disconnect();
        connectionsBleInstances.remove(address);
        return true;
    }

    public boolean isConnectedBle(String address) {
        BluetoothLeConnection connection = connectionsBleInstances.get(address);
        return connection != null && connection.isConnected();
    }

    public void writeBle(String address, String command) throws BluetoothPermissionException {
        BluetoothLeConnection connection = connectionsBleInstances.get(address);
        if (connection == null || !connection.isConnected()) {
            throw new BluetoothPermissionException("Dispositivo BLE não conectado.");
        }

        byte[] bytes;
        if (connection.editorMode.equals(EditorMode.HEX)) {
            bytes = Helper.hexStringToByteArray(command);
        } else {
            bytes = command.getBytes(); // UTF-8 default
        }
        connection.write(bytes);
    }

    public String readBle(String address) throws BluetoothPermissionException {
        BluetoothLeConnection connection = connectionsBleInstances.get(address);
        if (connection == null || !connection.isConnected()) {
            throw new BluetoothPermissionException("BLE device not connected.");
        }
        return connection.read();
    }

    private void checksBleScannerPermission() throws BluetoothPermissionException {
        if (!bluetoothService.hasPermitions()) {
            throw new BluetoothPermissionException("Bluetooth not allowed");
        }
        if (!bluetoothService.isEnabled()) {
            throw new BluetoothPermissionException("Bluetooth not enabled");
        }
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new BluetoothPermissionException("Bluetooth Low Energy not supported");
        }
    }

    public boolean hasPermitions() {
        return  bluetoothService.hasPermitions() && activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }
}
