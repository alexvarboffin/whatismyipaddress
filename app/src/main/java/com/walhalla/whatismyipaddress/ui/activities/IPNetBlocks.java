package com.walhalla.whatismyipaddress.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.walhalla.whatismyipaddress.Helpers0;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.databinding.ActivityIpnetBlocksBinding;
import com.walhalla.whatismyipaddress.features.subdomain.BaseListActivity;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class IPNetBlocks extends BaseListActivity implements IPNetTask.View1 {

    EditText mask, limit;

    CheckBox isIp, isASN, isORG;
    private IPNetTask presenter;

    private static final String KEY_VAR0 = IPNetBlocks.class.getSimpleName();
    private ActivityIpnetBlocksBinding binding;


    public static Intent newInstance(Context context, String content) {
        Intent intent = new Intent(context, IPNetBlocks.class);
        intent.putExtra(KEY_VAR0, content);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityIpnetBlocksBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);

        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new IPNetTask(this, this, handler);

        String var0 = "";
        if (getIntent() != null) {
            var0 = getIntent().getStringExtra(KEY_VAR0);
        }

        comv19 = new ComV19();
        setupAdAtBottom();
        getBinding().title.setText(R.string.ip_netblocks);
        getBinding().ping.setText(R.string.ip_netblocks);

        mask = findViewById(R.id.mask);
        limit = findViewById(R.id.limit);
        isIp = findViewById(R.id.isIp);
        isORG = findViewById(R.id.isorg);
        isASN = findViewById(R.id.isasn);

        getBinding().ping.setOnClickListener(view -> {
            Helpers0.hideKeyboard(this);
            if (binding.ip.getText().toString().trim().equals("")
                    || binding.ip.getText().toString().trim().equals(" ")) {
                Toasty.custom(IPNetBlocks.this, "Provide IP/ASN/ORG".toUpperCase(),
                        comv19.getDrawable(IPNetBlocks.this, R.drawable.ic_cancel),
                        ContextCompat.getColor(IPNetBlocks.this, R.color.error),
                        ContextCompat.getColor(IPNetBlocks.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            } else {
                if (AssetUtils.isNetworkAvailable(this)) {
                    showProgress();
                    if (!isIp.isChecked() && !isASN.isChecked() && !isORG.isChecked()) {
                        Toasty.custom(IPNetBlocks.this, getString(R.string.err_check_one_types),
                                comv19.getDrawable(IPNetBlocks.this, R.drawable.ic_cancel),
                                ContextCompat.getColor(IPNetBlocks.this, R.color.error),
                                ContextCompat.getColor(IPNetBlocks.this, R.color.white),
                                Toasty.LENGTH_SHORT, true, true).show();
                    } else {

                        String ip = this.binding.ip.getText().toString().trim();
                        String maskT = mask.getText().toString().trim();
                        String limitT = limit.getText().toString().trim();
                        presenter.netblocks(ip, maskT, limitT, isIp.isChecked(), isASN.isChecked());
                    }
                } else {
                    Toasty.custom(
                            IPNetBlocks.this,
                            getString(R.string.internet_connectivity_problem),
                            comv19.getDrawable(IPNetBlocks.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(IPNetBlocks.this, R.color.error),
                            ContextCompat.getColor(IPNetBlocks.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();
                }
            }
        });

        isIp.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isASN.setChecked(false);
                isORG.setChecked(false);
            }
        });

        isASN.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isIp.setChecked(false);
                isORG.setChecked(false);
            }
        });

        isORG.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isASN.setChecked(false);
                isIp.setChecked(false);
            }
        });

        getBinding().back.setOnClickListener(view -> IPNetBlocks.super.onBackPressed());


        if (TextUtils.isEmpty(var0)) {
            presenter.init();
        } else {
            binding.ip.text = var0;
        }
    }

    @Override
    protected View getContentViewLayoutId() {
        return binding.getRoot();
    }

    @Override
    public void onListItemClick(ViewModel dataModel) {
        //snackbar
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            if (dataModel instanceof TwoColItem) {
                String value = ((TwoColItem) dataModel).value;
                ClipData clip = ClipData.newPlainText("IP Tools", value);
                clipboard.setPrimaryClip(clip);
                Toasty.custom(IPNetBlocks.this, String.format(getString(R.string.data_to_clipboard), value).toUpperCase(), comv19.getDrawable(IPNetBlocks.this,
                                R.drawable.ic_info), ContextCompat.getColor(IPNetBlocks.this, R.color.colorPrimaryDark),
                        ContextCompat.getColor(IPNetBlocks.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();

            }
        }
    }


    @Override
    public void displayScanResult(ArrayList<ViewModel> dataModels) {
        swap(dataModels);
    }

    @Override
    public void init(String ip, String mask9) {
        this.mask.text = mask9;
        this.binding.ip.setText(ip);
    }
}
