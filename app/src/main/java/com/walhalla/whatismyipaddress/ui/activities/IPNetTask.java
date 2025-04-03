package com.walhalla.whatismyipaddress.ui.activities;

import android.content.Context;
import android.os.Handler;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.generated.WhoisApi;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.TPreferences;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IPNetTask extends BasePresenter {


    private final String NO_DATA_FOUND;
    private final View1 view;
    private final TPreferences preferences;

    ArrayList<String> name, values;


    public IPNetTask(Context context, View1 view1, Handler handler) {
        super(handler);
        this.view = view1;
        NO_DATA_FOUND = context.getString(R.string.no_data_found);
        this.preferences = TPreferences.getInstance(context);
    }

    String parameter = "";

    public void netblocks(String ip, String maskT, String limitT, boolean ipChecked, boolean isASNisChecked) {

        view.showProgress();

        parameter = "";

        if (ipChecked) {
            parameter = "&ip=" + ip;
            if (!"".equals(maskT)) {
                parameter = parameter + "&mask=" + maskT;
            }
            if (!"".equals(limitT)) {
                parameter = parameter + "&limit=" + limitT;
            }
        } else if (isASNisChecked) {
            parameter = "&asn=" + ip;
            if (!"".equals(limitT)) {
                parameter = parameter + "&limit=" + limitT;
            }
        } else {
            parameter = "&org[]=" + ip;
            if (!"".equals(limitT)) {
                parameter = parameter + "&limit=" + limitT;
            }
        }


        executor.execute(() -> {
            DLog.d("@xx@" + parameter);
            try {
                OkHttpClient httpClient = NetworkUtils.makeOkhttp();
                String url = WhoisApi.ipNetBlock(parameter);
                DLog.d("@@" + url);
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .build();

                Response response = httpClient.newCall(request).execute();
                String networkResp = response.body().string();
                parseJSONStringToJSONObject(networkResp);

                handler.post(() -> {
                    view.hideProgress();

                    if (name.size() > 0) {
                        final ArrayList<ViewModel> dataModels = new ArrayList<>();
                        for (int i = 0; i < name.size(); i++) {
                            dataModels.add(new TwoColItem(name.get(i), values.get(i), R.color.colorPrimaryDark));
                        }
                        view.displayScanResult(dataModels);


                        preferences.setIPNetTaskKEY_IP(ip);
                        preferences.setIPNetTaskKEY_MASK(maskT);
                    } else {
                        final ArrayList<ViewModel> dataModels = new ArrayList<>();
                        dataModels.add(new TwoColItem(NO_DATA_FOUND, "", R.color.colorPrimaryDark));
                        view.displayScanResult(dataModels);
                    }
                });
            } catch (Exception e) {
                DLog.handleException(e);
                handleException(e);
            }


        });

    }


    private void handleException(Exception e) {
        handler.post(view::hideProgress);
        if (e instanceof UnknownHostException) {
            handler.post(() -> {
                final ArrayList<ViewModel> dataModels = new ArrayList<>();
                dataModels.add(new TwoColItem("UnknownHostException", "", R.color.error));
                view.displayScanResult(dataModels);
            });
        }
    }


    private void parseJSONStringToJSONObject(String networkResp) {
        //DLog.d("@@" + networkResp);

        name = new ArrayList<>();
        values = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(networkResp);
            if (jsonObject.has("ErrorMessage")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("ErrorMessage");
                Iterator<String> keys = jsonObject1.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    name.add(key.toUpperCase());
                    values.add(jsonObject1.getString(key));
                }
            }

            if (jsonObject.has("search")) {
                name.add("Search");
                values.add(jsonObject.getString("search"));
            }

            if (jsonObject.has("result")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                JSONArray jsonArray = jsonObject1.getJSONArray("inetnums");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                    if (i != 0) {
                        name.add("");
                        values.add("");
                    }

                    name.add("InetNums # " + (i + 1));
                    values.add("-------------------");

                    if (jsonObject2.has("inetnum")) {
                        name.add("Inetnum");
                        values.add(jsonObject2.getString("inetnum"));
                    }
                    if (jsonObject2.has("inetnumFirst")) {
                        name.add("Inetnum First");
                        values.add(jsonObject2.getString("inetnumFirst"));
                    }
                    if (jsonObject2.has("inetnumLast")) {
                        name.add("Inetnum Last");
                        values.add(jsonObject2.getString("inetnumLast"));
                    }
                    if (jsonObject2.has("as")) {
                        name.add("AS:");
                        values.add("");
                        JSONObject jsonObject3 = jsonObject2.getJSONObject("as");
                        Iterator<String> keys = jsonObject3.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            name.add(key.toUpperCase());
                            values.add(jsonObject3.getString(key));
                        }
                    }
                    if (jsonObject2.has("netname")) {
                        name.add("Net Name");
                        values.add(jsonObject2.getString("netname"));
                    }
                    if (jsonObject2.has("nethandle")) {
                        name.add("Net Handle");
                        values.add(jsonObject2.getString("nethandle"));
                    }
                    if (jsonObject2.has("description")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("description");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            name.add("Description" + j + ":");
                            values.add(jsonArray1.getString(j));
                        }
                    }
                    if (jsonObject2.has("modified")) {
                        name.add("Modified");
                        values.add(jsonObject2.getString("modified"));
                    }
                    if (jsonObject2.has("country")) {
                        name.add("Country");
                        values.add(jsonObject2.getString("country"));
                    }
                    if (jsonObject2.has("city")) {
                        name.add("City");
                        values.add(jsonObject2.getString("city"));
                    }
                    if (jsonObject2.has("address")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("address");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            name.add("Address" + " " + (j + 1));
                            values.add(jsonArray1.getString(j));
                        }
                    }
                    if (jsonObject2.has("abuseContact")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("abuseContact");
                        name.add("Abuse Contact:");
                        values.add("");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                            Iterator<String> keys = jsonObject3.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (key.equals("address")) {
                                    JSONArray jsonArray2 = jsonObject3.getJSONArray("address");
                                    for (int k = 0; k < jsonArray2.length(); k++) {
                                        name.add("Abuse Contact Address " + (k + 1));
                                        values.add(jsonArray2.getString(k));
                                    }
                                } else {
                                    name.add(key.toUpperCase());
                                    values.add(jsonObject3.getString(key));
                                }
                            }
                        }
                    }
                    if (jsonObject2.has("adminContact")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("adminContact");
                        name.add("Admin Contact:");
                        values.add("");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                            Iterator<String> keys = jsonObject3.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (key.equals("address")) {
                                    JSONArray jsonArray2 = jsonObject3.getJSONArray("address");
                                    for (int k = 0; k < jsonArray2.length(); k++) {
                                        name.add("Admin Contact Address " + (k + 1));
                                        values.add(jsonArray2.getString(k));
                                    }
                                } else {
                                    name.add(key.toUpperCase());
                                    values.add(jsonObject3.getString(key));
                                }
                            }
                        }
                    }
                    if (jsonObject2.has("org")) {
                        name.add("Organisation:");
                        values.add("");
                        JSONObject jsonObject3 = jsonObject2.getJSONObject("org");
                        Iterator<String> keys = jsonObject3.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (key.equals("address")) {
                                JSONArray jsonArray2 = jsonObject3.getJSONArray("address");
                                for (int k = 0; k < jsonArray2.length(); k++) {
                                    name.add("Org Address " + (k + 1));
                                    values.add(jsonArray2.getString(k));
                                }
                            } else {
                                name.add(key.toUpperCase());
                                values.add(jsonObject3.getString(key));
                            }
                        }
                    }
                    if (jsonObject2.has("mntBy")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("mntBy");
                        for (int m = 0; m < jsonArray1.length(); m++) {
                            name.add("MNT BY: " + (m + 1));
                            values.add("");
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(m);
                            Iterator<String> keys = jsonObject3.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                name.add(key.toUpperCase());
                                values.add(jsonObject3.getString(key));
                            }
                        }
                    }
                    if (jsonObject2.has("mntDomains")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("mntDomains");
                        for (int m = 0; m < jsonArray1.length(); m++) {
                            name.add("MNT Domains: " + (m + 1));
                            values.add("");
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(m);
                            Iterator<String> keys = jsonObject3.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                name.add(key.toUpperCase());
                                values.add(jsonObject3.getString(key));
                            }
                        }
                    }
                    if (jsonObject2.has("mntLower")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("mntLower");
                        for (int m = 0; m < jsonArray1.length(); m++) {
                            name.add("MNT LOWER: " + (m + 1));
                            values.add("");
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(m);
                            Iterator<String> keys = jsonObject3.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                name.add(key.toUpperCase());
                                values.add(jsonObject3.getString(key));
                            }
                        }
                    }
                    if (jsonObject2.has("mntRoutes")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("mntRoutes");
                        for (int m = 0; m < jsonArray1.length(); m++) {
                            name.add("MNT ROUTES: " + (m + 1));
                            values.add("");
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(m);
                            Iterator<String> keys = jsonObject3.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                name.add(key.toUpperCase());
                                values.add(jsonObject3.getString(key));
                            }
                        }
                    }
                    if (jsonObject2.has("remarks")) {
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("remarks");
                        for (int m = 0; m < jsonArray1.length(); m++) {
                            name.add("Remarks: " + (m + 1));
                            values.add(jsonArray1.getString(m));
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void init() {
        String ip = preferences.getIPNetTaskKEY_IP();
        String mask = preferences.getIPNetTaskKEY_MASK();
        view.init(ip, mask);
    }

    public interface View1 {
        void showProgress();

        void hideProgress();

        void displayScanResult(ArrayList<ViewModel> dataModels);

        void init(String ip, String mask);
    }
}