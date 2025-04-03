package com.walhalla.whatismyipaddress.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.whatismyipaddress.Helpers0;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;

import com.walhalla.whatismyipaddress.databinding.ActivityDnslookupBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;
import com.walhalla.whatismyipaddress.ui.activities.dnslookup.DNSLookupTask;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class DNSLookup extends BaseListActivity implements DNSLookupTask.View {

    private ActivityDnslookupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDnslookupBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        Handler handler = new Handler(Looper.getMainLooper());
        DNSLookupTask task = new DNSLookupTask(this, this, handler);
        comv19 = new ComV19();
        setupAdAtBottom();


        getBinding().title.setText(R.string.action_titleDnsLookupWhoisxml);

        getBinding().ping.setText(R.string.action_titleDnsLookupWhoisxml);
        getBinding().ping.setOnClickListener(view -> {
            Helpers0.hideKeyboard(this);
            if (binding.ip.getText().toString().trim().isEmpty()
                    || binding.ip.getText().toString().trim().equals(" ")) {
                Toasty.custom(DNSLookup.this, "Provide IP/Web URL".toUpperCase(),
                        comv19.getDrawable(DNSLookup.this, R.drawable.ic_cancel),
                        ContextCompat.getColor(DNSLookup.this, R.color.error),
                        ContextCompat.getColor(DNSLookup.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    task.dnsLook(binding.ip.getText().toString().trim());
                } else {
                    Toasty.custom(
                            DNSLookup.this,
                            getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(DNSLookup.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(DNSLookup.this, R.color.error),
                            ContextCompat.getColor(DNSLookup.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> DNSLookup.super.onBackPressed());
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }


    @Override
    public void successResult(ArrayList<ViewModel> dataModels) {
        swap(dataModels);
    }
}
