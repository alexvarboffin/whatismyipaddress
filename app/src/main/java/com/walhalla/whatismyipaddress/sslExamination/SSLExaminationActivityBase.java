package com.walhalla.whatismyipaddress.sslExamination;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.BuildConfig;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.databinding.ActivitySslexaminationBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SSLExaminationActivityBase extends BaseListActivity implements SSLExaminationPresenter.View{


    private SSLExaminationPresenter presenter;
    private ActivitySslexaminationBinding binding;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySslexaminationBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new SSLExaminationPresenter(this, this, handler);


        getBinding().title.setText(R.string.titleSSLExamination);
        getBinding().ping.setText(R.string.action_button_scan);

        if (BuildConfig.DEBUG) {
            binding.pingIp.setText("google.com");
            //binding.pingIp.setText("64.233.161.102");

        }
        getBinding().ping.setOnClickListener(view -> {
            hideKeyboard(this);
            if (binding.pingIp.getText().toString().trim().equals("")
                    || binding.pingIp.getText().toString().trim().equals(" ")
            ) {
                Toasty.custom(this, getString(R.string.provideallfields).toUpperCase(), 
                        comv19.getDrawable(this, R.drawable.ic_cancel), ContextCompat.getColor(this, R.color.error), ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    try {
                        showProgress();
                        String pingIpText = binding.pingIp.getText().toString().trim();
                        presenter.examinate(pingIpText);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toasty.custom(this, getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(this, R.drawable.ic_cancel),
                            ContextCompat.getColor(this, R.color.error),
                            ContextCompat.getColor(this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> super.onBackPressed());
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
