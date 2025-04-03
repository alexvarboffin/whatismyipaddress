package com.walhalla.whatismyipaddress.features.subdomain;

import static com.walhalla.whatismyipaddress.TApp.repository;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.boilerplate.domain.executor.impl.ThreadExecutor;
import com.walhalla.boilerplate.threading.MainThreadImpl;
import com.walhalla.compat.ComV19;
import com.walhalla.domain.interactors.AdvertInteractor;
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl;
import com.walhalla.domain.repository.AdvertRepository;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.adapter.ListAdapter;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.databinding.ActivityBaselistLayoutBinding;

import java.util.List;

import es.dmoral.toasty.Toasty;

public abstract class BaseListActivity extends AppCompatActivity
        implements ListAdapter.OnItemClickListener {

    private ListAdapter m;
    protected ComV19 comv19;

    public ActivityBaselistLayoutBinding getBinding() {
        return binding;
    }

    private ActivityBaselistLayoutBinding binding;

    protected final long start_time = System.currentTimeMillis();

    private final AdvertInteractor.Callback<View> callback = new AdvertInteractor.Callback<>() {
        @Override
        public void onMessageRetrieved(int id, View message) {
            DLog.d(message.getClass().getName() + " --> " + message.hashCode());

            if (binding.bottomButton != null) {
                DLog.d("@@@" + binding.bottomButton.getClass().getName());
                try {
                    //content.removeView(message);
                    if (message.getParent() != null) {
                        ((ViewGroup) message.getParent()).removeView(message);
                    }
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    message.setLayoutParams(params);

                    ViewTreeObserver vto = message.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT < 16) {
                                message.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                message.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            //int width = message.getMeasuredWidth();
                            //int height = message.getMeasuredHeight();
                            //DLog.i("@@@@" + height + "x" + width);
                            //setSpaceForAd(height);
                        }
                    });
                    binding.bottomButton.addView(message);

                } catch (Exception e) {
                    DLog.handleException(e);
                }
            }
        }

        @Override
        public void onRetrievalFailed(String error) {
            DLog.d("---->" + error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaselistLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //LinearLayout rootView = findViewById(R.id.root_layout);
        //View contentView = getLayoutInflater().inflate(getContentViewLayoutId(), rootView, false);
        //rootView.addView(contentView);


//        View contentView = getLayoutInflater().inflate(getContentViewLayoutId(), binding.contentContainer, false);
//        binding.contentContainer.addView(contentView);

        View contentView = getContentViewLayoutId();
        binding.contentContainer.addView(contentView);

        comv19 = new ComV19();
        m = new ListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.listView.setLayoutManager(layoutManager);
        //listView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        binding.listView.computeHorizontalScrollExtent();
        //listView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.listView.addItemDecoration(itemDecoration);
        binding.listView.setAdapter(m);
        m.setOnItemClickListener(this);
    }


    protected AdvertRepository loadRepository() {
        return repository;
    }


    public void swap(List<ViewModel> dataModels) {
        m.swap(dataModels);
    }

    @Override
    public void onListItemClick(ViewModel dataModel) {
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
        if (dataModel instanceof TwoColItem) {
            String value = ((TwoColItem) dataModel).value;
            copyToBuffer(value);
        }
    }

    public void copyToBuffer(String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("IP Tools", value);
            clipboard.setPrimaryClip(clip);
            Toasty.custom(this, String.format(getString(R.string.data_to_clipboard), value).toUpperCase(), comv19.getDrawable(this,
                            R.drawable.ic_info), ContextCompat.getColor(this, R.color.colorPrimaryDark),
                    ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
    }

    protected abstract View getContentViewLayoutId();

    protected void setupAdAtBottom() {

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

        AdvertInteractorImpl interactor = new AdvertInteractorImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(), loadRepository());
        //aa.attach(this);
        //DLog.d("---->" + aa.hashCode());
        interactor.selectView(binding.bottomButton, callback);
    }

    public void showProgress() {
        binding.ping.setEnabled(false);
        binding.spinKit.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        binding.spinKit.setVisibility(View.GONE);
        binding.ping.setEnabled(true);
    }
}

