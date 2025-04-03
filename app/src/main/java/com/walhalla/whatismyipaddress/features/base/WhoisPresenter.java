package com.walhalla.whatismyipaddress.features.base;

import android.content.Context;
import android.os.Handler;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;


import java.util.List;
//
//
// rdap RFC 7483

public abstract class WhoisPresenter extends BasePresenter {

    protected final Context context;

    public abstract void whois(String rawData);

    public interface Callback {

        void successResult(List<ViewModel> nameValue0);

        void showProgress();

        void hideProgress();

        void init0(String ip);
    }


    public final Callback callback;

    public WhoisPresenter(Context context, Handler handler, Callback callback) {
        super(handler);
        this.callback = callback;
        this.context = context;
    }

}
