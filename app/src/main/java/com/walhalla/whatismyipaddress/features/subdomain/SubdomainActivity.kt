package com.walhalla.whatismyipaddress.features.subdomain;

import static com.walhalla.whatismyipaddress.Helpers0.hideKeyboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;

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
import com.walhalla.ui.plugins.Module_U;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.TApp;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.ListAdapter;
import com.walhalla.whatismyipaddress.adapter.cert.Certificate;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.databinding.ActivitySubdomainBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import es.dmoral.toasty.Toasty;

public class SubdomainActivity extends AppCompatActivity implements SubdomainContract.View,
        ListAdapter.OnItemClickListener {
    private static final String KEY_VAR0 = SubdomainActivity.class.getSimpleName();

    public static Intent newInstance(Context context, String content) {
        Intent intent = new Intent(context, SubdomainActivity.class);
        intent.putExtra(KEY_VAR0, content);
        return intent;
    }

    private ActivitySubdomainBinding binding;
    private SubdomainPresenter presenter;

    private ListAdapter listAdapter;
    protected ComV19 comv19;

    public ActivitySubdomainBinding getBinding() {
        return binding;
    }


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
        binding = ActivitySubdomainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //@@
        binding.allOverflowMenu.setVisibility(View.GONE);
        binding.allOverflowMenu0.setVisibility(View.GONE);

        comv19 = new ComV19();
        listAdapter = new ListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.listView.setLayoutManager(layoutManager);
        //listView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        binding.listView.computeHorizontalScrollExtent();
        //listView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.listView.addItemDecoration(itemDecoration);
        binding.listView.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(this);
        //@@


        getBinding().title.setText(R.string.titleSubdomainFinder);

        String var0 = "";
        if (getIntent() != null) {
            var0 = getIntent().getStringExtra(KEY_VAR0);
        }

        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new SubdomainPresenter(this, handler, this);

        //View view = binding.getRoot();
        //setContentView(view);
        View aa = binding.requestTypeRadioGroup.getChildAt(0);
        if (aa instanceof RadioButton) {
            ((RadioButton) aa).setChecked(true);
        }

        getBinding().ping.setText(R.string.action_button_findsubdomain);
        getBinding().ping.setOnClickListener(v -> {
            String domain = binding.domainEditText.getText().toString().toLowerCase();
            findSubdomain(this, domain);
        });

//        binding.copyResultButton.setOnClickListener((View.OnClickListener) v ->
//        copyResult(binding.resultTextView.getText().toString()));
//        binding.saveToCloudButton.setOnClickListener((View.OnClickListener) v -> saveToCloud());

        binding.back.setOnClickListener(view -> super.onBackPressed());
        binding.allOverflowMenu.setOnClickListener(v -> showPopupMenu(v));
        binding.allOverflowMenu0.setOnClickListener(v -> showSharePopupMenu(v));

        if (TextUtils.isEmpty(var0)) {
            presenter.init();
        } else {
            init(var0);
        }
    }

    private void showSharePopupMenu(View view) {

        if (listAdapter.getModels().isEmpty()) {
            showValidationError("Run a scan first!");
            return;
        }

        android.widget.PopupMenu popup = new android.widget.PopupMenu(view.getContext(), view);
        android.view.MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        inflater.inflate(R.menu.popup_cert_all_share, menu);
        Object menuHelper;
        Class<?>[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
        }
        popup.setOnMenuItemClickListener(menuItem -> {

            int id = menuItem.getItemId();
            if (id == R.id.actionShareAllCommonName) {
                Set<String> set = extractCommonName();
                String joined = String.join("\n", set);
                shareText(joined);
            } else if (id == R.id.actionShareAllNameValue) {
                Set<String> set0 = extractNameValue();
                String joined0 = String.join("\n", set0);
                shareText(joined0);
            } else if (id == R.id.actionShareAllCommonNameAndNameValue) {
                Set<String> set0 = extractCommonNameNameValue();
                String joined0 = String.join("\n", set0);
                shareText(joined0);
            }
            return false;
        });
        popup.show();
    }

    private void showPopupMenu(View view) {
        if (listAdapter.getModels().isEmpty()) {
            showValidationError("Run a scan first!");
            return;
        }
        android.widget.PopupMenu popup = new android.widget.PopupMenu(view.getContext(), view);
        android.view.MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        inflater.inflate(R.menu.popup_cert_all_copy, menu);
        Object menuHelper;
        Class<?>[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
        }
        popup.setOnMenuItemClickListener(menuItem -> {

            int id = menuItem.getItemId();
            if (id == R.id.actionCopyAllCommonName) {
                Set<String> set = extractCommonName();
                String joined = String.join("\n", set);
                copyToBuffer(joined);
            } else if (id == R.id.actionCopyAllNameValue) {
                Set<String> set0 = extractNameValue();
                String joined0 = String.join("\n", set0);
                copyToBuffer(joined0);
            } else if (id == R.id.actionCopyAllCommonNameAndNameValue) {
                Set<String> set0 = extractCommonNameNameValue();
                String joined0 = String.join("\n", set0);
                copyToBuffer(joined0);
            }
            return false;
        });
        popup.show();
    }

    private void shareText(String value) {
        Module_U.shareText(this, value, "Subdomains Tools");
    }


    private Set<String> extractCommonName() {
        Set<String> set = new LinkedHashSet<>();
        List<ViewModel> objs = listAdapter.getModels();
        for (ViewModel obj : objs) {
            if (obj instanceof Certificate) {
                String commonName = ((Certificate) obj).getCommonName();
                set.add(commonName);
            }
        }
        return set;
    }

    private Set<String> extractCommonNameNameValue() {
        Set<String> set = new LinkedHashSet<>();
        List<ViewModel> objs = listAdapter.getModels();
        for (ViewModel obj : objs) {
            if (obj instanceof Certificate) {
                Certificate m = ((Certificate) obj);
                set.add(m.getCommonName());
                set.add(m.getNameValue());
            }
        }
        return set;
    }

    private Set<String> extractNameValue() {
        Set<String> set = new LinkedHashSet<>();
        List<ViewModel> objs = listAdapter.getModels();
        for (ViewModel obj : objs) {
            if (obj instanceof Certificate) {
                String nameValue = ((Certificate) obj).getNameValue();
                set.add(nameValue);
            }
        }
        return set;
    }


    public void init(String var0) {
        binding.domainEditText.setText(var0);
    }



    private void copyResult(String result) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", result);
        clipboard.setPrimaryClip(clip);
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

    private void findSubdomain(Context context, final String domain) {

        hideKeyboard(this);
        if (domain.isEmpty() || domain.equals(" ")) {
            //showToast("Please Input Domain!");
            Toasty.custom(context, getString(R.string.provideallfields).toUpperCase(), comv19.getDrawable(context, R.drawable.ic_cancel), ContextCompat.getColor(context, R.color.error), ContextCompat.getColor(context, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        } else {
            if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                try {
//                    int pingLimit = Integer.parseInt(limit.getText().toString().trim());
//                    int pingTimeout = Integer.parseInt(timeout.getText().toString().trim());
//                    String pingIpText = domain;
//                    presenter.startPing(pingIpText, pingTimeout, pingLimit);
                    showShoartToast("Send Request Data...");
                    binding.domainEditText.setEnabled(false);
                    getBinding().ping.setEnabled(false);
                    setMessage("");
                    setMessage("Finding Subdomain...");

                    int selectedRadioButtonId = binding.requestTypeRadioGroup.getCheckedRadioButtonId();
                    //String requestType = ((RadioButton) findViewById(selectedRadioButtonId)).getText().toString();
                    presenter.submitButtonClicked(domain, selectedRadioButtonId);

                } catch (Exception e) {
                    DLog.handleException(e);
                }
            } else {
                Toasty.custom(context, getString(R.string.internet_connectivity_problem), comv19.getDrawable(context, R.drawable.ic_cancel), ContextCompat.getColor(context, R.color.error), ContextCompat.getColor(context, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            }
        }
    }

    private void showShoartToast(String value) {
        Toasty.custom(this, value, comv19.getDrawable(this,
                        R.drawable.ic_info),
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.white),
                Toasty.LENGTH_SHORT, true, true).show();
    }

    public void showProgress() {
        getBinding().ping.setEnabled(false);
        getBinding().spinKit.setVisibility(View.VISIBLE);

        binding.allOverflowMenu.setVisibility(View.GONE);
        binding.allOverflowMenu0.setVisibility(View.GONE);

    }


    @Override
    public void showValidationError(String errorMessage) {
        hideProgress();
        Toasty.custom(this, errorMessage, comv19.getDrawable(this,
                        R.drawable.ic_cancel),
                ContextCompat.getColor(this, R.color.error),
                ContextCompat.getColor(this, R.color.white),
                Toasty.LENGTH_LONG, true, true).show();

    }

    @Override
    public void successSubdomainsResult(List<ViewModel> dataModels) {
//        StringBuilder result = new StringBuilder();
//        for (String subdomain : subdomains) {
//            result.append(subdomain).append("\n");
//        }
//        setMessage(result.toString());
//        binding.domainEditText.setEnabled(true);
//        binding.findSubdomainButton.setEnabled(true);
//        showToast("Successfully Get Response Data, Total Subdomains: " + subdomains.size());
        swap(dataModels);
    }

    public void hideProgress() {
        getBinding().ping.setEnabled(true);
        getBinding().spinKit.setVisibility(View.GONE);

        binding.allOverflowMenu.setVisibility(View.VISIBLE);
        binding.allOverflowMenu0.setVisibility(View.VISIBLE);
    }

    private void setMessage(String s) {
        List<ViewModel> dataModels = new ArrayList<>();
        dataModels.add(new TwoColItem(s, ""));
        swap(dataModels);
    }


    @Override
    public void copyToBuffer(String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Subdomains Tools", value);
            clipboard.setPrimaryClip(clip);
            Toasty.custom(this, String.format(getString(R.string.data_to_clipboard), value).toUpperCase(), comv19.getDrawable(this,
                            R.drawable.ic_info), ContextCompat.getColor(this, R.color.colorPrimaryDark),
                    ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
    }

    public void swap(List<ViewModel> dataModels) {
        listAdapter.swap(dataModels);
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
        } else if (dataModel instanceof Certificate) {
            String value = ((Certificate) dataModel).getCommonName();
            copyToBuffer(value);
        }
    }


    protected AdvertRepository loadRepository() {
        return TApp.repository;
    }

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
}