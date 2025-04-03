package com.walhalla.whatismyipaddress.features.subdomain;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.adapter.cert.Certificate;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.TPreferences;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.singlcol.SingleItem;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.features.base.BasePresenter;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class SubdomainPresenter extends BasePresenter implements SubdomainContract.Presenter {


    private static final String AA_MM = "https://crt.sh/?q=";
    private final SubdomainContract.View view;
    private final TPreferences preferences;

    ArrayList<ViewModel> subdomains;

    public SubdomainPresenter(Context context, Handler handler, SubdomainContract.View view) {
        super(handler);
        this.view = view;
        this.preferences = TPreferences.getInstance(context);
        //demotest();
    }

//    private void demotest() {
//        subdomains=new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Certificate s = new Certificate();
//            s.setCommonName("glass.ext.google.com");
//            s.setIssuerName("C=US, O=Google Inc, CN=Google Internet Authority");
//            s.setNameValue("*.glass.ext.google.com\nglass.ext.google.com\n*.ice.ext.google.com\nice.ext.google.com");
//            s.setEntryTimestamp("2013-03-26T10:50:37.169");
//            s.setNotBefore("2012-08-02T02:50:55");
//            s.setNotAfter("2013-06-07T19:43:27");
//            s.setId(365343L);
//            s.setResultCount(5);
//            s.setSerialNumber("1a4072f70000000064c0");
//            subdomains.add(s);
//        }
//        handler.post(() -> {
//            view.hideProgress();
//            view.successResult(subdomains);
//        });
//    }

    @Override
    public void submitButtonClicked(String domain, int selectedRadioButtonId) {
        subdomains = new ArrayList<>();
        if (view != null) {
            view.showProgress();
            view.successSubdomainsResult(subdomains);//send clear list
        }
        String mm = "";
        if (selectedRadioButtonId == R.id.scantypeQuick) {
            mm = AA_MM + domain + "&output=json";
        } else {
            mm = AA_MM + domain + "&output=json&exclude=expired";
        }
        subdomainFinderComplete(domain, mm);
    }

    private void subdomainFinderComplete(final String domain, String mm) {
        executor.execute(() -> {
            try {
                URL url = new URL(mm);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStream inputStream = conn.getInputStream();
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                subdomains = new ArrayList<>();

                try {
                    Gson gson = new Gson();
                    Certificate[] certificates = gson.fromJson(response, Certificate[].class);
                    subdomains.addAll(Arrays.asList(certificates));
                } catch (Exception e) {
                    JSONObject jsonObject = new JSONObject(response);
                    for (int i = 0; i < jsonObject.length(); i++) {
                        String commonName = jsonObject.getJSONArray("common_name").getString(i);
                        subdomains.add(new TwoColItem(commonName, ""));
                    }
                }
                if(!subdomains.isEmpty()){
                    preferences.setSubdomainDomain(domain);
                }
                handler.post(() -> {
                    view.hideProgress();
                    view.successSubdomainsResult(subdomains);
                });
                scanner.close();
                inputStream.close();
            } catch (Exception e) {
                DLog.handleException(e);
                showError("Failed to Get Response Data: " + e.getLocalizedMessage());
            }
        });
    }

    private void showError(String err) {
        DLog.d(err);

        List<ViewModel> aa = new ArrayList<>();
        aa.add(new SingleItem(err, R.color.error));
        handler.post(() -> {
            view.hideProgress();
            view.successSubdomainsResult(aa);
        });
    }

    public void init() {
        String ip = preferences.getSubdomainDomain();
        view.init(ip);
    }
}
