package com.walhalla.whatismyipaddress.ui.Subnet

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aaronjwood.portauthority.db.Database0
import com.aaronjwood.portauthority.listener.ScanPortsListener
import com.aaronjwood.portauthority.network.Host
import com.aaronjwood.portauthority.response.HostAsyncResponse
import com.aaronjwood.portauthority.utils.Constants
import com.aaronjwood.portauthority.utils.UserPreference
import com.walhalla.boilerplate.threading.MainThreadImpl.Companion.instance
import com.walhalla.domain.interactors.AdvertInteractor
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl
import com.walhalla.domain.repository.AdvertRepository
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.utils.Errors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import java.util.Arrays
import java.util.Collections

abstract class HostActivity : AppCompatActivity(), HostAsyncResponse {
    protected val start_time: Long = System.currentTimeMillis()
    private var content: FrameLayout? = null

    private val callback: AdvertInteractor.Callback<View> =
        object : AdvertInteractor.Callback<View> {
            override fun onMessageRetrieved(id: Int, message: View) {
                d(message.javaClass.getName() + " --> " + message.hashCode())

                if (content != null) {
                    //d("@@@@@@@@@@" + content.javaClass.getName())
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

    @JvmField
    protected var adapter: ArrayAdapter<String?>? = null

    @JvmField
    protected val ports: MutableList<String?> =
        Collections.synchronizedList<String?>(ArrayList<String?>())
    @JvmField
    protected var scanProgressDialog: ProgressDialog? = null
    @JvmField
    protected var portRangeDialog: Dialog? = null
    @JvmField
    protected var handler: Handler? = null
    private var database0: Database0? = null

    private var interactor: AdvertInteractorImpl? = null


    /**
     * Activity created
     *
     * @param savedInstanceState Data from a saved state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interactor = AdvertInteractorImpl(CoroutineScope(Dispatchers.IO), MainScope(), loadRepository()!!)
        database0 = Database0.getInstance(applicationContext)
        handler = Handler(Looper.getMainLooper())
    }


    /**
     * Activity paused
     */
    public override fun onPause() {
        super.onPause()

        if (scanProgressDialog != null && scanProgressDialog!!.isShowing()) {
            scanProgressDialog!!.dismiss()
        }
        if (portRangeDialog != null && portRangeDialog!!.isShowing()) {
            portRangeDialog!!.dismiss()
        }
        scanProgressDialog = null
        portRangeDialog = null
    }

    /**
     * Save the state of the activity
     *
     * @param savedState Data to save
     */
    public override fun onSaveInstanceState(savedState: Bundle) {
        super.onSaveInstanceState(savedState)

        val savedList = ports.toTypedArray<String?>()
        savedState.putStringArray("ports", savedList)
    }

    /**
     * Restore saved data
     *
     * @param savedInstanceState Data from a saved state
     */
    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val savedList = savedInstanceState.getStringArray("ports")
        if (savedList != null) {
            ports.addAll(Arrays.asList<String?>(*savedList))
        }

        setupPortsAdapter()
    }

    protected abstract fun setupPortsAdapter()

    /**
     * Event handler for when the port range reset is triggered
     *
     * @param start Starting port picker
     * @param stop  Stopping port picker
     */
    protected fun resetPortRangeScanClick(start: NumberPicker, stop: NumberPicker) {
        portRangeDialog!!.findViewById<View?>(R.id.resetPortRangeScan)
            .setOnClickListener(View.OnClickListener { v: View? ->
                start.setValue(Constants.MIN_PORT_VALUE)
                stop.setValue(Constants.MAX_PORT_VALUE)
            })
    }

    /**
     * Event handler for when the port range scan is finally initiated
     *
     * @param start    Starting port picker
     * @param stop     Stopping port picker
     * @param timeout  Socket timeout
     * @param activity Calling activity
     * @param ip       IP address
     */
    protected fun startPortRangeScanClick(
        start: NumberPicker,
        stop: NumberPicker,
        timeout: Int,
        activity: HostActivity,
        ip: String?
    ) {
        val startPortRangeScan = portRangeDialog!!.findViewById<Button>(R.id.startPortRangeScan)
        startPortRangeScan.setOnClickListener(object : ScanPortsListener(ports, adapter) {
            /**
             * Click handler for starting a port range scan
             * @param v
             */
            override fun onClick(v: View?) {
                super.onClick(v)

                start.clearFocus()
                stop.clearFocus()

                val startPort = start.getValue()
                val stopPort = stop.getValue()
                if ((startPort - stopPort > 0)) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Please pick a valid port range",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                UserPreference.savePortRangeStart(activity, startPort)
                UserPreference.savePortRangeHigh(activity, stopPort)

                scanProgressDialog = ProgressDialog(activity, R.style.DialogTheme)
                scanProgressDialog!!.setCancelable(false)
                val titleTemplate = getResources().getString(R.string.scanning_port_range)
                val title = String.format(titleTemplate, startPort, stopPort)

                scanProgressDialog!!.setTitle(title)
                scanProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                scanProgressDialog!!.setProgress(0)
                scanProgressDialog!!.setMax(stopPort - startPort + 1)
                //                scanProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.cancel),
//                        new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Действия при нажатии кнопки "Отмена"
//                        scanProgressDialog.dismiss(); // Закрыть диалоговое окно
//                    }
//                });
                scanProgressDialog!!.show()

                Host.scanPorts(ip, startPort, stopPort, timeout, activity)
            }
        })
    }

    /**
     * Event handler for when an item on the port list is clicked
     */
    protected fun portListClick(portList: ListView, ip: String?) {
        portList.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            /**
             * Click handler to open certain ports to the browser
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = portList.getItemAtPosition(position) as String?
                if (item == null) {
                    return
                }

                var intent: Intent? = null

                if (item.contains("80 -")) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://" + ip))
                }

                if (item.contains("443 -")) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://" + ip))
                }

                if (item.contains("8080 -")) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://" + ip + ":8080"))
                }

                val packageManager = getPackageManager()
                if (intent != null && packageManager != null) {
                    if (packageManager.resolveActivity(intent, 0) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "No application found to open this to the browser!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    /**
     * Delegate to handle incrementing the scan progress dialog
     *
     * @param output The amount of progress to increment
     */
    override fun processFinish(output: Int) {
        handler!!.post(Runnable {
            if (scanProgressDialog != null) {
                scanProgressDialog!!.incrementProgressBy(output)
            }
        })
    }

    /**
     * Delegate to handle open ports
     *
     * @param output Contains the port number and associated banner (if any)
     */
    override fun processFinish(output: SparseArray<String?>) {
        val scannedPort = output.keyAt(0)
        var item = scannedPort.toString()

        var name = database0!!.selectPortDescription(scannedPort.toString())
        name = if (name.isEmpty()) "unknown" else name
        item = formatOpenPort(output, scannedPort, name, item)
        addOpenPort(item)
    }

    /**
     * Formats a found open port with its name, description, and associated visualization
     *
     * @param entry       Structure holding information about the found open port with its description
     * @param scannedPort The port number
     * @param portName    Friendly name for the port
     * @param item        Contains the transformed output for the open port
     * @return If all associated data is found a port along with its description, underlying service, and visualization is constructed
     */
    private fun formatOpenPort(
        entry: SparseArray<String?>,
        scannedPort: Int,
        portName: String?,
        item: String?
    ): String {
        var data = item + " - " + portName
        if (entry.get(scannedPort) != null) {
            data += " (" + entry.get(scannedPort) + ")"
        }

        //If the port is in any way related to HTTP then present a nice globe icon next to it via unicode
        if (scannedPort == 80 || scannedPort == 443 || scannedPort == 8080) {
            data += " \uD83C\uDF0E"
        }

        return data
    }

    /**
     * Adds an open port that was found on a host to the list
     *
     * @param port Port number and description
     */
    private fun addOpenPort(port: String?) {
        setAnimations()
        handler!!.post(Runnable {
            ports.add(port)
            Collections.sort<String?>(ports, Comparator { lhs: String?, rhs: String? ->
                val left = lhs!!.substring(0, lhs.indexOf('-'.code.toChar()) - 1).toInt()
                val right = rhs!!.substring(0, rhs.indexOf('-'.code.toChar()) - 1).toInt()
                left - right
            })
            adapter!!.notifyDataSetChanged()
        })
    }

    protected abstract fun setAnimations()

    /**
     * Delegate to handle bubbled up errors
     *
     * @param output The exception we want to handle
     * @param <T>    Exception
    </T> */
    override fun <T : Throwable?> processFinish(output: T?) {
        handler!!.post(Runnable {
            Errors.showError(
                applicationContext,
                output!!.localizedMessage
            )
        })
    }


    protected fun setupAdAtBottom(content: FrameLayout) {
        //FrameLayout content = findViewById(android.R.id.content);

        this.content = content

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
        //aa.attach(this);
        //DLog.d("---->" + aa.hashCode());
        interactor!!.selectView(content, callback)
    }

    protected abstract fun loadRepository(): AdvertRepository? //    private void setSpaceForAd(int height) {
    //        DLog.d("@@@@@@@@" + height);
    /*        FrameLayout content = findViewById(android.R.id.content);
    * /        if (content != null)
    {
        * /            View child0 = content.getChildAt(0);
        * /            //child0.setPadding(0, 0, 0, 50);
        * /
        * /            FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) child0.getLayoutParams();
        * /            //lp.bottomMargin = height;
        * /            child0.setLayoutParams(lp);
        * /
    } */
    //    }
    /*    AdView adView = new AdView(this);
    * /    adView.setAdSize(AdSize.BANNER);
    * /    adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111"); */
    //
    //    private void addLayoutToContent(View ad) {
    //        content.addView(ad);
    //        AdView mAdView = ad.findViewById(R.id.adView);
    //        //mAdView.setAdListener(new AdListener(mAdView));
    //        mAdView.loadAd(new AdRequest.Builder().build());
    //    }
}
