package com.github.brmaschio.capacitorbluetoothserial;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "BrMCapacitorBluetoothSerial")
public class BrMCapacitorBluetoothSerialPlugin extends Plugin {

    private BrMCapacitorBluetoothSerial implementation = new BrMCapacitorBluetoothSerial();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }
}
