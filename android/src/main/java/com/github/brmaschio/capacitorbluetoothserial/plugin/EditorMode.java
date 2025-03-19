package com.github.brmaschio.capacitorbluetoothserial.plugin;

public enum EditorMode {

    HEX("HEX"),
    TEXT("TEXT");

    private final String desc;

    EditorMode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static EditorMode getByDesc(String desc) {
        if (desc != null) {
            for (EditorMode e : values()) {
                if (e.getDesc().equals(desc))
                    return e;
            }
        }
        return null;
    }

}
