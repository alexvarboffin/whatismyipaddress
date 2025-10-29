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
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.Toolbar
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.druk.servicebrowser.ui.adapter.TxtRecordsAdapter
import com.github.druk.rx2dnssd.BonjourService
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.TApp

class RegisterServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_activity)
        setSupportActionBar(findViewById<Toolbar?>(R.id.toolbar))
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, RegisterServiceFragment()).commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setResult(bonjourService: BonjourService?) {
        setResult(RESULT_OK, Intent().putExtra(SERVICE, bonjourService))
        finish()
    }

    class RegisterServiceFragment : Fragment(), OnEditorActionListener, View.OnClickListener {
        private var serviceNameEditText: EditText? = null
        private var regTypeEditText: AppCompatAutoCompleteTextView? = null
        private var portEditText: EditText? = null
        private var adapter: TxtRecordsAdapter? = null
        private val mRecords = ArrayMap<String?, String?>()

        override fun onAttach(context: Context) {
            super.onAttach(context)
            setHasOptionsMenu(true)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_register_service, container, false)
            serviceNameEditText = view.findViewById<EditText>(R.id.service_name)
            regTypeEditText = view.findViewById<AppCompatAutoCompleteTextView>(R.id.reg_type)
            portEditText = view.findViewById<EditText>(R.id.port)

            serviceNameEditText!!.setOnEditorActionListener(this)
            regTypeEditText!!.setOnEditorActionListener(this)
            portEditText!!.setOnEditorActionListener(this)

            adapter = object : TxtRecordsAdapter() {
                override fun onItemClick(view: View?, position: Int) {
                    val builder = AlertDialog.Builder(requireActivity())
                    val key = getKey(position)
                    val value = getValue(position)
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setMessage("Do you really want to delete " + key + "=" + value + " ?")
                        .setPositiveButton(
                            android.R.string.ok,
                            DialogInterface.OnClickListener { dialog: DialogInterface?, id1: Int ->
                                mRecords.remove(key)
                                adapter!!.swapTXTRecords(mRecords)
                                adapter!!.notifyDataSetChanged()
                            })
                        .setNegativeButton(
                            android.R.string.cancel,
                            DialogInterface.OnClickListener { dialog: DialogInterface?, id1: Int -> })
                    builder.create().show()
                }
            }

            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.setLayoutManager(
                LinearLayoutManager(
                    view.getContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
            recyclerView.setAdapter(adapter)

            val regTypes = TApp.getRegTypeManager(requireContext())!!.listRegTypes
            regTypeEditText!!.setAdapter(
                ArrayAdapter<String?>(
                    requireContext(),
                    android.R.layout.select_dialog_item,
                    regTypes
                )
            )

            view.findViewById<View?>(R.id.fab).setOnClickListener(this)

            return view
        }

        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.menu_registered_services, menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            val id = item.getItemId()

            if (id == R.id.action_add) {
                val builder = AlertDialog.Builder(requireActivity())
                val view = requireActivity().layoutInflater
                    .inflate(R.layout.dialog_add_txt_records, null)
                val keyTextView = view.findViewById<TextView>(R.id.key)
                val valueTextView = view.findViewById<TextView>(R.id.value)
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setMessage("Add TXT record")
                    .setView(view)
                    .setPositiveButton(
                        android.R.string.ok,
                        DialogInterface.OnClickListener { dialog: DialogInterface?, id1: Int ->
                            mRecords.put(
                                keyTextView.getText().toString(),
                                valueTextView.getText().toString()
                            )
                            adapter!!.swapTXTRecords(mRecords)
                            adapter!!.notifyDataSetChanged()
                        })
                    .setNegativeButton(
                        android.R.string.cancel,
                        DialogInterface.OnClickListener { dialog: DialogInterface?, id1: Int -> })
                builder.create().show()
                return true
            }

            return super.onOptionsItemSelected(item)
        }

        override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
            if (getView() == null || actionId != EditorInfo.IME_ACTION_DONE) {
                return false
            }
            val id = v.getId()
            if (id == R.id.service_name) {
                regTypeEditText!!.requestFocus()
                return true
            } else if (id == R.id.reg_type) {
                portEditText!!.requestFocus()
                return true
            } else if (id == R.id.port) {
                val imm =
                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                return true
            }
            return false
        }

        override fun onClick(v: View?) {
            if (getView() == null) {
                return
            }
            val serviceName = serviceNameEditText!!.text.toString()
            val reqType = regTypeEditText!!.getText().toString()
            val port = portEditText!!.getText().toString()
            var portNumber = 0

            var isValid = true
            if (TextUtils.isEmpty(serviceName)) {
                isValid = false
                serviceNameEditText!!.setError("Service name can't be unspecified")
            }
            if (TextUtils.isEmpty(reqType)) {
                isValid = false
                regTypeEditText!!.setError("Reg type can't be unspecified")
            }
            if (TextUtils.isEmpty(port)) {
                isValid = false
                portEditText!!.setError("Port can't be unspecified")
            } else {
                try {
                    portNumber = port.toInt()
                    if (portNumber < 0 || portNumber > 65535) {
                        isValid = false
                        portEditText!!.setError("Invalid port number (0-65535)")
                    }
                } catch (e: NumberFormatException) {
                    isValid = false
                    portEditText!!.setError("Invalid port number (0-65535)")
                }
            }

            if (isValid) {
                if (getActivity() is RegisterServiceActivity) {
                    (getActivity() as RegisterServiceActivity).setResult(
                        BonjourService.Builder(0, 0, serviceName, reqType, null).port(portNumber)
                            .dnsRecords(mRecords).build()
                    )
                }
            }
        }
    }

    companion object {
        private const val SERVICE = "service"

        fun createIntent(context: Context?): Intent {
            return Intent(context, RegisterServiceActivity::class.java)
        }

        fun parseResult(intent: Intent): BonjourService? {
            return intent.getParcelableExtra<BonjourService>(SERVICE)
        }
    }
}
