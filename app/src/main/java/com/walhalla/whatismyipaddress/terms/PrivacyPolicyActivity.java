package com.walhalla.whatismyipaddress.terms;

import static com.walhalla.whatismyipaddress.AssetUtils.loadFromAsset;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.databinding.ActivityPrivacyPolicyBinding;
import com.walhalla.whatismyipaddress.databinding.FragmentPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ActivityPrivacyPolicyBinding binding;


    //private FragmentPrivacyPolicyBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        //binding.textView1.setText(loadFromAsset(this, "privacy_en.html"));

//        binding = FragmentPrivacyPolicyBinding.inflate(getLayoutInflater());
//        setContentView(R.layout.activity_privacy_policy);
        binding.wvpolicy.loadDataWithBaseURL(null, AssetUtils.loadFromAsset(this, "privacy_en.html"), "text/html", "UTF-8", null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}