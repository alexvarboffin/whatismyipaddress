//package com.walhalla.whatismyipaddress.ui.fragment;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.SubMenu;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.content.ContextCompat;
//import androidx.databinding.DataBindingUtil;
//import androidx.fragment.app.Fragment;
//
//import com.stealthcopter.networktools.ARPInfo;
//import com.stealthcopter.networktools.IPTools;
//import com.stealthcopter.networktools.Ping;
//import com.stealthcopter.networktools.PortScan;
//import com.stealthcopter.networktools.SubnetDevices;
//import com.stealthcopter.networktools.WakeOnLan;
//import com.stealthcopter.networktools.ping.PingResult;
//import com.stealthcopter.networktools.ping.PingStats;
//import com.stealthcopter.networktools.subnet.Device;
//import com.walhalla.whatismyipaddress.R;
//import com.walhalla.whatismyipaddress.databinding.ActivityMainBinding;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Fragment2 extends Fragment {
//
//    private ActivityMainBinding mBind;
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mBind = DataBindingUtil.inflate(inflater, R.layout.activity_main, container, false);
//        return mBind.getRoot();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        InetAddress ipAddress = IPTools.getLocalIPv4Address();
//        if (ipAddress != null) {
//            mBind.editIpAddress.setText(ipAddress.getHostAddress());
//        }
//
//        mBind.pingButton.setOnClickListener(v -> new Thread(() -> {
//            try {
//                doPing();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start());
//
//        mBind.wolButton.setOnClickListener(v -> new Thread(() -> {
//            try {
//                doWakeOnLan();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start());
//
//        mBind.portScanButton.setOnClickListener(v -> new Thread(() -> {
//            try {
//                doPortScan();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start());
//
//        mBind.subnetDevicesButton.setOnClickListener(v -> new Thread(() -> {
//            try {
//                findSubnetDevices();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start());
//    }
//
//    private void appendResultsText(final String text) {
//       getActivity().runOnUiThread(() -> {
//            mBind.resultText.append(text + "\n");
//            mBind.scrollView1.post(() -> mBind.scrollView1.fullScroll(View.FOCUS_DOWN));
//        });
//    }
//

//    private void doPing() throws Exception {
//        String ipAddress = mBind.editIpAddress.getText().toString();
//
//        if (TextUtils.isEmpty(ipAddress)) {
//            appendResultsText(R.string.invalid_ip_address);
//            return;
//        }
//
//        setEnabled(mBind.pingButton, false);
//
//        // Perform a single synchronous ping
//        PingResult pingResult = null;
//        try {
//            pingResult = Ping.onAddress(ipAddress).setTimeOutMillis(1000).doPing();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            appendResultsText(e.getMessage());
//            setEnabled(mBind.pingButton, true);
//            return;
//        }
//
//
//        appendResultsText("Pinging Address: " + pingResult.getAddress().getHostAddress());
//        appendResultsText("HostName: " + pingResult.getAddress().getHostName());
//        appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()));
//
//
//        // Perform an asynchronous ping
//        Ping.onAddress(ipAddress).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
//            @Override
//            public void onResult(PingResult pingResult) {
//                if (pingResult.isReachable) {
//                    appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()));
//                } else {
//                    appendResultsText(getString(R.string.timeout));
//                }
//            }
//
//            @Override
//            public void onFinished(PingStats pingStats) {
//                appendResultsText(String.format("Pings: %d, Packets lost: %d",
//                        pingStats.getNoPings(), pingStats.getPacketsLost()));
//                appendResultsText(String.format("Min/Avg/Max Time: %.2f/%.2f/%.2f ms",
//                        pingStats.getMinTimeTaken(), pingStats.getAverageTimeTaken(), pingStats.getMaxTimeTaken()));
//                setEnabled(mBind.pingButton, true);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                // TODO: STUB METHOD
//                setEnabled(mBind.pingButton, true);
//            }
//        });
//    }
//

//
//    private void doPortScan() throws Exception {
//        String ipAddress = mBind.editIpAddress.getText().toString();
//
//        if (TextUtils.isEmpty(ipAddress)) {
//            appendResultsText(R.string.invalid_ip_address);
//            setEnabled(mBind.portScanButton, true);
//            return;
//        }
//
//        setEnabled(mBind.portScanButton, false);
//
//        // Perform synchronous port scan
//        appendResultsText("PortScanning IP: " + ipAddress);
//        ArrayList<Integer> openPorts = PortScan---onAddress(ipAddress).setPort(21).setMethodTCP().doScan();
//
//        final long startTimeMillis = System.currentTimeMillis();
//
//        // Perform an asynchronous port scan
//        PortScan portScan = PortScan---onAddress(ipAddress).setPortsAll().setMethodTCP().doScan(new PortScan.PortListener() {
//            @Override
//            public void onResult(int portNo, boolean open) {
//                if (open) appendResultsText("Open: " + portNo);
//            }
//
//            @Override
//            public void onFinished(ArrayList<Integer> openPorts) {
//                appendResultsText("Open Ports: " + openPorts.size());
//                appendResultsText("Time Taken: " + ((System.currentTimeMillis() - startTimeMillis) / 1000.0f));
//                setEnabled(mBind.portScanButton, true);
//            }
//        });
//
//        // Below is example of how to cancel a running scan
//        // portScan.cancel();
//    }
//
//
//    private void findSubnetDevices() {
//        setEnabled(mBind.subnetDevicesButton, false);
//        final long startTimeMillis = System.currentTimeMillis();
//
//        SubnetDevices subnetDevices = SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
//            @Override
//            public void onDeviceFound(Device device) {
//                appendResultsText("Device: " + device.ip + " " + device.hostname);
//            }
//
//            @Override
//            public void onFinished(ArrayList<Device> devicesFound) {
//                float timeTaken = (System.currentTimeMillis() - startTimeMillis) / 1000.0f;
//                appendResultsText("Devices Found: " + devicesFound.size());
//                appendResultsText("Finished " + timeTaken + " s");
//                setEnabled(mBind.subnetDevicesButton, true);
//            }
//        });
//        // Below is example of how to cancel a running scan
//        // subnetDevices.cancel();
//
//    }
//
//
////    @Override
////    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
////        inflater.inflate(R.menu.menu_blocklist, menu);
////
//////        SubMenu subMenu = menu.getItem(0).getSubMenu();
//////        for (int i = 0; i < 10; i++) {
//////            subMenu.add(Menu.NONE, i + i, Menu.NONE, "888");
//////            subMenu.getItem(i).setIcon(Com19.getDrawable(getContext(), R.drawable.favorite));
//////        }
//////
//////        //Other
//////        subMenu.add(Menu.NONE, 781, Menu.NONE, "System.Properties");
//////        subMenu.getItem(10)
//////                .setIcon(Com19.getDrawable(getContext(), R.drawable.favorite));
////
////        super.onCreateOptionsMenu(menu, inflater);
////    }
//
////    @Override
////    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//////        if (item.getItemId() == R.id.action_github) {
//////            Intent i = new Intent(Intent.ACTION_VIEW);
//////            i.setData(Uri.parse(getString(R.string.github_url)));
//////            startActivity(i);
//////            return true;
//////        } else {
////        return super.onOptionsItemSelected(item);
//////        }
////    }
//
//}