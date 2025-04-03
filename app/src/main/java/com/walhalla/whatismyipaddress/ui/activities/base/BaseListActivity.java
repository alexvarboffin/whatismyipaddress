//package com.walhalla.whatismyipaddress.ui.activities.base;
//
//import static com.walhalla.whatismyipaddress.TApp.repository;
//
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.annotation.LayoutRes;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.walhalla.compat.ComV19;
//import com.walhalla.domain.repository.AdvertRepository;
//
//import com.walhalla.whatismyipaddress.R;
//import com.walhalla.whatismyipaddress.adapter.ListAdapter;
//import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
//import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
//import com.walhalla.whatismyipaddress.databinding.ActivityBaselistLayoutBinding;
//
//import java.util.List;
//
//import es.dmoral.toasty.Toasty;
//
//public abstract class BaseListActivity extends BActivity
//        implements ListAdapter.OnItemClickListener {
//
//    private ListAdapter adapter;
//    protected ComV19 comv19;
//
//
//
//    private ActivityBaselistLayoutBinding binding;
//
//    public ActivityBaselistLayoutBinding getBinding() {
//        return binding;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityBaselistLayoutBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        //LinearLayout rootView = findViewById(R.id.root_layout);
//        //View contentView = getLayoutInflater().inflate(getContentViewLayoutId(), rootView, false);
//        //rootView.addView(contentView);
//
//
//        View contentView = getLayoutInflater().inflate(getContentViewLayoutId(), binding.contentContainer, false);
//        binding.contentContainer.addView(contentView);
//
////        View contentView = getContentViewLayoutId();
////        binding.contentContainer.addView(contentView);
//
//        comv19 = new ComV19();
//        adapter = new ListAdapter(this);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        binding.listView.setLayoutManager(layoutManager);
//        //listView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
//        binding.listView.computeHorizontalScrollExtent();
//        //listView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        binding.listView.addItemDecoration(itemDecoration);
//        binding.listView.setAdapter(adapter);
//        adapter.setOnItemClickListener(this);
//    }
//
//    @Override
//    protected AdvertRepository loadRepository() {
//        return repository;
//    }
//
//
//    public void swap(List<ViewModel> dataModels) {
//        adapter.swap(dataModels);
//    }
//
//    @Override
//    public void onListItemClick(ViewModel dataModel) {
////                Snackbar snackbar = Snackbar
////                        .make(layout, getString(R.string.q_copy_value_to_clipboard), Snackbar.LENGTH_LONG)
////                        .setAction(R.string.action_copy, view1 -> {
////                            if (dataModel instanceof TwoColItem) {
////                                String value = ((TwoColItem) dataModel).value;
////                                copyToBuffer(value);
////                            }
////                        });
////
////                snackbar.show();
//        if (dataModel instanceof TwoColItem) {
//            String value = ((TwoColItem) dataModel).value;
//            copyToBuffer(value);
//        }
//    }
//
//    public void copyToBuffer(String value) {
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        if (clipboard != null) {
//            ClipData clip = ClipData.newPlainText("IP Tools", value);
//            clipboard.setPrimaryClip(clip);
//            Toasty.custom(this, String.format(getString(R.string.data_to_clipboard), value).toUpperCase(), comv19.getDrawable(this,
//                            R.drawable.ic_info), ContextCompat.getColor(this, R.color.colorPrimaryDark),
//                    ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
//        }
//    }
//
//    @LayoutRes
//    protected abstract View getContentViewLayoutId();
//}
