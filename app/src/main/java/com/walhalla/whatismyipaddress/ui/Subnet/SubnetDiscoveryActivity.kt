package com.walhalla.whatismyipaddress.ui.Subnet

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AnimationUtils
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aaronjwood.portauthority.adapter.HostAdapter
import com.aaronjwood.portauthority.async.DownloadAsyncTask
import com.aaronjwood.portauthority.async.DownloadOuisAsyncTask
import com.aaronjwood.portauthority.async.DownloadOuisAsyncTask.OuisListener
import com.aaronjwood.portauthority.async.DownloadPortDataAsyncTask
import com.aaronjwood.portauthority.async.ScanHostsAsyncTask
import com.aaronjwood.portauthority.db.Database0
import com.aaronjwood.portauthority.network.Host
import com.aaronjwood.portauthority.network.Wireless
import com.aaronjwood.portauthority.network.Wireless.NoConnectivityManagerException
import com.aaronjwood.portauthority.network.Wireless.NoWifiInterface
import com.aaronjwood.portauthority.network.Wireless.NoWifiManagerException
import com.aaronjwood.portauthority.parser.OuiParser
import com.aaronjwood.portauthority.parser.PortParser
import com.aaronjwood.portauthority.response.MainAsyncResponse
import com.aaronjwood.portauthority.utils.UserPreference
import com.walhalla.boilerplate.domain.executor.impl.ThreadExecutor
import com.walhalla.boilerplate.threading.MainThreadImpl
import com.walhalla.domain.interactors.AdvertInteractor
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl
import com.walhalla.domain.repository.AdvertRepository
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.TApp
import com.walhalla.whatismyipaddress.databinding.ActivitySubnetBinding
import com.walhalla.whatismyipaddress.utils.Errors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import java.net.SocketException
import java.net.UnknownHostException
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

class SubnetDiscoveryActivity : AppCompatActivity(), MainAsyncResponse, OuisListener {
    private var wifi: Wireless? = null

    private var cachedWanIp: String? = null

    private var discoverHostsStr: String? =
        null // Cache this so it's not looked up every time a host is found.
    private var scanProgressDialog: ProgressDialog? = null
    private val signalHandler = Handler()
    private var scanHandler: Handler? = null
    private val intentFilter = IntentFilter()
    private var adapter: HostAdapter? = null
    private var hosts: MutableList<Host?>? = Collections.synchronizedList<Host?>(ArrayList<Host?>())
    private var db: Database0? = null
    private var ouiTask: DownloadAsyncTask? = null
    private var portTask: DownloadAsyncTask? = null
    private var sortAscending = false

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        /**
         * Detect if a network connection has been lost or established
         * @param context
         * @param intent
         */
        override fun onReceive(context: Context?, intent: Intent) {
            val info = intent.getParcelableExtra<NetworkInfo?>(WifiManager.EXTRA_NETWORK_INFO)
            if (info == null) {
                return
            }

            getNetworkInfo(info)
        }
    }


    private var binding: ActivitySubnetBinding? = null

    fun getBinding(): ActivitySubnetBinding {
        return binding!!
    }

    /**
     * Activity created
     *
     * @param savedInstanceState Data from a saved state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubnetBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())

        getBinding().back.setOnClickListener(View.OnClickListener { view: View? -> super.onBackPressed() })
        getBinding().title.setText(R.string.title_subnet)


        getBinding().ping.setText(R.string.hostDiscovery)


        discoverHostsStr = getResources().getString(R.string.hostDiscovery)

        setupAdAtBottom()


        val context = getApplicationContext()
        wifi = Wireless(context)
        scanHandler = Handler(Looper.getMainLooper())

        checkDatabase()
        db = Database0.getInstance(context)

        setupHostsAdapter()
        setupHostDiscovery()

        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)

        ssidAccess(context)
    }


    private fun loadRepository(): AdvertRepository? {
        return TApp.repository
    }


    /**
     * Android 8+ now requires extra location permissions to read the SSID.
     * Determine what permissions to prompt the user for based on saved state.
     *
     * @param context
     */
    private fun ssidAccess(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (UserPreference.getCoarseLocationPermDiag(context) || UserPreference.getFineLocationPermDiag(
                    context
                )
            ) {
                return
            }

            val activity: Activity = this
            var version = "8-9"
            var message = getResources().getString(R.string.ssidCoarseMsg, version)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                version = "10+"
                message = getResources().getString(R.string.ssidFineMsg, version)
            }

            val title = getResources().getString(R.string.ssidAccessTitle, version)
            AlertDialog.Builder(activity, R.style.DialogTheme).setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                    android.R.string.ok,
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
                        dialogInterface!!.dismiss()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            UserPreference.saveFineLocationPermDiag(context)
                        } else {
                            UserPreference.saveCoarseLocationPermDiag(context)
                        }

                        var perm = Manifest.permission.ACCESS_COARSE_LOCATION
                        var request: Int = COARSE_LOCATION_REQUEST
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            perm = Manifest.permission.ACCESS_FINE_LOCATION
                            request = FINE_LOCATION_REQUEST
                        }
                        if (ContextCompat.checkSelfPermission(
                                context,
                                perm
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf<String>(perm),
                                request
                            )
                        }
                    })
                .setIcon(android.R.drawable.ic_dialog_alert).show().setCanceledOnTouchOutside(false)
        }
    }

    /**
     * Determines if the initial download of OUI and port data needs to be done.
     */
    fun checkDatabase() {
        if (getDatabasePath(Database0.DATABASE_NAME).exists()) {
            return
        }

        val activity = this
        AlertDialog.Builder(activity, R.style.DialogTheme)
            .setTitle(R.string.ouiDbTitle)
            .setMessage(R.string.ouiDbMsg)
            .setPositiveButton(
                android.R.string.yes,
                DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
                    dialogInterface!!.dismiss()
                    ouiTask = DownloadOuisAsyncTask(db, OuiParser(), activity)
                    ouiTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                })
            .setNegativeButton(
                android.R.string.no,
                DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int -> dialogInterface!!.cancel() })
            .setIcon(android.R.drawable.ic_dialog_alert).show()
            .setCanceledOnTouchOutside(false)

        AlertDialog.Builder(activity, R.style.DialogTheme)
            .setTitle(R.string.portDbTitle)
            .setMessage(R.string.portDbMsg)
            .setPositiveButton(
                android.R.string.yes,
                DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
                    dialogInterface!!.dismiss()
                    portTask = DownloadPortDataAsyncTask(db, PortParser(), activity)
                    portTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                })
            .setNegativeButton(
                android.R.string.no,
                DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int -> dialogInterface!!.cancel() })
            .setIcon(android.R.drawable.ic_dialog_alert).show()
            .setCanceledOnTouchOutside(false)
    }

    /**
     * Sets up animations for the activity
     */
    private fun setAnimations() {
        val animation = AnimationUtils.loadLayoutAnimation(
            this@SubnetDiscoveryActivity,
            R.anim.layout_slide_in_bottom
        )
        binding!!.listView.setLayoutAnimation(animation)
    }

    /**
     * Sets up the adapter to handle discovered hosts
     */
    private fun setupHostsAdapter() {
        setAnimations()
        adapter = HostAdapter(hosts)
        val mLayoutManager = LinearLayoutManager(this)
        binding!!.listView.setLayoutManager(mLayoutManager)
        val mDividerItemDecoration = DividerItemDecoration(this, mLayoutManager.getOrientation())
        binding!!.listView.addItemDecoration(mDividerItemDecoration)
        binding!!.listView.setItemAnimator(DefaultItemAnimator())
        binding!!.listView.setAdapter(adapter)
        if (!hosts!!.isEmpty()) {
            getBinding().ping.setText(discoverHostsStr + " (" + hosts!!.size + ")")
        }
    }

    /**
     * Sets up the device's MAC address and vendor
     */
    override fun setupMac() {
        try {
            if (!wifi!!.isEnabled()) {
                binding!!.deviceMacAddress.setText(R.string.wifiDisabled)
                binding!!.deviceMacVendor.setText(R.string.wifiDisabled)
                return
            }
            val mac = wifi!!.getMacAddress()
            binding!!.deviceMacAddress.setText(mac)
            val vendor = Host.findMacVendor(mac, db)
            binding!!.deviceMacVendor.setText(vendor)
        } catch (e: UnknownHostException) {
            binding!!.deviceMacAddress.setText(R.string.noWifiConnection)
            binding!!.deviceMacVendor.setText(R.string.noWifiConnection)
        } catch (e: SocketException) {
            binding!!.deviceMacAddress.setText(R.string.noWifiConnection)
            binding!!.deviceMacVendor.setText(R.string.noWifiConnection)
        } catch (e: NoWifiManagerException) {
            binding!!.deviceMacAddress.setText(R.string.noWifiConnection)
            binding!!.deviceMacVendor.setText(R.string.noWifiConnection)
        } catch (e: SQLiteException) {
            binding!!.deviceMacVendor.setText(R.string.getMacVendorFailed)
        } catch (e: UnsupportedOperationException) {
            binding!!.deviceMacVendor.setText(R.string.getMacVendorFailed)
        } catch (e: NoWifiInterface) {
            binding!!.deviceMacAddress.setText(R.string.noWifiInterface)
        }
    }

    /**
     * Sets up event handlers and functionality for host discovery
     */
    private fun setupHostDiscovery() {
        binding!!.ping.setOnClickListener(object : View.OnClickListener {
            /**
             * Click handler to perform host discovery
             * @param v
             */
            override fun onClick(v: View?) {
                val resources = getResources()
                val context = getApplicationContext()
                try {
                    if (!wifi!!.isEnabled()) {
                        Errors.showError(context, resources.getString(R.string.wifiDisabled))
                        return
                    }

                    if (!wifi!!.isConnectedWifi()) {
                        Errors.showError(context, resources.getString(R.string.notConnectedWifi))
                        return
                    }
                } catch (e: NoWifiManagerException) {
                    Errors.showError(context, resources.getString(R.string.failedWifiManager))
                    return
                } catch (e: NoConnectivityManagerException) {
                    Errors.showError(context, resources.getString(R.string.failedWifiManager))
                    return
                }


                showProgress()

                val numSubnetHosts: Int
                try {
                    numSubnetHosts = wifi!!.getNumberOfHostsInWifiSubnet()
                } catch (e: NoWifiManagerException) {
                    Errors.showError(context, resources.getString(R.string.failedSubnetHosts))
                    return
                }

                setAnimations()

                hosts!!.clear()
                getBinding().ping.setText(discoverHostsStr)
                adapter!!.notifyDataSetChanged()

                scanProgressDialog =
                    ProgressDialog(this@SubnetDiscoveryActivity, R.style.DialogTheme)
                scanProgressDialog!!.setCancelable(false)
                scanProgressDialog!!.setTitle(resources.getString(R.string.hostScan))
                scanProgressDialog!!.setMessage(
                    String.format(
                        resources.getString(R.string.subnetHosts),
                        numSubnetHosts
                    )
                )
                scanProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                scanProgressDialog!!.setProgress(0)
                scanProgressDialog!!.setMax(numSubnetHosts)
                scanProgressDialog!!.show()

                try {
                    val ip = wifi!!.getInternalWifiIpAddress<Int?>(Int::class.java)
                    ScanHostsAsyncTask(
                        this@SubnetDiscoveryActivity,
                        db
                    ).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        ip,
                        wifi!!.getInternalWifiSubnet(),
                        UserPreference.getHostSocketTimeout(context)
                    )
                    binding!!.ping.setAlpha(.3f)
                    binding!!.ping.setEnabled(false)
                } catch (e: UnknownHostException) {
                    Errors.showError(context, resources.getString(R.string.notConnectedWifi))
                } catch (e: NoWifiManagerException) {
                    Errors.showError(context, resources.getString(R.string.notConnectedWifi))
                }
            }
        })

        adapter!!.setOnItemClickListener(HostAdapter.OnItemClickListener { host: Host? ->
            if (host == null) {
                return@OnItemClickListener
            }
            val intent = Intent(this@SubnetDiscoveryActivity, LanHostActivity::class.java)
            intent.putExtra(LanHostActivity.ARG_HOST, host)
            startActivity(intent)
        })

        registerForContextMenu(binding!!.listView)
    }

    private fun showProgress() {
        binding!!.spinKit.setVisibility(View.VISIBLE)
    }

    /**
     * Inflate our context menu to be used on the host list
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    override fun onCreateContextMenu(menu: ContextMenu?, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v.getId() == R.id.listView) {
            val inflater = menuInflater
            inflater.inflate(R.menu.host_menu, menu)
        }
    }

    /**
     * Handles actions selected from the context menu for a host
     *
     * @param item
     * @return
     */
    @SuppressLint("NonConstantResourceId")
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.getMenuInfo() as AdapterContextMenuInfo?
        val itemId = item.getItemId()
        if (itemId == R.id.sortIp) {
            sortAscending = !sortAscending
            if (sortAscending) {
                adapter!!.sortDataByIpAddressRL()
                return true
            }
            adapter!!.sortDataByIpAddressLR()
            return true
        } else if (itemId == R.id.sortHostname) {
            if (sortAscending) {
                adapter!!.sortDataByHostname()
            } else {
                adapter!!.sortDataByHostnameLR()
            }

            sortAscending = !sortAscending
            return true
        } else if (itemId == R.id.sortVendor) {
            if (sortAscending) {
                adapter!!.sortDataByVendorAscending()
            } else {
                adapter!!.sortDataByVendorDescending()
            }

            sortAscending = !sortAscending
            return true
        } else if (itemId == R.id.copyHostname) {
            if (info != null) {
                setClip("hostname", hosts!!.get(info.position)!!.getHostname())
            }

            return true
        } else if (itemId == R.id.copyIp) {
            if (info != null) {
                setClip("ip", hosts!!.get(info.position)!!.getIp())
            }
            return true
        } else if (itemId == R.id.copyMac) {
            if (info != null) {
                setClip("mac", hosts!!.get(info.position)!!.getMac())
            }
            return true
        }
        return super.onContextItemSelected(item)
    }

    /**
     * Sets some text to the system's clipboard
     *
     * @param label Label for the text being set
     * @param text  The text to save to the system's clipboard
     */
    private fun setClip(label: CharSequence?, text: String?) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard != null) {
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
        }
    }

    /**
     * Gets network information about the device and updates various UI elements
     */
    private fun getNetworkInfo(info: NetworkInfo) {
        setupMac()
        this.externalIp

        val resources = getResources()
        val context = getApplicationContext()
        try {
            val enabled = wifi!!.isEnabled()
            if (!info.isConnected() || !enabled) {
                signalHandler.removeCallbacksAndMessages(null)
                binding!!.internalIpAddress.setText(Wireless.getInternalMobileIpAddress())
            }

            if (!enabled) {
                binding!!.signalStrength.setText(R.string.wifiDisabled)
                binding!!.ssid.setText(R.string.wifiDisabled)
                binding!!.bssid.setText(R.string.wifiDisabled)
                return
            }
        } catch (e: NoWifiManagerException) {
            Errors.showError(context, resources.getString(R.string.failedWifiManager))
        }

        if (!info.isConnected()) {
            binding!!.signalStrength.setText(R.string.noWifiConnection)
            binding!!.ssid.setText(R.string.noWifiConnection)
            binding!!.bssid.setText(R.string.noWifiConnection)
            return
        }

        signalHandler.postDelayed(object : Runnable {
            override fun run() {
                val signal: Int
                val speed: Int
                try {
                    speed = wifi!!.getLinkSpeed()
                } catch (e: NoWifiManagerException) {
                    Errors.showError(context, resources.getString(R.string.failedLinkSpeed))
                    return
                }
                try {
                    signal = wifi!!.getSignalStrength()
                } catch (e: NoWifiManagerException) {
                    Errors.showError(context, resources.getString(R.string.failedSignal))
                    return
                }

                binding!!.signalStrength.setText(
                    String.format(
                        resources.getString(R.string.signalLink),
                        signal,
                        speed
                    )
                )
                signalHandler.postDelayed(this, TIMER_INTERVAL.toLong())
            }
        }, 0)

        this.internalIp

        val wifiSsid: String?
        val wifiBssid: String?
        try {
            wifiSsid = wifi!!.getSSID()
        } catch (e: NoWifiManagerException) {
            Errors.showError(context, resources.getString(R.string.failedSsid))
            return
        }
        try {
            wifiBssid = wifi!!.getBSSID()
        } catch (e: NoWifiManagerException) {
            Errors.showError(context, resources.getString(R.string.failedBssid))
            return
        }
        binding!!.ssid.setText(wifiSsid)
        binding!!.bssid.setText(wifiBssid)
    }


    private val internalIp: Unit
        /**
         * Wrapper method for getting the internal wireless IP address.
         * This gets the netmask, counts the bits set (subnet size),
         * then prints it along side the IP.
         */
        get() {
            try {
                val netmask = wifi!!.getInternalWifiSubnet()
                val internalIpWithSubnet =
                    wifi!!.getInternalWifiIpAddress<String?>(String::class.java) + "/" + netmask
                binding!!.internalIpAddress.setText(internalIpWithSubnet)
            } catch (e: UnknownHostException) {
                Errors.showError(
                    getApplicationContext(),
                    getResources().getString(R.string.notConnectedLan)
                )
            } catch (e: NoWifiManagerException) {
                Errors.showError(
                    getApplicationContext(),
                    getResources().getString(R.string.notConnectedLan)
                )
            }
        }

    private val externalIp: Unit
        /**
         * Wrapper for getting the external IP address
         * We can control whether or not to do this based on the user's preference
         * If the user doesn't want this then hide the appropriate views
         */
        get() {
            if (UserPreference.getFetchExternalIp(this)) {
                binding!!.externalIpAddressLabel.setVisibility(View.VISIBLE)
                binding!!.externalIpAddress.setVisibility(View.VISIBLE)
                if (cachedWanIp == null) {
                    wifi!!.getExternalIpAddress(this)
                }
            } else {
                binding!!.externalIpAddressLabel.setVisibility(View.GONE)
                binding!!.externalIpAddress.setVisibility(View.GONE)
            }
        }

    /**
     * Activity paused
     */
    public override fun onPause() {
        super.onPause()

        unregisterReceiver(receiver)
        signalHandler.removeCallbacksAndMessages(null)

        if (scanProgressDialog != null) {
            scanProgressDialog!!.dismiss()
        }

        if (ouiTask != null) {
            ouiTask!!.cancel(true)
        }

        if (portTask != null) {
            portTask!!.cancel(true)
        }

        scanProgressDialog = null
        ouiTask = null
        portTask = null
    }

    /**
     * Activity resumed.
     */
    public override fun onResume() {
        super.onResume()

        registerReceiver(receiver, intentFilter)
    }

    /**
     * Save the state of an activity
     *
     * @param savedState Data to save
     */
    public override fun onSaveInstanceState(savedState: Bundle) {
        super.onSaveInstanceState(savedState)

        val adapter = binding!!.listView.getAdapter() as HostAdapter?
        if (adapter != null) {
            val adapterData = ArrayList<Host?>()
            for (i in 0..<adapter.getItemCount()) {
                val item = adapter.getItem(i)
                adapterData.add(item)
            }
            savedState.putSerializable("hosts", adapterData)
            savedState.putString("wanIp", cachedWanIp)
        }
    }

    /**
     * Activity state restored
     *
     * @param savedState Saved data from the saved state
     */
    public override fun onRestoreInstanceState(savedState: Bundle) {
        super.onRestoreInstanceState(savedState)

        cachedWanIp = savedState.getString("wanIp")
        binding!!.externalIpAddress.setText(cachedWanIp)
        hosts = savedState.getSerializable("hosts") as ArrayList<Host?>?
        if (hosts != null) {
            setupHostsAdapter()
        }
    }

    /**
     * Delegate to update the host list and dismiss the progress dialog
     * Gets called when host discovery has finished
     *
     * @param h The host to add to the list of discovered hosts
     * @param i Number of hosts
     */
    override fun processFinish(h: Host?, i: AtomicInteger) {
        scanHandler!!.post(Runnable {
            if (h != null) {
                hosts!!.add(h)
            }
            //DLog.d("@@@@@@@@@@@@@" + h);
            adapter!!.sortDataByIpAddressLR()


            getBinding().ping.setText(discoverHostsStr + " (" + hosts!!.size + ")")
            if (i.decrementAndGet() == 0) {
                binding!!.ping.setAlpha(1f)
                binding!!.ping.setEnabled(true)
                binding!!.spinKit.setVisibility(View.GONE)
            }
        })
    }

    /**
     * Delegate to update the progress of the host discovery scan
     *
     * @param output The amount of progress to increment by
     */
    override fun processFinish(output: Int) {
        if (scanProgressDialog != null && scanProgressDialog!!.isShowing()) {
            scanProgressDialog!!.incrementProgressBy(output)
        }
    }

    /**
     * Delegate to handle setting the external IP in the UI
     *
     * @param output External IP
     */
    override fun processFinish(output: String?) {
        cachedWanIp = output
        binding!!.externalIpAddress.setText(output)
    }

    /**
     * Delegate to dismiss the progress dialog
     *
     * @param output
     */
    override fun processFinish(output: Boolean) {
        scanHandler!!.post(Runnable {
            if (output && scanProgressDialog != null && scanProgressDialog!!.isShowing()) {
                scanProgressDialog!!.dismiss()
            }
        })
    }

    /**
     * Delegate to handle bubbled up errors
     *
     * @param output The exception we want to handle
     * @param <T>    Exception
    </T> */
    override fun <T : Throwable?> processFinish(output: T?) {
        scanHandler!!.post(Runnable {
            Errors.showError(
                getApplicationContext(),
                output!!.getLocalizedMessage()
            )
        })
    }

    private val start_time = System.currentTimeMillis()
    private var content: FrameLayout? = null

    private val callback: AdvertInteractor.Callback<View> =
        object : AdvertInteractor.Callback<View> {
            override fun onMessageRetrieved(id: Int, message: View) {
                d(message.javaClass.name + " --> " + message.hashCode())

                if (content != null) {
                    //d("@@@" + content.javaClass.name)
                    try {
                        //content.removeView(message);
                        if (message.parent != null) {
                            (message.parent as ViewGroup).removeView(message)
                        }
                        val params = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.gravity = Gravity.BOTTOM or Gravity.CENTER
                        message.setLayoutParams(params)


                        val vto = message.getViewTreeObserver()
                        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                            @SuppressLint("ObsoleteSdkInt")
                            override fun onGlobalLayout() {
                                if (Build.VERSION.SDK_INT < 16) {
                                    message.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                                } else {
                                    message.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                                }
                                //int width = message.getMeasuredWidth();
                                //int height = message.getMeasuredHeight();
                                //DLog.i("@@@@" + height + "x" + width);
                                //setSpaceForAd(height);
                            }
                        })
                        content!!.addView(message)
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
            }

            override fun onRetrievalFailed(error: String) {
                d("---->" + error)
            }
        }


    private fun setupAdAtBottom() {
        //FrameLayout content = findViewById(android.R.id.content);

        content = binding!!.bottomButton

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
        val interactor = AdvertInteractorImpl(CoroutineScope(Dispatchers.IO), MainScope(), loadRepository()!!)

        //aa.attach(this);
        //DLog.d("---->" + aa.hashCode());
        interactor.selectView(content!!, callback)
    }

    companion object {
        //Subnet Discovery tool
        private const val TIMER_INTERVAL = 1500
        private const val COARSE_LOCATION_REQUEST = 1
        private const val FINE_LOCATION_REQUEST = 2
    }
}