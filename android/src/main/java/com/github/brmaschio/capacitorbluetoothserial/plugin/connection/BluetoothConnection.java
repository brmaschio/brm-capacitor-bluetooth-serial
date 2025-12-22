package com.github.brmaschio.capacitorbluetoothserial.plugin.connection;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BluetoothPermissionException;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.EditorMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnection extends Thread {

    private BluetoothSocket socket = null;
    private InputStream reader;
    private OutputStream writer;
    private final StringBuffer readBuffer;
    private boolean connected = false;
    private final EditorMode editorMode;

    @SuppressLint("MissingPermission")
    public BluetoothConnection(BluetoothDevice device, EditorMode editorMode) throws BluetoothPermissionException {
        this.editorMode = editorMode;
        connect(device);
        readBuffer = new StringBuffer();
    }

    public void run() {
        byte[] buffer = new byte[1024];
        while (true) {
            if (connected) {
                try {
                    int bytesRead = reader.read(buffer);
                    if(this.editorMode.equals(EditorMode.HEX)) {
                        appendToBuffer(Helper.bytesToHex(buffer, bytesRead));
                    } else {
                        appendToBuffer(new String(buffer, 0, bytesRead));
                    }
                } catch (IOException e) {
                    try {
                        disconnect();
                    } catch (BluetoothPermissionException ex) {
                        connected = false;
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        }
    }

    private void appendToBuffer(String data) {
        synchronized (this.readBuffer) {
            this.readBuffer.append(data);
        }
    }

    public void disconnect() throws BluetoothPermissionException {
        try {
            socket.close();
            connected = false;
        } catch (IOException e) {
            throw new BluetoothPermissionException("Erro To disconnect");
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void write(byte[] bytes) throws BluetoothPermissionException {
        try {
            writer.write(bytes);
        } catch (IOException e) {
            throw new BluetoothPermissionException("Erro To write");
        }
    }

    public String read() {
        String data;
        synchronized (readBuffer) {
            int index = readBuffer.length();
            data = readBuffer.substring(0, index);
            readBuffer.delete(0, index);
        }
        return data.trim().isEmpty() ? null : data;
    }

    @SuppressLint("MissingPermission")
    private void connect(BluetoothDevice device) throws BluetoothPermissionException {
        try {
            socket = device.createRfcommSocketToServiceRecord(Helper.DEFAULT_UUID);
            socket.connect();
            writer = socket.getOutputStream();
            reader = socket.getInputStream();
            connected = true;
        } catch (IOException e) {
            connected = false;
            throw new BluetoothPermissionException("Erro To Connect");
        }
    }

    public EditorMode getEditorMode() {
        return editorMode;
    }
}
