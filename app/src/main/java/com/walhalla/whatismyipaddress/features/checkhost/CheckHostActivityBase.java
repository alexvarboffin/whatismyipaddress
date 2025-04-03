package com.walhalla.whatismyipaddress.features.checkhost;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.databinding.ActivityCheckhostBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class CheckHostActivityBase extends BaseListActivity
        implements CheckhostPresenter.CheckhostView {

    private CheckhostPresenter presenter;

    private static final String KEY_ARG_ID = CheckHostActivityBase.class.getSimpleName();

    private ActivityCheckhostBinding binding;

    public static Intent newInstance(Context context, String content) {
        Intent intent = new Intent(context, CheckHostActivityBase.class);
        intent.putExtra(KEY_ARG_ID, content);
        return intent;
    }


    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCheckhostBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        String var0 = handleInstance(savedInstanceState);


        comv19 = new ComV19();
        setupAdAtBottom();
        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new CheckhostPresenter(this, this, handler);

        getBinding().title.setText(R.string.titleGeoPing);
        getBinding().ping.setText(R.string.action_button_geoping);

        getBinding().ping.setOnClickListener(view -> {
            hideKeyboard(this);
            if (binding.pingIp.getText().toString().trim().equals("")
                    || binding.pingIp.getText().toString().trim().equals(" ")) {
                Toasty.custom(this, getString(R.string.provideallfields).toUpperCase(), comv19.getDrawable(this, R.drawable.ic_cancel), ContextCompat.getColor(this, R.color.error), ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    String HOSTNAME = binding.pingIp.getText().toString().trim().toLowerCase();

                    try {
                        if (HOSTNAME.startsWith("http://") || HOSTNAME.startsWith("https://")) {
                            new URL(HOSTNAME);
                        } else {
                            new URL("http://" + HOSTNAME);
                        }
                    } catch (MalformedURLException ignored) {
                        Toasty.custom(this, "Unknown Host/IP/ or http/https: " + HOSTNAME, comv19.getDrawable(this, R.drawable.ic_cancel), ContextCompat.getColor(this, R.color.error), ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                        return;
                    }

//                    try {
//                        URI uri = new URI(HOSTNAME);
//                        if (uri.getScheme() == null || (!uri.getScheme().equals("http") && !uri.getScheme().equals("https"))) {
//                            throw new MalformedURLException();
//                        }
//                        URL url = uri.toURL();
//                        // Все проверки прошли успешно, URL-адрес корректен
//                    } catch (URISyntaxException | MalformedURLException ignored) {
//                        Toasty.custom(this, "Unknown Host/IP/ or http/https: " + HOSTNAME, comv19.getDrawable(this, R.drawable.ic_cancel), ContextCompat.getColor(this, R.color.error), ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
//                        return;
//                    }

                    presenter.checkhost(HOSTNAME);
                } else {
                    Toasty.custom(this, getString(R.string.internet_connectivity_problem), comv19.getDrawable(this, R.drawable.ic_cancel), ContextCompat.getColor(this, R.color.error), ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        getBinding().back.setOnClickListener(view -> super.onBackPressed());

        if (TextUtils.isEmpty(var0)) {
            presenter.init();
        } else {
            init(var0);
        }
    }

    private String handleInstance(Bundle savedInstanceState) {
        String var0 = "";
        if (savedInstanceState != null && savedInstanceState.getInt(KEY_ARG_ID, 0) > 0) {
            // Восстанавливаем id из сохраненного состояния, если оно не пустое и id положительный
            var0 = savedInstanceState.getString(KEY_ARG_ID);
            DLog.d("@@R@@" + (getIntent() != null) + ", " + var0);
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(KEY_ARG_ID)) {
                // Если интент не пустой и содержит id, извлекаем его из интента
                var0 = intent.getStringExtra(KEY_ARG_ID);
            }
            DLog.d("@@N@@" + (getIntent() != null) + ", " + var0);
        }
        return var0;
    }


    @Override
    public void successResult(List<ViewModel> dataModels) {
        swap(dataModels);
    }

    @Override
    public void init(String ip) {
        binding.pingIp.setText(ip);
    }
}