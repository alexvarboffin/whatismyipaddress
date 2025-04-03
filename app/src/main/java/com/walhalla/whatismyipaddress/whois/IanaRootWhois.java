package com.walhalla.whatismyipaddress.whois;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.Helpers0;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.databinding.ActivityRootDomainWhoisBinding;
import com.walhalla.whatismyipaddress.features.base.WhoisPresenter;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;


import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class IanaRootWhois extends BaseListActivity implements WhoisPresenter.Callback {

    private RootWhoisManager presenter;


    private static final String KEY_VAR0 = IanaRootWhois.class.getSimpleName();
    private ActivityRootDomainWhoisBinding binding;



    public static Intent newInstance(Context context, String content) {
        Intent intent = new Intent(context, IanaRootWhois.class);
        intent.putExtra(KEY_VAR0, content);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRootDomainWhoisBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new RootWhoisManager(this, handler, this);

        String var0 = "";
        if (getIntent() != null) {
            var0 = getIntent().getStringExtra(KEY_VAR0);
        }

        comv19 = new ComV19();
        setupAdAtBottom();


        getBinding().title.setText(R.string.title_iana_whois);

        getBinding().ping.setText(R.string.action_whois);
        getBinding().ping.setOnClickListener(view -> {
            Helpers0.hideKeyboard(this);
            if (binding.ip.getText().toString().trim().equals("") || binding.ip.getText().toString().trim().equals(" ")) {

                Drawable draw0 = comv19.getDrawable(IanaRootWhois.this, R.drawable.ic_cancel);
                Toasty.custom(IanaRootWhois.this, "Provide IP/Web URL".toUpperCase(), draw0,
                        ContextCompat.getColor(IanaRootWhois.this, R.color.error),
                        ContextCompat.getColor(IanaRootWhois.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    presenter.whois(binding.ip.getText().toString().trim());
                } else {
                    Toasty.custom(IanaRootWhois.this,
                            getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(IanaRootWhois.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(IanaRootWhois.this, R.color.error),
                            ContextCompat.getColor(IanaRootWhois.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> super.onBackPressed());
//        if (BuildConfig.DEBUG) {
//            binding.ip.setText("music");
//            whois.callOnClick();
//        }

        if (TextUtils.isEmpty(var0)) {
            presenter.init();
        } else {
            binding.ip.setText(var0);
        }
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    @Override
    public void successResult(List<ViewModel> dataModels) {
        hideProgress();
        if (dataModels != null && dataModels.size() > 0) {
            swap(dataModels);
        } else {
            final ArrayList<ViewModel> empty = new ArrayList<>();
            empty.add(new TwoColItem(getString(R.string.no_data_found), "", R.color.colorPrimaryDark));
            swap(dataModels);
        }
    }


    @Override
    public void init0(String ip) {
        binding.ip.setText(ip);
    }

//    {"ErrorMessage": {
//        "errorCode": "WHOIS_01",
//                "msg": "\"1.\" is an invalid domain name"
//    }}

}
