package com.aaronjwood.portauthority.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SimpleLoggingInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.d("@@@","Intercepted headers: {} from URL: {}"+request.headers()+ request.url());
        return chain.proceed(request);
    }
}