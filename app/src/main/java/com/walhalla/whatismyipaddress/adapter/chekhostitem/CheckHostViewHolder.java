package com.walhalla.whatismyipaddress.adapter.chekhostitem;

import static com.walhalla.whatismyipaddress.features.checkhost.PingResult.PING_RESULT_OK;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.features.checkhost.PingItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CheckHostViewHolder extends RecyclerView.ViewHolder {

    private final ImageView icon;
    public final SpinKitView spin0;
    private final ImageView stat;

    public TextView code;

    public TextView value1;
    public TextView value2;
    public TextView _ip;

    public CheckHostViewHolder(@NonNull View itemView) {
        super(itemView);
        code = itemView.findViewById(R.id.code);
        value1 = itemView.findViewById(R.id.value1);
        value2 = itemView.findViewById(R.id.value2);
        _ip = itemView.findViewById(R.id.value3);
        stat = itemView.findViewById(R.id.value4);
        icon = itemView.findViewById(R.id.icon);
        spin0 = itemView.findViewById(R.id.spin_kit);
    }

    public void bind(CheckHostItem dataModel) {
//        code.setText(dataModel.serverKey);
//        value.setText(dataModel.serverCity + ", " + dataModel.serverCountry);

        code.setText(dataModel.serverCity + ", " + dataModel.serverCountry);

        if (dataModel.items != null) {
            String ip = "";
            List<Double> numbers = new ArrayList<>();
            double sum = 0;

            int total = dataModel.items.size();
            int k = 0;
            for (PingItem item : dataModel.items) {
                if (PING_RESULT_OK.equals(item.pingResult)) {
                    ++k;
                    Double time = item.pingTime;
                    numbers.add(time);
                    sum += time;

                    if (!TextUtils.isEmpty(item.pingIp)) {
                        ip = item.pingIp;
                    }
                }
            }
            String m = k + "/" + total;
            value1.setText(m);
            spin0.setVisibility(View.GONE);

            if (numbers.isEmpty()) {
                value2.setText(null);
            } else {
                Double min = Collections.min(numbers);
                Double max = Collections.max(numbers);

                double average = numbers.size() > 0 ? sum / numbers.size() : 0;
                float aMs = (float) (average * 1000);

                String formattedNumber = String.format(Locale.CANADA, "%.2f", aMs);
                //Location Progress Avg Time Status
                value2.setText(formattedNumber);
            }

            if (ip.isEmpty()) {
                _ip.setText("cannot resolved");
            } else {
                _ip.setText("" + ip);
            }

            if (k == 4) {
                stat.setImageResource(R.drawable.abc_ic5);
            } else if (k == 3) {
                stat.setImageResource(R.drawable.abc_ic4);
            } else if (k == 2) {
                stat.setImageResource(R.drawable.abc_ic3);
            } else {
                stat.setImageResource(R.drawable.abc_ic2);
            }

        } else {
            value1.setText(null);
            value2.setText(null);
            spin0.setVisibility(View.VISIBLE);
            _ip.setText(null);
        }


        if (dataModel.color > 0) {
            code.setTextColor(code.getContext().getResources().getColor(dataModel.color));
        }
        if (!TextUtils.isEmpty(dataModel.countryCode)) {
            try {
                String raw = "raw/ic_" + dataModel.countryCode + ".png";
//                if (err_not_available.equals(obj.content)) {
//                    raw = "ic_unknown.png";
//                } else {
//                    raw = "raw/ic_" + dataModel.countryCode + ".png";
//                }
                InputStream ims = itemView.getContext().getAssets().open((raw).toLowerCase());
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                icon.setImageDrawable(d);
                ims.close();
            } catch (IOException ex) {
                DLog.handleException(ex);
            }
        }
    }

}