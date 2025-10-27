package com.walhalla.whatismyipaddress.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import com.walhalla.boilerplate.domain.executor.impl.BackgroundExecutor;
import com.walhalla.boilerplate.threading.MainThreadImpl;
import com.walhalla.netdiscover.Connectivity;
import com.walhalla.netdiscover.Graph;
import com.walhalla.whatismyipaddress.Helpers0;
import com.walhalla.whatismyipaddress.adapter.ListAdapter;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.netdiscover.NonScrollListView;
import com.walhalla.ui.DLog;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

import com.hsalf.smileyrating.SmileyRating;
import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.NetworkUtils;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.databinding.FragmentDashboardBinding;
import com.walhalla.whatismyipaddress.interactor.PublicIpInteractor;
import com.walhalla.whatismyipaddress.ui.activities.SpeedTest;

public class DashboardFragment extends BaseFragment {

    private SmileyRating rating;
    private TextView signalStrengthTxt, signalPercentage, signalStrength;
    private WifiManager wifiManager;
    private Timer timerObj;

    private RelativeLayout layout;

    private final ArrayList<ViewModel> names = new ArrayList<>();


    private TextView linkSpeed;
    private TextView publicIP;

    private TextView rx, tx;

    private final Handler mHandler = new Handler();
    private Double startRx = 0.0;
    private Double startTx = 0.0;
    private String ip = "";

    private RelativeLayout upSpeedView, downSpeedView;
    private String value_yes;
    private String value_no;

    private LinearLayoutManager layoutManager;
    NonScrollListView listView;
    private ComV19 comv19;
    private Drawable circleBg;
    private FragmentDashboardBinding binding;

    //    private InterstitialAd mInterstitialAd;

    private static byte[] reverse(byte[] array) {

        if (null == array) {
            return array;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }

        return array;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        circleBg = comv19.getDrawable(getContext(), R.drawable.circle_bg);
        layoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        value_yes = getString(R.string.value_yes);
        value_no = getString(R.string.value_no);

        DLog.d("[AAAA] " + this.hashCode());

//        AdView mAdView = root.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        rating = root.findViewById(R.id.smileRating);
        signalStrengthTxt = root.findViewById(R.id.signalStrengthTxt);
        signalPercentage = root.findViewById(R.id.signalPercentage);
        signalStrength = root.findViewById(R.id.signalStrength);
        listView = root.findViewById(R.id.listView);
        layout = root.findViewById(R.id.layout);
        linkSpeed = root.findViewById(R.id.linkValue);
        publicIP = root.findViewById(R.id.publicIp);
        rx = root.findViewById(R.id.rx);
        tx = root.findViewById(R.id.tx);
        upSpeedView = root.findViewById(R.id.packetsTxView);
        downSpeedView = root.findViewById(R.id.packetsRxView);

        publicIP.setOnClickListener(view -> {
            String value = publicIP.getText().toString();
            callback.shareText(value);

//            Snackbar snackbar = Snackbar.make(layout, "Copy \"" +  + "\" to Clipboard?", Snackbar.LENGTH_LONG).setAction("Copy",
//                    view1 -> {
//                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                if (clipboard != null) {
//                    ClipData clip = ClipData.newPlainText("IP Tools", publicIP.getText().toString());
//                    clipboard.setPrimaryClip(clip);
//                    Toasty.custom(getContext(), "copied to clipboard".toUpperCase(), comv19.getDrawable(getContext(), R.drawable.ic_info), ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
//                }
//            });
//            snackbar.show();
        });

        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            PublicIpInteractor publicIp = new PublicIpInteractor(BackgroundExecutor.getInstance(), MainThreadImpl.getInstance(), new PublicIpInteractor.Callback() {
                @Override
                public void onMessageRetrieved(String ip0) {
                    DashboardFragment.this.ip = ip0;
                    DashboardFragment.this.publicIP.setText(ip);
                }

                @Override
                public void onRetrievalFailed(String error) {

                }
            });
            publicIp.execute();
        }

        loadListView();
        callTimerTask(getContext());
        Helpers0.hideKeyboard(getActivity());

        startRx = TrafficStats.getTotalRxBytes() / 1024.0;
        startTx = TrafficStats.getTotalTxBytes() / 1024.0;

        if (startRx == TrafficStats.UNSUPPORTED || startTx == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 2000);
        }

        root.findViewById(R.id.speedTest).setOnClickListener(view -> startActivity(new Intent(getContext(), SpeedTest.class)));

        //upSpeedView.setBackgroundColor(Color.GREEN);
        upSpeedView.setOnClickListener(view -> startActivity(new Intent(getContext(), Graph.class)));
        //downSpeedView.setBackgroundColor(Color.GREEN);
        downSpeedView.setOnClickListener(view -> startActivity(new Intent(getContext(), Graph.class)));

//        mInterstitialAd = new InterstitialAd(getContext());
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() { if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    } else {
//                        Log.d("TAG", "The interstitial wasn't loaded yet.");
//                    }
//                    }
//                });
//            }
//        }, 6000);
    }


    private final Runnable mRunnable = new Runnable() {
        public void run() {
            Double rxBytes = ((TrafficStats.getTotalRxBytes() / 1024.0) - startRx);
            startRx = startRx + rxBytes;
            rx.setText((String.format(Locale.CANADA, "%.2f", rxBytes)) + " Kbps");
            Double txBytes = ((TrafficStats.getTotalTxBytes() / 1024.0) - startTx);
            startTx = startTx + txBytes;
            tx.setText((String.format(Locale.CANADA, "%.2f", txBytes)) + " Kbps");
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private void loadListView() {

        if (wifiManager != null) {
            try {
                if (Connectivity.isConnectedWifi(getContext())) {

                    WifiInfo wifiinfo = wifiManager.getConnectionInfo();
                    byte[] myIPAddress = BigInteger.valueOf(wifiinfo.getIpAddress()).toByteArray();
                    InetAddress myInetIP = InetAddress.getByAddress(reverse(myIPAddress));

                    names.add(new TwoColItem("Local IP ", String.valueOf(myInetIP.getHostAddress())));
                    names.add(new TwoColItem("Mac Address ", getMacAddr()));
                    names.add(new TwoColItem("Gateway", getGateway()));
                    names.add(new TwoColItem("Subnet Mask", getSubnetMask()));
                    if (getDNS() != null) {
                        ArrayList<String> dns = getDNS();
                        for (int i = 0; i < dns.size(); i++) {
                            names.add(new TwoColItem("DNS ADDRESS " + i, intToIP(Integer.parseInt(dns.get(i)))));
                        }
                    }
                    names.add(new TwoColItem("Broadcast Address", getBroadcastAddress()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        names.add(new TwoColItem("Frequency", (wifiManager.getConnectionInfo().getFrequency()) + "MHz"));
                        names.add(new TwoColItem("Channel", String.valueOf(getChannelFromFrequency(wifiManager.getConnectionInfo().getFrequency()))));
                        names.add(new TwoColItem("Server Address", intToIP(wifiManager.getDhcpInfo().serverAddress)));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (wifiManager.is5GHzBandSupported()) {
                            names.add(new TwoColItem("Is 5 Ghz Supported", value_yes));
                        } else {
                            names.add(new TwoColItem("Is 5 Ghz Supported", value_no));
                        }
                    }
                    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                        names.add(new TwoColItem(getString(R.string.dashboard_item_title_wifi_enabled), value_yes));
                    } else {
                        names.add(new TwoColItem(getString(R.string.dashboard_item_title_wifi_enabled), value_no));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        names.add(new TwoColItem(getString(R.string.dashboard_item_title_p2p_support), (wifiManager.isP2pSupported()) ? value_yes : value_no));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (wifiManager.isTdlsSupported()) {
                            names.add(new TwoColItem(getString(R.string.dashboard_item_title_tdls_support), value_yes));
                        } else {
                            names.add(new TwoColItem(getString(R.string.dashboard_item_title_tdls_support), value_no));
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String value = (Integer.parseInt(String.valueOf(wifiManager.getDhcpInfo().leaseDuration)) / 60) + " Min";
                        names.add(new TwoColItem("Lease Time", value));
                    }
                } else {
                    names.add(new TwoColItem("IP Address", getMobileIPAddress()));

                    if (getContext() != null) {
                        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
                        if (manager != null) {
                            String carrierName = manager.getNetworkOperatorName();
                            names.add(new TwoColItem("Carrier Name", carrierName));
                        }
                    }
                }
            } catch (IOException e) {
                DLog.handleException(e);
            }
        }

        if (names.size() > 0) {
            ListAdapter adapter = new ListAdapter(names, getContext(), "Dashboard");
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(adapter);
            adapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
                @Override
                public void onListItemClick(ViewModel dataModel) {
                    if (dataModel instanceof TwoColItem) {
                        String value = ((TwoColItem) dataModel).value;
                        Snackbar snackbar = Snackbar.make(layout, "Copy \"" + value + "\" to Clipboard?", Snackbar.LENGTH_LONG).setAction("Copy", view1 -> {
                            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            if (clipboard != null) {
                                ClipData clip = ClipData.newPlainText("IP Tools", value);
                                clipboard.setPrimaryClip(clip);
                                Toasty.custom(getContext(), "copied to clipboard".toUpperCase(), comv19.getDrawable(getContext(), R.drawable.ic_info), ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                            }
                        });
                        snackbar.show();
                    }
                }

                @Override
                public void copyToBuffer(String commonName) {

                }
            });
        }
    }

    /**
     * LTE or WiFi
     */
    private String speedOfWifi() {
        if (Connectivity.isConnectedMobile(getContext())) {
            TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("@@@", "@@@ no 999999");
                }
                int aaa = TelephonyManager.NETWORK_TYPE_UNKNOWN;
                try {
                    aaa = manager.getNetworkType();// NETWORK_TYPE_LTE 13 not_crash, but why?
                    //aaa = manager.getDataNetworkType();
                    DLog.d("####>>> " + aaa + "");
                } catch (Exception e) {
                    DLog.d("#### " + e.getMessage() + "");
                }
                switch (aaa) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return "1xRTT";
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return "CDMA";
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return "EDGE / 0.3Mbps Max Speed";
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return "eHRPD";
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return "EVDO rev. 0";
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return "EVDO rev. A";
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return "EVDO rev. B";
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return "GPRS / 0.1 Mbps Max Speed";
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return "HSDPA / 3.6 Mbps Max Speed";
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return "HSPA / 21 Mbps Max Speed";
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return "HSPA+ / 42.2 Mbps Max Speed";
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return "HSUPA / 5.76 Mbps Max Speed";
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return "iDen";
                    case TelephonyManager.NETWORK_TYPE_LTE://13
                        return "LTE / 12 Mbps Max Speed";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return "UMTS";
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        return "Unknown";
                }
            }
        } else {
            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                Integer speed = wifiInfo.getLinkSpeed();
                return (speed) + " Mbps";
            }
        }

        return "NOT AVAILABLE";
    }

    private String getGateway() {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        int gatewayway = dhcp.gateway;
        gatewayway = (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) ? Integer.reverseBytes(gatewayway) : gatewayway;
        byte[] ipAddressByte = BigInteger.valueOf(gatewayway).toByteArray();
        try {
            return String.valueOf(InetAddress.getByAddress(ipAddressByte)).replace("/", "");
        } catch (UnknownHostException e) {
            return "0";
        }
    }

    private String getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (broadcast >> (k * 8));
        return String.valueOf(InetAddress.getByAddress(quads)).replace("/", "");
    }

    private String getSubnetMask() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifiManager.getDhcpInfo();

        String mask = intToIP(dhcp.netmask);

        return mask;
    }

    private ArrayList<String> getDNS() {
        try {
            WifiManager mWifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
            int dns1 = dhcpInfo.dns1;
            int dns2 = dhcpInfo.dns2;

            ArrayList<String> dnsList = new ArrayList<>();
            dnsList.add(String.valueOf(dns1));
            dnsList.add(String.valueOf(dns2));

            return dnsList;

        } catch (Exception ex) {
            DLog.handleException(ex);
        }
        return null;
    }

    private static String intToIP(int ipAddress) {
        String ret = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        return ret;
    }

    private static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            DLog.handleException(ex);
        }
        return "02:00:00:00:00:00";
    }


    private final static ArrayList<Integer> channelsFrequency = new ArrayList<Integer>(Arrays.asList(0, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462, 2467, 2472, 2484));

    public static int getChannelFromFrequency(int frequency) {
        return channelsFrequency.indexOf(Integer.valueOf(frequency));
    }

    private int getWifiStrengthPercentage() {
        try {
//            int mobileRssi = 0;
            int level = 0;
            int percentage = 0;

//            if (Connectivity.isConnectedMobile(getContext())){
//                TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        Toasty.custom(getContext(), "GIVE LOCATION PERMISSION IN SETTINGS".toUpperCase(), Com19.getDrawable(getContext(),
//                                R.drawable.ic_cancel), ContextCompat.getColor(getContext(), R.color.error),
//                                ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
//                    } else {
//                        CellInfoGsm cellinfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
//                        CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
//                        mobileRssi = cellSignalStrengthGsm.getDbm();
//                    }
//                } else {
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                        CellInfoGsm cellinfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
//                        CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
//                        mobileRssi = cellSignalStrengthGsm.getDbm();
//                    }
//                }
//
//            }

            int rssi = wifiManager.getConnectionInfo().getRssi();

            if (Connectivity.isConnectedWifi(getContext())) {
                signalStrengthTxt.setText(wifiManager.getConnectionInfo().getSSID());
                level = WifiManager.calculateSignalLevel(rssi, 10);
                percentage = (int) ((level / 10.0) * 100);
            }
//            else {
//                signalStrengthTxt.setText(wifiManager.getConnectionInfo().getSSID());
//                level = WifiManager.calculateSignalLevel(mobileRssi, 10);
//                percentage = (int) ((level / 10.0) * 100);
//                linkSpeed.setText(String.valueOf(mobileRssi));
//            }


            if (rssi <= 0 && rssi >= -50) {
                rating.setRating(SmileyRating.Type.GREAT, true);
            } else if (rssi < -50 && rssi >= -70) {
                rating.setRating(SmileyRating.Type.GOOD, true);
            } else if (rssi < -70 && rssi >= -80) {
                rating.setRating(SmileyRating.Type.OKAY, true);
            } else if (rssi < -80 && rssi >= -100) {
                rating.setRating(SmileyRating.Type.BAD, true);
            } else {
                rating.setRating(SmileyRating.Type.TERRIBLE, true);
            }

            return percentage;

        } catch (Exception e) {
            return 0;
        }
    }

    private void callTimerTask(Context context) {
        int color_wifi = ContextCompat.getColor(context, R.color.smile_wifi);
        int not_connected_to_wifi = ContextCompat.getColor(context, R.color.colorAccent);
        Drawable bg = comv19.getDrawable(getContext(), R.drawable.circle_bg_accent);

        timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            final int percentage = getWifiStrengthPercentage();
                            if (percentage == 0) {
                                signalStrengthTxt.setText(getResources().getString(R.string.dashboard_not_wifi));
                                signalPercentage.setText("0");
                                signalPercentage.setBackground(bg);
                                signalStrength.setBackground(bg);
                                signalStrength.setText("0");
                                linkSpeed.setText(speedOfWifi());
                                configureRatingBar(not_connected_to_wifi);

                                // TODO Add Listener for Wifi/Mobile Data Toggle. So list view not has to update in each cycle.
                                names.clear();
                                //values.clear();
                                loadListView();
                            } else {
                                signalPercentage.setBackground(circleBg);
                                signalStrength.setBackground(circleBg);
                                signalStrength.setText((wifiManager.getConnectionInfo().getRssi() + " "));
                                signalPercentage.setText((percentage) + "%");
                                linkSpeed.setText(speedOfWifi());
                                configureRatingBar(color_wifi);

                                // TODO Add Listener for Wifi/Mobile Data Toggle. So list view not has to update in each cycle.

                                names.clear();
                                //values.clear();
                                loadListView();
                            }
                        }
                    });
                }
            }
        };
        timerObj.schedule(timerTaskObj, 0, 4000);
    }

    private static String getMobileIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private void configureRatingBar(int color) {

        int active_element_color = Color.parseColor("#353234");

        rating.setFaceColor(SmileyRating.Type.GREAT, active_element_color);
        rating.setFaceBackgroundColor(SmileyRating.Type.GREAT, color);

        rating.setFaceColor(SmileyRating.Type.GOOD, active_element_color);
        rating.setFaceBackgroundColor(SmileyRating.Type.GOOD, color);

        rating.setFaceColor(SmileyRating.Type.OKAY, active_element_color);
        rating.setFaceBackgroundColor(SmileyRating.Type.OKAY, color);

        rating.setFaceColor(SmileyRating.Type.BAD, active_element_color);
        rating.setFaceBackgroundColor(SmileyRating.Type.BAD, color);

        rating.setFaceColor(SmileyRating.Type.TERRIBLE, active_element_color);
        rating.setFaceBackgroundColor(SmileyRating.Type.TERRIBLE, color);

        rating.disallowSelection(true);

    }

    @Override
    public void onPause() {
        timerObj.cancel();
        timerObj.purge();
        super.onPause();
    }

    @Override
    public void onStop() {
        timerObj.cancel();
        timerObj.purge();
        super.onStop();
    }

    @Override
    public void onResume() {
        callTimerTask(getContext());
        super.onResume();
    }

}