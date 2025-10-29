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
package com.druk.servicebrowser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.druk.servicebrowser.ui.adapter.ServiceAdapter
import com.github.druk.rx2dnssd.BonjourService
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.TApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class RegistrationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.content, RegistrationsFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class RegistrationsFragment : Fragment() {
        private var adapter: ServiceAdapter? = null
        private var mDisposable: Disposable? = null
        private var mRecyclerView: RecyclerView? = null
        private var mNoServiceView: View? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            adapter = object : ServiceAdapter(requireContext()) {
                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    holder.text1.text = getItem(position).serviceName
                    holder.text2.text = getItem(position).regType
                    holder.icon.setImageResource(getIconForService(getItem(position)))
                    holder.itemView.setOnClickListener { v: View? ->
                        startActivityForResult(
                            ServiceActivity.startActivity(getContext(), getItem(position), true),
                            STOP_REQUEST_CODE
                        )
                    }
                }
            }
        }

        private fun getIconForService(service: BonjourService): Int {
            Log.d("RegistrationsActivity", "Service type: " + service.regType)
            return if (service.regType.contains("_http._tcp")) {
                R.drawable.ic_public
            } else if (service.regType.contains("_http-alt._tcp")) {
                R.drawable.ic_public
            } else if (service.regType.contains("_printer._tcp")) {
                R.drawable.ic_print
            } else if (service.regType.contains("_ipp._tcp")) {
                R.drawable.ic_print
            } else if (service.regType.contains("_ipps._tcp")) {
                R.drawable.ic_print
            } else if (service.regType.contains("_pdl-datastream._tcp")) {
                R.drawable.ic_print
            } else if (service.regType.contains("_adb._tcp")) {
                R.drawable.ic_adb
            } else if (service.regType.contains("_cache._tcp")) {
                R.drawable.ic_cached
            } else if (service.regType.contains("_device-info._tcp")) {
                R.drawable.ic_info
            } else if (service.regType.contains("_nvstream_dbd._tcp")) {
                R.drawable.ic_gamepad
            } else {
                R.drawable.ic_dns
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == REGISTER_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    data?.let {
                        val bonjourService = RegisterServiceActivity.parseResult(it)
                        mDisposable = TApp.getRegistrationManager(requireContext())
                            .register(requireContext(), bonjourService!!)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                Consumer { service: BonjourService? -> this@RegistrationsFragment.updateServices() },
                                Consumer { throwable: Throwable ->
                                    Toast.makeText(
                                        this@RegistrationsFragment.context,
                                        "Error: " + throwable.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                    }
                }
                return
            } else if (requestCode == STOP_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    val bonjourService = ServiceActivity.parseResult(data)
                    TApp.getRegistrationManager(requireContext()).unregister(bonjourService)
                    updateServices()
                }
                return
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_registrations, container, false)
            mRecyclerView = view.findViewById(R.id.recycler_view)
            mRecyclerView?.setLayoutManager(
                LinearLayoutManager(
                    view.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            mRecyclerView?.setAdapter(adapter)
            mNoServiceView = view.findViewById(R.id.no_service)
            view.findViewById<View>(R.id.fab).setOnClickListener { v: View? ->
                this@RegistrationsFragment.startActivityForResult(
                    RegisterServiceActivity.createIntent(
                        context
                    ), REGISTER_REQUEST_CODE
                )
            }
            updateServices()
            return view
        }

        override fun onStop() {
            super.onStop()
            if (mDisposable != null && !mDisposable!!.isDisposed) {
                mDisposable!!.dispose()
            }
        }

        private fun updateServices() {
            val registeredServices = TApp.getRegistrationManager(
                requireContext()
            ).registeredServices
            adapter!!.swap(registeredServices)
            mNoServiceView!!.visibility =
                if (registeredServices.size > 0) View.GONE else View.VISIBLE
        }

        companion object {
            private const val REGISTER_REQUEST_CODE = 100
            private const val STOP_REQUEST_CODE = 101
        }
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, RegistrationsActivity::class.java))
        }
    }
}
