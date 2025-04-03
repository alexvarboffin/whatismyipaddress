package com.walhalla.whatismyipaddress.features.checkhost;

import static com.walhalla.whatismyipaddress.features.checkhost.PingResult.PING_RESULT_CANNOT_RESOLVE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.chekhostitem.CheckHostItem;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckhostPresenter extends BasePresenter {


    private final String internet_connectivity_problem0;
    //  https://check-host.net
    int[] aaa = new int[]{
            116, 101, 110, 46, 116, 115, 111, 104, 45, 107, 99, 101, 104, 99, 47, 47, 58, 115, 112, 116, 116, 104
    };


    private static final String KEY_IP = "key_cms_r_ip0";
    private static final int THREAD_COUNT = 4;

    private final String NO_DATA_FOUND;
    private final SharedPreferences preferences;
    List<CheckHostItem> models = new ArrayList<>();

    private final CheckhostView view;

    String ip;

    private List<String> name, values;
    private final String ERROR_MESSAGE = "ErrorMessage";
    private Timer timer;
    private OkHttpClient httpClient;


    public CheckhostPresenter(Context context, CheckhostView view, Handler handler) {
        super(handler);
        this.view = view;
        this.NO_DATA_FOUND = context.getString(R.string.no_data_found);
        this.internet_connectivity_problem0 = context.getString(R.string.internet_connectivity_problem);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    enum checktype {
        ping, http, tcp, dns, udp
    }

    public void checkhost(String HOSTNAME) {

        try {
            view.showProgress();
            models = new ArrayList<>();
            if (timer != null) {
                timer.cancel();
            }

            executor.execute(() -> {
                String request_id = "";
                try {
                    //tcp host=smtp://gmail.com
                    //ping http host=check-host.net
                    //tcp dns host=https://check-host.net
                    //URLEncoder.encode(ip, "UTF-8")

                    //&node=us1.node.check-host.net&node=ch1.node.check-host.net'
                    String CHECKTYPE = "ping";
                    //int MAX_NODES = 50;
                    int MAX_NODES = 10;

                    httpClient = NetworkUtils.makeOkhttp();
                    String url = dec0(aaa) + "/check-"
                            + CHECKTYPE + "?host="
                            + HOSTNAME + "&max_nodes="
                            + MAX_NODES;
                    //+ "&node=<NODE>"

                    DLog.d("@@" + url);

                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Accept", "application/json")
                            .method("GET", null)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String networkResp = response.body().string();
                        DLog.d("@@@@@@@@@@" + networkResp);

                        try {
                            JSONObject object = new JSONObject(networkResp);
                            if (object.has("error")) {
                                //"limit_exceeded"
                                showError(object.getString("error"));
                            } else {
                                //            String ok = json.getString("ok");
                                request_id = object.getString("request_id");
//            String permanent_link = json.getString("permanent_link");
//            DLog.d(permanent_link);

                                JSONObject nodes = object.getJSONObject("nodes");
                                //DLog.d(gson.toJson(nodes));
                                //ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

                                Iterator<String> serverIterator = nodes.keys();
                                while (serverIterator.hasNext()) {
                                    String serverKey = serverIterator.next();
                                    if (nodes.isNull(serverKey)) {
                                        DLog.d("Пропускаем сервер " + serverKey);
                                        continue;
                                    }
                                    JSONArray serverData = nodes.getJSONArray(serverKey);
                                    String serverName = serverData.getString(0);
                                    String serverCountry = serverData.getString(1);
                                    String serverCity = serverData.getString(2);
                                    String serverIP = serverData.getString(3);
                                    String serverAS = serverData.getString(4);

                                    CheckHostItem item = new CheckHostItem(serverKey, R.color.colorPrimaryDark);
                                    item.serverCountry = serverCountry;
                                    item.serverCity = serverCity;
                                    item.serverIP = serverIP;
                                    item.serverAS = serverAS;
                                    item.countryCode = serverName;
                                    //executor.execute(new ServerRequestRunnable(serverName, serverIP));
                                    models.add(item);
                                }
                                //executor.shutdown();
                            }
                        } catch (Exception e) {
                            showError(e.getLocalizedMessage());
                        }
                    }

                    preferences.edit().putString(KEY_IP, HOSTNAME).apply();

                } catch (Exception e) {
                    //UnknownHostException
                    DLog.handleException(e);
                    if (e instanceof UnknownHostException) {
                        showError(internet_connectivity_problem0);
                    } else {
                        showError(NO_DATA_FOUND);
                    }
                }

                if (!models.isEmpty()) {
                    handler.post(() -> {
                        view.hideProgress();
                        List<ViewModel> aa = new ArrayList<>(models);
                        view.successResult(aa);
                    });
                    if (!TextUtils.isEmpty(request_id)) {
                        timer = new Timer();
                        String finalRequest_id = request_id;
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                checkResult(finalRequest_id);
                            }
                        };
                        // Запускаем задачу каждую секунду 1000
                        timer.scheduleAtFixedRate(timerTask, 0, 500);
                    }
                }

            });
        } catch (Exception e) {
            DLog.d("<aaa> " + e.getLocalizedMessage());
        }
    }

    public String dec0(int[] intArray) {
        char[] strArray = new char[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            strArray[i] = (char) intArray[i];
        }
        return new StringBuilder((String.valueOf(strArray))).reverse().toString();
    }

    private void showError(String err) {
        List<ViewModel> aa = new ArrayList<>();
        aa.add(new SingleItem(err, R.color.colorPrimaryDark));
        handler.post(() -> {
            view.hideProgress();
            view.successResult(aa);
        });
    }

    public void stopSendingRequests() {
        //DLog.d("Отменяем задачу и закрываем OkHttpClient");
        timer.cancel();
        if (httpClient != null) {
            httpClient.dispatcher().cancelAll();
            httpClient.connectionPool().evictAll();
        }
    }

    private void checkResult(String REQUEST_ID) {
        try {
            httpClient = NetworkUtils.makeOkhttp();
            String url = dec0(aaa) + "/check-result/" + REQUEST_ID;
            //DLog.d("@url@" + url);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .method("GET", null)
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String networkResp = response.body().string();
                DLog.d("[*]" + networkResp);
                try {
                    JSONObject nodes = new JSONObject(networkResp);
                    if (nodes.has("error")) {
                        //"limit_exceeded"
                        showError(nodes.getString("error"));
                    } else {
                        parseJSONStringToJSONObject1(nodes);
                    }
                } catch (Exception e) {
                    showError(e.getLocalizedMessage());
                }
            }
        } catch (Exception e) {
            DLog.d("<00> " + e.getClass().getSimpleName() + " " + e.getLocalizedMessage());
        }
    }

    private void parseJSONStringToJSONObject1(JSONObject nodes) {
        boolean notTerminate = false;
        JSONArray pingData0 = null;
        boolean needUpdate = false;
        try {
            //DLog.d(gson.toJson(nodes));
            Iterator<String> serverIterator = nodes.keys();

            while (serverIterator.hasNext()) {
                String serverKey = serverIterator.next();
                if (nodes.isNull(serverKey)) {
                    DLog.d("Пропускаем, сервер еще сканирует" + serverKey);
                    notTerminate = true;
                    continue;
                }
                JSONArray serverData = nodes.getJSONArray(serverKey);
                List<PingItem> items = new ArrayList<>();
                pingData0 = serverData.getJSONArray(0);
                for (int i = 0; i < pingData0.length(); i++) {

                    PingItem item = null;

                    if (pingData0.isNull(i)) {
                        //cannot resolve host
                        //DLog.d("@@@@@@@@@@@@@@@ cannot resolve host " + pingData0.get(i));
                        //не смог преобразовать доменное имя.
                        //[[null]],
                        item = new PingItem(PING_RESULT_CANNOT_RESOLVE, null);
                    } else {
                        JSONArray item0 = pingData0.getJSONArray(i);
                        if (item0.length() == 2) {
                            item = new PingItem(item0.getString(0), item0.getDouble(1));

                        } else if (item0.length() == 3) {
                            item = new PingItem(item0.getString(0), item0.getDouble(1), item0.getString(2));
                        }
                    }

                    if (item != null) {
                        items.add(item);
                    }
                }

                int res = findAndUpdateElement(serverKey, items);
                if (res > -1) {
                    needUpdate = true;
                }
            }

        } catch (JSONException e) {
            DLog.d(e.getLocalizedMessage() + " " + pingData0);
        }

        if (needUpdate) {
            handler.post(() -> {
                List<ViewModel> aa = new ArrayList<>(models);
                view.successResult(aa);
            });
        }

        if (!notTerminate) {
            stopSendingRequests();
        }
    }

    private int findAndUpdateElement(String serverKey, List<PingItem> items) {
        int index = -1;
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).getServerKey().equals(serverKey)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            CheckHostItem item = models.get(index);
            item.items = items;
        }
        return index;
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public void init() {
        String ip = preferences.getString(KEY_IP, "8.8.8.8");
        view.init(ip);
    }

    // Presenter implementation


    public interface CheckhostView {

        void showProgress();

        void hideProgress();

        void successResult(List<ViewModel> dataModels);

        void init(String ip);
    }
}