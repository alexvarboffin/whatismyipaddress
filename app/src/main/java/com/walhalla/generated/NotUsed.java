package com.walhalla.generated;

import android.annotation.SuppressLint;
import android.net.wifi.WifiInfo;

import com.walhalla.ui.DLog;

public class NotUsed {
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static String getWifiMac(WifiInfo cc) {
        String wifiMac = "";
        try {
            wifiMac = cc.getMacAddress();
            DLog.d("@@@@ wifiMac: " + wifiMac);
        } catch (Exception e) {
            DLog.handleException(e);
        }
        return wifiMac;
    }
}
