package com.walhalla.whatismyipaddress.ipconverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.preference.PreferenceManager;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class IPAddressConverterTask extends BasePresenter {
    private static final String KEY_IP = "key_ip_mm";

    private final View00 view;
    private final SharedPreferences preferences;

    public IPAddressConverterTask(Context context, View00 view, Handler handler) {
        super(handler);
        this.view = view;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void converter(String ipAddress) {

        executor.execute(() -> {

            ArrayList<ViewModel> data = new ArrayList<>();
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);

                //String decimal = String.valueOf(inetAddress.hashCode());
                String[] octets = ipAddress.split("\\.");
                data.add(new SingleItem("Dotted decimal: " + ipAddress, R.color.colorPrimaryDark));

                long decimal = 0;
                for (int i = 0; i < octets.length; i++) {
                    int octetValue = Integer.parseInt(octets[i]);
                    decimal = (decimal << 8) + octetValue;
                }

                String decimalString = String.valueOf(decimal);
                data.add(new SingleItem("Decimal: " + decimalString, R.color.colorPrimaryDark));

                // Десятковий шістнадцятковий формат
                String hexadecimal = "0x" + Integer.toHexString(inetAddress.hashCode()).toUpperCase();
                data.add(new SingleItem("Hexadecimal: " + hexadecimal, R.color.colorPrimaryDark));

                // Крапковий шістнадцятковий формат
                StringBuilder dottedHex = new StringBuilder();
                for (String octet : octets) {
                    dottedHex.append("0x").append(Integer.toHexString(Integer.parseInt(octet))).append(".");
                }
                dottedHex.deleteCharAt(dottedHex.length() - 1);
                data.add(new SingleItem("Dotted hex: " + dottedHex, R.color.colorPrimaryDark));

                // Октальний формат
                StringBuilder octal = new StringBuilder();
                for (String octet : octets) {
                    octal.append(Integer.toOctalString(Integer.parseInt(octet))).append(".");
                }
                octal.deleteCharAt(octal.length() - 1);
                data.add(new SingleItem("Octal: " + octal, R.color.colorPrimaryDark));

                // Бінарний формат
                StringBuilder binary = new StringBuilder();
                for (String octet : octets) {
                    binary.append(String.format("%8s", Integer.toBinaryString(Integer.parseInt(octet))))
                            .append(".");
                }
                binary.deleteCharAt(binary.length() - 1);
                data.add(new SingleItem("Binary: " + binary, R.color.colorPrimaryDark));

                preferences.edit().putString(KEY_IP, ipAddress).apply();

            } catch (UnknownHostException e) {
                DLog.handleException(e);
            }
            handler.post(() -> {
                view.hideProgress();
                view.displayScanResult(data);
            });
        });
    }

    public void init() {
        String ip = preferences.getString(KEY_IP, "8.8.8.8");
        view.init(ip);
    }


    public interface View00 {
        void showProgress();

        void hideProgress();

        void displayScanProgress(String result);

        void handleException(String ipText, Exception e0);

        void displayScanResult(ArrayList<ViewModel> dataModels);

        void init(String ip);
    }
}
