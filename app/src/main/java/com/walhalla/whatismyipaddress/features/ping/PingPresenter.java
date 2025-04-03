package com.walhalla.whatismyipaddress.features.ping;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.preference.PreferenceManager;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import java.util.ArrayList;

public class PingPresenter extends BasePresenter {

    private final PingView view;
    private final SharedPreferences preferences;
    private static final String KEY_IP = "key_ip_mm898_bb";
    private static final String KEY_TIMEOUT = "key_TIMEOUT_mm898_bb";
    private static final String KEY_LIMIT = "KEY_LIMIT_mm898_bb";


    private ArrayList<ViewModel> pingList;

    public PingPresenter(Context context, PingView view, Handler handler) {
        super(handler);
        this.view = view;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void init() {
        String ip = preferences.getString(KEY_IP, "google.com");
        int pingTimeout = preferences.getInt(KEY_TIMEOUT,1000);
        int pingLimit = preferences.getInt(KEY_LIMIT, 5);
        view.init0(ip, pingTimeout, pingLimit);
    }

    public void startPing(String pingIpText, int pingTimeout, int pingLimit) {


        pingList = new ArrayList<>();
        if (view != null) {
            view.showProgress();
            //view.displayScanResult(pingList);//send clear list
        }

        Ping.onAddress(pingIpText)
                .setTimeOutMillis(pingTimeout)
                .setTimes(pingLimit)
                .doPing(new Ping.PingListener() {
                    @Override
                    public void onResult(PingResult pingResult) {

                        StringBuilder sb = new StringBuilder();
                        sb.append("Is Reachable: " + pingResult.isReachable);
                        if (null != pingResult.error) {
                            sb.append("\nIs Error: " + pingResult.error);
                        }
                        sb.append("\nAddress: " + pingResult.ia.toString().replace("/", " "));
                        sb.append("\nTime Taken: " + pingResult.timeTaken * 1000 + " Millisecond");

                        //DLog.d("@@@ww" + pingResult.ia + " " + pingResult.isReachable + " " + pingResult.error + " " + pingResult.timeTaken);

                        if (pingResult.isReachable()) {
                            pingList.add(new TwoColItem(sb.toString(), "", R.color.colorPrimaryDark));
                        } else {
                            pingList.add(new TwoColItem(sb.toString(), "", R.color.error));
                        }
                    }

                    @Override
                    public void onFinished(PingStats pingStats) {
                        DLog.d(pingStats.toString());
                        pingList.add(new SingleItem("RESULT SHOWN BELOW:", R.color.colorPrimaryDark));

                        String tmp = "Is Reachable: " + pingStats.isReachable() + "\nPackets Lost: " + pingStats.getPacketsLost() + "\nAddress: " + pingStats.getAddress().toString().replace("/", "") + "\nAverage Time Taken: " + pingStats.getAverageTimeTakenMillis() + " Millisecond" + "\nMax Time Taken: " + pingStats.getMaxTimeTakenMillis() + " Millisecond" + "\nNo of Pings: " + pingStats.getNoPings();
                        if (pingStats.isReachable()) {
                            pingList.add(new SingleItem(tmp, R.color.gray));
                        } else {
                            pingList.add(new SingleItem(tmp, R.color.error));
                        }

                        if (pingList.size() > 0) {
                           preferences.edit()
                                   .putString(KEY_IP, pingIpText)
                                   .putInt(KEY_TIMEOUT, pingTimeout)
                                   .putInt(KEY_LIMIT, pingLimit)
                                   .apply();


                            final ArrayList<ViewModel> dataModels = new ArrayList<>();
                            dataModels.addAll(pingList);

                            handler.post(() -> {
                                if (view != null) {
                                    view.hideProgress();
                                    view.successResult(dataModels);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        final ArrayList<ViewModel> dataModels = new ArrayList<>();

                        String mm;
                        if (e.getLocalizedMessage() == null) {
                            mm = e.toString();
                        } else {
                            mm = e.getLocalizedMessage();
                        }
                        dataModels.add(new TwoColItem(mm, "", R.color.error));
                        handler.post(() -> {
                            if (view != null) {
                                view.hideProgress();
                                view.successResult(dataModels);
                            }
                        });
                    }
                });
    }

    public interface PingView {

        void showProgress();

        void hideProgress();

        void successResult(ArrayList<ViewModel> dataModels);

        void init0(String pingIpText, int pingTimeout, int pingLimit);
    }
}
