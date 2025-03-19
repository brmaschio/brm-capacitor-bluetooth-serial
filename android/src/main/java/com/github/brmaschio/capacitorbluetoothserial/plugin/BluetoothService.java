package com.github.brmaschio.capacitorbluetoothserial.plugin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BluetoothService {

    private final Context context;
    private final Activity activity;

    private Map<String, BluetoothConnection> connections = new HashMap<>();
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

    public boolean isEnabled() {
        boolean hasBluetoothFeature = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        return hasBluetoothFeature && hasPermitions() && bluetoothAdapter.isEnabled();
    }

    public void requestPermissions() {
        int requestCode = 100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-11
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        } else {
            // Android 6-9
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, requestCode);
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

        if(connection == null || !connection.isConnected()) {
            throw new BluetoothPermissionException("Device not found");
        }

        if(connection.getEditorMode().equals(EditorMode.HEX)) {
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
        if(connection == null || !connection.isConnected()) {
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
