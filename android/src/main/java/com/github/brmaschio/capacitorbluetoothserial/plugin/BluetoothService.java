package com.github.brmaschio.capacitorbluetoothserial.plugin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.annotation.PermissionCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BluetoothService {

    private final Context context;
    private final Activity activity;

    private final Map<String, BluetoothConnection> connections = new HashMap<>();
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothService(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
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

        String logMsg = "Check is enabled."
                .concat(" hasBluetoothFeature: ").concat(String.valueOf(hasBluetoothFeature))
                .concat(" hasPermitions: ").concat(String.valueOf(hasPermitions))
                .concat(" bluetoothAdapterIsEnabled: ").concat(String.valueOf(bluetoothAdapterIsEnabled));
        Log.i(Helper.TAG, logMsg);

        return hasBluetoothFeature && hasPermitions && bluetoothAdapterIsEnabled;
    }

    public String loadPermissionsAlias() {
        Log.i(Helper.TAG, "Request BLUETOOTH Permissions");
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
        Log.i(Helper.TAG, "Check if device is already connected");
        BluetoothConnection connection = getConnection(address);
        if (connection != null) {
            return connection.isConnected();
        }
        return false;
    }

    public boolean connect(String address, EditorMode editorMode) throws BluetoothPermissionException {

        Log.i(Helper.TAG, "Trying to connect with device");

        BluetoothConnection connection = getConnection(address);
        if (connection != null && connection.isConnected()) {
            Log.i(Helper.TAG, "Device already connected");
            return true;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            throw new BluetoothPermissionException("Device not found");
        }

        connection = new BluetoothConnection(device, editorMode);
        connection.start();
        connections.put(device.getAddress(), connection);
        Log.i(Helper.TAG, "Success connecting to device");
        return true;

    }

    public boolean disconnect(String address) throws BluetoothPermissionException {

        Log.i(Helper.TAG, "Trying to disconnect with device");

        BluetoothConnection connection = getConnection(address);
        if (connection == null || !connection.isConnected()) {
            Log.i(Helper.TAG, "Device disconnected");
            return true;
        }

        connection.disconnect();
        connections.remove(address);
        Log.i(Helper.TAG, "Successful disconnection from device");

        return true;

    }

    public void write(String address, String command) throws BluetoothPermissionException {

        Log.i(Helper.TAG, "Trying write command");

        BluetoothConnection connection;
        synchronized (this) {
            connection = getConnection(address);
        }

        if (connection == null || !connection.isConnected()) {
            throw new BluetoothPermissionException("Device not found");
        }

        if (connection.getEditorMode().equals(EditorMode.HEX)) {
            byte[] bytes = Helper.hexStringToByteArray(command);
            Log.i(Helper.TAG, "Command HEX: " + command + " | Bytes: " + Arrays.toString(bytes));
            connection.write(bytes);
        } else {
            byte[] bytes = Helper.toByteArray(command);
            Log.i(Helper.TAG, "Command TEXT: " + command + " | Bytes: " + Arrays.toString(bytes));
            connection.write(bytes);
        }

        Log.i(Helper.TAG, "Success write command");

    }

    public String read(String address) throws BluetoothPermissionException {

        Log.i(Helper.TAG, "Trying read");

        BluetoothConnection connection = getConnection(address);
        if (connection == null || !connection.isConnected()) {
            throw new BluetoothPermissionException("Device not found");
        }

        String data = connection.read();
        Log.i(Helper.TAG, "Success read, data: " + data);
        return data;

    }

    private BluetoothConnection getConnection(String address) {
        return connections.get(address);
    }

}
