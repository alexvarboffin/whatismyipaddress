package com.walhalla.whatismyipaddress.ui.adapter.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.adapter.ComplexAdapter;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0;

import java.io.IOException;
import java.io.InputStream;

public class MapViewHolder extends ComplexAdapter.SimpleViewHolder {

    private final ImageView imageView;
    private final View icon;
    private final String err_not_available;
    private final TextView content0;


    public MapViewHolder(View itemView, String err_not_available) {
        super(itemView);
        icon = itemView.findViewById(R.id.icon);
        imageView = itemView.findViewById(R.id.icon01);
        this.err_not_available = err_not_available;
        content0 = itemView.findViewById(R.id.tv_description);
    }

    public void bind(ViewModel0 obj, ComplexAdapter.CallbackDefault mCallback, Context context) {
        super.bind(obj);
        this.icon.setOnClickListener(v -> mCallback.locationItemSelected(v, obj.content));

        if (null != obj.icon) {
            try {
                imageView.setImageResource(obj.icon);
            } catch (Exception r) {
                DLog.handleException(r);
            }
        }
        if (!err_not_available.equals(obj.content)) {
            String label1 = obj.content;
            SpannableString content = new SpannableString(label1);
            content.setSpan(new UnderlineSpan(), 0, label1.length(), 0);
            this.content0.setText(content);
        } else {
            this.content0.setText(obj.content);
        }
    }
}