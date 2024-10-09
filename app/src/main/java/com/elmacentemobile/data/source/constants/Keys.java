package com.elmacentemobile.data.source.constants;

public class Keys {
    public native String mapsKey();
    public native String secretKey();

    public native String ipStackKey();

    static {
        System.loadLibrary("native-lib");
    }

}
