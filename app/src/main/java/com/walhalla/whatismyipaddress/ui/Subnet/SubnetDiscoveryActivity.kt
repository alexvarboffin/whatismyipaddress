package com.walhalla.whatismyipaddress.ui.Subnet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aaronjwood.portauthority.adapter.HostAdapter;
import com.aaronjwood.portauthority.async.DownloadAsyncTask;
import com.aaronjwood.portauthority.async.DownloadOuisAsyncTask;

import com.aaronjwood.portauthority.async.DownloadPortDataAsyncTask;
import com.aaronjwood.portauthority.async.ScanHostsAsyncTask;
import com.aaronjwood.portauthority.db.Database0;
import com.aaronjwood.portauthority.network.Host;
import com.aaronjwood.portauthority.network.Wireless;
import com.aaronjwood.portauthority.parser.OuiParser;
import com.aaronjwood.portauthority.parser.PortParser;
import com.aaronjwood.portauthority.response.MainAsyncResponse;
import com.walhalla.boilerplate.domain.executor.impl.ThreadExecutor;
import com.walhalla.boilerplate.threading.MainThreadImpl;
import com.walhalla.domain.interactors.AdvertInteractor;
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl;
import com.walhalla.domain.repository.AdvertRepository;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.databinding.ActivitySubnetBinding;
import com.walhalla.whatismyipaddress.utils.Errors;
import com.aaronjwood.portauthority.utils.UserPreference;

import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.TApp;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public final class SubnetDiscoveryActivity extends AppCompatActivity
        implements MainAsyncResponse, DownloadOuisAsyncTask.OuisListener {


    //Subnet Discovery tool

    private final static int TIMER_INTERVAL = 1500;
    private final static int COARSE_LOCATION_REQUEST = 1;
    private final static int FINE_LOCATION_REQUEST = 2;

    private Wireless wifi;

    private String cachedWanIp;

    private String discoverHostsStr; // Cache this so it's not looked up every time a host is found.
    private ProgressDialog scanProgressDialog;
    private final Handler signalHandler = new Handler();
    private Handler scanHandler;
    private final IntentFilter intentFilter = new IntentFilter();
    private HostAdapter adapter;
    private List<Host> hosts = Collections.synchronizedList(new ArrayList<>());
    private Database0 db;
    private DownloadAsyncTask ouiTask;
    private DownloadAsyncTask portTask;
    private boolean sortAscending;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        /**
         * Detect if a network connection has been lost or established
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info == null) {
                return;
            }

            getNetworkInfo(info);
        }

    };


    private ActivitySubnetBinding binding;

    public ActivitySubnetBinding getBinding() {
        return binding;
    }

    /**
     * Activity created
     *
     * @param savedInstanceState Data from a saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubnetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBinding().back.setOnClickListener(view -> super.onBackPressed());
        getBinding().title.setText(R.string.title_subnet);


        getBinding().ping.setText(R.string.hostDiscovery);


        discoverHostsStr = getResources().getString(R.string.hostDiscovery);

        setupAdAtBottom();


        Context context = getApplicationContext();
        wifi = new Wireless(context);
        scanHandler = new Handler(Looper.getMainLooper());

        checkDatabase();
        db = Database0.getInstance(context);

        setupHostsAdapter();
        setupHostDiscovery();

        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        ssidAccess(context);
    }


    private AdvertRepository loadRepository() {
        return TApp.repository;
    }


    /**
     * Android 8+ now requires extra location permissions to read the SSID.
     * Determine what permissions to prompt the user for based on saved state.
     *
     * @param context
     */
    private void ssidAccess(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (UserPreference.getCoarseLocationPermDiag(context) || UserPreference.getFineLocationPermDiag(context)) {
                return;
            }

            Activity activity = this;
            String version = "8-9";
            String message = getResources().getString(R.string.ssidCoarseMsg, version);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                version = "10+";
                message = getResources().getString(R.string.ssidFineMsg, version);
            }

            String title = getResources().getString(R.string.ssidAccessTitle, version);
            new AlertDialog.Builder(activity, R.style.DialogTheme).setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            UserPreference.saveFineLocationPermDiag(context);
                        } else {
                            UserPreference.saveCoarseLocationPermDiag(context);
                        }

                        String perm = Manifest.permission.ACCESS_COARSE_LOCATION;
                        int request = COARSE_LOCATION_REQUEST;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            perm = Manifest.permission.ACCESS_FINE_LOCATION;
                            request = FINE_LOCATION_REQUEST;
                        }

                        if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, new String[]{perm}, request);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show().setCanceledOnTouchOutside(false);
        }
    }

    /**
     * Determines if the initial download of OUI and port data needs to be done.
     */
    public void checkDatabase() {
        if (getDatabasePath(Database0.DATABASE_NAME).exists()) {
            return;
        }

        final SubnetDiscoveryActivity activity = this;
        new AlertDialog.Builder(activity, R.style.DialogTheme)
                .setTitle(R.string.ouiDbTitle)
                .setMessage(R.string.ouiDbMsg)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    ouiTask = new DownloadOuisAsyncTask(db, new OuiParser(), activity);
                    ouiTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.cancel())
                .setIcon(android.R.drawable.ic_dialog_alert).show()
                .setCanceledOnTouchOutside(false);

        new AlertDialog.Builder(activity, R.style.DialogTheme)
                .setTitle(R.string.portDbTitle)
                .setMessage(R.string.portDbMsg)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    portTask = new DownloadPortDataAsyncTask(db, new PortParser(), activity);
                    portTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.cancel())
                .setIcon(android.R.drawable.ic_dialog_alert).show()
                .setCanceledOnTouchOutside(false);
    }

    /**
     * Sets up animations for the activity
     */
    private void setAnimations() {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(SubnetDiscoveryActivity.this, R.anim.layout_slide_in_bottom);
        binding.listView.setLayoutAnimation(animation);
    }

    /**
     * Sets up the adapter to handle discovered hosts
     */
    private void setupHostsAdapter() {
        setAnimations();
        adapter = new HostAdapter(hosts);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.listView.setLayoutManager(mLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        binding.listView.addItemDecoration(mDividerItemDecoration);
        binding.listView.setItemAnimator(new DefaultItemAnimator());
        binding.listView.setAdapter(adapter);
        if (!hosts.isEmpty()) {
            getBinding().ping.setText(discoverHostsStr + " (" + hosts.size() + ")");
        }
    }

    /**
     * Sets up the device's MAC address and vendor
     */

    @Override
    public void setupMac() {
        try {
            if (!wifi.isEnabled()) {
                binding.deviceMacAddress.setText(R.string.wifiDisabled);
                binding.deviceMacVendor.setText(R.string.wifiDisabled);
                return;
            }
            String mac = wifi.getMacAddress();
            binding.deviceMacAddress.setText(mac);
            String vendor = Host.findMacVendor(mac, db);
            binding.deviceMacVendor.setText(vendor);
        } catch (UnknownHostException | SocketException | Wireless.NoWifiManagerException e) {
            binding.deviceMacAddress.setText(R.string.noWifiConnection);
            binding.deviceMacVendor.setText(R.string.noWifiConnection);
        } catch (SQLiteException | UnsupportedOperationException e) {
            binding.deviceMacVendor.setText(R.string.getMacVendorFailed);
        } catch (Wireless.NoWifiInterface e) {
            binding.deviceMacAddress.setText(R.string.noWifiInterface);
        }
    }

    /**
     * Sets up event handlers and functionality for host discovery
     */
    private void setupHostDiscovery() {
        binding.ping.setOnClickListener(new View.OnClickListener() {

            /**
             * Click handler to perform host discovery
             * @param v
             */
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                Context context = getApplicationContext();
                try {
                    if (!wifi.isEnabled()) {
                        Errors.showError(context, resources.getString(R.string.wifiDisabled));
                        return;
                    }

                    if (!wifi.isConnectedWifi()) {
                        Errors.showError(context, resources.getString(R.string.notConnectedWifi));
                        return;
                    }
                } catch (Wireless.NoWifiManagerException |
                         Wireless.NoConnectivityManagerException e) {
                    Errors.showError(context, resources.getString(R.string.failedWifiManager));
                    return;
                }


                showProgress();

                int numSubnetHosts;
                try {
                    numSubnetHosts = wifi.getNumberOfHostsInWifiSubnet();
                } catch (Wireless.NoWifiManagerException e) {
                    Errors.showError(context, resources.getString(R.string.failedSubnetHosts));
                    return;
                }

                setAnimations();

                hosts.clear();
                getBinding().ping.setText(discoverHostsStr);
                adapter.notifyDataSetChanged();

                scanProgressDialog = new ProgressDialog(SubnetDiscoveryActivity.this, R.style.DialogTheme);
                scanProgressDialog.setCancelable(false);
                scanProgressDialog.setTitle(resources.getString(R.string.hostScan));
                scanProgressDialog.setMessage(String.format(resources.getString(R.string.subnetHosts), numSubnetHosts));
                scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                scanProgressDialog.setProgress(0);
                scanProgressDialog.setMax(numSubnetHosts);
                scanProgressDialog.show();

                try {
                    Integer ip = wifi.getInternalWifiIpAddress(Integer.class);
                    new ScanHostsAsyncTask(SubnetDiscoveryActivity.this, db).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ip, wifi.getInternalWifiSubnet(), UserPreference.getHostSocketTimeout(context));
                    binding.ping.setAlpha(.3f);
                    binding.ping.setEnabled(false);
                } catch (UnknownHostException | Wireless.NoWifiManagerException e) {
                    Errors.showError(context, resources.getString(R.string.notConnectedWifi));
                }
            }
        });

        adapter.setOnItemClickListener(host -> {
            if (host == null) {
                return;
            }
            Intent intent = new Intent(SubnetDiscoveryActivity.this, LanHostActivity.class);
            intent.putExtra(LanHostActivity.ARG_HOST, host);
            startActivity(intent);
        });

        registerForContextMenu(binding.listView);
    }

    private void showProgress() {
        binding.spinKit.setVisibility(View.VISIBLE);
    }

    /**
     * Inflate our context menu to be used on the host list
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.host_menu, menu);
        }
    }

    /**
     * Handles actions selected from the context menu for a host
     *
     * @param item
     * @return
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemId = item.getItemId();
        if (itemId == R.id.sortIp) {
            sortAscending = !sortAscending;
            if (sortAscending) {
                adapter.sortDataByIpAddressRL();
                return true;
            }
            adapter.sortDataByIpAddressLR();
            return true;
        } else if (itemId == R.id.sortHostname) {
            if (sortAscending) {
                adapter.sortDataByHostname();
            } else {
                adapter.sortDataByHostnameLR();
            }

            sortAscending = !sortAscending;
            return true;
        } else if (itemId == R.id.sortVendor) {
            if (sortAscending) {
                adapter.sortDataByVendorAscending();
            } else {
                adapter.sortDataByVendorDescending();
            }

            sortAscending = !sortAscending;
            return true;
        } else if (itemId == R.id.copyHostname) {
            if (info != null) {
                setClip("hostname", hosts.get(info.position).getHostname());
            }

            return true;
        } else if (itemId == R.id.copyIp) {
            if (info != null) {
                setClip("ip", hosts.get(info.position).getIp());
            }
            return true;
        } else if (itemId == R.id.copyMac) {
            if (info != null) {
                setClip("mac", hosts.get(info.position).getMac());
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Sets some text to the system's clipboard
     *
     * @param label Label for the text being set
     * @param text  The text to save to the system's clipboard
     */
    private void setClip(CharSequence label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }
    }

    /**
     * Gets network information about the device and updates various UI elements
     */
    private void getNetworkInfo(NetworkInfo info) {
        setupMac();
        getExternalIp();

        final Resources resources = getResources();
        final Context context = getApplicationContext();
        try {
            boolean enabled = wifi.isEnabled();
            if (!info.isConnected() || !enabled) {
                signalHandler.removeCallbacksAndMessages(null);
                binding.internalIpAddress.setText(Wireless.getInternalMobileIpAddress());
            }

            if (!enabled) {
                binding.signalStrength.setText(R.string.wifiDisabled);
                binding.ssid.setText(R.string.wifiDisabled);
                binding.bssid.setText(R.string.wifiDisabled);
                return;
            }
        } catch (Wireless.NoWifiManagerException e) {
            Errors.showError(context, resources.getString(R.string.failedWifiManager));
        }

        if (!info.isConnected()) {
            binding.signalStrength.setText(R.string.noWifiConnection);
            binding.ssid.setText(R.string.noWifiConnection);
            binding.bssid.setText(R.string.noWifiConnection);
            return;
        }

        signalHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int signal;
                int speed;
                try {
                    speed = wifi.getLinkSpeed();
                } catch (Wireless.NoWifiManagerException e) {
                    Errors.showError(context, resources.getString(R.string.failedLinkSpeed));
                    return;
                }
                try {
                    signal = wifi.getSignalStrength();
                } catch (Wireless.NoWifiManagerException e) {
                    Errors.showError(context, resources.getString(R.string.failedSignal));
                    return;
                }

                binding.signalStrength.setText(String.format(resources.getString(R.string.signalLink), signal, speed));
                signalHandler.postDelayed(this, TIMER_INTERVAL);
            }
        }, 0);

        getInternalIp();

        String wifiSsid;
        String wifiBssid;
        try {
            wifiSsid = wifi.getSSID();
        } catch (Wireless.NoWifiManagerException e) {
            Errors.showError(context, resources.getString(R.string.failedSsid));
            return;
        }
        try {
            wifiBssid = wifi.getBSSID();
        } catch (Wireless.NoWifiManagerException e) {
            Errors.showError(context, resources.getString(R.string.failedBssid));
            return;
        }
        binding.ssid.setText(wifiSsid);
        binding.bssid.setText(wifiBssid);
    }


    /**
     * Wrapper method for getting the internal wireless IP address.
     * This gets the netmask, counts the bits set (subnet size),
     * then prints it along side the IP.
     */
    private void getInternalIp() {
        try {
            int netmask = wifi.getInternalWifiSubnet();
            String internalIpWithSubnet = wifi.getInternalWifiIpAddress(String.class) + "/" + netmask;
            binding.internalIpAddress.setText(internalIpWithSubnet);
        } catch (UnknownHostException | Wireless.NoWifiManagerException e) {
            Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
        }
    }

    /**
     * Wrapper for getting the external IP address
     * We can control whether or not to do this based on the user's preference
     * If the user doesn't want this then hide the appropriate views
     */
    private void getExternalIp() {
        if (UserPreference.getFetchExternalIp(this)) {
            binding.externalIpAddressLabel.setVisibility(View.VISIBLE);
            binding.externalIpAddress.setVisibility(View.VISIBLE);
            if (cachedWanIp == null) {
                wifi.getExternalIpAddress(this);
            }
        } else {
            binding.externalIpAddressLabel.setVisibility(View.GONE);
            binding.externalIpAddress.setVisibility(View.GONE);
        }
    }

    /**
     * Activity paused
     */
    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
        signalHandler.removeCallbacksAndMessages(null);

        if (scanProgressDialog != null) {
            scanProgressDialog.dismiss();
        }

        if (ouiTask != null) {
            ouiTask.cancel(true);
        }

        if (portTask != null) {
            portTask.cancel(true);
        }

        scanProgressDialog = null;
        ouiTask = null;
        portTask = null;
    }

    /**
     * Activity resumed.
     */
    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(receiver, intentFilter);
    }

    /**
     * Save the state of an activity
     *
     * @param savedState Data to save
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedState) {
        super.onSaveInstanceState(savedState);

        HostAdapter adapter = (HostAdapter) binding.listView.getAdapter();
        if (adapter != null) {
            ArrayList<Host> adapterData = new ArrayList<>();
            for (int i = 0; i < adapter.getItemCount(); i++) {
                Host item = (Host) adapter.getItem(i);
                adapterData.add(item);
            }
            savedState.putSerializable("hosts", adapterData);
            savedState.putString("wanIp", cachedWanIp);
        }
    }

    /**
     * Activity state restored
     *
     * @param savedState Saved data from the saved state
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onRestoreInstanceState(@NonNull Bundle savedState) {
        super.onRestoreInstanceState(savedState);

        cachedWanIp = savedState.getString("wanIp");
        binding.externalIpAddress.setText(cachedWanIp);
        hosts = (ArrayList<Host>) savedState.getSerializable("hosts");
        if (hosts != null) {
            setupHostsAdapter();
        }
    }

    /**
     * Delegate to update the host list and dismiss the progress dialog
     * Gets called when host discovery has finished
     *
     * @param h The host to add to the list of discovered hosts
     * @param i Number of hosts
     */
    @Override
    public void processFinish(final Host h, final AtomicInteger i) {
        scanHandler.post(() -> {
            if (h != null) {
                hosts.add(h);

            }
            //DLog.d("@@@@@@@@@@@@@" + h);
            adapter.sortDataByIpAddressLR();


            getBinding().ping.setText(discoverHostsStr + " (" + hosts.size() + ")");
            if (i.decrementAndGet() == 0) {
                binding.ping.setAlpha(1);
                binding.ping.setEnabled(true);
                binding.spinKit.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Delegate to update the progress of the host discovery scan
     *
     * @param output The amount of progress to increment by
     */
    @Override
    public void processFinish(int output) {
        if (scanProgressDialog != null && scanProgressDialog.isShowing()) {
            scanProgressDialog.incrementProgressBy(output);
        }
    }

    /**
     * Delegate to handle setting the external IP in the UI
     *
     * @param output External IP
     */
    @Override
    public void processFinish(String output) {
        cachedWanIp = output;
        binding.externalIpAddress.setText(output);
    }

    /**
     * Delegate to dismiss the progress dialog
     *
     * @param output
     */
    @Override
    public void processFinish(final boolean output) {
        scanHandler.post(() -> {
            if (output && scanProgressDialog != null && scanProgressDialog.isShowing()) {
                scanProgressDialog.dismiss();
            }
        });
    }

    /**
     * Delegate to handle bubbled up errors
     *
     * @param output The exception we want to handle
     * @param <T>    Exception
     */
    @Override
    public <T extends Throwable> void processFinish(final T output) {
        scanHandler.post(() -> Errors.showError(getApplicationContext(), output.getLocalizedMessage()));
    }

    private final long start_time = System.currentTimeMillis();
    private FrameLayout content;

    private final AdvertInteractor.Callback<View> callback = new AdvertInteractor.Callback<>() {
        @Override
        public void onMessageRetrieved(int id, View message) {
            DLog.d(message.getClass().getName() + " --> " + message.hashCode());

            if (content != null) {
                DLog.d("@@@" + content.getClass().getName());
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
                        @SuppressLint("ObsoleteSdkInt")
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


    private void setupAdAtBottom() {

        //FrameLayout content = findViewById(android.R.id.content);
        content = binding.bottomButton;

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

        AdvertInteractorImpl interactor = new AdvertInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(), loadRepository());
        //aa.attach(this);
        //DLog.d("---->" + aa.hashCode());
        interactor.selectView(content, callback);
    }
}