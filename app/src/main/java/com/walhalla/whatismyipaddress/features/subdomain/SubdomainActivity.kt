package com.walhalla.whatismyipaddress.features.subdomain

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.walhalla.boilerplate.domain.executor.impl.ThreadExecutor
import com.walhalla.boilerplate.threading.MainThreadImpl
import com.walhalla.compat.ComV19
import com.walhalla.domain.interactors.AdvertInteractor
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl
import com.walhalla.domain.repository.AdvertRepository
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.plugins.Module_U.shareText
import com.walhalla.whatismyipaddress.AssetUtils
import com.walhalla.whatismyipaddress.Helpers0
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.TApp
import com.walhalla.whatismyipaddress.adapter.ListAdapter
import com.walhalla.whatismyipaddress.adapter.cert.Certificate
import com.walhalla.whatismyipaddress.adapter.items.ViewModel
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem
import com.walhalla.whatismyipaddress.databinding.ActivitySubdomainBinding
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope

import java.util.Locale
import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.Long
import kotlin.arrayOf

class SubdomainActivity : AppCompatActivity(), SubdomainContract.View,
    ListAdapter.OnItemClickListener {
    private var binding: ActivitySubdomainBinding? = null
    private var presenter: SubdomainPresenter? = null

    private var listAdapter: ListAdapter? = null
    protected var comv19: ComV19? = null

    fun getBinding(): ActivitySubdomainBinding {
        return binding!!
    }


    protected val start_time: Long = System.currentTimeMillis()

    private val callback: AdvertInteractor.Callback<View> =
        object : AdvertInteractor.Callback<View> {
            override fun onMessageRetrieved(id: Int, message: View) {
                d(message.javaClass.getName() + " --> " + message.hashCode())

                if (binding!!.bottomButton != null) {
                    d("@@@" + binding!!.bottomButton.javaClass.name)
                    try {
                        //content.removeView(message);
                        if (message.getParent() != null) {
                            (message.getParent() as ViewGroup).removeView(message)
                        }
                        val params = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.gravity = Gravity.BOTTOM or Gravity.CENTER
                        message.setLayoutParams(params)

                        val vto = message.viewTreeObserver
                        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                if (Build.VERSION.SDK_INT < 16) {
                                    message.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                } else {
                                    message.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                }
                                //int width = message.getMeasuredWidth();
                                //int height = message.getMeasuredHeight();
                                //DLog.i("@@@@" + height + "x" + width);
                                //setSpaceForAd(height);
                            }
                        })
                        binding!!.bottomButton.addView(message)
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
            }

            override fun onRetrievalFailed(error: kotlin.String) {
                d("---->" + error)
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubdomainBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())

        //@@
        binding!!.allOverflowMenu.visibility = View.GONE
        binding!!.allOverflowMenu0.visibility = View.GONE

        comv19 = ComV19()
        listAdapter = ListAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        binding!!.listView.setLayoutManager(layoutManager)
        //listView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        binding!!.listView.computeHorizontalScrollExtent()
        //listView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        val itemDecoration: ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding!!.listView.addItemDecoration(itemDecoration)
        binding!!.listView.setAdapter(listAdapter)
        listAdapter!!.setOnItemClickListener(this)


        //@@
        getBinding().title.setText(R.string.titleSubdomainFinder)

        var var0: kotlin.String = ""
        if (getIntent() != null) {
            var0 = intent.getStringExtra(KEY_VAR0)?:""
        }

        val handler = Handler(Looper.getMainLooper())
        presenter = SubdomainPresenter(this, handler, this)

        //View view = binding.getRoot();
        //setContentView(view);
        val aa = binding!!.requestTypeRadioGroup.getChildAt(0)
        if (aa is RadioButton) {
            aa.setChecked(true)
        }

        getBinding().ping.setText(R.string.action_button_findsubdomain)
        getBinding().ping.setOnClickListener(View.OnClickListener { v: View? ->
            val domain =
                binding!!.domainEditText.getText().toString().lowercase(Locale.getDefault())
            findSubdomain(this, domain)
        })

        //        binding.copyResultButton.setOnClickListener((View.OnClickListener) v ->
//        copyResult(binding.resultTextView.getText().toString()));
//        binding.saveToCloudButton.setOnClickListener((View.OnClickListener) v -> saveToCloud());
        binding!!.back.setOnClickListener(View.OnClickListener { view: View? -> super.onBackPressed() })
        binding!!.allOverflowMenu.setOnClickListener(View.OnClickListener { v: View? ->
            showPopupMenu(
                v!!
            )
        })
        binding!!.allOverflowMenu0.setOnClickListener(View.OnClickListener { v: View? ->
            showSharePopupMenu(
                v!!
            )
        })

        if (TextUtils.isEmpty(var0)) {
            presenter!!.init()
        } else {
            init(var0)
        }
    }

    private fun showSharePopupMenu(view: View) {
        if (listAdapter!!.getModels().isEmpty()) {
            showValidationError("Run a scan first!")
            return
        }

        val popup = PopupMenu(view.getContext(), view)
        val inflater = popup.getMenuInflater()
        val menu = popup.getMenu()
        inflater.inflate(R.menu.popup_cert_all_share, menu)
        val menuHelper: Any?
        val argTypes: Array<Class<*>>?
        try {
            val fMenuHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.setAccessible(true)
            menuHelper = fMenuHelper.get(popup)
            argTypes = arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
        }
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem: MenuItem? ->
            val id = menuItem!!.itemId
            if (id == R.id.actionShareAllCommonName) {
                val set = extractCommonName()
                val joined = java.lang.String.join("\n", set)
                shareText(joined)
            } else if (id == R.id.actionShareAllNameValue) {
                val set0 = extractNameValue()
                val joined0 = java.lang.String.join("\n", set0)
                shareText(joined0)
            } else if (id == R.id.actionShareAllCommonNameAndNameValue) {
                val set0 = extractCommonNameNameValue()
                val joined0 = java.lang.String.join("\n", set0)
                shareText(joined0)
            }
            false
        })
        popup.show()
    }

    private fun showPopupMenu(view: View) {
        if (listAdapter!!.getModels().isEmpty()) {
            showValidationError("Run a scan first!")
            return
        }
        val popup = PopupMenu(view.getContext(), view)
        val inflater = popup.getMenuInflater()
        val menu = popup.getMenu()
        inflater.inflate(R.menu.popup_cert_all_copy, menu)
        val menuHelper: Any?
        val argTypes: Array<Class<*>>?
        try {
            val fMenuHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.setAccessible(true)
            menuHelper = fMenuHelper.get(popup)
            argTypes = arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
        }
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem: MenuItem? ->
            val id = menuItem!!.getItemId()
            if (id == R.id.actionCopyAllCommonName) {
                val set = extractCommonName()
                val joined = java.lang.String.join("\n", set)
                copyToBuffer(joined)
            } else if (id == R.id.actionCopyAllNameValue) {
                val set0 = extractNameValue()
                val joined0 = java.lang.String.join("\n", set0)
                copyToBuffer(joined0)
            } else if (id == R.id.actionCopyAllCommonNameAndNameValue) {
                val set0 = extractCommonNameNameValue()
                val joined0 = java.lang.String.join("\n", set0)
                copyToBuffer(joined0)
            }
            false
        })
        popup.show()
    }

    private fun shareText(value: kotlin.String) {
        shareText(this, value, "Subdomains Tools")
    }


    private fun extractCommonName(): MutableSet<kotlin.String?> {
        val set: MutableSet<kotlin.String?> = LinkedHashSet<kotlin.String?>()
        val objs = listAdapter!!.getModels()
        for (obj in objs) {
            if (obj is Certificate) {
                val commonName = obj.getCommonName()
                set.add(commonName)
            }
        }
        return set
    }

    private fun extractCommonNameNameValue(): MutableSet<kotlin.String?> {
        val set: MutableSet<kotlin.String?> = LinkedHashSet<kotlin.String?>()
        val objs = listAdapter!!.getModels()
        for (obj in objs) {
            if (obj is Certificate) {
                val m = obj
                set.add(m.getCommonName())
                set.add(m.getNameValue())
            }
        }
        return set
    }

    private fun extractNameValue(): MutableSet<kotlin.String?> {
        val set: MutableSet<kotlin.String?> = LinkedHashSet<kotlin.String?>()
        val objs = listAdapter!!.getModels()
        for (obj in objs) {
            if (obj is Certificate) {
                val nameValue = obj.getNameValue()
                set.add(nameValue)
            }
        }
        return set
    }


    override fun init(var0: kotlin.String?) {
        binding!!.domainEditText.setText(var0)
    }


    private fun copyResult(result: kotlin.String?) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", result)
        clipboard.setPrimaryClip(clip)
        //s("Copied to clipboard");
    }

    //    private void saveToCloud() {
    //        final String result = binding.resultTextView.getText().toString();
    //        if (!result.isEmpty()) {
    //            new Thread(() -> {
    //                try {
    //                    URL url = new URL("https://8080.my.id/paste/save.php");
    //                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    //                    conn.setRequestMethod("POST");
    //                    conn.setDoOutput(true);
    //                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    //
    //                    String params = "data=" + result;
    //                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
    //                    wr.writeBytes(params);
    //                    wr.flush();
    //                    wr.close();
    //
    //                    InputStream inputStream = conn.getInputStream();
    //                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
    //                    String response = scanner.hasNext() ? scanner.next() : "";
    //
    //                    JSONObject jsonObject = new JSONObject(response);
    //                    final String outputURL = jsonObject.getJSONArray("output").getJSONObject(0).getString("url");
    //
    //                    runOnUiThread(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            binding.urlPasteEditText.setText(outputURL);
    //                            binding.urlPasteEditText.setVisibility(View.VISIBLE);
    //                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    //                            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied URL", outputURL);
    //                            clipboard.setPrimaryClip(clip);
    //                            showToast("URL Copied to clipboard");
    //                        }
    //                    });
    //
    //                    scanner.close();
    //                    inputStream.close();
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                    showToast("Failed to Save to Cloud");
    //                }
    //            }).start();
    //        } else {
    //            showToast("Result is Empty");
    //        }
    //    }
    private fun findSubdomain(context: Context, domain: kotlin.String) {
        Helpers0.hideKeyboard(this)
        if (domain.isEmpty() || domain == " ") {
            //showToast("Please Input Domain!");
            Toasty.custom(
                context,
                getString(R.string.provideallfields).uppercase(Locale.getDefault()),
                comv19!!.getDrawable(context, R.drawable.ic_cancel),
                ContextCompat.getColor(context, R.color.error),
                ContextCompat.getColor(context, R.color.white),
                Toasty.LENGTH_SHORT,
                true,
                true
            ).show()
        } else {
            if (AssetUtils.isNetworkAvailable(this)) {
                showProgress()
                try {
//                    int pingLimit = Integer.parseInt(limit.getText().toString().trim());
//                    int pingTimeout = Integer.parseInt(timeout.getText().toString().trim());
//                    String pingIpText = domain;
//                    presenter.startPing(pingIpText, pingTimeout, pingLimit);
                    showShoartToast("Send Request Data...")
                    binding!!.domainEditText.isEnabled = false
                    getBinding().ping.isEnabled = false
                    setMessage("")
                    setMessage("Finding Subdomain...")

                    val selectedRadioButtonId =
                        binding!!.requestTypeRadioGroup.checkedRadioButtonId
                    //String requestType = ((RadioButton) findViewById(selectedRadioButtonId)).getText().toString();
                    presenter!!.submitButtonClicked(domain, selectedRadioButtonId)
                } catch (e: Exception) {
                    handleException(e)
                }
            } else {
                Toasty.custom(
                    context,
                    getString(R.string.internet_connectivity_problem),
                    comv19!!.getDrawable(context, R.drawable.ic_cancel),
                    ContextCompat.getColor(context, R.color.error),
                    ContextCompat.getColor(context, R.color.white),
                    Toasty.LENGTH_SHORT,
                    true,
                    true
                ).show()
            }
        }
    }

    private fun showShoartToast(value: kotlin.String) {
        Toasty.custom(
            this, value, comv19!!.getDrawable(
                this,
                R.drawable.ic_info
            ),
            ContextCompat.getColor(this, R.color.colorPrimaryDark),
            ContextCompat.getColor(this, R.color.white),
            Toasty.LENGTH_SHORT, true, true
        ).show()
    }

    override fun showProgress() {
        getBinding().ping.setEnabled(false)
        getBinding().spinKit.setVisibility(View.VISIBLE)

        binding!!.allOverflowMenu.visibility = View.GONE
        binding!!.allOverflowMenu0.visibility = View.GONE
    }


    override fun showValidationError(errorMessage: kotlin.String) {
        hideProgress()
        Toasty.custom(
            this, errorMessage, comv19!!.getDrawable(
                this,
                R.drawable.ic_cancel
            ),
            ContextCompat.getColor(this, R.color.error),
            ContextCompat.getColor(this, R.color.white),
            Toasty.LENGTH_LONG, true, true
        ).show()
    }

    override fun successSubdomainsResult(dataModels: MutableList<ViewModel?>?) {
//        StringBuilder result = new StringBuilder();
//        for (String subdomain : subdomains) {
//            result.append(subdomain).append("\n");
//        }
//        setMessage(result.toString());
//        binding.domainEditText.setEnabled(true);
//        binding.findSubdomainButton.setEnabled(true);
//        showToast("Successfully Get Response Data, Total Subdomains: " + subdomains.size());
        swap(dataModels)
    }

    override fun hideProgress() {
        getBinding().ping.setEnabled(true)
        getBinding().spinKit.visibility = View.GONE

        binding!!.allOverflowMenu.visibility = View.VISIBLE
        binding!!.allOverflowMenu0.visibility = View.VISIBLE
    }

    private fun setMessage(s: kotlin.String?) {
        val dataModels: MutableList<ViewModel?> = ArrayList<ViewModel?>()
        dataModels.add(TwoColItem(s, ""))
        swap(dataModels)
    }


    override fun copyToBuffer(value: kotlin.String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard != null) {
            val clip = ClipData.newPlainText("Subdomains Tools", value)
            clipboard.setPrimaryClip(clip)
            Toasty.custom(
                this, kotlin.String.format(getString(R.string.data_to_clipboard), value).uppercase(
                    Locale.getDefault()
                ), comv19!!.getDrawable(
                    this,
                    R.drawable.ic_info
                ), ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true
            ).show()
        }
    }

    fun swap(dataModels: MutableList<ViewModel?>?) {
        listAdapter!!.swap(dataModels)
    }

    override fun onListItemClick(dataModel: ViewModel?) {
//                Snackbar snackbar = Snackbar
//                        .make(layout, getString(R.string.q_copy_value_to_clipboard), Snackbar.LENGTH_LONG)
//                        .setAction(R.string.action_copy, view1 -> {
//                            if (dataModel instanceof TwoColItem) {
//                                String value = ((TwoColItem) dataModel).value;
//                                copyToBuffer(value);
//                            }
//                        });
//
//                snackbar.show();

        if (dataModel is TwoColItem) {
            val value = dataModel.value
            copyToBuffer(value)
        } else if (dataModel is Certificate) {
            val value = dataModel.commonName
            copyToBuffer(value)
        }
    }


    protected fun loadRepository(): AdvertRepository? {
        return TApp.repository
    }

    protected fun setupAdAtBottom() {
        //FrameLayout content = findViewById(android.R.id.content);

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
        interactor.selectView(binding!!.bottomButton, callback)
    }

    companion object {
        private val KEY_VAR0: kotlin.String = SubdomainActivity::class.java.simpleName

        fun newInstance(context: Context?, content: kotlin.String?): Intent {
            val intent = Intent(context, SubdomainActivity::class.java)
            intent.putExtra(KEY_VAR0, content)
            return intent
        }
    }
}