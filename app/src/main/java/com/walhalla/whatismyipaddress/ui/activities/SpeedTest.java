
package com.walhalla.whatismyipaddress.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.walhalla.netdiscover.core.Speedtest;
import com.walhalla.netdiscover.core.config.SpeedtestConfig;
import com.walhalla.netdiscover.core.config.TelemetryConfig;
import com.walhalla.netdiscover.core.serverSelector.TestPoint;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.GaugeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class SpeedTest extends AppCompatActivity {

    //    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AdView mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        transition(R.id.page_splash, 0);
        new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (Throwable t) {
                }
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    final ImageView v = findViewById(R.id.testBackground);
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.drawable.testbackground, options);
                    int ih = options.outHeight, iw = options.outWidth;
                    if (4 * ih * iw > 16 * 1024 * 1024) throw new Exception("Too big");
                    options.inJustDecodeBounds = false;
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int vh = displayMetrics.heightPixels, vw = displayMetrics.widthPixels;
                    double desired = Math.max(vw, vh) * 0.7;
                    double scale = desired / Math.max(iw, ih);
                    final Bitmap b = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.testbackground, options), (int) (iw * scale), (int) (ih * scale), true);
                    runOnUiThread(() -> v.setImageBitmap(b));
                } catch (Throwable t) {
                    DLog.d("Failed to load testbackground (" + t.getMessage() + ")");
                }
                page_init();
            }
        }.start();

        findViewById(R.id.back)
                .setOnClickListener(view -> SpeedTest.super.onBackPressed());

//        mInterstitialAd = new InterstitialAd(SpeedTest.this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SpeedTest.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() { if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    } else {
//                        Log.d("TAG", "The interstitial wasn't loaded yet.");
//                    }
//                    }
//                });
//            }
//        }, 6000);
    }

    private Speedtest st = null;

    private void page_init() {
        new Thread() {
            @Override
            public void run() {
                runOnUiThread(() -> transition(R.id.page_init, TRANSITION_LENGTH));
                final TextView t = findViewById(R.id.init_text);
                runOnUiThread(() -> t.setText(R.string.init_init));
                SpeedtestConfig config = null;
                TelemetryConfig telemetryConfig = null;
                TestPoint[] servers = null;
                try {
                    String c = readFileFromAssets("SpeedtestConfig.json");
                    JSONObject o = new JSONObject(c);
                    config = new SpeedtestConfig(o);
                    c = readFileFromAssets("TelemetryConfig.json");
                    o = new JSONObject(c);
                    telemetryConfig = new TelemetryConfig(o);
                    if (telemetryConfig.getTelemetryLevel().equals(TelemetryConfig.LEVEL_DISABLED)) {
                        runOnUiThread(() -> hideView(R.id.privacy_open));
                    }
                    if (st != null) {
                        try {
                            st.abort();
                        } catch (Throwable e) {
                        }
                    }
                    st = new Speedtest();
                    st.setSpeedtestConfig(config);
                    st.setTelemetryConfig(telemetryConfig);
                    c = readFileFromAssets("ServerList.json");
//                    if(c.startsWith("\"")||c.startsWith("'")){ //fetch server list from URL
//                        if(!st.loadServerList(c.subSequence(1,c.length()-1).toString())){
//                            throw new Exception("Failed to load server list");
//                        }
//                    }else{ //use provided server list
                    JSONArray a = new JSONArray(c);
                    if (a.length() == 0) throw new Exception("No test points");
                    ArrayList<TestPoint> s = new ArrayList<>();
                    for (int i = 0; i < a.length(); i++) s.add(new TestPoint(a.getJSONObject(i)));
                    servers = s.toArray(new TestPoint[0]);
                    st.addTestPoints(servers);
//                    }
                    final String testOrder = config.getTest_order();
                    runOnUiThread(() -> {
                        if (!testOrder.contains("D")) {
                            hideView(R.id.dlArea);
                        }
                        if (!testOrder.contains("U")) {
                            hideView(R.id.ulArea);
                        }
                        if (!testOrder.contains("P")) {
                            hideView(R.id.pingArea);
                        }
                        if (!testOrder.contains("I")) {
                            hideView(R.id.ipInfo);
                        }
                    });
                } catch (final Throwable e) {
                    DLog.d(e.toString());

                    st = null;
                    transition(R.id.page_fail, TRANSITION_LENGTH);
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.fail_text)).setText(getString(R.string.initFail_configError) + ": " + e.getMessage());
                        final Button b = findViewById(R.id.fail_button);
                        b.setText(R.string.initFail_retry);
                        b.setOnClickListener(v -> {
                            page_init();
                            b.setOnClickListener(null);
                        });
                    });
                    return;
                }
                runOnUiThread(() -> t.setText(R.string.init_selecting));
                st.selectServer(new Speedtest.ServerSelectedHandler() {
                    @Override
                    public void onServerSelected(final TestPoint server) {
                        runOnUiThread(() -> {
                            if (server == null) {
                                transition(R.id.page_fail, TRANSITION_LENGTH);
                                ((TextView) findViewById(R.id.fail_text)).setText(getString(R.string.initFail_noServers));
                                final Button b = findViewById(R.id.fail_button);
                                b.setText(R.string.initFail_retry);
                                b.setOnClickListener(v -> {
                                    page_init();
                                    b.setOnClickListener(null);
                                });
                            } else {
                                page_serverSelect(server, st.getTestPoints());
                            }
                        });
                    }
                });
            }
        }.start();
    }

    private void page_serverSelect(TestPoint selected, TestPoint[] servers) {
        transition(R.id.page_serverSelect, TRANSITION_LENGTH);
        reinitOnResume = true;
        final ArrayList<TestPoint> availableServers = new ArrayList<>();
        for (TestPoint t : servers) {
            if (t.getPing() != -1) availableServers.add(t);
        }
        int selectedId = availableServers.indexOf(selected);
        final Spinner spinner = findViewById(R.id.serverList);
        ArrayList<String> options = new ArrayList<>();
        for (TestPoint t : availableServers) {
            options.add(t.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options.toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedId);
        final Button b = findViewById(R.id.start);
        b.setOnClickListener(v -> {
            reinitOnResume = false;
            page_test(availableServers.get(spinner.getSelectedItemPosition()));
            b.setOnClickListener(null);
        });
        TextView t = findViewById(R.id.privacy_open);
        t.setOnClickListener(v -> page_privacy());
    }

    private void page_privacy() {
        transition(R.id.page_privacy, TRANSITION_LENGTH);
        reinitOnResume = false;
        ((WebView) findViewById(R.id.privacy_policy)).loadUrl(getString(R.string.url_privacy_policy));
        TextView t = findViewById(R.id.privacy_close);
        t.setOnClickListener(v -> {
            transition(R.id.page_serverSelect, TRANSITION_LENGTH);
            reinitOnResume = true;
        });
    }

    private void page_test(final TestPoint selected) {
        transition(R.id.page_test, TRANSITION_LENGTH);
        try {
            st.setSelectedServer(selected);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        ((TextView) findViewById(R.id.serverName)).setText(selected.getName());
        ((TextView) findViewById(R.id.dlText)).setText(format(0));
        ((TextView) findViewById(R.id.ulText)).setText(format(0));
        ((TextView) findViewById(R.id.pingText)).setText(format(0));
        ((TextView) findViewById(R.id.jitterText)).setText(format(0));
        ((ProgressBar) findViewById(R.id.dlProgress)).setProgress(0);
        ((ProgressBar) findViewById(R.id.ulProgress)).setProgress(0);
        ((GaugeView) findViewById(R.id.dlGauge)).setValue(0);
        ((GaugeView) findViewById(R.id.ulGauge)).setValue(0);
        ((TextView) findViewById(R.id.ipInfo)).setText("");

        findViewById(R.id.logo_inapp)
                .setOnClickListener(v -> {
//            String url = getString(R.string.logo_inapp_link);
//            if (url.isEmpty()) return;
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
                });
        final View endTestArea = findViewById(R.id.endTestArea);
        final int endTestAreaHeight = endTestArea.getHeight();
        ViewGroup.LayoutParams p = endTestArea.getLayoutParams();
        p.height = 0;
        endTestArea.setLayoutParams(p);
        findViewById(R.id.shareButton).setVisibility(View.GONE);
        st.start(new Speedtest.SpeedtestHandler() {
            @Override
            public void onDownloadUpdate(final double dl, final double progress) {
                runOnUiThread(() -> {
                    ((TextView) findViewById(R.id.dlText)).setText(progress == 0 ? "..." : format(dl));
                    ((GaugeView) findViewById(R.id.dlGauge)).setValue(progress == 0 ? 0 : mbpsToGauge(dl));
                    ((ProgressBar) findViewById(R.id.dlProgress)).setProgress((int) (100 * progress));
                });
            }

            @Override
            public void onUploadUpdate(final double ul, final double progress) {
                runOnUiThread(() -> {
                    ((TextView) findViewById(R.id.ulText)).setText(progress == 0 ? "..." : format(ul));
                    ((GaugeView) findViewById(R.id.ulGauge)).setValue(progress == 0 ? 0 : mbpsToGauge(ul));
                    ((ProgressBar) findViewById(R.id.ulProgress)).setProgress((int) (100 * progress));
                });

            }

            @Override
            public void onPingJitterUpdate(final double ping, final double jitter, final double progress) {
                runOnUiThread(() -> {
                    ((TextView) findViewById(R.id.pingText)).setText(progress == 0 ? "..." : format(ping));
                    ((TextView) findViewById(R.id.jitterText)).setText(progress == 0 ? "..." : format(jitter));
                });
            }

            @Override
            public void onIPInfoUpdate(final String ipInfo) {
                runOnUiThread(() -> ((TextView) findViewById(R.id.ipInfo)).setText(ipInfo));
            }

            @Override
            public void onTestIDReceived(final String id, final String shareURL) {
                if (shareURL == null || shareURL.isEmpty() || id == null || id.isEmpty()) return;
                runOnUiThread(() -> {
                    Button shareButton = findViewById(R.id.shareButton);
                    shareButton.setVisibility(View.INVISIBLE);
                    shareButton.setOnClickListener(v -> {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, shareURL);
                        startActivity(Intent.createChooser(share, getString(R.string.test_share)));
                    });
                });
            }

            @Override
            public void onEnd() {
                runOnUiThread(() -> {
                    final Button restartButton = findViewById(R.id.restartButton);
                    restartButton.setOnClickListener(v -> {
                        page_init();
                        restartButton.setOnClickListener(null);
                    });
                });
                final long startT = System.currentTimeMillis(), endT = startT + TRANSITION_LENGTH;
                new Thread() {
                    public void run() {
                        while (System.currentTimeMillis() < endT) {
                            final double f = (double) (System.currentTimeMillis() - startT) / (double) (endT - startT);
                            runOnUiThread(() -> {
                                ViewGroup.LayoutParams p1 = endTestArea.getLayoutParams();
                                p1.height = (int) (endTestAreaHeight * f);
                                endTestArea.setLayoutParams(p1);
                            });
                            try {
                                sleep(10);
                            } catch (Throwable t) {
                            }
                        }
                    }
                }.start();
            }

            @Override
            public void onCriticalFailure(String err) {
                runOnUiThread(() -> {
                    transition(R.id.page_fail, TRANSITION_LENGTH);
                    ((TextView) findViewById(R.id.fail_text)).setText(getString(R.string.testFail_err));
                    final Button b = findViewById(R.id.fail_button);
                    b.setText(R.string.testFail_retry);
                    b.setOnClickListener(v -> {
                        page_init();
                        b.setOnClickListener(null);
                    });
                });
            }
        });
    }

    private String format(double d) {
        Locale l;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            l = getResources().getConfiguration().getLocales().get(0);
        } else {
            l = getResources().getConfiguration().locale;
        }
        if (d < 10) return String.format(l, "%.2f", d);
        if (d < 100) return String.format(l, "%.1f", d);
        return "" + Math.round(d);
    }

    private int mbpsToGauge(double s) {
        return (int) (1000 * (1 - (1 / (Math.pow(1.3, Math.sqrt(s))))));
    }

    private String readFileFromAssets(String name) throws Exception {
        BufferedReader b = new BufferedReader(new InputStreamReader(getAssets().open(name)));
        StringBuilder ret = new StringBuilder();
        try {
            for (; ; ) {
                String s = b.readLine();
                if (s == null) break;
                ret.append(s);
            }
        } catch (EOFException e) {
        }
        return ret.toString();
    }

    private void hideView(int id) {
        View v = findViewById(id);
        if (v != null) v.setVisibility(View.GONE);
    }

    private boolean reinitOnResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (reinitOnResume) {
            reinitOnResume = false;
            page_init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            st.abort();
            st = null;
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPage == R.id.page_privacy)
            transition(R.id.page_serverSelect, TRANSITION_LENGTH);
        else super.onBackPressed();
    }

    //PAGE TRANSITION SYSTEM

    private int currentPage = -1;
    private boolean transitionBusy = false;
    private final int TRANSITION_LENGTH = 300;

    private void transition(final int page, final int duration) {
        if (transitionBusy) {
            new Thread() {
                public void run() {
                    try {
                        sleep(10);
                    } catch (Throwable t) {
                    }
                    transition(page, duration);
                }
            }.start();
        } else transitionBusy = true;
        if (page == currentPage) return;
        final ViewGroup oldPage = currentPage == -1 ? null : (ViewGroup) findViewById(currentPage),
                newPage = page == -1 ? null : (ViewGroup) findViewById(page);
        new Thread() {
            public void run() {
                long t = System.currentTimeMillis(), endT = t + duration;
                runOnUiThread(() -> {
                    if (newPage != null) {
                        newPage.setAlpha(0);
                        newPage.setVisibility(View.VISIBLE);
                    }
                    if (oldPage != null) {
                        oldPage.setAlpha(1);
                    }
                });
                while (t < endT) {
                    t = System.currentTimeMillis();
                    final float f = (float) (endT - t) / (float) duration;
                    runOnUiThread(() -> {
                        if (newPage != null) newPage.setAlpha(1 - f);
                        if (oldPage != null) oldPage.setAlpha(f);
                    });
                    try {
                        sleep(10);
                    } catch (Throwable ignored) {
                    }
                }
                currentPage = page;
                runOnUiThread(() -> {
                    if (oldPage != null) {
                        oldPage.setAlpha(0);
                        oldPage.setVisibility(View.INVISIBLE);
                    }
                    if (newPage != null) {
                        newPage.setAlpha(1);
                    }
                    transitionBusy = false;
                });
            }
        }.start();
    }
}
