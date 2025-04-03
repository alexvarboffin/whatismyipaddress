package com.walhalla.whatismyipaddress.ui.Subnet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aaronjwood.portauthority.db.Database0;
import com.aaronjwood.portauthority.listener.ScanPortsListener;
import com.aaronjwood.portauthority.network.Host;
import com.aaronjwood.portauthority.response.HostAsyncResponse;
import com.aaronjwood.portauthority.utils.Constants;

import com.walhalla.boilerplate.domain.executor.impl.ThreadExecutor;
import com.walhalla.boilerplate.threading.MainThreadImpl;
import com.walhalla.domain.interactors.AdvertInteractor;
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl;
import com.walhalla.domain.repository.AdvertRepository;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.utils.Errors;
import com.aaronjwood.portauthority.utils.UserPreference;
import com.walhalla.whatismyipaddress.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class HostActivity extends AppCompatActivity
        implements HostAsyncResponse {
    protected final long start_time = System.currentTimeMillis();
    private FrameLayout content;

    private final AdvertInteractor.Callback<View> callback = new AdvertInteractor.Callback<>() {
        @Override
        public void onMessageRetrieved(int id, View message) {
            DLog.d(message.getClass().getName() + " --> " + message.hashCode());

            if (content != null) {
                DLog.d("@@@@@@@@@@" + content.getClass().getName());
                try {
                    //content.removeView(message);
                    if (message.getParent() != null) {
                        ((ViewGroup) message.getParent()).removeView(message);
                    }
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    message.setLayoutParams(params);


                    ViewTreeObserver vto = message.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT < 16) {
                                message.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                message.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            //int width = message.getMeasuredWidth();
                            //int height = message.getMeasuredHeight();
                            //DLog.i("@@@@" + height + "x" + width);
                            //setSpaceForAd(height);
                        }
                    });
                    content.addView(message);

                } catch (Exception e) {
                    DLog.handleException(e);
                }
            }
        }

        @Override
        public void onRetrievalFailed(String error) {
            DLog.d("---->" + error);
        }
    };

    protected ArrayAdapter<String> adapter;

    protected final List<String> ports = Collections.synchronizedList(new ArrayList<>());
    protected ProgressDialog scanProgressDialog;
    protected Dialog portRangeDialog;
    protected Handler handler;
    private Database0 database0;

    private AdvertInteractorImpl interactor;


    /**
     * Activity created
     *
     * @param savedInstanceState Data from a saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactor = new AdvertInteractorImpl(ThreadExecutor.getInstance(), MainThreadImpl.getInstance(), loadRepository());
        database0 = Database0.getInstance(getApplicationContext());
        handler = new Handler(Looper.getMainLooper());
    }


    /**
     * Activity paused
     */
    @Override
    public void onPause() {
        super.onPause();

        if (scanProgressDialog != null && scanProgressDialog.isShowing()) {
            scanProgressDialog.dismiss();
        }
        if (portRangeDialog != null && portRangeDialog.isShowing()) {
            portRangeDialog.dismiss();
        }
        scanProgressDialog = null;
        portRangeDialog = null;
    }

    /**
     * Save the state of the activity
     *
     * @param savedState Data to save
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedState) {
        super.onSaveInstanceState(savedState);

        String[] savedList = ports.toArray(new String[0]);
        savedState.putStringArray("ports", savedList);
    }

    /**
     * Restore saved data
     *
     * @param savedInstanceState Data from a saved state
     */
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String[] savedList = savedInstanceState.getStringArray("ports");
        if (savedList != null) {
            ports.addAll(Arrays.asList(savedList));
        }

        setupPortsAdapter();
    }

    protected abstract void setupPortsAdapter();

    /**
     * Event handler for when the port range reset is triggered
     *
     * @param start Starting port picker
     * @param stop  Stopping port picker
     */
    protected void resetPortRangeScanClick(final NumberPicker start, final NumberPicker stop) {
        portRangeDialog.findViewById(R.id.resetPortRangeScan).setOnClickListener(v -> {
            start.setValue(Constants.MIN_PORT_VALUE);
            stop.setValue(Constants.MAX_PORT_VALUE);
        });
    }

    /**
     * Event handler for when the port range scan is finally initiated
     *
     * @param start    Starting port picker
     * @param stop     Stopping port picker
     * @param timeout  Socket timeout
     * @param activity Calling activity
     * @param ip       IP address
     */
    protected void startPortRangeScanClick(final NumberPicker start, final NumberPicker stop, final int timeout, final HostActivity activity, final String ip) {
        Button startPortRangeScan = portRangeDialog.findViewById(R.id.startPortRangeScan);
        startPortRangeScan.setOnClickListener(new ScanPortsListener(ports, adapter) {

            /**
             * Click handler for starting a port range scan
             * @param v
             */
            @Override
            public void onClick(View v) {
                super.onClick(v);

                start.clearFocus();
                stop.clearFocus();

                int startPort = start.getValue();
                int stopPort = stop.getValue();
                if ((startPort - stopPort > 0)) {
                    Toast.makeText(getApplicationContext(), "Please pick a valid port range", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserPreference.savePortRangeStart(activity, startPort);
                UserPreference.savePortRangeHigh(activity, stopPort);

                scanProgressDialog = new ProgressDialog(activity, R.style.DialogTheme);
                scanProgressDialog.setCancelable(false);
                String titleTemplate = getResources().getString(R.string.scanning_port_range);
                String title = String.format(titleTemplate, startPort, stopPort);

                scanProgressDialog.setTitle(title);
                scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                scanProgressDialog.setProgress(0);
                scanProgressDialog.setMax(stopPort - startPort + 1);
//                scanProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.cancel),
//                        new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Действия при нажатии кнопки "Отмена"
//                        scanProgressDialog.dismiss(); // Закрыть диалоговое окно
//                    }
//                });
                scanProgressDialog.show();

                Host.scanPorts(ip, startPort, stopPort, timeout, activity);
            }
        });
    }

    /**
     * Event handler for when an item on the port list is clicked
     */
    protected void portListClick(ListView portList, final String ip) {
        portList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * Click handler to open certain ports to the browser
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) portList.getItemAtPosition(position);
                if (item == null) {
                    return;
                }

                Intent intent = null;

                if (item.contains("80 -")) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + ip));
                }

                if (item.contains("443 -")) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + ip));
                }

                if (item.contains("8080 -")) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + ip + ":8080"));
                }

                PackageManager packageManager = getPackageManager();
                if (intent != null && packageManager != null) {
                    if (packageManager.resolveActivity(intent, 0) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No application found to open this to the browser!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Delegate to handle incrementing the scan progress dialog
     *
     * @param output The amount of progress to increment
     */
    @Override
    public void processFinish(final int output) {
        handler.post(() -> {
            if (scanProgressDialog != null) {
                scanProgressDialog.incrementProgressBy(output);
            }
        });
    }

    /**
     * Delegate to handle open ports
     *
     * @param output Contains the port number and associated banner (if any)
     */
    @Override
    public void processFinish(SparseArray<String> output) {
        int scannedPort = output.keyAt(0);
        String item = String.valueOf(scannedPort);

        String name = database0.selectPortDescription(String.valueOf(scannedPort));
        name = (name.isEmpty()) ? "unknown" : name;
        item = formatOpenPort(output, scannedPort, name, item);
        addOpenPort(item);
    }

    /**
     * Formats a found open port with its name, description, and associated visualization
     *
     * @param entry       Structure holding information about the found open port with its description
     * @param scannedPort The port number
     * @param portName    Friendly name for the port
     * @param item        Contains the transformed output for the open port
     * @return If all associated data is found a port along with its description, underlying service, and visualization is constructed
     */
    private String formatOpenPort(SparseArray<String> entry, int scannedPort, String portName, String item) {
        String data = item + " - " + portName;
        if (entry.get(scannedPort) != null) {
            data += " (" + entry.get(scannedPort) + ")";
        }

        //If the port is in any way related to HTTP then present a nice globe icon next to it via unicode
        if (scannedPort == 80 || scannedPort == 443 || scannedPort == 8080) {
            data += " \uD83C\uDF0E";
        }

        return data;
    }

    /**
     * Adds an open port that was found on a host to the list
     *
     * @param port Port number and description
     */
    private void addOpenPort(final String port) {
        setAnimations();
        handler.post(() -> {
            ports.add(port);
            Collections.sort(ports, (lhs, rhs) -> {
                int left = Integer.parseInt(lhs.substring(0, lhs.indexOf('-') - 1));
                int right = Integer.parseInt(rhs.substring(0, rhs.indexOf('-') - 1));

                return left - right;
            });

            adapter.notifyDataSetChanged();
        });
    }

    protected abstract void setAnimations();

    /**
     * Delegate to handle bubbled up errors
     *
     * @param output The exception we want to handle
     * @param <T>    Exception
     */
    public <T extends Throwable> void processFinish(final T output) {
        handler.post(() -> Errors.showError(getApplicationContext(), output.getLocalizedMessage()));
    }


    protected void setupAdAtBottom(FrameLayout content) {

        //FrameLayout content = findViewById(android.R.id.content);
        this.content = content;

//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.BOTTOM;

//        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater()
//                .inflate(R.layout.ad_layout, null);
//        linearLayout.setLayoutParams(params);
//
//        // adding viewtreeobserver to get height of linearLayout layout , so that
//        // android.R.id.content will set margin of that height
//        ViewTreeObserver vto = linearLayout.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @SuppressLint("ObsoleteSdkInt")
//            @Override
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT < 16) {
//                    linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                } else {
//                    linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//                int width = linearLayout.getMeasuredWidth();
//                int height = linearLayout.getMeasuredHeight();
//                //DLog.i("@@@@" + height + "x" + width);
//                setSpaceForAd(height);
//            }
//        });
//        addLayoutToContent(linearLayout);
        //aa.attach(this);
        //DLog.d("---->" + aa.hashCode());
        interactor.selectView(content, callback);
    }

    protected abstract AdvertRepository loadRepository();

//    private void setSpaceForAd(int height) {
//        DLog.d("@@@@@@@@" + height);
////        FrameLayout content = findViewById(android.R.id.content);
////        if (content != null) {
////            View child0 = content.getChildAt(0);
////            //child0.setPadding(0, 0, 0, 50);
////
////            FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) child0.getLayoutParams();
////            //lp.bottomMargin = height;
////            child0.setLayoutParams(lp);
////        }
//    }

////    AdView adView = new AdView(this);
////    adView.setAdSize(AdSize.BANNER);
////    adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
//
//    private void addLayoutToContent(View ad) {
//        content.addView(ad);
//        AdView mAdView = ad.findViewById(R.id.adView);
//        //mAdView.setAdListener(new AdListener(mAdView));
//        mAdView.loadAd(new AdRequest.Builder().build());
//    }
}
