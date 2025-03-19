package com.github.brmaschio.capacitorbluetoothserial.plugin;

import android.util.Log;

public class BluetoothPermissionException extends Exception {

    public BluetoothPermissionException(String message) {
        super(message);
        Log.e(Helper.TAG, message);
    }
}
