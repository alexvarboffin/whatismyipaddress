package com.walhalla.whatismyipaddress.features.base;

import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BasePresenter {

    protected final Handler handler;
    protected final ExecutorService executor;

    public BasePresenter(Handler handler) {
        this.handler = handler;
        this.executor = Executors.newSingleThreadExecutor();
    }


}
