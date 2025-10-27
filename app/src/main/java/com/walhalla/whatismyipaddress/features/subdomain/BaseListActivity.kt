package com.walhalla.whatismyipaddress.features.subdomain

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
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
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.TApp
import com.walhalla.whatismyipaddress.adapter.ListAdapter
import com.walhalla.whatismyipaddress.adapter.items.ViewModel
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem
import com.walhalla.whatismyipaddress.databinding.ActivityBaselistLayoutBinding
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import java.util.Locale

abstract class BaseListActivity : AppCompatActivity(), ListAdapter.OnItemClickListener {
    private var m: ListAdapter? = null
    @JvmField
    protected var comv19: ComV19? = null

    fun getBinding(): ActivityBaselistLayoutBinding {
        return binding!!
    }

    private var binding: ActivityBaselistLayoutBinding? = null

    @JvmField
    protected val start_time: Long = System.currentTimeMillis()

    private val callback: AdvertInteractor.Callback<View> =
        object : AdvertInteractor.Callback<View> {
            override fun onMessageRetrieved(id: Int, message: View) {
                d(message.javaClass.getName() + " --> " + message.hashCode())

                if (binding!!.bottomButton != null) {
                    d("@@@" + binding!!.bottomButton.javaClass.name)
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

            override fun onRetrievalFailed(error: String) {
                d("---->" + error)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaselistLayoutBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())


        //LinearLayout rootView = findViewById(R.id.root_layout);
        //View contentView = getLayoutInflater().inflate(getContentViewLayoutId(), rootView, false);
        //rootView.addView(contentView);


//        View contentView = getLayoutInflater().inflate(getContentViewLayoutId(), binding.contentContainer, false);
//        binding.contentContainer.addView(contentView);
        val contentView = this.contentViewLayoutId
        binding!!.contentContainer.addView(contentView)

        comv19 = ComV19()
        m = ListAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        binding!!.listView.setLayoutManager(layoutManager)
        //listView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        binding!!.listView.computeHorizontalScrollExtent()
        //listView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        val itemDecoration: ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding!!.listView.addItemDecoration(itemDecoration)
        binding!!.listView.setAdapter(m)
        m!!.setOnItemClickListener(this)
    }


    protected fun loadRepository(): AdvertRepository? {
        return TApp.repository
    }


    fun swap(dataModels: MutableList<ViewModel?>?) {
        m!!.swap(dataModels)
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
        }
    }

    override fun copyToBuffer(value: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard != null) {
            val clip = ClipData.newPlainText("IP Tools", value)
            clipboard.setPrimaryClip(clip)
            Toasty.custom(
                this, String.format(getString(R.string.data_to_clipboard), value).uppercase(
                    Locale.getDefault()
                ), comv19!!.getDrawable(
                    this,
                    R.drawable.ic_info
                ), ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true
            ).show()
        }
    }

    protected abstract val contentViewLayoutId: View?

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

    fun showProgress() {
        binding!!.ping.setEnabled(false)
        binding!!.spinKit.setVisibility(View.VISIBLE)
    }

    fun hideProgress() {
        binding!!.spinKit.setVisibility(View.GONE)
        binding!!.ping.setEnabled(true)
    }
}

