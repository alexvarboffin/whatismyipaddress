package com.walhalla.whatismyipaddress.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.ui.DLog.handleException
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.features.checkhost.CheckHostActivityBase
import com.walhalla.whatismyipaddress.features.ping.PingIp
import com.walhalla.whatismyipaddress.features.portscanning.PortScanner
import com.walhalla.whatismyipaddress.features.rdap.RdapActivityBase
import com.walhalla.whatismyipaddress.features.subdomain.SubdomainActivity
import com.walhalla.whatismyipaddress.features.websniffer.WebSnifferActivity
import com.walhalla.whatismyipaddress.ipcalculator.IpCalcActivityBase
import com.walhalla.whatismyipaddress.ipconverter.IPAddressConverter
import com.walhalla.whatismyipaddress.reverseIpLookup.ReverseIpLookup
import com.walhalla.whatismyipaddress.sslExamination.SSLExaminationActivityBase
import com.walhalla.whatismyipaddress.ui.Subnet.SubnetDiscoveryActivity
import com.walhalla.whatismyipaddress.ui.Subnet.WanHostActivity
import com.walhalla.whatismyipaddress.ui.activities.DNSLookup
import com.walhalla.whatismyipaddress.ui.activities.IPNetBlocks
import com.walhalla.whatismyipaddress.ui.activities.SpeedTest
import com.walhalla.whatismyipaddress.ui.activities.WakeOnLan5
import com.walhalla.whatismyipaddress.ui.adapter.ComplexAdapter
import com.walhalla.whatismyipaddress.ui.adapter.ComplexAdapter.Callback2Menu
import com.walhalla.whatismyipaddress.ui.adapter.entity.Header1
import com.walhalla.whatismyipaddress.ui.adapter.entity.ToolsMenu
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0
import com.walhalla.whatismyipaddress.ui.adapter.menu.MSpeedTest
import com.walhalla.whatismyipaddress.whois.IanaRootWhois

class MiscellaneousFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_misc, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val models: MutableList<ViewModel0?> = ArrayList<ViewModel0?>()

        //models.add(new Header1(getString(R.string.misc_tools)));
        //models.add(new Header1(""));
        models.add(MSpeedTest(getString(R.string.title_speed_test)))
        models.add(Header1(getString(R.string.other_useful_tools)))


        models.add(ToolsMenu(getString(R.string.title_ping), R.drawable.ic_ping))
        models.add(ToolsMenu(getString(R.string.titleGeoPing), R.drawable.ic_ping))


        models.add(ToolsMenu(getString(R.string.menu_port_scanning), R.drawable.ic_port_scanning))


        //models.add(new ToolsMenu(getString(R.string.subnet_devices_connected_to_your_network), R.drawable.ic_filter_tilt_shift_black_24dp));

        //models.add(new ToolsMenu(getString(R.string.whois), R.drawable.ic_filter_tilt_shift_black_24dp));
        models.add(
            ToolsMenu(
                getString(R.string.subnet_devices_connected_to_your_network),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )
        models.add(
            ToolsMenu(
                getString(R.string.action_titleWanhostScanner),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )

        models.add(
            ToolsMenu(
                getString(R.string.action_title_rdap),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )


        models.add(
            ToolsMenu(
                getString(R.string.root_whois),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )


        models.add(
            ToolsMenu(
                getString(R.string.titleWakeonlan),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )
        models.add(
            ToolsMenu(
                getString(R.string.action_title_ip_netblocks),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )
        models.add(
            ToolsMenu(
                getString(R.string.action_titleDnsLookupWhoisxml),
                R.drawable.ic_dns_24px
            )
        )


        models.add(
            ToolsMenu(
                getString(R.string.menu_title_rdns_lookup),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )

        models.add(
            ToolsMenu(
                getString(R.string.action_title_websniffer),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )

        //+
        models.add(
            ToolsMenu(
                getString(R.string.titleSubdomainFinder),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )


        models.add(
            ToolsMenu(
                getString(R.string.titleSSLExamination),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )


        models.add(
            ToolsMenu(
                getString(R.string.action_title_ipconverter),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )
        models.add(
            ToolsMenu(
                getString(R.string.title_IPCalculator),
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )

        models.add(
            ToolsMenu(
                "Bonjour Service Scanner aka mDNS/DNS-SD Scanner",
                R.drawable.ic_filter_tilt_shift_black_24dp
            )
        )
        val adapter = ComplexAdapter(models, activity)

        val mLayoutManager = LinearLayoutManager(activity)
        recyclerView.setLayoutManager(mLayoutManager)
        val mDividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            mLayoutManager.orientation
        )
        recyclerView.addItemDecoration(mDividerItemDecoration)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(adapter)
        adapter.setMenuCallback { adapterPosition: Int ->
            when (adapterPosition) {
                0 -> swape(SpeedTest::class.java)
                1 -> {}
                2 -> swape(PingIp::class.java)
                3 -> swape(CheckHostActivityBase::class.java)
                4 -> swape(PortScanner::class.java)
                5 -> swape(SubnetDiscoveryActivity::class.java) //subnet
                6 -> swape(WanHostActivity::class.java) //subnet
                7 -> swape(RdapActivityBase::class.java)
                8 -> swape(IanaRootWhois::class.java)
                9 -> swape(WakeOnLan5::class.java)
                10 -> swape(IPNetBlocks::class.java)
                11 -> swape(DNSLookup::class.java)
                12 -> swape(ReverseIpLookup::class.java)
                13 -> swape(WebSnifferActivity::class.java)
                14 -> swape(SubdomainActivity::class.java)
                15 -> swape(SSLExaminationActivityBase::class.java)
                16 -> swape(IPAddressConverter::class.java)
                17 -> swape(IpCalcActivityBase::class.java)

                18 -> swape(com.druk.servicebrowser.ui.MainActivity::class.java)


                else -> Toast.makeText(context, "@$adapterPosition", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //        AdView mAdView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

//

//        view.findViewById(R.id.speedTest).setOnClickListener(this);
    }

    private fun swape(clazz: Class<*>?) {
        try {
            startActivity(Intent(getContext(), clazz))
        } catch (e: Exception) {
            handleException(e)
        }
    }
}