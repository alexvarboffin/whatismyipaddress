package com.walhalla.whatismyipaddress.features.portscanning;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.preference.PreferenceManager;

import com.stealthcopter.networktools.PortScan;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PortScanPresenter extends BasePresenter {

    private final PortScanContract.View view;
    private final SharedPreferences preferences;
    private ArrayList<ViewModel> portData;
    private static final String KEY_IP = "key_ip_mm898";

    public PortScanPresenter(Context context, PortScanContract.View view, Handler handler) {
        super(handler);
        this.view = view;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private final PortScan.PortListener portListener = new PortScan.PortListener() {


        @Override
        public void onResult(int portNo, boolean isOpen) {

            //onResultThread[pool-20-thread-46,5,main]
            //DLog.d("@x@ onResult" + Thread.currentThread());

            if (isOpen) {
                //String result = "Port " + portNo + " is " + (isOpen ? "isOpen" : "closed");
                String result = "Port No:" + portNo + " / Is Open: TRUE";
                portData.add(new TwoColItem(result, "", R.color.colorPrimaryDark));
                handler.post(() -> view.displayScanProgress(result));
            } else {
//                String result = "Port " + portNo + " is " + (isOpen ? "open" : "closed");
//                portData.add(new TwoColItem(result, "", R.color.error));
//                handler.post(() -> view.displayScanProgress(result));
            }
        }

        @Override
        public void onFinished(ArrayList<Integer> openPorts) {
            //onFinishedThread[Thread-19,5,main]
            //DLog.d("@x@ onFinished" + Thread.currentThread());

            handler.post(() -> {
                //UIThread[main,5,main]
                //DLog.d("@x@ UI" + Thread.currentThread());

                final ArrayList<ViewModel> dataModels = new ArrayList<>();
                if (portData.size() > 0) {
                    dataModels.addAll(portData);
                } else {
                    dataModels.add(new TwoColItem(" No Open Port Found - Change Port Numbers", "", R.color.error));
                }
                view.hideProgress();
                view.displayScanResult(dataModels);
            });
        }

    };

    public void startPortScan(String ipText, int timeout, ArrayList<Integer> ports) {
        portData = new ArrayList<>();
        if (view != null) {
            view.showProgress();
            view.displayScanResult(portData);//send clear list
        }
        executor.execute(() -> {
            //Background work here
            try {
                InetAddress address1 = InetAddress.getByName(ipText);
                handler.post(() -> {
                    if (view != null) {
                        String result = "Scanning " + (address1);
                        portData.add(new TwoColItem(result, "", R.color.colorPrimaryDark));
                        view.displayScanResult(portData);
                    }
                });
                PortScan.onAddress(address1)
                        .setTimeOutMillis(timeout)
                        .setPorts(ports)
                        .setMethodTCP()
                        //not work => .setMethodUDP()
                        .doScan(portListener);
                preferences.edit().putString(KEY_IP, ipText).apply();
            } catch (Exception e) {
                DLog.handleException(e);
                if (e instanceof UnknownHostException) {
                    //showError(internet_connectivity_problem0);
                } else {
                    //showError(NO_DATA_FOUND);
                }

                handler.post(() -> {
                    if (view != null) {
                        view.hideProgress();
                        view.handleException(ipText, e);
                    }
                });
            }
        });
    }

    public void init() {
        String ip = preferences.getString(KEY_IP, "8.8.8.8");
        view.init0(ip);
    }
}

