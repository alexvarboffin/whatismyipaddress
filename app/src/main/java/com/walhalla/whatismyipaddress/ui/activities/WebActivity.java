package com.walhalla.whatismyipaddress.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.databinding.ActivityMyBinding;

import im.delight.android.webview.AdvancedWebView;

import static androidx.browser.browseractions.BrowserActionsIntent.KEY_TITLE;
import static androidx.browser.customtabs.CustomTabsService.KEY_URL;

public class WebActivity extends AppCompatActivity
        implements AdvancedWebView.Listener {

    private ActivityMyBinding binding;
    private String title = "";

    public static Intent newInstance(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        Bundle b = new Bundle();
        b.putString(KEY_URL, url);
        b.putString(KEY_TITLE, title);
        intent.putExtras(b);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Bundle bundle = getIntent().getExtras();
        String url = ""; // or other values
        if (bundle != null) {
            url = bundle.getString(KEY_URL);
            title = bundle.getString(KEY_TITLE);
        }

        binding.webView.setListener(this, this);
        binding.webView.setMixedContentAllowed(false);
        binding.webView.loadUrl(url);


//        if (BuildConfig.DEBUG) {
//            mBinding.adView.setVisibility(View.GONE);
//        } else {
        binding.adView.loadAd(new AdRequest.Builder().build());
//        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        binding.toolbar.setSubtitle(title);
        binding.webView.onResume();
        if (binding.adView != null) {
            binding.adView.resume();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        binding.webView.onPause();
        if (binding.adView != null) {
            binding.adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding.webView.onDestroy();
        if (binding.adView != null) {
            binding.adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        binding.webView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (!binding.webView.onBackPressed()) {
            return;
        }
        // ...
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        showProgressBar();
    }

    @Override
    public void onPageFinished(String url) {
        hideProgressBar();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        DLog.d(description + errorCode);
        hideProgressBar();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType,
                                    long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    public void showProgressBar() {
        if (null != findViewById(R.id.scan_progress)) {
            ((ProgressBar) findViewById(R.id.scan_progress)).setIndeterminate(true);
        }
    }


    public void hideProgressBar() {
        if (null != findViewById(R.id.scan_progress)) {
            ((ProgressBar) findViewById(R.id.scan_progress)).setIndeterminate(false);
        }
    }
}
