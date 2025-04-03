package com.walhalla.whatismyipaddress.domain;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.helper.IPInfoRemote;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class IpInfoRepositoryExternal {

    private static final String TAG = "{e}";
    int cnt = 2;

    public IPInfoRemote getRemoteInfo(Context context, String userAgent) {
        String json = null;
        while (TextUtils.isEmpty(json) && cnt > 0) {
            try {
                json = getJSON0(10000, userAgent);
                Log.d(TAG, "RemoteInfo: " + cnt + " " + ((json == null) ? "null" : json.length()) + "\t");
                cnt--;
            } catch (Exception ex) {
                Log.d(TAG, "@@@ Error when getting ip" + ex.getLocalizedMessage());
                return null;
            }
        }
        if (json == null) {
            return null;
        }


        return makeObj(json);
        //return makeObj1(json);

    }

//    private IPInfoRemote makeObj1(String json) {
//        IPInfoRemote obj = new IPInfoRemote();
//        try {
//            JSONObject object = new JSONObject(new JSONTokener(json));
//            if (object.has("status")) {
//                String status = object.getString("status");
//                if ("success".equalsIgnoreCase(status)) {
//
//                    obj.continent = object.getString("continent");
//                    obj.continentCode = object.getString("continentCode");
//                    obj.ip = object.getString("query");
//
//                    String country = object.getString("country");
//                    String country_code = object.getString("countryCode");
//
//                    obj.region = object.getString("region");
//                    obj.regionName = object.getString("regionName");
//
//                    obj.ip = object.getString("query");
//
//
//                    obj.timezone = object.getString("timezone");
//                    obj.Netname = object.getString("org");//netname
//
//                    obj.lat = object.getString("lat");
//                    obj.lat = object.getString("lon");
//
//                    obj.city = object.getString("city");
//                    obj.offset = object.getInt("offset");
//
//                    obj.district = object.getString("district");//empty
//                    obj.postal = object.getString("zip");
//                    obj.currency = object.getString("currency");
//
//                    obj.country = "[" + country_code + "] " + country;
//
//
//                    try {
//                        obj.hostname = object.getString("hostname");
//                    } catch (JSONException e) {
//                        Log.d(TAG, "@ not hostname");
//                    }
//
//
////            JSONArray jarr = object.getJSONArray("descr");
////            if (jarr != null && jarr.length() > 0) {
////                ArrayList<String> ls = new ArrayList<>();
////                for (int idx = 0; idx < jarr.length(); idx++) {
////                    ls.add(jarr.getString(idx));
////                }
////                infoRemote.description = ls.toArray(new String[0]);
////            }
//                    if ("null".equals(obj.hostname)) {
//                        obj.hostname = null;
//                    }
//                    if ("null".equals(obj.country)) {
//                        obj.country = null;
//                    }
//                    if ("null".equals(obj.Netname)) {
//                        obj.Netname = null;
//                    }
//                } else if ("fail".equalsIgnoreCase(status)) {
//                    //object.getString("message")
//                            ?lang = ru
//                }
//            }
//        } catch (Exception e) {
//            DLog.handleException(e);
//        }
//        return obj;
//    }

    private IPInfoRemote makeObj(String json) {
        IPInfoRemote obj = new IPInfoRemote();
        try {
            JSONObject object;
            object = new JSONObject(new JSONTokener(json));
            obj.ip = object.getString("ip");
            try {
                obj.hostname = object.getString("hostname");
            } catch (JSONException e) {
                Log.d(TAG, "@ not hostname");
            }
            obj.city = object.getString("city");
            obj.region = object.getString("region");
            obj.country = object.getString("country");
            obj.loc = object.getString("loc");
            obj.Netname = object.getString("org");//netname

            obj.postal = object.getString("postal");
            obj.timezone = object.getString("timezone");

//            JSONArray jarr = object.getJSONArray("descr");
//            if (jarr != null && jarr.length() > 0) {
//                ArrayList<String> ls = new ArrayList<>();
//                for (int idx = 0; idx < jarr.length(); idx++) {
//                    ls.add(jarr.getString(idx));
//                }
//                infoRemote.description = ls.toArray(new String[0]);
//            }
            if ("null".equals(obj.hostname)) {
                obj.hostname = null;
            }
            if ("null".equals(obj.country)) {
                obj.country = null;
            }
            if ("null".equals(obj.Netname)) {
                obj.Netname = null;
            }
        } catch (Exception e) {
            DLog.handleException(e);
        }
        return obj;
    }

    HostnameVerifier hostnameVerifier = (hostname, session) -> {
        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
        return hv.verify("ipinfo.io", session);
    };


    //не работает с тор
    private String getJSON0(int timeout, String ua) {

        // Создание объекта TrustManager, который игнорирует проверку сертификата

//        TrustManager[] trustAllCerts = new TrustManager[] {
//                new X509TrustManager() {
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return new X509Certificate[0];
//                    }
//                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
//                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
//                }
//        };

        String url = "https://ipinfo.io/json?token=85997aaf83a469";
//        String url = "https://ipinfo.io/json";
//        String url = "https://ipinfo.io/city,region,country";

        //String url = "https://ip-api.com/json";

//        try {
//            Log.d(TAG,  "getJSON: " + Thread.currentThread().getName());
//            TimeUnit.SECONDS.sleep(10);
//        Log.d(TAG, "-->>>: " + url + " ###" + timeout);


        HttpsURLConnection connection = null;
        try {
            // Установка игнорирования проверки сертификата для SSLContext
//            SSLContext sslContext;
//            try {
//                sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, trustAllCerts, new SecureRandom());
//                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//
//            } catch (NoSuchAlgorithmException | KeyManagementException e) {
//                DLog.handleException(e);
//            }

            URL url1 = new URL(url);
            connection = (HttpsURLConnection) url1.openConnection();
            connection.setHostnameVerifier(hostnameVerifier);
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Content-length", "0");
            connection.addRequestProperty("User-Agent", ua);
            //connection.addRequestProperty("Authorization", "Bearer 85997aaf83a469");
            connection.addRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.addRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setUseCaches(false);
//            connection.setAllowUserInteraction(false);
//            connection.setConnectTimeout(timeout);
//            connection.setReadTimeout(timeout);
            connection.connect();

            //if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            int status = connection.getResponseCode();
            Log.d(TAG, "[status] " + status);
            switch (status) {

                case HttpsURLConnection.HTTP_OK:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    return sb.toString();

                case 429:
                    Log.d(TAG, "TO MANY REQUEST " + status);
                    break;

                case 403:
                    Log.d(TAG, "403 Forbidden " + status + ", " + url);
                    break;

                default:
                    Log.d(TAG, "getJSON: " + status);
                    break;
            }

        } catch (IOException ex) {
            DLog.handleException(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    DLog.handleException(ex);
                }
            }
        }

//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }


    // ONLY HTTP

    private String getJSON1(int timeout, String ua) {

        try {
            OkHttpClient httpClient = NetworkUtils.makeOkhttp();
            String url = "http://ip-api.com/json";
            Request request = new Request.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:50.0) Gecko/20100101 Firefox/50.0") //optional
                    .url(url)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d(TAG, responseBody);
            } else {
                Log.d(TAG, "Request was not successful: " + response.code());
            }

        } catch (Exception ex) {
            DLog.handleException(ex);
        }
        return null;
    }

}
