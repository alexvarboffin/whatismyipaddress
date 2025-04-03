package com.walhalla.whatismyipaddress.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.walhalla.whatismyipaddress.BuildConfig;
import com.walhalla.whatismyipaddress.Helpers0;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;

import com.walhalla.whatismyipaddress.databinding.ActivityWakeonlanBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;
import com.walhalla.whatismyipaddress.ui.activities.wakeonlan.WakeOnLanAdapter;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class WakeOnLan5 extends BaseListActivity implements WakeOnLanAdapter.View1 {
    
    private EditText macET;
    private String macAddress;

    private WakeOnLanAdapter task;
    private ActivityWakeonlanBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding= ActivityWakeonlanBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        Handler handler = new Handler(Looper.getMainLooper());
        task = new WakeOnLanAdapter(this, WakeOnLan5.this, handler);

        setupAdAtBottom();

        macET = findViewById(R.id.mac);
        

        getBinding().title.setText(R.string.titleWakeonlan);
        getBinding().ping.setText(R.string.wakeOnLan);
        
        getBinding().ping.setOnClickListener(view -> {
            Helpers0.hideKeyboard(this);

            String ip = binding.ip.getText().toString().trim();
            macAddress = macET.getText().toString().trim();

            if (TextUtils.isEmpty(ip)
                    || ip.equals(" ")) {


                Toasty.custom(WakeOnLan5.this, getString(R.string.invalid_ip_address).toUpperCase(), comv19.getDrawable(WakeOnLan5.this,
                                R.drawable.ic_cancel), ContextCompat.getColor(WakeOnLan5.this, R.color.error),
                        ContextCompat.getColor(WakeOnLan5.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    task.wakeOnlan(ip, macAddress);
                } else {
                    Toasty.custom(
                            WakeOnLan5.this,
                            getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(WakeOnLan5.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(WakeOnLan5.this, R.color.error),
                            ContextCompat.getColor(WakeOnLan5.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> WakeOnLan5.super.onBackPressed());

        if (BuildConfig.DEBUG) {
            binding.ip.setText("127.0.0.1");
            macET.setText("F0:98:9D:1C:93:F6");
        }
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
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
}
