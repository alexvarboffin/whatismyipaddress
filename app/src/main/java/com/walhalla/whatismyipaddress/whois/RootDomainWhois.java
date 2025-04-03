package com.walhalla.whatismyipaddress.whois;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.ui.DLog;
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

public class RootDomainWhois extends BaseListActivity implements WhoisPresenter.Callback {

    private ActivityRootDomainWhoisBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding= ActivityRootDomainWhoisBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);

        comv19 = new ComV19();
        setupAdAtBottom();


        //@@@@@


        getBinding().ping.setOnClickListener(view -> {
            Helpers0.hideKeyboard(this);
            if ("".equals(binding.ip.getText().toString().trim())
                    || binding.ip.getText().toString().trim().equals(" ")) {

                Drawable draw0 = comv19.getDrawable(RootDomainWhois.this, R.drawable.ic_cancel);
                Toasty.custom(RootDomainWhois.this, "Provide IP/Web URL".toUpperCase(), draw0,
                        ContextCompat.getColor(RootDomainWhois.this, R.color.error),
                        ContextCompat.getColor(RootDomainWhois.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    Handler handler = new Handler(Looper.getMainLooper());
                    WhoisPresenter whoisTask =
                            //new WMNew
                            new RootWhoisManager
                                    (this, handler, this);
                    whoisTask.whois(binding.ip.getText().toString().trim());
                } else {
                    Toasty.custom(RootDomainWhois.this,
                            getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(RootDomainWhois.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(RootDomainWhois.this, R.color.error),
                            ContextCompat.getColor(RootDomainWhois.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> RootDomainWhois.super.onBackPressed());
//        if (BuildConfig.DEBUG) {
//            binding.ip.setText("2222");
//            whois.callOnClick();
//        }
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    @Override
    public void successResult(List<ViewModel> dataModels) {
        DLog.d("@@@" + dataModels);
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

    }

//    {"ErrorMessage": {
//        "errorCode": "WHOIS_01",
//                "msg": "\"1.\" is an invalid domain name"
//    }}

}
