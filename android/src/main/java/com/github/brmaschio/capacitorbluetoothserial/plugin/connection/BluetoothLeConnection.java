package com.github.brmaschio.capacitorbluetoothserial.plugin.connection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.RequiresPermission;

import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BluetoothPermissionException;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.BrMBleGattCallback;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.EditorMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.Helper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BluetoothLeConnection extends Thread  {

    public final Context context;
    public final BluetoothDevice device;
    private UUID serviceUuid;

    public volatile boolean running = true;
    private BluetoothGatt socket;
    private BluetoothGattCharacteristic reader;
    public UUID readerUuid;
    private BluetoothGattCharacteristic writer;
    private UUID writerUuid;
    public final BlockingQueue<byte[]> readBuffer;
    public final EditorMode editorMode;
    public boolean connected = false;

    @SuppressLint("MissingPermission")
    public BluetoothLeConnection(Context context, BluetoothDevice device, EditorMode editorMode,
                                 UUID serviceUuid, UUID readerUuid, UUID writerUuid) throws BluetoothPermissionException {
        this.serviceUuid = serviceUuid;
        this.readerUuid = readerUuid;
        this.writerUuid = writerUuid;
        this.context = context;
        this.editorMode = editorMode;
        this.device = device;
        readBuffer = new ArrayBlockingQueue<>(100);
        connect();
    }

    @SuppressLint("MissingPermission")
    public void disconnect() {

        running = false;
        readBuffer.offer(new byte[0]);

        if (this.socket != null && connected) {
            this.socket.disconnect();
        }
        disconnectAndCleanUp(this.socket);
    }

    public boolean isConnected() {
        return this.connected;
    }

    @SuppressLint("MissingPermission")
    public void write(byte[] bytes) throws BluetoothPermissionException {
        if (!this.connected || this.socket == null || this.writer == null) {
            throw new BluetoothPermissionException("BLE device not connected or write capability unavailable.");
        }

        this.writer.setValue(bytes);
        boolean success = this.socket.writeCharacteristic(this.writer);
        if (!success) {
            throw new BluetoothPermissionException("Failed to write to BLE feature.");
        }

    }

    public String read() throws BluetoothPermissionException {

        if (!running) return null;

        byte[] dataBytes = null;
        try {
            dataBytes = this.readBuffer.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BluetoothPermissionException("Fail disconnect on run process");
        }

        if (dataBytes == null) {
            if (!this.connected) {
                throw new BluetoothPermissionException("Fail disconnect on run process");
            }
            return null;
        }

        String data;
        if (this.editorMode.equals(EditorMode.HEX)) {
            data = Helper.bytesToHex(dataBytes, dataBytes.length);
        } else {
            data = new String(dataBytes);
        }

        return data.trim().isEmpty() ? null : data;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void connect() throws BluetoothPermissionException {
        socket = this.device.connectGatt(this.context, false, new BrMBleGattCallback(this));
        if (socket == null) {
            throw new BluetoothPermissionException("Erro To Connect");
        }
    }

    @SuppressLint("MissingPermission")
    public void disconnectAndCleanUp(BluetoothGatt gatt) {
        if (gatt != null) {
            gatt.close();
        }
        this.socket = null;
        this.connected = false;
        this.reader = null;
        this.writer = null;
        this.serviceUuid = null;
        this.readerUuid = null;
        this.writerUuid = null;
        this.running = false;
        this.readBuffer.clear();
    }

    @SuppressLint("MissingPermission")
    public void findCharacteristicsAndEnableNotifications(BluetoothGatt gatt) throws BluetoothPermissionException {
        if (gatt == null) {
            throw new BluetoothPermissionException("BluetoothGatt is null when trying to find features.");
        }

        List<BluetoothGattService> services = gatt.getServices();
        if (services == null || services.isEmpty()) {
            throw new BluetoothPermissionException("No GATT service found for: " + device.getAddress());
        }

        boolean foundSpecificUuids = false;

        // PRIORITY 1: If UUIDs were provided, try to find them specifically.
        if (this.serviceUuid != null && this.readerUuid != null && this.writerUuid != null) {
            BluetoothGattService s = gatt.getService(this.serviceUuid);
            if (s != null) {
                this.reader = s.getCharacteristic(this.readerUuid);
                this.writer = s.getCharacteristic(this.writerUuid);
                if (this.reader != null && this.writer != null) {
                    foundSpecificUuids = true;
                }
            }
            if (!foundSpecificUuids) {
                this.serviceUuid = null; this.readerUuid = null; this.writerUuid = null;
            }
        }

        // PRIORITY 2: Heuristic self-discovery - Attempt to find a known UART service.
        if (!foundSpecificUuids) {
            for (BluetoothGattService service : services) {
                // Pular serviços padrão, a menos que sejam os únicos disponíveis
                if (isExcludedService(service.getUuid()) && services.size() > Helper.EXCLUDE_SERVICE_UUIDS.length) {
                    continue;
                }
                if (isKnownUartService(service.getUuid())) {
                    this.reader = getCharacteristicByUuids(service, Helper.KNOWN_UART_RX_CHARACTERISTIC_UUIDS);
                    this.writer = getCharacteristicByUuids(service, Helper.KNOWN_UART_TX_CHARACTERISTIC_UUIDS);
                    if (this.reader != null && this.writer != null) {
                        this.serviceUuid = service.getUuid();
                        this.readerUuid = this.reader.getUuid();
                        this.writerUuid = this.writer.getUuid();
                        foundSpecificUuids = true;
                        break;
                    }
                }
            }
        }

        // PRIORITY 3: Most generic heuristic if not yet found
        if (!foundSpecificUuids) {
            for (BluetoothGattService service : services) {
                if (isExcludedService(service.getUuid()) && services.size() > Helper.EXCLUDE_SERVICE_UUIDS.length) {
                    continue; // Pular serviços padrão
                }
                BluetoothGattCharacteristic potentialWrite = null;
                BluetoothGattCharacteristic potentialRead = null;

                for (BluetoothGattCharacteristic charac : service.getCharacteristics()) {
                    int properties = charac.getProperties();
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ||
                            (properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
                        potentialWrite = charac;
                    }
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0 ||
                            (properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                        potentialRead = charac;
                    }
                }

                if (potentialWrite != null && potentialRead != null) {
                    this.serviceUuid = service.getUuid();
                    this.writer = potentialWrite;
                    this.reader = potentialRead;
                    this.readerUuid = potentialRead.getUuid();
                    this.writerUuid = potentialWrite.getUuid();
                    foundSpecificUuids = true;
                    break;
                }
            }
        }

        if (!foundSpecificUuids) {
            String errorMessage = "No valid communication characteristics (read/write) could be found for the device: " +
                    device.getAddress() + ". Verify that the UUIDs were provided correctly or that the device has a compatible GATT profile.";
            disconnectAndCleanUp(gatt);
            throw new BluetoothPermissionException(errorMessage);
        }

        // If we find the features, we now enable notifications for the reading feature.
        // This should be done AFTER selecting the feature.
        if (this.reader != null && (this.reader.getProperties() & (BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_INDICATE)) != 0) {
            gatt.setCharacteristicNotification(this.reader, true);
            BluetoothGattDescriptor descriptor = this.reader.getDescriptor(Helper.CCCD_UUID);
            if (descriptor != null) {
                if ((this.reader.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else if ((this.reader.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                }
                gatt.writeDescriptor(descriptor);
            }
        }
    }

    private boolean isExcludedService(UUID serviceUuid) {
        for (UUID uuid : Helper.EXCLUDE_SERVICE_UUIDS) {
            if (uuid.equals(serviceUuid)) {
                return true;
            }
        }
        return false;
    }

    private boolean isKnownUartService(UUID serviceUuid) {
        for (UUID uuid : Helper.KNOWN_UART_SERVICE_UUIDS) {
            if (uuid.equals(serviceUuid)) {
                return true;
            }
        }
        return false;
    }

    private BluetoothGattCharacteristic getCharacteristicByUuids(BluetoothGattService service, UUID[] characteristicUuids) {
        for (UUID uuid : characteristicUuids) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid);
            if (characteristic != null) {
                return characteristic;
            }
        }
        return null;
    }

}
