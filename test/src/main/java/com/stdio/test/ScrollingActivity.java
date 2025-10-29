package com.stdio.test;

import android.os.Bundle;

import com.aaronjwood.portauthority.utils.NetworkUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.stdio.test.databinding.ActivityScrollingBinding;
import com.walhalla.ui.DLog;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG0 = "@@@";
    private ActivityScrollingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private String getJSON1(int timeout, String ua) {

        try {
            OkHttpClient httpClient = NetworkUtils.makeOkhttp();
            String url = "http://ip-api.com/json";
            Request request = new Request.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:50.0) Gecko/20100101 Firefox/50.0") //optional
                    .url(url)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d(TAG0, responseBody);
            } else {
                Log.d(TAG0, "Request was not successful: " + response.code());
            }

        } catch (Exception ex) {
            DLog.handleException(ex);
        }
        return null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}