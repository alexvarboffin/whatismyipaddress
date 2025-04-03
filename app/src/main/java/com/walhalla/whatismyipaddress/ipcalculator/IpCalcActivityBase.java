package com.walhalla.whatismyipaddress.ipcalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.walhalla.whatismyipaddress.databinding.ActivityIpCalcBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class IpCalcActivityBase extends BaseListActivity implements IpCalcAdapter.View1 {


    private String ipAddress;
    private String macAddress;

    private IpCalcAdapter task;
    private ActivityIpCalcBinding binding;


    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityIpCalcBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        Handler handler = new Handler(Looper.getMainLooper());
        task = new IpCalcAdapter(this, IpCalcActivityBase.this, handler);

        setupAdAtBottom();

        getBinding().title.setText(R.string.title_IPCalculator);
        getBinding().ping.setText(R.string.action_calculate);
        getBinding().ping.setOnClickListener(view -> {
            Helpers0.hideKeyboard(this);

            ipAddress = binding.ip.getText().toString().trim();
            macAddress = binding.mac.getText().toString().trim();

            if (TextUtils.isEmpty(ipAddress)
                    || ipAddress.equals(" ")) {


                Toasty.custom(IpCalcActivityBase.this, getString(R.string.invalid_ip_address).toUpperCase(), comv19.getDrawable(IpCalcActivityBase.this,
                                R.drawable.ic_cancel), ContextCompat.getColor(IpCalcActivityBase.this, R.color.error),
                        ContextCompat.getColor(IpCalcActivityBase.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    task.wakeOnlan(IpCalcActivityBase.this, ipAddress, macAddress);
                } else {
                    Toasty.custom(
                            IpCalcActivityBase.this,
                            getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(IpCalcActivityBase.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(IpCalcActivityBase.this, R.color.error),
                            ContextCompat.getColor(IpCalcActivityBase.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> IpCalcActivityBase.super.onBackPressed());
        task.init();
    }


    @Override
    public void onListItemClick(ViewModel dataModel) {
//        Snackbar snackbar = Snackbar
//                .make(layout, getString(R.string.q_copy_value_to_clipboard), Snackbar.LENGTH_LONG)
//                .setAction(R.string.action_copy, view1 -> {
//                    if (dataModel instanceof TwoColItem) {
//                        String value = ((TwoColItem) dataModel).value;
//                        copyToBuffer(value);
//                    }
//                });
//        snackbar.show();

        if (dataModel instanceof TwoColItem) {
            String value = ((TwoColItem) dataModel).value;
            copyToBuffer(value);
        }
    }

    public void copyToBuffer(String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("IP Tools", value);
            clipboard.setPrimaryClip(clip);
            Toasty.custom(this, String.format(getString(R.string.data_to_clipboard), value).toUpperCase(), comv19.getDrawable(this,
                            R.drawable.ic_info), ContextCompat.getColor(this, R.color.colorPrimaryDark),
                    ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
    }


    @Override
    public void displayScanResult(ArrayList<ViewModel> dataModels) {
        swap(dataModels);
    }

    @Override
    public void init(String ip, String mask) {
        binding.ip.setText(ip);
        binding.mac.setText(mask);
    }
}
