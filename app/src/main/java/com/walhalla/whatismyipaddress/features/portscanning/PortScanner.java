package com.walhalla.whatismyipaddress.features.portscanning;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.databinding.ActivityPortScanningBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PortScanner extends BaseListActivity
        implements PortScanContract.View {



    private PortScanPresenter presenter;
    private static final String KEY_VAR0 = PortScanner.class.getSimpleName();
    private ActivityPortScanningBinding binding;


    public static Intent newInstance(Context context, String content) {
        Intent intent = new Intent(context, PortScanner.class);
        intent.putExtra(KEY_VAR0, content);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPortScanningBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);

        String var0 = "";
        if (getIntent() != null) {
            var0 = getIntent().getStringExtra(KEY_VAR0);
        }

        comv19 = new ComV19();
        setupAdAtBottom();

        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new PortScanPresenter(this, this, handler);


        //only ip
        //pingIp.setKeyListener(DigitsKeyListener.getInstance(getString(R.string.ipDigits)));
        //pingIp.setHint(R.string.hint_enter_ip);


        binding.pingIp.setKeyListener(DigitsKeyListener.getInstance(getString(R.string.digitsIpOrHost)));
        binding.pingIp.setRawInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        binding.pingIp.setHint(R.string.hint_enter_ip_or_host);

        getBinding().ping.setText(R.string.action_button_scan);

        getBinding().title.setText(R.string.action_title_port_scanner);

        getBinding().ping.setOnClickListener(view -> {
            hideKeyboard(this);
            String ipText = binding.pingIp.getText().toString().trim();

            if (ipText.isEmpty()
                    || ipText.equals(" ")
                    || binding.max.getText().toString().trim().equals("")
                    || binding.min.getText().toString().trim().equals("")
                    || binding.timeout.getText().toString().trim().equals("")) {
                Toasty.custom(PortScanner.this, getString(R.string.provideallfields).toUpperCase(),
                        comv19.getDrawable(PortScanner.this,
                                R.drawable.ic_cancel), ContextCompat.getColor(PortScanner.this, R.color.error),
                        ContextCompat.getColor(PortScanner.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                String _tmp = binding.min.getText().toString().trim();
                if (Integer.parseInt(_tmp) 
                        > Integer.parseInt(binding.max.getText().toString().trim())) {
                    Toasty.custom(PortScanner.this, getString(R.string.min_port_no_cannot_be_greater).toUpperCase(), comv19.getDrawable(PortScanner.this,
                                    R.drawable.ic_cancel), ContextCompat.getColor(PortScanner.this, R.color.error),
                            ContextCompat.getColor(PortScanner.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                } else {
                    if (AssetUtils.isNetworkAvailable(this)) {
                        showProgress();
                       
                        int time = Integer.parseInt(binding.timeout.getText().toString().trim());
                        int minPortNum = Integer.parseInt(binding.min.getText().toString().trim());
                        int maxPortNum = Integer.parseInt(binding.max.getText().toString().trim());

                        ArrayList<Integer> ports = new ArrayList<>();

                        for (int i = minPortNum; i <= maxPortNum; i++) {
                            ports.add(i);
                        }

                        //boolean mThread = Looper.myLooper() == Looper.getMainLooper();
                        //boolean mThread2 = Looper.getMainLooper().getThread() == Thread.currentThread();
                        //DLog.d("@xx@aaa" + Thread.currentThread() + " :: " + mThread + " " + mThread2);
                        presenter.startPortScan(ipText, time, ports);

                    } else {
                        connectionError();
                    }
                }
            }
        });

        getBinding().back.setOnClickListener(view -> PortScanner.super.onBackPressed());

        if (TextUtils.isEmpty(var0)) {
            presenter.init();
        } else {
           binding.pingIp.setText(var0);
        }
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    private void connectionError() {

        //int color_error = ContextCompat.getColor(this, R.color.error);
        //int color_error = Color.rgb(248, 10, 56);

        Toasty.custom(PortScanner.this, R.string.internet_connectivity_problem,
                comv19.getDrawable(PortScanner.this, R.drawable.ic_cancel),
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
    public void init0(String ip) {
        binding.pingIp.setText(ip);
    }

    @Override
    public void handleException(String ipText, Exception e0) {
        //e.toString().toUpperCase()
        Toasty.custom(PortScanner.this, "Unknown Host: " + ipText, comv19.getDrawable(PortScanner.this,
                        R.drawable.ic_cancel),
                ContextCompat.getColor(PortScanner.this, R.color.error),
                ContextCompat.getColor(PortScanner.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();

    }


}
