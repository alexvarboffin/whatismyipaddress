package com.walhalla.whatismyipaddress.ui.activities.wakeonlan;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

import com.stealthcopter.networktools.ARPInfo;
import com.stealthcopter.networktools.WakeOnLan;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;

public class WakeOnLanAdapter extends BasePresenter {

    private final String NO_DATA_FOUND;
    private final View1 view;

    private ArrayList<String> name;
    private ArrayList<String> values;


    private String macAddress;
    String ip;

    public WakeOnLanAdapter(Context context, View1 view1, Handler handler) {
        super(handler);
        this.view = view1;
        NO_DATA_FOUND = context.getString(R.string.no_data_found);
    }

    public void wakeOnlan(String ipAddress, String macAddress) {

        name = new ArrayList<>();
        values = new ArrayList<>();

        this.ip = ipAddress;
        this.macAddress = macAddress;


        executor.execute(() -> {
            try {

//                String aa = getMACFromIPAddress(ipAddress);
//                appendResultsText("@@@@@@@@@@: " + aa);

                appendResultsText("IP address: " + ip);
                // Get mac address from IP (using arp cache)
                //String macAddress = ARPInfo.getMACFromIPAddress(ip);
//                if (macAddress == null) {
//                    appendResultsText("Could not fromIPAddress MAC address, cannot send WOL packet without it.");
//                    return null;
//                }




                appendResultsText("MAC address: " + macAddress);
                String mm = ARPInfo.getIPAddressFromMAC(macAddress);
                if (mm != null) {
                    appendResultsText("IP address2: " + mm);
                }

                // Send Wake on lan packed to ip/mac
                try {
                    WakeOnLan.sendWakeOnLan(ip, macAddress);
                    appendResultsText("WOL Packet sent");
                } catch (IOException e) {
                    appendResultsText(e.getMessage());
                    DLog.handleException(e);
                } finally {
                }
            } catch (Exception e) {
                DLog.handleException(e);
            }

            handler.post(() -> {
                view.hideProgress();

                if (!name.isEmpty()) {
                    final ArrayList<ViewModel> dataModels = new ArrayList<>();
                    for (int i = 0; i < name.size(); i++) {
                        dataModels.add(new TwoColItem(name.get(i), values.get(i), R.color.colorPrimaryDark));
                    }
                    view.displayScanResult(dataModels);
                } else {
                    final ArrayList<ViewModel> dataModels = new ArrayList<>();
                    dataModels.add(new TwoColItem(NO_DATA_FOUND, "", R.color.colorPrimaryDark));
                    view.displayScanResult(dataModels);
                }
            });
        });
    }

    public static String getMACFromIPAddress(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

            if (networkInterface != null) {
                byte[] mac = networkInterface.getHardwareAddress();

                if (mac != null) {
                    StringBuilder sb = new StringBuilder();

                    for (byte b : mac) {
                        sb.append(String.format("%02X:", b));
                    }

                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    return sb.toString();
                }
            }
        } catch (IOException e) {
            DLog.handleException(e);
        }
        return null;
    }

    private void appendResultsText(String s) {
        name.add("");
        values.add(s);
    }

    public interface View1 {
        void showProgress();

        void hideProgress();

        void displayScanResult(ArrayList<ViewModel> dataModels);
    }
}