package com.app.optali;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Created by root on 13/04/17.
 */

public class BluetoothHelper {

    public static String shortUuidFormat = "0000%04X-0000-1000-8000-00805F9B34FB";

    public static UUID sixteenBitUuid(long shortUuid) {
        assert shortUuid >= 0 && shortUuid <= 0xFFFF;
        return UUID.fromString(String.format(shortUuidFormat, shortUuid & 0xFFFF));
    }
}
