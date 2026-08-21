package com.github.brmaschio.capacitorbluetoothserial.plugin.connection;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.github.brmaschio.capacitorbluetoothserial.BrMCapacitorBluetoothSerialPlugin;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BluetoothPermissionException;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.EditorMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.Helper;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.ReadMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.WriteMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnection extends Thread {

    private final BrMCapacitorBluetoothSerialPlugin plugin;
    private final BluetoothDevice device;

    private BluetoothSocket socket = null;
    private InputStream reader;
    private OutputStream writer;
    private final StringBuffer readBuffer;
    private boolean connected = false;
    private final EditorMode editorMode;
    private final PacketParser parser;

    @SuppressLint("MissingPermission")
    public BluetoothConnection(BluetoothDevice device, EditorMode editorMode, ReadMode readMode,
                               BrMCapacitorBluetoothSerialPlugin plugin) throws BluetoothPermissionException {
        this.plugin = plugin;
        this.device = device;
        this.editorMode = editorMode;
        this.parser = new PacketParser(readMode);
        connect();
        readBuffer = new StringBuffer();
    }

    public void run() {
        byte[] buffer = new byte[1024];

        while (true) {
            if(this.connected) {
                try {
                    int bytesRead = reader.read(buffer);

                    String data;
                    if(this.editorMode.equals(EditorMode.HEX)) {
                        data = Helper.bytesToHex(buffer, bytesRead);
                    } else {
                        data = new String(buffer, 0, bytesRead);
                    }

                    if(!data.trim().isEmpty()) {
                        appendToBuffer(data);
                        plugin.notifyDataReceived(device.getAddress(), data);
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

    public void write(byte[] bytes, WriteMode writeMode) throws BluetoothPermissionException {
        try {
            byte[] command = parser.applyWriteMode(bytes, writeMode);
            writer.write(command);
            writer.flush();
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
    private void connect() throws BluetoothPermissionException {
        try {
            socket = this.device.createRfcommSocketToServiceRecord(Helper.DEFAULT_UUID);
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
