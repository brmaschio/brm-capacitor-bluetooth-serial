package com.github.brmaschio.capacitorbluetoothserial.plugin.connection;

import android.util.Log;

import com.github.brmaschio.capacitorbluetoothserial.plugin.core.ReadMode;
import com.github.brmaschio.capacitorbluetoothserial.plugin.core.WriteMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketParser {

    private final ReadMode readMode;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private boolean receivingPacket = false;

    public PacketParser(ReadMode readMode) {
        this.readMode = readMode;
    }

    public byte[] applyWriteMode(byte[] bytes, WriteMode writeMode) {
        return switch (writeMode) {
            case CR -> append(bytes, new byte[]{0x0D});
            case LF -> append(bytes, new byte[]{0x0A});
            case CRLF -> append(bytes, new byte[]{0x0D, 0x0A});
            default -> bytes;
        };
    }

    public synchronized List<byte[]> process(byte[] data) {
        List<byte[]> packets = new ArrayList<>();
        if (data == null || data.length == 0) {
            return packets;
        }
        switch (readMode) {
            case RAW:
                packets.add(data);
                break;
            case STX_ETX:
                processStxEtx(data, packets);
                break;
            case CRLF:
                processDelimiter(data, new byte[]{0x0D, 0x0A}, packets);
                break;
            case CR:
                processDelimiter(data, new byte[]{0x0D}, packets);
                break;
            case LF:
                processDelimiter(data, new byte[]{0x0A}, packets);
                break;
        }
        return packets;
    }

    private byte[] append(byte[] original, byte[] suffix) {
        byte[] result = new byte[original.length + suffix.length];
        System.arraycopy(original, 0, result, 0, original.length);
        System.arraycopy(suffix, 0, result, original.length, suffix.length);
        return result;
    }

    private void processStxEtx(byte[] data, List<byte[]> packets) {
        for (byte b : data) {
            if (b == 0x02) {
                buffer.reset();
                receivingPacket = true;
            }
            if (receivingPacket) {
                buffer.write(b);
            }

            if (b == 0x03 && receivingPacket) {
                byte[] packet = buffer.toByteArray();
                packets.add(removeStxEtx(packet));
                buffer.reset();
                receivingPacket = false;
            }
        }
    }

    private byte[] removeStxEtx(byte[] packet) {
        if (packet.length <= 2) {
            return new byte[0];
        }
        return Arrays.copyOfRange(packet, 1, packet.length - 1);
    }

    private void processDelimiter(byte[] data, byte[] delimiter, List<byte[]> packets) {
        for (byte b : data) {
            buffer.write(b);
            byte[] current = buffer.toByteArray();
            if (endsWith(current, delimiter)) {
                packets.add(Arrays.copyOf(current, current.length - delimiter.length));
                buffer.reset();
            }
        }
    }

    private boolean endsWith(byte[] data, byte[] suffix) {
        if (data.length < suffix.length) {
            return false;
        }
        int start = data.length - suffix.length;
        for (int i = 0; i < suffix.length; i++) {
            if (data[start + i] != suffix[i]) {
                return false;
            }
        }
        return true;
    }

    public ReadMode getReadMode() {
        return this.readMode;
    }

}
