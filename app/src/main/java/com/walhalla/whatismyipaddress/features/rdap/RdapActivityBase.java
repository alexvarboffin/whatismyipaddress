package com.walhalla.whatismyipaddress.features.rdap;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.databinding.ActivityRdapBinding;
import com.walhalla.whatismyipaddress.features.base.WhoisPresenter;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;
import com.walhalla.whatismyipaddress.features.whois.WhoisContract;


import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class RdapActivityBase extends BaseListActivity implements WhoisPresenter.Callback, WhoisContract.View {


    private WhoisPresenter presenter;
    private ActivityRdapBinding binding;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRdapBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);

        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new RDAPPresenter(this, handler, this);
        comv19 = new ComV19();
        setupAdAtBottom();

        getBinding().title.setText(R.string.action_title_rdap);
        getBinding().ping.setText(R.string.action_title_rdap);


        getBinding().ping.setOnClickListener(view -> {
            hideKeyboard(this);
            if (binding.ip.getText().toString().trim().isEmpty()
                    || binding.ip.getText().toString().trim().equals(" ")) {

                Drawable draw0 = comv19.getDrawable(RdapActivityBase.this, R.drawable.ic_cancel);
                Toasty.custom(RdapActivityBase.this, "Provide IP/Web URL".toUpperCase(), draw0,
                        ContextCompat.getColor(RdapActivityBase.this, R.color.error),
                        ContextCompat.getColor(RdapActivityBase.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    presenter.whois(binding.ip.getText().toString().trim());
                } else {
                    Toasty.custom(RdapActivityBase.this,
                            getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(RdapActivityBase.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(RdapActivityBase.this, R.color.error),
                            ContextCompat.getColor(RdapActivityBase.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        findViewById(R.id.back)
                .setOnClickListener(view -> RdapActivityBase.super.onBackPressed());
//        if (BuildConfig.DEBUG) {
//            binding.ip.setText("music");
//            getBinding().ping.callOnClick();
//        }

        binding.example.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.rdap_dialog_title_example);
            String[] sites = {
                    "193.0.6.139.in-addr.arpa",
                    "2001:67c:2e8:9::c100:14e6",
                    "8.8.8.8",
                    "RIPE-RIPE",
                    "google.com"
            };
            builder.setItems(sites, (dialog, which) -> {
                String site = sites[which];
                binding.ip.setText(site);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    @Override
    public void successResult(List<ViewModel> dataModels) {
        if (dataModels != null && dataModels.size() > 0) {
            swap(dataModels);
        } else {
            final ArrayList<ViewModel> empty = new ArrayList<>();
            empty.add(new TwoColItem(getString(R.string.no_data_found), "", R.color.colorPrimaryDark));
            swap(empty);
        }
    }

    @Override
    public void init0(String ip) {

    }

    @Override
    public void displayScanProgress(String result) {

    }

    @Override
    public void handleException(String ipText, Exception e0) {

    }

    @Override
    public void displayScanResult(ArrayList<ViewModel> dataModels) {

    }

//    {"ErrorMessage": {
//        "errorCode": "WHOIS_01",
//                "msg": "\"1.\" is an invalid domain name"
//    }}


}
