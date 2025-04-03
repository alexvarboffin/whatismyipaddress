package com.walhalla.whatismyipaddress.features.cmschecker;

import static com.walhalla.whatismyipaddress.features.checkhost.PingResult.PING_RESULT_CANNOT_RESOLVE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;
import com.walhalla.whatismyipaddress.features.checkhost.PingItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CMSCheckerPresenter extends BasePresenter {


    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.63 Safari/537.31";
    private static final String KEY_RESULT = "result";


    private final String internet_connectivity_problem0;


    // https://whatcms.org/APIEndpoint?key=745aaac9fc2d1acc0e20330469b1db3979be347b5542b3b5b790b42d10cb68cac78c2f&url=
    public static int[] v0 = new int[]{61, 108, 114, 117, 38, 102, 50, 99, 56, 55, 99, 97, 99, 56, 54, 98, 99, 48, 49, 100, 50, 52, 98, 48, 57, 55, 98, 53, 98, 51, 98, 50, 52, 53, 53, 98, 55, 52, 51, 101, 98, 57, 55, 57, 51, 98, 100, 49, 98, 57, 54, 52, 48, 51, 51, 48, 50, 101, 48, 99, 99, 97, 49, 100, 50, 99, 102, 57, 99, 97, 97, 97, 53, 52, 55, 61, 121, 101, 107, 63, 116, 110, 105, 111, 112, 100, 110, 69, 73, 80, 65, 47, 103, 114, 111, 46, 115, 109, 99, 116, 97, 104, 119, 47, 47, 58, 115, 112, 116, 116, 104};

    private static final String KEY_HOST = "key_CheckhostPresenter_ip0";
    private static final int THREAD_COUNT = 4;

    private final String NO_DATA_FOUND;
    private final SharedPreferences preferences;
    List<ViewModel> models = new ArrayList<>();

    private final CheckhostView view;

    String ip;

    private List<String> name, values;
    private final String ERROR_MESSAGE = "ErrorMessage";
    private Timer timer;
    private final OkHttpClient httpClient = NetworkUtils.makeOkhttp();

    public CMSCheckerPresenter(Context context, CheckhostView view, Handler handler) {
        super(handler);
        this.view = view;
        this.NO_DATA_FOUND = context.getString(R.string.no_data_found);
        this.internet_connectivity_problem0 = context.getString(R.string.internet_connectivity_problem);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    enum checktype {
        ping, http, tcp, dns, udp
    }

    public void checkCMS(String site) {

        try {
            view.showProgress();
            models = new ArrayList<>();
            if (timer != null) {
                timer.cancel();
            }

            executor.execute(() -> {
                String request_id = "";
                try {
                    Request request = new Request.Builder()
                            .url(dec0(v0) + site)
                            .header("User-Agent", USER_AGENT)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String networkResp = response.body().string();
                        DLog.d("@@@@@@@@@@" + networkResp);

                        try {
                            JSONObject object = new JSONObject(networkResp);
//                            if (object.has("error")) {
//                                //"limit_exceeded"
//                                showError(object.getString("error"));
//                            } else

                            if (object.has("Success")) {


                                if (object.has(KEY_RESULT)) {
                                    JSONObject res = object.getJSONObject(KEY_RESULT);
                                    SingleItem mm1 = new SingleItem("WebSite : " + site);
                                    models.add(mm1);

                                    if (res.has("name")) {
                                        SingleItem mm2 = new SingleItem("CMS: " + res.getString("name"));
                                        models.add(mm2);
                                    }
                                    if (res.has("version")) {
                                        SingleItem mm3 = new SingleItem("Version: " + res.getString("version"));
                                        models.add(mm3);
                                    }
                                }

                            } else if (object.has("CMS Not Found")) {
                                ViewModel mma = new SingleItem("WebSite : " + site);
                                ViewModel mmb = new SingleItem("CMS : Not Found");
                                models.add(mma);
                                models.add(mmb);

                            } else {
                                ViewModel mm1 = new SingleItem("There Is A Problem");
                                ViewModel mm2 = new SingleItem("1. Checking The Connection");
                                ViewModel mm3 = new SingleItem("2. Enter Website Without HTTP/HTTPs");
                                ViewModel mm4 = new SingleItem("3. Check If Website Working");
                                models.add(mm1);
                                models.add(mm2);
                                models.add(mm3);
                                models.add(mm4);
                            }
                        } catch (Exception e) {
                            showError(e.getLocalizedMessage());
                        }
                    }

                    preferences.edit().putString(KEY_HOST, site).apply();

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
        httpClient.dispatcher().cancelAll();
        httpClient.connectionPool().evictAll();
    }


    //Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public void init() {
        String ip = preferences.getString(KEY_HOST, "wordpress.com");
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