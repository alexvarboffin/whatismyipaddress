package com.walhalla.whatismyipaddress.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.walhalla.generated.Config;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.BuildConfig;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.databinding.MainBinding;
import com.walhalla.whatismyipaddress.features.checkhost.CheckHostActivityBase;
import com.walhalla.whatismyipaddress.features.ping.PingIp;
import com.walhalla.whatismyipaddress.features.portscanning.PortScanner;
import com.walhalla.whatismyipaddress.helper.DataHandler;
import com.walhalla.whatismyipaddress.helper.EntityWrapper;
import com.walhalla.whatismyipaddress.helper.IPInfoLocal;
import com.walhalla.whatismyipaddress.helper.IPInfoRemote;
import com.walhalla.whatismyipaddress.ui.activities.IPNetBlocks;
import com.walhalla.whatismyipaddress.ui.activities.WebActivity;
import com.walhalla.whatismyipaddress.ui.adapter.ComplexAdapter;
import com.walhalla.whatismyipaddress.ui.adapter.entity.CountryObj;
import com.walhalla.whatismyipaddress.ui.adapter.exobj.ExObj;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ExObj2;
import com.walhalla.whatismyipaddress.ui.adapter.entity.GatewayObj2;
import com.walhalla.whatismyipaddress.ui.adapter.entity.GooglePlayViewModel;
import com.walhalla.whatismyipaddress.ui.adapter.entity.Header0;
import com.walhalla.whatismyipaddress.ui.adapter.entity.MyIpObj;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0;
import com.walhalla.whatismyipaddress.ui.adapter.map.MapObj;
import com.walhalla.whatismyipaddress.whois.IanaRootWhois;

import java.util.ArrayList;
import java.util.List;


public class Fragment1 extends Fragment
        implements ComplexAdapter.CallbackDefault {

    private String ERR_NOT_AVAILABLE;

    private Fragment1Callback mListener;

    //private PublisherAdView mPublisherAdView = null;

    public static Fragment1 newInstance(String shell) {
        return new Fragment1();
    }

    protected IPInfoLocal dataLocal;

//    private Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message inputMessage) {
//            // Gets the image task from the incoming Message object.
//            //PhotoTask photoTask = (PhotoTask) inputMessage.obj;
//        }
//
//    };

    /**
     * Vars
     */
    private List<ViewModel0> mList;
    //private static final String handler = "file:///android_asset/flag/%1$s.png";

    private static final boolean DEBUG = BuildConfig.DEBUG;
    protected DataHandler mDatabaseManager;

    private MainBinding mBinding;
    private ComplexAdapter mComplexAdapter;
    private PopupMenu popup;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final DataHandler.IDataRemoteLoadedListener mCallback =
            new DataHandler.IDataRemoteLoadedListener() {

                @Override
                public void errorHandler(String err) {
                    runOnUiThread(() -> {
                        if (mListener != null) {
                            mListener.errorToast(err);
                        }
                    });
                }

                @Override
                public void dataRemoteLoadedHandler(boolean success, IPInfoRemote iPInfoRemote) {
                    runOnUiThread(() -> {
                        mBinding.progress.setVisibility(View.GONE);
                        IPInfoRemote data = mDatabaseManager.dataRemote;
                        if (data == null) {
                            data = new IPInfoRemote();
                        }

                        String txt = valid(data.ip);
                        if (mListener != null) {
                            mListener.testNotify(txt);
                        }

                        FragmentActivity a = getActivity();
                        if (a != null) {
                            mList.add(new MyIpObj(a.getString(R.string.label_public_ip), txt));
                            mList.add(new ExObj(a.getString(R.string.label_hostname), valid(data.hostname), R.drawable.ic_computer_black_24dp));
                            mList.add(new ExObj(a.getString(R.string.label_city), valid(data.city), R.drawable.ic_location_city_black_24dp));
                            mList.add(new ExObj(a.getString(R.string.label_region), valid(data.region), R.drawable.ic_location_city_black_24dp));
                            mList.add(new CountryObj(a.getString(R.string.label_country), valid(data.country)));
                            mList.add(new MapObj(a.getString(R.string.label_loc), valid(data.loc), R.drawable.ic_location_on_black_24dp));
                            mList.add(new ExObj(a.getString(R.string.label_org), valid(data.Netname), R.drawable.ic_filter_tilt_shift_black_24dp));
                            mList.add(new ExObj(a.getString(R.string.label_postal), valid(data.postal), R.drawable.ic_filter_tilt_shift_black_24dp));
                            mList.add(new ExObj(a.getString(R.string.label_timezone), valid(data.timezone), R.drawable.ic_filter_tilt_shift_black_24dp));
                            if (data.description != null) {
                                String s = data.getDescription();
                                mList.add(new ExObj2(a.getString(R.string.label_description), s));
                            }
                        }

                        demo(mComplexAdapter);
                    });
                }

                @Override
                public void dataRemoteLoaderStarted() {
                    runOnUiThread(() -> mBinding.progress.setVisibility(View.VISIBLE));
                }
            };


    private String valid(String o) {
        return (TextUtils.isEmpty(o)) ? ERR_NOT_AVAILABLE : o;
    }

    private void runOnUiThread(Runnable runnable) {
        if (mHandler != null && isAdded()) {
            mHandler.post(runnable);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ERR_NOT_AVAILABLE = getString(R.string.err_not_available);
        Handler handler = new Handler(Looper.getMainLooper());
        mDatabaseManager = new DataHandler(getActivity(), handler);

//        mBinding.toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
//        mBinding.toolbar.setNavigationOnClickListener(v -> {
//            Module_U.aboutDialog(this);
//        });
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = MainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mComplexAdapter = new ComplexAdapter(null, getActivity());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(mComplexAdapter);
        mComplexAdapter.setDefaultCallback(this);
        mDatabaseManager.setDataRemoteLoadedListener(mCallback);
        requestData(getActivity());
    }

    void requestData(Context context) {
        this.mList = new ArrayList<>();
        this.mDatabaseManager.loadDataConnection(context);
        this.mDatabaseManager.loadDataRemote();

        if (mDatabaseManager.dataRemoteLoading) {
            mBinding.progress.setVisibility(View.VISIBLE);
        } else {
            mBinding.progress.setVisibility(View.GONE);
        }
    }


    private void demo(ComplexAdapter adapter) {

        /*
         * Local data
         */

        dataLocal = DataHandler.requestLocalInfo(getActivity());
        if (dataLocal == null) {
            dataLocal = new IPInfoLocal();
        }
        mList.add(new ExObj(getString(R.string.label_local_ip), valid(dataLocal.localIp), R.drawable.ic_filter_tilt_shift_black_24dp));
        mList.add(new ExObj(getString(R.string.label_local_ipv6), valid(dataLocal.localIPv6), R.drawable.ic_filter_tilt_shift_black_24dp));
        mList.add(new GatewayObj2(getString(R.string.label_default_gateway), valid(dataLocal.gateway)));
        mList.add(new ExObj(getString(R.string.label_mask), valid(dataLocal.mask), R.drawable.ic_filter_tilt_shift_black_24dp));

        mList.add(new ExObj(getString(R.string.label_dns1), valid(dataLocal.dns1), R.drawable.ic_filter_tilt_shift_black_24dp));
        mList.add(new ExObj(getString(R.string.label_dns2), valid(dataLocal.dns2), R.drawable.ic_filter_tilt_shift_black_24dp));


        EntityWrapper dataConnection = mDatabaseManager.dataConnection;
        if (dataConnection == null) {
            dataConnection = new EntityWrapper();
        }
        mList.add(new ExObj(getString(R.string.label_connection_type), valid(dataConnection.connection_type), R.drawable.ic_filter_tilt_shift_black_24dp));
        if (dataConnection.connection_subtype != null && !dataConnection.connection_subtype.isEmpty()) {
            mList.add(new ExObj(getString(R.string.label_connection_subtype), valid(dataConnection.connection_subtype), R.drawable.ic_filter_tilt_shift_black_24dp));
        }

//        qInsert("state", dataConnection.state);
//        qInsert("reason", dataConnection.reason);
//        qInsert("extra", dataConnection.extra);
//        qInsert("failover", "" + dataConnection.failover);
//        qInsert("available", "" + dataConnection.available);
//        qInsert("roaming", "" + dataConnection.roaming);

        mList.add(new ExObj(getString(R.string.label_ssid),
                //valid(dataConnection.SSID)
                dataConnection.SSID == null ? getString(R.string.err_not_available) : dataConnection.SSID
                , R.drawable.ic_signal_wifi_4_bar_black_24dp));
        mList.add(new ExObj("BSSID", valid(dataConnection.BSSID), R.drawable.ic_filter_tilt_shift_black_24dp));


        mList.add(new ExObj("RSSI: ", valid("" + dataConnection.mRssi), R.drawable.ic_filter_tilt_shift_black_24dp));

        if (dataConnection.mLinkSpeed > -1) {
            mList.add(new ExObj("Link speed: ", "" + dataConnection.mLinkSpeed + WifiInfo.LINK_SPEED_UNITS, R.drawable.ic_filter_tilt_shift_black_24dp));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (dataConnection.mTxLinkSpeed > 0) {
                mList.add(new ExObj("Tx Link speed: ", "" + dataConnection.mTxLinkSpeed + WifiInfo.LINK_SPEED_UNITS, R.drawable.ic_filter_tilt_shift_black_24dp));
            }
            if (dataConnection.mRxLinkSpeed > 0) {
                mList.add(new ExObj("Rx Link speed: ", "" + dataConnection.mRxLinkSpeed + WifiInfo.LINK_SPEED_UNITS, R.drawable.ic_filter_tilt_shift_black_24dp));
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mList.add(new ExObj("Frequency: ", dataConnection.mFrequency + WifiInfo.FREQUENCY_UNITS, R.drawable.ic_wifi_tethering_black_24dp));
        }
        if (dataConnection.mNetworkId > 0) {
            mList.add(new ExObj("Net ID: ", "" + dataConnection.mNetworkId, R.drawable.ic_filter_tilt_shift_black_24dp));
        }
        if (dataConnection.operator != null) {
            mList.add(new ExObj(getString(R.string.label_operator), valid(dataConnection.operator), R.drawable.ic_filter_tilt_shift_black_24dp));
        }

        //More apps
        mList.add(new Header0(getString(R.string.action_discover_more_app), "", "", 0));
        mList.add(new GooglePlayViewModel(getString(R.string.app1), "", "com.walhalla.ttloader", R.drawable.ic_promo_0));
        mList.add(new GooglePlayViewModel(getString(R.string.app2), "", "com.walhalla.mtprotolist", R.drawable.ic_promo_1));
        mList.add(new GooglePlayViewModel(getString(R.string.app3), "", "com.walhalla.vibro", R.drawable.ic_promo_2));
        adapter.swap(mList);
    }

    private void qInsert(String s, String obj) {
        if (obj != null && !obj.isEmpty()) {
            mList.add(new ExObj(s, obj, R.drawable.ic_filter_tilt_shift_black_24dp));
        }
    }

    @Override
    public void locationItemSelected(View v, String content) {

        int id = v.getId();
        if (id == R.id.icon) {
            popup = new PopupMenu(getActivity(), v);
            popup.getMenuInflater().inflate(R.menu.menu_map_popup, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_popup_copy) {
                    AssetUtils.copyToClipboard(content, getActivity());
                    return true;
                } else if (item.getItemId() == R.id.menu_popup_open_map) {
                    AssetUtils.location(content, getActivity());
                    return true;
                }
                return false;
            });
            popup.show();
        }

    }

    @Override
    public void publicIpMenuPressed(View v, String content) {
        int id = v.getId();
        if (id == R.id.icon) {
            popup = new PopupMenu(getActivity(), v);
            popup.getMenuInflater().inflate(R.menu.menu_ip_tools_popup, popup.getMenu());
            if (ERR_NOT_AVAILABLE.equals(content)) {
                MenuItem tmp = popup.getMenu().findItem(R.id.menu_ip_ping);
                tmp.setVisible(false);
                MenuItem tmp1 = popup.getMenu().findItem(R.id.menu_ip_geoping);
                tmp1.setVisible(false);
                MenuItem tmp2 = popup.getMenu().findItem(R.id.menu_ip_portscan);
                tmp2.setVisible(false);
                MenuItem tmp3 = popup.getMenu().findItem(R.id.menu_ip_whois);
                tmp3.setVisible(false);
                MenuItem tmp4 = popup.getMenu().findItem(R.id.menu_ip_netblocks);
                tmp4.setVisible(false);
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_popup_copy) {
                    AssetUtils.copyToClipboard(content, getActivity());
                    return true;
                } else if (item.getItemId() == R.id.menu_ip_ping) {
                    //Tools.copyToClipboard(content, getActivity());
                    try {
                        Intent intent = PingIp.newInstance(getActivity(), content);
                        startActivity(intent);
                    } catch (Exception e) {
                        DLog.handleException(e);
                    }
                    return true;
                } else if (item.getItemId() == R.id.menu_ip_geoping) {
                    //Tools.copyToClipboard(content, getActivity());
                    try {
                        Intent intent = CheckHostActivityBase.newInstance(getActivity(), content);
                        startActivity(intent);
                    } catch (Exception e) {
                        DLog.handleException(e);
                    }
                    return true;
                } else if (item.getItemId() == R.id.menu_ip_portscan) {
                    //Tools.copyToClipboard(content, getActivity());
                    try {
                        Intent intent = PortScanner.newInstance(getActivity(), content);
                        startActivity(intent);
                    } catch (Exception e) {
                        DLog.handleException(e);
                    }
                    return true;
                } else if (item.getItemId() == R.id.menu_ip_whois) {
                    //Tools.copyToClipboard(content, getActivity());
                    try {
                        Intent intent = IanaRootWhois.newInstance(getActivity(), content);
                        startActivity(intent);
                    } catch (Exception e) {
                        DLog.handleException(e);
                    }
                    return true;
                } else if (item.getItemId() == R.id.menu_ip_netblocks) {
                    //Tools.copyToClipboard(content, getActivity());
                    try {
                        Intent intent = IPNetBlocks.newInstance(getActivity(), content);
                        startActivity(intent);
                    } catch (Exception e) {
                        DLog.handleException(e);
                    }
                    return true;
                }

                return false;
            });
            popup.show();
        }
    }

    @Override
    public void copyToClipboardPressed(View v, String content) {
        int id = v.getId();
        if (id == R.id.icon) {
            popup = new PopupMenu(getActivity(), v);
            popup.getMenuInflater().inflate(R.menu.menu_simpe_popup, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_popup_copy) {
                    AssetUtils.copyToClipboard(content, getActivity());
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }

    @Override
    public void makeIpInfo(View v, ViewModel0 obj) {
        String url = String.format(Config.IP_INFO_EXTENDED_URL, obj.content);

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    Toast.makeText(SubdomainActivity.this, "Browser not found", Toast.LENGTH_SHORT).show();
//                }
        String title = "IP Information";
        Intent intent = WebActivity.newInstance(getActivity(), url, title);
        startActivity(intent);
    }

    @Override
    public void makeGateway(ViewModel0 obj) {
        String url;
        if (getString(R.string.err_not_available).equals(obj.content)) {
            url = "192.168.0.1";
        } else {
            url = obj.content;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    Toast.makeText(SubdomainActivity.this, "Browser not found", Toast.LENGTH_SHORT).show();
//                }

        String title = "" + obj.content;
        Intent intent = WebActivity.newInstance(getActivity(), url, title);
        startActivity(intent);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_none, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            requestData(getActivity());
            return true;
        } else if (id == R.id.action_share) {
            AssetUtils.shareString(AssetUtils.getDataText(this.mDatabaseManager, dataLocal, getActivity()), getActivity());
            return false;
        } else if (id == R.id.action_copy) {
            AssetUtils.copyToClipboard(AssetUtils.getDataText(this.mDatabaseManager, dataLocal, getActivity()), getActivity());
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Fragment1Callback) {
            mListener = (Fragment1Callback) context;
        } else {
            throw new RuntimeException(context + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface Fragment1Callback {

        void testNotify(String txt);

        void errorToast(String err);
    }
}
