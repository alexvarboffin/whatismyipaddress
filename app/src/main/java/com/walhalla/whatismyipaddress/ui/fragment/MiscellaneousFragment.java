package com.walhalla.whatismyipaddress.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.features.checkhost.CheckHostActivityBase;
import com.walhalla.whatismyipaddress.features.rdap.RdapActivityBase;
import com.walhalla.whatismyipaddress.features.subdomain.SubdomainActivity;
import com.walhalla.whatismyipaddress.features.websniffer.WebSnifferActivity;
import com.walhalla.whatismyipaddress.ipcalculator.IpCalcActivityBase;
import com.walhalla.whatismyipaddress.ipconverter.IPAddressConverter;
import com.walhalla.whatismyipaddress.reverseIpLookup.ReverseIpLookup;
import com.walhalla.whatismyipaddress.sslExamination.SSLExaminationActivityBase;
import com.walhalla.whatismyipaddress.ui.Subnet.SubnetDiscoveryActivity;
import com.walhalla.whatismyipaddress.ui.Subnet.WanHostActivity;
import com.walhalla.whatismyipaddress.ui.activities.DNSLookup;
import com.walhalla.whatismyipaddress.ui.activities.IPNetBlocks;
import com.walhalla.whatismyipaddress.features.ping.PingIp;
import com.walhalla.whatismyipaddress.features.portscanning.PortScanner;

import com.walhalla.whatismyipaddress.ui.activities.SpeedTest;

import com.walhalla.whatismyipaddress.ui.activities.WakeOnLan5;
import com.walhalla.whatismyipaddress.whois.IanaRootWhois;
import com.walhalla.whatismyipaddress.ui.adapter.ComplexAdapter;
import com.walhalla.whatismyipaddress.ui.adapter.entity.Header1;
import com.walhalla.whatismyipaddress.ui.adapter.menu.MSpeedTest;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ToolsMenu;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0;

import java.util.ArrayList;
import java.util.List;

public class MiscellaneousFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_misc, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        List<ViewModel0> models = new ArrayList<>();

        //models.add(new Header1(getString(R.string.misc_tools)));
        //models.add(new Header1(""));

        models.add(new MSpeedTest(getString(R.string.title_speed_test)));
        models.add(new Header1(getString(R.string.other_useful_tools)));


        models.add(new ToolsMenu(getString(R.string.title_ping), R.drawable.ic_ping));
        models.add(new ToolsMenu(getString(R.string.titleGeoPing), R.drawable.ic_ping));


        models.add(new ToolsMenu(getString(R.string.menu_port_scanning), R.drawable.ic_port_scanning));
        //models.add(new ToolsMenu(getString(R.string.subnet_devices_connected_to_your_network), R.drawable.ic_filter_tilt_shift_black_24dp));

        //models.add(new ToolsMenu(getString(R.string.whois), R.drawable.ic_filter_tilt_shift_black_24dp));


        models.add(new ToolsMenu(getString(R.string.subnet_devices_connected_to_your_network), R.drawable.ic_filter_tilt_shift_black_24dp));
        models.add(new ToolsMenu(getString(R.string.action_titleWanhostScanner), R.drawable.ic_filter_tilt_shift_black_24dp));

        models.add(new ToolsMenu(getString(R.string.action_title_rdap), R.drawable.ic_filter_tilt_shift_black_24dp));


        models.add(new ToolsMenu(getString(R.string.root_whois), R.drawable.ic_filter_tilt_shift_black_24dp));


        models.add(new ToolsMenu(getString(R.string.titleWakeonlan), R.drawable.ic_filter_tilt_shift_black_24dp));
        models.add(new ToolsMenu(getString(R.string.action_title_ip_netblocks), R.drawable.ic_filter_tilt_shift_black_24dp));
        models.add(new ToolsMenu(getString(R.string.action_titleDnsLookupWhoisxml), R.drawable.ic_dns_24px));


        models.add(new ToolsMenu(getString(R.string.menu_title_rdns_lookup), R.drawable.ic_filter_tilt_shift_black_24dp));

        models.add(new ToolsMenu(getString(R.string.action_title_websniffer), R.drawable.ic_filter_tilt_shift_black_24dp));

        //+
        models.add(new ToolsMenu(getString(R.string.titleSubdomainFinder), R.drawable.ic_filter_tilt_shift_black_24dp));


        models.add(new ToolsMenu(getString(R.string.titleSSLExamination), R.drawable.ic_filter_tilt_shift_black_24dp));


        models.add(new ToolsMenu(getString(R.string.action_title_ipconverter), R.drawable.ic_filter_tilt_shift_black_24dp));
        models.add(new ToolsMenu(getString(R.string.title_IPCalculator), R.drawable.ic_filter_tilt_shift_black_24dp));


        ComplexAdapter adapter = new ComplexAdapter(models, getActivity());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setMenuCallback(adapterPosition -> {
            switch (adapterPosition) {

                case 0:
                    swape(SpeedTest.class);
                    break;

                case 1:
                    //header Toast.makeText(getContext(), "@" + adapterPosition, Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    swape(PingIp.class);
                    break;

                case 3:
                    swape(CheckHostActivityBase.class);
                    break;

                case 4:
                    swape(PortScanner.class);
                    break;

                case 5:
                    swape(SubnetDiscoveryActivity.class);//subnet
                    break;

                case 6:
                    swape(WanHostActivity.class);//subnet
                    break;

                case 7:
                    swape(RdapActivityBase.class);
                    break;
                case 8:
                    swape(IanaRootWhois.class);
                    break;

                case 9:
                    swape(WakeOnLan5.class);
                    break;

                case 10:
                    swape(IPNetBlocks.class);
                    break;

                case 11:
                    swape(DNSLookup.class);
                    break;

                case 12:
                    swape(ReverseIpLookup.class);
                    break;

                case 13:
                    swape(WebSnifferActivity.class);
                    break;


                case 14:
                    swape(SubdomainActivity.class);
                    break;

                case 15:
                    swape(SSLExaminationActivityBase.class);
                    break;

                case 16:
                    swape(IPAddressConverter.class);
                    break;

                case 17:
                    swape(IpCalcActivityBase.class);
                    break;

                default:
                    Toast.makeText(getContext(), "@" + adapterPosition, Toast.LENGTH_SHORT).show();
                    break;

            }
        });

//        AdView mAdView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

//

//        view.findViewById(R.id.speedTest).setOnClickListener(this);
    }

    private void swape(Class<?> clazz) {
        try {
            startActivity(new Intent(getContext(), clazz));
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }
}