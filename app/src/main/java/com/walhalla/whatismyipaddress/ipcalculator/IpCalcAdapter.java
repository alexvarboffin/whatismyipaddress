package com.walhalla.whatismyipaddress.ipcalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.preference.PreferenceManager;

import com.stealthcopter.networktools.ARPInfo;
import com.stealthcopter.networktools.WakeOnLan;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class IpCalcAdapter extends BasePresenter {

    private static final String KEY_MASK = "key_mask";
    private static final String KEY_IP = "key_ip";

    private final String NO_DATA_FOUND;
    private final View1 view;

    private ArrayList<String> name;
    private ArrayList<String> values;
    private final SharedPreferences preferences;


    String ip;

    public IpCalcAdapter(Context context, View1 view1, Handler handler) {
        super(handler);
        this.view = view1;
        NO_DATA_FOUND = context.getString(R.string.no_data_found);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void wakeOnlan(Context context, String ipAddress, String subnetMask) {

        name = new ArrayList<>();
        values = new ArrayList<>();

        this.ip = ipAddress;

        executor.execute(() -> {

            try {
                InetAddress ip = InetAddress.getByName(ipAddress);
                InetAddress subnet = InetAddress.getByName(subnetMask);

                String networkAddress = getNetworkAddress(ip, subnet);
                String broadcastAddress = getBroadcastAddress(ip, subnet);
                int prefixLength = getPrefixLength(subnet);
                String wildcardMask = getWildcardMask(subnet);
                String cidr = getCidr(subnet);
                int availableHosts = getAvailableHosts(subnet);

                appendResultsText(context.getString(R.string.ip_address), ip.getHostAddress());
                appendResultsText(context.getString(R.string.subnet_mask), subnet.getHostAddress());

                appendResultsText(context.getString(R.string.network_address), networkAddress);
                appendResultsText(context.getString(R.string.broadcast_address), broadcastAddress);

                appendResultsText(context.getString(R.string.prefix_length), "" + prefixLength);
                appendResultsText(context.getString(R.string.wildcard_mask), wildcardMask);
                appendResultsText(context.getString(R.string.cidr), cidr);
                appendResultsText(context.getString(R.string.available_hosts), "" + availableHosts);


//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");
//                appendResultsText("@@@@@@@@@");

                preferences.edit().putString(KEY_IP, ipAddress).putString(KEY_MASK, subnetMask).apply();

            } catch (UnknownHostException e) {
                DLog.handleException(e);
            }

            handler.post(() -> {
                view.hideProgress();

                if (name.size() > 0) {
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


    private void appendResultsText(String s) {
        name.add("");
        values.add(s);
    }

    private void appendResultsText(String s1, String s2) {
        name.add(s1);
        values.add(s2);
    }

    private String getNetworkAddress(InetAddress ip, InetAddress subnet) {
        byte[] ipBytes = ip.getAddress();
        byte[] subnetBytes = subnet.getAddress();
        byte[] networkBytes = new byte[ipBytes.length];

        for (int i = 0; i < ipBytes.length; i++) {
            networkBytes[i] = (byte) (ipBytes[i] & subnetBytes[i]);
        }
        try {
            return InetAddress.getByAddress(networkBytes).getHostAddress();
        } catch (UnknownHostException e) {
            DLog.handleException(e);
        }
        return "";
    }

    private String getBroadcastAddress(InetAddress ip, InetAddress subnet) {
        byte[] ipBytes = ip.getAddress();
        byte[] subnetBytes = subnet.getAddress();
        byte[] broadcastBytes = new byte[ipBytes.length];

        for (int i = 0; i < ipBytes.length; i++) {
            broadcastBytes[i] = (byte) (ipBytes[i] | ~subnetBytes[i]);
        }

        try {
            InetAddress broadcastAddress = InetAddress.getByAddress(broadcastBytes);
            return broadcastAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getPrefixLength(InetAddress subnet) {
        byte[] subnetBytes = subnet.getAddress();
        int prefixLength = 0;

        for (byte subnetByte : subnetBytes) {
            for (int i = 7; i >= 0; i--) {
                if ((subnetByte & (1 << i)) == 0) {
                    prefixLength++;
                } else {
                    break;
                }
            }
        }

        return prefixLength;
    }

    private String getWildcardMask(InetAddress subnet) {
        byte[] subnetBytes = subnet.getAddress();
        byte[] wildcardBytes = new byte[subnetBytes.length];

        for (int i = 0; i < subnetBytes.length; i++) {
            wildcardBytes[i] = (byte) ~subnetBytes[i];
        }

        try {
            InetAddress wildcardMask = InetAddress.getByAddress(wildcardBytes);
            return wildcardMask.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getCidr(InetAddress subnet) {
        int prefixLength = getPrefixLength(subnet);
        return "/" + prefixLength;
    }


    private int getAvailableHosts(InetAddress subnet) {
        int prefixLength = getPrefixLength(subnet);
        int bitsRemaining = 32 - prefixLength;
        return (int) Math.pow(2, bitsRemaining) - 2;
    }

    public void init() {
        String ip = preferences.getString(KEY_IP, "127.0.0.1");
        String mask = preferences.getString(KEY_MASK, "255.255.0.0");
        view.init(ip, mask);
    }

    public interface View1 {
        void showProgress();

        void hideProgress();

        void displayScanResult(ArrayList<ViewModel> dataModels);

        void init(String ip, String mask);
    }
}