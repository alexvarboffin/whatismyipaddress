package com.aaronjwood.portauthority.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class NetworkUtils {


    public static OkHttpClient makeOkhttp() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new SimpleLoggingInterceptor());
        return httpClientBuilder.build();

    }
}
