package com.github.brmaschio.capacitorbluetoothserial;

import android.Manifest;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.github.brmaschio.capacitorbluetoothserial.plugin.service.BluetoothLeService;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BluetoothPermissionException;
import com.github.brmaschio.capacitorbluetoothserial.plugin.service.BluetoothService;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.EditorMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.Helper;

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
    private BluetoothLeService bleService;

    @Override
    public void load() {
        btService = new BluetoothService(getContext(), getActivity());
        bleService = new BluetoothLeService(getContext(), getActivity(), btService);
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
    public void scanBleDevices(PluginCall call) {

        Integer timeout = call.getInt("timeout");
        timeout = timeout != null ? timeout : 5000;

        try {
            JSObject ret = new JSObject();
            ret.put("devices", bleService.scanBleDevices(timeout));
            call.resolve(ret);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void isConnected(PluginCall call) {
        String address = call.getString("address");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject response = new JSObject();
        response.put("isConnected", btService.isConnected(address));
        call.resolve(response);
    }

    @PluginMethod
    public void isConnectedBle(PluginCall call) {
        String address = call.getString("address");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject response = new JSObject();
        response.put("isConnected", bleService.isConnectedBle(address));
        call.resolve(response);
    }

    @PluginMethod
    public void connect(PluginCall call) {
        String address = call.getString("address");
        String mode = call.getString("mode", EditorMode.TEXT.getDesc());
        EditorMode editorMode = EditorMode.getByDesc(mode);

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject ret = new JSObject();
        try {
            boolean connected = btService.connect(address, editorMode);
            ret.put("connected", connected);
            call.resolve(ret);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void connectBle(PluginCall call) {
        String address = call.getString("address");
        String mode = call.getString("mode", EditorMode.TEXT.getDesc());
        EditorMode editorMode = EditorMode.getByDesc(mode);

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject ret = new JSObject();
        try {
            boolean connected = bleService.connectBle(address, null, null, null, editorMode);
            ret.put("connected", connected);
            call.resolve(ret);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void disconnect(PluginCall call) {
        String address = call.getString("address");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject ret = new JSObject();
        try {
            ret.put("disconnected", btService.disconnect(address));
            call.resolve(ret);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void disconnectBle(PluginCall call) {
        String address = call.getString("address");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject ret = new JSObject();
        try {
            ret.put("disconnected", bleService.disconnectBle(address));
            call.resolve(ret);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void write(PluginCall call) {
        String address = call.getString("address");
        String command = call.getString("command");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }
        if (Helper.isEmpity(command)) {
            call.reject("command not found");
            return;
        }

        try {
            btService.write(address, command);
            call.resolve();
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void writeBle(PluginCall call) {
        String address = call.getString("address");
        String command = call.getString("command");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }
        if (Helper.isEmpity(command)) {
            call.reject("command not found");
            return;
        }

        try {
            bleService.writeBle(address, command);
            call.resolve();
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void read(PluginCall call) {
        String address = call.getString("address");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject ret = new JSObject();
        try {
            ret.put("data", btService.read(address));
            call.resolve(ret);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void readBle(PluginCall call) {
        String address = call.getString("address");

        if (Helper.isEmpity(address)) {
            call.reject("address not found");
            return;
        }

        JSObject ret = new JSObject();
        try {
            ret.put("data", bleService.readBle(address));
            call.resolve(ret);
        } catch (BluetoothPermissionException e) {
            call.reject(e.getMessage(), e);
        }
    }

}
