package com.github.brmaschio.capacitorbluetoothserial;

import android.Manifest;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.github.brmaschio.capacitorbluetoothserial.plugin.BluetoothPermissionException;
import com.github.brmaschio.capacitorbluetoothserial.plugin.BluetoothService;
import com.github.brmaschio.capacitorbluetoothserial.plugin.EditorMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.Helper;

import java.util.Arrays;

@CapacitorPlugin(
        name = "BrMCapacitorBluetoothSerial",
        permissions = {
                @Permission(
                        alias = "BLUETOOTH",
                        strings = { Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT }
                ),
                @Permission(
                        alias = "BLUETOOTH-10-11",
                        strings = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }
                ),
                @Permission(
                        alias = "BLUETOOTH-6-9",
                        strings = { Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN }
                )
        }
)
public class BrMCapacitorBluetoothSerialPlugin extends Plugin {

    private BluetoothService btService;

    @Override
    public void load() {
        btService = new BluetoothService(getContext(), getActivity());
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        String alian = btService.loadPermissionsAlias();
        requestPermissionForAlias(alian, call, "requestPermissionsCallback");
    }

    @PermissionCallback
    public void requestPermissionsCallback(PluginCall call) {
        JSObject result = new JSObject();
        result.put("hasPermitions", btService.hasPermitions());
        call.resolve(result);
    }

    @PluginMethod
    public void hasPermitions(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("hasPermitions", btService.hasPermitions());
        call.resolve(ret);
    }

    @PluginMethod()
    public void isEnabled(PluginCall call) {
        boolean enabled = btService.isEnabled();
        JSObject response = new JSObject();
        response.put("isEnabled", enabled);
        call.resolve(response);
    }

    @PluginMethod
    public void listPairedDevices(PluginCall call) {
        JSObject ret = new JSObject();
        try {
            ret.put("devices", btService.listPairedDevices());
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void isConnected(PluginCall call) {
        String address = call.getString("address");

        if (address == null) {
            call.reject("address not found");
        }

        JSObject response = new JSObject();
        response.put("isConnected", btService.isConnected(address));
        call.resolve(response);
    }

    @PluginMethod
    public void connect(PluginCall call) {
        String address = call.getString("address");
        String mode = call.getString("mode", EditorMode.TEXT.getDesc());
        EditorMode editorMode = EditorMode.getByDesc(mode);

        if (address == null) {
            call.reject("address not found");
        }

        JSObject ret = new JSObject();
        try {
            boolean connected = btService.connect(address, editorMode);
            ret.put("connected", connected);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void disconnect(PluginCall call) {
        String address = call.getString("address");

        if (address == null) {
            call.reject("address not found");
        }

        JSObject ret = new JSObject();
        try {
            ret.put("disconnected", btService.disconnect(address));
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void write(PluginCall call) {
        String address = call.getString("address");
        String command = call.getString("command");

        if (address == null) {
            call.reject("address not found");
        }
        if (command == null) {
            call.reject("command not found");
        }

        try {
            btService.write(address, command);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
        call.resolve();
    }

    @PluginMethod
    public void read(PluginCall call) {
        String address = call.getString("address");

        if (address == null) {
            call.reject("address not found");
        }

        JSObject ret = new JSObject();
        try {
            ret.put("data", btService.read(address));
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
        call.resolve(ret);
    }

}
