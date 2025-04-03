package com.walhalla.whatismyipaddress.features.ping;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.compat.ComV19;
import com.walhalla.ui.DLog;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.databinding.ActivityPingBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PingIp extends BaseListActivity
        implements PingPresenter.PingView {


    private static final String KEY_VAR0 = PingIp.class.getSimpleName();
    private PingPresenter presenter;
    private ActivityPingBinding binding;

    public static Intent newInstance(Context context, String content) {
        Intent intent = new Intent(context, PingIp.class);
        intent.putExtra(KEY_VAR0, content);
        return intent;
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPingBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        String var0 = "";
        if (getIntent() != null) {
            var0 = getIntent().getStringExtra(KEY_VAR0);
        }

        comv19 = new ComV19();
        setupAdAtBottom();
        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new PingPresenter(this, this, handler);


        getBinding().ping.setOnClickListener(view -> {
            hideKeyboard(this);
            if (binding.pingIp.getText().toString().trim().equals("") || binding.pingIp.getText().toString().trim().equals(" ")
                    || binding.limit.getText().toString().trim().equals("") || binding.timeout.getText().toString().trim().equals("")) {
                Toasty.custom(PingIp.this, getString(R.string.provideallfields).toUpperCase(), comv19.getDrawable(PingIp.this, R.drawable.ic_cancel), ContextCompat.getColor(PingIp.this, R.color.error), ContextCompat.getColor(PingIp.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    try {
                        int pingLimit = Integer.parseInt(binding.limit.getText().toString().trim());
                        int pingTimeout = Integer.parseInt(binding.timeout.getText().toString().trim());
                        String pingIpText = binding.pingIp.getText().toString().trim();
                        presenter.startPing(pingIpText, pingTimeout, pingLimit);

                    } catch (Exception e) {
                        DLog.handleException(e);
                    }
                } else {
                    Toasty.custom(PingIp.this, getString(R.string.internet_connectivity_problem), comv19.getDrawable(PingIp.this, R.drawable.ic_cancel), ContextCompat.getColor(PingIp.this, R.color.error), ContextCompat.getColor(PingIp.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> PingIp.super.onBackPressed());
        if (TextUtils.isEmpty(var0)) {
            presenter.init();
        } else {
            binding.pingIp.setText(var0);
        }
    }


    @Override
    public void successResult(ArrayList<ViewModel> dataModels) {
        swap(dataModels);
    }

    @Override
    public void init0(String pingIpText, int pingTimeout, int pingLimit) {
        try {
            binding.pingIp.setText(pingIpText);
            binding.timeout.setText("" + pingTimeout);
            binding.limit.setText("" + pingLimit);
        } catch (Exception r) {
            DLog.handleException(r);
        }
    }


}
