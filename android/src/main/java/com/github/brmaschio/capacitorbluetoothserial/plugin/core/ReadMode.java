package com.github.brmaschio.capacitorbluetoothserial.plugin.core;

public enum ReadMode {

    RAW("RAW"),
    CR("CR"),
    LF("LF"),
    CRLF("CRLF"),
    STX_ETX("STX_ETX");

    private final String desc;

    ReadMode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static ReadMode getByDesc(String desc) {
        if (desc != null) {
            for (ReadMode e : values()) {
                if (e.getDesc().equals(desc))
                    return e;
            }
        }
        return null;
    }

}
