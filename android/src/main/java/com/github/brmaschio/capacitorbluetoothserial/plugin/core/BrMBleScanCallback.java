package com.github.brmaschio.capacitorbluetoothserial.plugin.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import java.util.List;
import java.util.Set;

public class BrMBleScanCallback extends ScanCallback {

    private final JSArray devices;
    private final Set<String> discoveredAddresses;

    public BrMBleScanCallback(JSArray devices, Set<String> discoveredAddresses) {
        this.devices = devices;
        this.discoveredAddresses = discoveredAddresses;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        handleScanResult(result);
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        for (ScanResult result : results) {
            handleScanResult(result);
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
    }

    @SuppressLint("MissingPermission")
    private void handleScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        if (device != null && device.getName() != null && !discoveredAddresses.contains(device.getAddress())) {
            JSObject json = new JSObject();
            json.put("name", device.getName());
            json.put("address", device.getAddress());
            devices.put(json);
            discoveredAddresses.add(device.getAddress());
        }
    }

}
