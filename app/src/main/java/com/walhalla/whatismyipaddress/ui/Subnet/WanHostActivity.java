package com.walhalla.whatismyipaddress.ui.Subnet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.aaronjwood.portauthority.listener.ScanPortsListener;
import com.aaronjwood.portauthority.network.Host;
import com.aaronjwood.portauthority.utils.Constants;
import com.aaronjwood.portauthority.utils.UserPreference;
import com.walhalla.compat.ComV19;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.TApp;
import com.walhalla.whatismyipaddress.databinding.ActivityWanhostBinding;

import es.dmoral.toasty.Toasty;

import com.walhalla.domain.repository.AdvertRepository;

public final class WanHostActivity extends HostActivity {

    private ComV19 comv19;

    public ActivityWanhostBinding getBinding() {
        return binding;
    }

    private ActivityWanhostBinding binding;

//    @Override
//    public View setLayout() {
//        return binding.getRoot();
//    }

    /**
     * Activity created
     *
     * @param savedInstanceState Data from a saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWanhostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        comv19 = new ComV19();


        getBinding().title.setText(R.string.action_titleWanhostScanner);
        getBinding().back.setOnClickListener(view -> super.onBackPressed());

        String lastUsedHost = UserPreference.getLastUsedHostAddress(this);
        DLog.d("@@" + lastUsedHost);
        this.binding.hostAddress.setText(lastUsedHost);
        setupPortsAdapter();
        this.setupPortScan();
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
    /**
     * Sets up animations for the activity
     */
    protected void setAnimations() {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_slide_in_bottom);
        binding.portList.setLayoutAnimation(animation);
    }
    @Override
    protected AdvertRepository loadRepository() {
        return TApp.repository;
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
                String whost = binding.hostAddress.getText().toString();
                //DLog.d("@@" + whost);

                if (noneValid()) {
                    return;
                }

                int startPort = 1;
                int stopPort = 1024;
                scanProgressDialog = new ProgressDialog(WanHostActivity.this, R.style.DialogTheme);
                scanProgressDialog.setCancelable(false);
                String titleTemplate = getResources().getString(R.string.scanning_port_range);
                String title = String.format(titleTemplate, startPort, stopPort);

                scanProgressDialog.setTitle(title);
                scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                scanProgressDialog.setProgress(0);
                scanProgressDialog.setMax(1024);
                scanProgressDialog.show();

                Host.scanPorts(whost, startPort, stopPort, UserPreference.getWanSocketTimeout(getApplicationContext()), WanHostActivity.this);
                portListClick(binding.portList, whost);
            }
        });
    }

    private boolean noneValid() {
        String whost = binding.hostAddress.getText().toString();
        boolean noneValid = TextUtils.isEmpty(whost);
        if (noneValid) {
            Toasty.custom(this, getString(R.string.provideallfields).toUpperCase(), comv19.getDrawable(this, R.drawable.ic_cancel), ContextCompat.getColor(this, R.color.error), ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
        return noneValid;
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
                if (noneValid()) {
                    return;
                }

                portRangeDialog = new Dialog(WanHostActivity.this, R.style.DialogTheme);
                //PortRangeBinding.
                portRangeDialog.setContentView(R.layout.port_range);
                portRangeDialog.show();

                NumberPicker portRangePickerStart = portRangeDialog.findViewById(R.id.portRangePickerStart);
                NumberPicker portRangePickerStop = portRangeDialog.findViewById(R.id.portRangePickerStop);

                portRangePickerStart.setMinValue(Constants.MIN_PORT_VALUE);
                portRangePickerStart.setMaxValue(Constants.MAX_PORT_VALUE);
                portRangePickerStart.setValue(UserPreference.getPortRangeStart(WanHostActivity.this));
                portRangePickerStart.setWrapSelectorWheel(false);
                portRangePickerStop.setMinValue(Constants.MIN_PORT_VALUE);
                portRangePickerStop.setMaxValue(Constants.MAX_PORT_VALUE);
                portRangePickerStop.setValue(UserPreference.getPortRangeHigh(WanHostActivity.this));
                portRangePickerStop.setWrapSelectorWheel(false);

                String whost = binding.hostAddress.getText().toString();
                DLog.d("@@@" + whost);

                startPortRangeScanClick(portRangePickerStart, portRangePickerStop, UserPreference.getWanSocketTimeout(getApplicationContext()), WanHostActivity.this, whost);
                resetPortRangeScanClick(portRangePickerStart, portRangePickerStop);
                portListClick(binding.portList, whost);
            }
        });
    }


    /**
     * Sets up event handlers and functionality for various port scanning features
     */
    private void setupPortScan() {
        this.scanWellKnownPortsClick();
        this.scanPortRangeClick();
    }

    /**
     * Delegate to determine if the progress dialog should be dismissed or not
     *
     * @param output True if the dialog should be dismissed
     */
    @Override
    public void processFinish(boolean output) {
        if (this.scanProgressDialog != null && this.scanProgressDialog.isShowing()) {
            this.scanProgressDialog.dismiss();
        }

        if (this.portRangeDialog != null && this.portRangeDialog.isShowing()) {
            this.portRangeDialog.dismiss();
        }

        if (!output) {
            handler.post(() -> Toast.makeText(getApplicationContext(), "Please enter a valid URL or IP address", Toast.LENGTH_SHORT).show());
        }
    }
}
