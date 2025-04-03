package com.walhalla.whatismyipaddress.ui.adapter.exobj;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.adapter.ComplexAdapter;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0;

import java.io.IOException;
import java.io.InputStream;

public class ExtendedViewHolder extends ComplexAdapter.SimpleViewHolder {

    private final ImageView imageView;
    private final View icon;
    private final String err_not_available;


    public ExtendedViewHolder(View itemView, String err_not_available) {
        super(itemView);
        icon = itemView.findViewById(R.id.icon);
        imageView = itemView.findViewById(R.id.icon01);
        this.err_not_available = err_not_available;
    }

    public void bind(ViewModel0 obj, ComplexAdapter.CallbackDefault mCallback, boolean flag, Context context) {
        super.bind(obj);
        this.icon.setOnClickListener(v -> mCallback.copyToClipboardPressed(v, obj.content));

        if (flag) {
            try {
                String raw = "";
                if (err_not_available.equals(obj.content)) {
                    raw = "ic_unknown.png";
                } else {
                    raw = "raw/ic_" + obj.content + ".png";
                }
                InputStream ims = context.getAssets().open((raw).toLowerCase());
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                imageView.setImageDrawable(d);
                ims.close();
                return;
            } catch (IOException ex) {
                DLog.handleException(ex);
            }
        }

        if (null != obj.icon) {
            try {
                imageView.setImageResource(obj.icon);
            } catch (Exception r) {
                DLog.handleException(r);
            }
        }
    }
}