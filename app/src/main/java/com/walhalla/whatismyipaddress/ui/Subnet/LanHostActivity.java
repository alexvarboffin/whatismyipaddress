package com.walhalla.whatismyipaddress.ui.Subnet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aaronjwood.portauthority.listener.ScanPortsListener;
import com.aaronjwood.portauthority.network.Host;
import com.aaronjwood.portauthority.network.Wireless;
import com.aaronjwood.portauthority.utils.Constants;
import com.walhalla.domain.repository.AdvertRepository;
import com.walhalla.whatismyipaddress.databinding.ActivitySubnetLanhostBinding;
import com.walhalla.whatismyipaddress.utils.Errors;
import com.aaronjwood.portauthority.utils.UserPreference;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.TApp;

public final class LanHostActivity extends HostActivity {

    public static final String ARG_HOST = "HOST";

    private Wireless wifi;
    private Host host;

    public ActivitySubnetLanhostBinding getBinding() {
        return binding;
    }

    private ActivitySubnetLanhostBinding binding;


    /**
     * Activity created
     *
     * @param savedInstanceState Data from a saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubnetLanhostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        host = (Host) extras.get(ARG_HOST);
        if (host == null) {
            return;
        }

        binding.back.setOnClickListener(view -> super.onBackPressed());

        wifi = new Wireless(getApplicationContext());
        binding.hostMacVendor.setText(host.getVendor());
        binding.hostName.setText(host.getHostname());
        binding.hostMac.setText(host.getMac());
        binding.ipAddress.setText(host.getIp());

        setupPortsAdapter();
        setupPortScan();
        setupWol();
        setupAdAtBottom(binding.bottomButton);
    }

    /**
     * Sets up the adapter to handle discovered ports
     */
    public void setupPortsAdapter() {
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.port_list_item, ports);
        binding.portList.setAdapter(adapter);
        setAnimations();
    }

    protected void setAnimations() {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_slide_in_bottom);
        binding.portList.setLayoutAnimation(animation);
    }

    @Override
    protected AdvertRepository loadRepository() {
        return TApp.repository;
    }


    /**
     * Save the state of the activity
     *
     * @param savedState Data to save
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putSerializable(ARG_HOST, host);
    }

    /**
     * Restore saved data
     *
     * @param savedInstanceState Data from a saved state
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        host = (Host) savedInstanceState.get(ARG_HOST);
    }

    /**
     * Event handler for when the well known port scan is initiated
     */
    private void scanWellKnownPortsClick() {
        binding.scanWellKnownPorts.setOnClickListener(new ScanPortsListener(ports, adapter) {
            /**
             * Click handler for scanning well known ports
             * @param v
             */
            @Override
            public void onClick(View v) {
                super.onClick(v);

                try {
                    if (!wifi.isConnectedWifi()) {
                        Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
                        return;
                    }
                } catch (Wireless.NoConnectivityManagerException e) {
                    Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
                    return;
                }

                int startPort = 1;
                int stopPort = 1024;
                scanProgressDialog = new ProgressDialog(LanHostActivity.this, R.style.DialogTheme);
                scanProgressDialog.setCancelable(false);
                String titleTemplate = getResources().getString(R.string.scanning_port_range);
                String title = String.format(titleTemplate, startPort, stopPort);
                scanProgressDialog.setTitle(title);
                scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                scanProgressDialog.setProgress(0);
                scanProgressDialog.setMax(1024);
                scanProgressDialog.show();

                Host.scanPorts(host.getIp(), startPort, stopPort, UserPreference.getLanSocketTimeout(getApplicationContext()), LanHostActivity.this);
            }
        });
    }

    /**
     * Event handler for when a port range scan is requested
     */
    private void scanPortRangeClick() {
        binding.scanPortRange.setOnClickListener(new View.OnClickListener() {

            /**
             * Click handler for scanning a port range
             * @param v
             */
            @Override
            public void onClick(View v) {
                try {
                    if (!wifi.isConnectedWifi()) {
                        Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
                        return;
                    }
                } catch (Wireless.NoConnectivityManagerException e) {
                    Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
                    return;
                }

                portRangeDialog = new Dialog(LanHostActivity.this, R.style.DialogTheme);
                portRangeDialog.setContentView(R.layout.port_range);
                portRangeDialog.show();

                NumberPicker portRangePickerStart = portRangeDialog.findViewById(R.id.portRangePickerStart);
                NumberPicker portRangePickerStop = portRangeDialog.findViewById(R.id.portRangePickerStop);

                portRangePickerStart.setMinValue(Constants.MIN_PORT_VALUE);
                portRangePickerStart.setMaxValue(Constants.MAX_PORT_VALUE);
                portRangePickerStart.setValue(UserPreference.getPortRangeStart(LanHostActivity.this));
                portRangePickerStart.setWrapSelectorWheel(false);
                portRangePickerStop.setMinValue(Constants.MIN_PORT_VALUE);
                portRangePickerStop.setMaxValue(Constants.MAX_PORT_VALUE);
                portRangePickerStop.setValue(UserPreference.getPortRangeHigh(LanHostActivity.this));
                portRangePickerStop.setWrapSelectorWheel(false);

                startPortRangeScanClick(portRangePickerStart, portRangePickerStop, UserPreference.getLanSocketTimeout(getApplicationContext()), LanHostActivity.this, host.getIp());
                resetPortRangeScanClick(portRangePickerStart, portRangePickerStop);
            }
        });
    }

    /**
     * Event handler for waking up a host via WoL
     */
    private void setupWol() {
        Button wakeUpButton = findViewById(R.id.wakeOnLan);
        wakeUpButton.setOnClickListener(v -> {
            try {
                if (!wifi.isConnectedWifi()) {
                    Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
                    return;
                }
            } catch (Wireless.NoConnectivityManagerException e) {
                Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
                return;
            }

            host.wakeOnLan();
            Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.waking), host.getHostname()), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Sets up event handlers and functionality for various port scanning features
     */
    private void setupPortScan() {
        scanWellKnownPortsClick();
        scanPortRangeClick();
        portListClick(binding.portList, host.getIp());
    }


    /**
     * Delegate to determine if the progress dialog should be dismissed or not
     *
     * @param output True if the dialog should be dismissed
     */
    @Override
    public void processFinish(boolean output) {
        if (output && scanProgressDialog != null && scanProgressDialog.isShowing()) {
            scanProgressDialog.dismiss();
        }
        if (output && portRangeDialog != null && portRangeDialog.isShowing()) {
            portRangeDialog.dismiss();
        }
    }
}
