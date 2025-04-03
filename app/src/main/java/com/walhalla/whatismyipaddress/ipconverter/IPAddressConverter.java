package com.walhalla.whatismyipaddress.ipconverter;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.databinding.ActivityIpconverterBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class IPAddressConverter extends BaseListActivity
        implements IPAddressConverterTask.View00 {


    private IPAddressConverterTask presenter;
    private ActivityIpconverterBinding binding;

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityIpconverterBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);

        comv19 = new ComV19();
        setupAdAtBottom();

        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new IPAddressConverterTask(this, this, handler);

        getBinding().title.setText(R.string.action_title_ipconverter);
        getBinding().ping.setText(R.string.action_button_convert);
        getBinding().ping.setOnClickListener(view -> {
            hideKeyboard(this);

            if (binding.ip.getText().toString().trim().equals("")
                    || binding.ip.getText().toString().trim().equals(" ")) {
                Toasty.custom(IPAddressConverter.this, getString(R.string.provideallfields).toUpperCase(),
                        comv19.getDrawable(IPAddressConverter.this,
                                R.drawable.ic_cancel), ContextCompat.getColor(IPAddressConverter.this, R.color.error),
                        ContextCompat.getColor(IPAddressConverter.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    String ipText = binding.ip.getText().toString().trim();
                    //boolean mThread = Looper.myLooper() == Looper.getMainLooper();
                    //boolean mThread2 = Looper.getMainLooper().getThread() == Thread.currentThread();
                    //DLog.d("@xx@aaa" + Thread.currentThread() + " :: " + mThread + " " + mThread2);
                    presenter.converter(ipText);

                } else {
                    connectionError();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> IPAddressConverter.super.onBackPressed());
        presenter.init();
    }

    private void connectionError() {

        //int color_error = ContextCompat.getColor(this, R.color.error);
        //int color_error = Color.rgb(248, 10, 56);

        Toasty.custom(IPAddressConverter.this, R.string.internet_connectivity_problem,
                comv19.getDrawable(IPAddressConverter.this, R.drawable.ic_cancel),
                R.color.error, R.color.white, Toasty.LENGTH_SHORT, true, true).show();
    }

    @Override
    public void displayScanProgress(String result) {
        DLog.d("@@@" + result);
    }

    @Override
    public void displayScanResult(ArrayList<ViewModel> dataModels) {
        swap(dataModels);
    }

    @Override
    public void init(String ip) {
        binding.ip.setText(ip);
        //scanBtn.callOnClick();
    }

    @Override
    public void handleException(String ipText, Exception e0) {
        //e.toString().toUpperCase()
        Toasty.custom(IPAddressConverter.this, "Unknown Host: " + ipText, comv19.getDrawable(IPAddressConverter.this,
                        R.drawable.ic_cancel),
                ContextCompat.getColor(IPAddressConverter.this, R.color.error),
                ContextCompat.getColor(IPAddressConverter.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();

    }


}
