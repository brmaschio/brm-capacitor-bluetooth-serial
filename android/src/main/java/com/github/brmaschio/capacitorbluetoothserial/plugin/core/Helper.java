package com.github.brmaschio.capacitorbluetoothserial.plugin.core;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Helper {

    public static final UUID[] KNOWN_UART_SERVICE_UUIDS = {
            UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    };
    public static final UUID[] KNOWN_UART_TX_CHARACTERISTIC_UUIDS = {
            UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    };
    public static final UUID[] KNOWN_UART_RX_CHARACTERISTIC_UUIDS = {
            UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
    };
    public static final UUID[] EXCLUDE_SERVICE_UUIDS = {
            UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
    };

    public static final UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String TAG = "BrMBluetooth";

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] toByteArray(String value) {
        if (value == null) {
            return new byte[0];
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static String bytesToHex(byte[] bytes, int length) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            hexString.append(String.format("%02X", bytes[i]));
        }
        return hexString.toString();
    }

    public static boolean isEmpity(String stg) {
        return stg == null || stg.trim().isEmpty();
    }

}
