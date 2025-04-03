package com.walhalla.whatismyipaddress.ui.activities.dnslookup;

import android.content.Context;
import android.os.Handler;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.generated.WhoisApi;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DNSLookupTask extends BasePresenter {

    private final String NO_DATA_FOUND;
    //ArrayList<ViewModel> models = new ArrayList<>();

    private final View view;
    String ip;

    private List<String> name, values;
    private final String ERROR_MESSAGE = "ErrorMessage";

    public DNSLookupTask(Context context, DNSLookupTask.View view, Handler handler) {
        super(handler);
        this.view = view;
        NO_DATA_FOUND = context.getString(R.string.no_data_found);
    }


    public void dnsLook(String ip) {
        this.ip = ip;
        view.showProgress();
        executor.execute(() -> {
            DLog.d("@xx@" + ip);
            try {
                final String url = WhoisApi.ipDNSLookup(ip);
                DLog.d("@@" + url);

                OkHttpClient httpClient = NetworkUtils.makeOkhttp();
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .build();
                Response response = httpClient.newCall(request).execute();
                String networkResp = response.body().string();
                parseJSONStringToJSONObject(networkResp);

                handler.post(() -> {
                    view.hideProgress();
                    if (name != null && name.size() > 0) {
                        final ArrayList<ViewModel> dataModels = new ArrayList<>();
                        for (int i = 0; i < name.size(); i++) {
                            dataModels.add(new TwoColItem(name.get(i), values.get(i), R.color.colorPrimaryDark));
                        }
                        view.successResult(dataModels);
                    } else {
                        final ArrayList<ViewModel> dataModels = new ArrayList<>();
                        dataModels.add(new TwoColItem(NO_DATA_FOUND, "", R.color.colorPrimaryDark));
                        view.successResult(dataModels);
                    }
                });

            } catch (Exception e) {
                DLog.handleException(e);
            }
        });
    }


    private void parseJSONStringToJSONObject(String networkResp) {
        name = new ArrayList<>();
        values = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(networkResp);


            if (jsonObject.has(ERROR_MESSAGE)) {
                JSONObject jsonObject1 = jsonObject.getJSONObject(ERROR_MESSAGE);
                Iterator<String> keys = jsonObject1.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    name.add(key.toUpperCase());
                    values.add(jsonObject1.getString(key));
                }
            }

            if (jsonObject.has("DNSData")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("DNSData");

                if (jsonObject1.has("domainName")) {
                    name.add("Domain Name");
                    values.add(jsonObject1.getString("domainName"));
                }

                if (jsonObject1.has("audit")) {
                    name.add("Audit:");
                    values.add("");
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("audit");
                    Iterator<String> keys = jsonObject2.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        name.add(key.toUpperCase());
                        values.add(jsonObject2.getString(key));
                    }
                }
                if (jsonObject1.has("dnsRecords")) {
                    name.add("DNS Records:");
                    values.add("");
                    JSONArray jsonArray = jsonObject1.getJSONArray("dnsRecords");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        name.add("DNS Record no. " + (i + 1));
                        values.add("---------------");
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        Iterator<String> keys = jsonObject2.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (key.equals("strings")) {
                                JSONArray jsonArray1 = jsonObject2.getJSONArray(key);
                                for (int k = 0; k < jsonArray1.length(); k++) {
                                    name.add("");
                                    values.add((String) jsonArray1.get(k));
                                }
                            } else {
                                name.add(key.toUpperCase());
                                values.add(jsonObject2.getString(key));
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            DLog.handleException(ex);
            ex.printStackTrace();
        }
    }

    public interface View {
        void showProgress();

        void hideProgress();

        void successResult(ArrayList<ViewModel> dataModels);
    }
}