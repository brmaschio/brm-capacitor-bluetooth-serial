package com.github.brmaschio.capacitorbluetoothserial.plugin.core;

public enum WriteMode {

    RAW("RAW"),
    CR("CR"),
    LF("LF"),
    CRLF("CRLF");

    private final String desc;

    WriteMode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static WriteMode getByDesc(String desc) {
        if (desc != null) {
            for (WriteMode e : values()) {
                if (e.getDesc().equals(desc))
                    return e;
            }
        }
        return null;
    }

}
