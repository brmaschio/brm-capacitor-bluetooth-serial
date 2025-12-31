package com.github.brmaschio.capacitorbluetoothserial.plugin.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.github.brmaschio.capacitorbluetoothserial.BrMCapacitorBluetoothSerialPlugin;
import com.github.brmaschio.capacitorbluetoothserial.plugin.connection.BluetoothConnection;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BluetoothPermissionException;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.EditorMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.Helper;

import java.util.HashMap;
import java.util.Map;

public class BluetoothService {

    private final Context context;
    private final Activity activity;
    private final BrMCapacitorBluetoothSerialPlugin plugin;

    private final Map<String, BluetoothConnection> connections = new HashMap<>();
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothService(Context context, Activity activity, BrMCapacitorBluetoothSerialPlugin plugin) {
        this.context = context;
        this.activity = activity;
        this.plugin = plugin;
    }

    public boolean hasPermitions() {
        // Android 12+ (API 31)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        // Android 10-11 (API 29-30)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        // Android 6-9 (API 23-28)
        else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @SuppressLint("MissingPermission")
    public boolean isEnabled() {
        boolean hasBluetoothFeature = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        boolean hasPermitions = hasPermitions();
        boolean bluetoothAdapterIsEnabled = bluetoothAdapter.isEnabled();

        if (hasBluetoothFeature && hasPermitions && !bluetoothAdapterIsEnabled) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 200);
        }

        return hasBluetoothFeature && hasPermitions && bluetoothAdapterIsEnabled;
    }

    public String loadPermissionsAlias() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return "BLUETOOTH";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return "BLUETOOTH-10-11";
        } else {
            return "BLUETOOTH-6-9";
        }
    }

    @SuppressLint("MissingPermission")
    public JSArray listPairedDevices() throws BluetoothPermissionException {
        if (!hasPermitions()) {
            throw new BluetoothPermissionException("Bluetooth not allowed");
        }
        JSArray devices = new JSArray();
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            JSObject json = new JSObject();
            json.put("name", device.getName());
            json.put("address", device.getAddress());
            devices.put(json);
        }
        return devices;
    }

    public boolean isConnected(String address) {
        BluetoothConnection connection = getConnection(address);
        if (connection != null) {
            return connection.isConnected();
        }
        return false;
    }

    public boolean connect(String address, EditorMode editorMode) throws BluetoothPermissionException {

        if(!hasPermitions()) {
            throw new BluetoothPermissionException("Without Permission");
        }

        BluetoothConnection connection = getConnection(address);
        if (connection != null && connection.isConnected()) {
            return true;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            throw new BluetoothPermissionException("Device not found");
        }

        connection = new BluetoothConnection(device, editorMode, this.plugin);
        connection.start();
        connections.put(device.getAddress(), connection);
        return true;

    }

    public boolean disconnect(String address) throws BluetoothPermissionException {
        BluetoothConnection connection = getConnection(address);
        if (connection == null || !connection.isConnected()) {
            return true;
        }

        connection.disconnect();
        connections.remove(address);
        return true;
    }

    public void write(String address, String command) throws BluetoothPermissionException {

        BluetoothConnection connection;
        synchronized (this) {
            connection = getConnection(address);
        }

        if (connection == null || !connection.isConnected()) {
            throw new BluetoothPermissionException("Device not found");
        }

        if (connection.getEditorMode().equals(EditorMode.HEX)) {
            byte[] bytes = Helper.hexStringToByteArray(command);
            connection.write(bytes);
        } else {
            byte[] bytes = Helper.toByteArray(command);
            connection.write(bytes);
        }

    }

    public String read(String address) throws BluetoothPermissionException {
        BluetoothConnection connection = getConnection(address);
        if (connection == null || !connection.isConnected()) {
            throw new BluetoothPermissionException("Device not found");
        }
        return connection.read();
    }

    private BluetoothConnection getConnection(String address) {
        return connections.get(address);
    }

}
