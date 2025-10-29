/*
 * Copyright (C) 2015 Andriy Druk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.druk.servicebrowser.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.druk.servicebrowser.Config
import com.druk.servicebrowser.Config.EMPTY_DOMAIN
import com.druk.servicebrowser.R
import com.druk.servicebrowser.RegTypeManager
import com.druk.servicebrowser.ui.adapter.ServiceAdapter
import com.druk.servicebrowser.ui.viewmodel.RegTypeBrowserViewModel
import com.druk.servicebrowser.ui.viewmodel.RegTypeBrowserViewModel.BonjourDomain
import com.github.druk.rx2dnssd.BonjourService
import com.walhalla.whatismyipaddress.TApp
import io.reactivex.functions.Consumer
import java.util.Collections

class RegTypeBrowserFragment : ServiceBrowserFragment() {
    private var mRegTypeManager: RegTypeManager? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRegTypeManager = TApp.getRegTypeManager(requireContext())
        mAdapter = object : ServiceAdapter(requireActivity()) {
            var drawable: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_star)

            override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
                val domain = getItem(i) as BonjourDomain
                val regType = domain.serviceName + "." + domain.regType
                    .split(Config.REG_TYPE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0] + "."
                val regTypeDescription = mRegTypeManager!!.getRegTypeDescription(regType)
                if (regTypeDescription != null) {
                    viewHolder.text1.setText(regType + " (" + regTypeDescription + ")")
                } else {
                    viewHolder.text1.setText(regType)
                }

                if (favouritesManager!!.isFavourite(regType)) {
                    viewHolder.text1.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        drawable,
                        null
                    )
                } else {
                    viewHolder.text1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }

                viewHolder.icon.setImageResource(getIconForRegType(regType))
                viewHolder.text2.setText(domain.serviceCount.toString() + " services")
                viewHolder.itemView.setOnClickListener(mListener)
            }

            public override fun sortServices(services: ArrayList<BonjourService>) {
                Collections.sort<BonjourService>(
                    services,
                    Comparator { lhs: BonjourService, rhs: BonjourService ->
                        val lhsRegType = lhs.serviceName + "." + lhs.regType.split(
                            Config.REG_TYPE_SEPARATOR.toRegex()
                        ).dropLastWhile { it.isEmpty() }.toTypedArray()[0] + "."
                        val rhsRegType = rhs.serviceName + "." + rhs.regType
                            .split(Config.REG_TYPE_SEPARATOR.toRegex())
                            .dropLastWhile { it.isEmpty() }.toTypedArray()[0] + "."
                        val isLhsFavourite = favouritesManager!!.isFavourite(lhsRegType)
                        val isRhsFavourite = favouritesManager!!.isFavourite(rhsRegType)
                        if (isLhsFavourite && isRhsFavourite) {
                            return@Comparator lhs.serviceName.compareTo(rhs.serviceName)
                        } else if (isLhsFavourite) {
                            return@Comparator -1
                        } else if (isRhsFavourite) {
                            return@Comparator 1
                        }
                        lhs.serviceName.compareTo(rhs.serviceName)
                    })
            }
        }
    }

    override fun createViewModel() {
        val viewModel = ViewModelProvider(this)
            .get<RegTypeBrowserViewModel>(RegTypeBrowserViewModel::class.java)

        val errorAction = Consumer { throwable: Throwable? ->
            Log.e("DNSSD", "Error: ", throwable)
            this@RegTypeBrowserFragment.showError(throwable!!)
        }

        val servicesAction = Consumer { services: MutableCollection<BonjourDomain>? ->
            mAdapter!!.clear()
            for (bonjourDomain in services!!) {
                if (bonjourDomain.serviceCount > 0) {
                    mAdapter!!.add(bonjourDomain)
                }
            }
            this@RegTypeBrowserFragment.showList()
            mAdapter!!.notifyDataSetChanged()
        }

        viewModel.startDiscovery(servicesAction, errorAction)
    }

    override fun favouriteMenuSupport(): Boolean {
        return false
    }

    override fun onStart() {
        super.onStart()
        // Favourites can be changed
        mAdapter!!.sortServices()
        mAdapter!!.notifyDataSetChanged()
    }

    private fun getIconForRegType(regType: String): Int {
        Log.d("RegTypeBrowser", "Service type: $regType")
        if (regType.contains("_http._tcp")) {
            return R.drawable.ic_public
        } else if (regType.contains("_http-alt._tcp")) {
            return R.drawable.ic_public
        } else if (regType.contains("_printer._tcp")) {
            return R.drawable.ic_print
        } else if (regType.contains("_ipp._tcp")) {
            return R.drawable.ic_print
        } else if (regType.contains("_ipps._tcp")) {
            return R.drawable.ic_print
        } else if (regType.contains("_pdl-datastream._tcp")) {
            return R.drawable.ic_print
        } else if (regType.contains("_adb._tcp")) {
            return R.drawable.ic_adb
        } else if (regType.contains("_cache._tcp")) {
            return R.drawable.ic_cached
        } else if (regType.contains("_device-info._tcp")) {
            return R.drawable.ic_info
        } else if (regType.contains("_nvstream_dbd._tcp")) {
            return R.drawable.ic_gamepad
        } else {
            return R.drawable.ic_dns
        }
    }

    companion object {
        private const val TAG = "RegTypeBrowser"

        fun newInstance(regType: String?): Fragment {
            return fillArguments(RegTypeBrowserFragment(), EMPTY_DOMAIN, regType)
        }
    }
}
