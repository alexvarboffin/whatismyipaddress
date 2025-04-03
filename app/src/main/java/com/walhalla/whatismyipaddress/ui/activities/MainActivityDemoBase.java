package com.walhalla.whatismyipaddress.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.walhalla.whatismyipaddress.databinding.ActivityPingBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;


public class MainActivityDemoBase extends BaseListActivity {

    private ActivityPingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPingBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setupAdAtBottom();
        test();
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }


    private void test() {
        long time = System.currentTimeMillis();
        long delta = time - start_time;
        Log.d("[@@@@@@]", " " + delta);//1785
    }
}
