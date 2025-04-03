package com.walhalla.whatismyipaddress.features.checkhost;


import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.walhalla.ui.DLog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerRequestRunnable implements Runnable {
    private String serverName;
    private String serverIP;

    public ServerRequestRunnable(String serverName, String serverIP) {
        this.serverName = serverName;
        this.serverIP = serverIP;
    }

    @Override
    public void run() {
        OkHttpClient httpClient = NetworkUtils.makeOkhttp();
        Request request = new Request.Builder()
                .url("http://" + serverIP) // Замените на свой URL для запроса на сервер
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DLog.d("Request to server " + serverName + " failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                DLog.d("Response from server " + serverName + ": " + response.body().string());
            }
        });
    }
}
