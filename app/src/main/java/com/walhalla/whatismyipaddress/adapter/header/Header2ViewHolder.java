package com.walhalla.whatismyipaddress.adapter.header;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;

public class Header2ViewHolder extends RecyclerView.ViewHolder {

    public final ImageView icon;
    TextView name;

    public Header2ViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.code);
        icon = itemView.findViewById(R.id.icon);
    }

    public void bind(Header2Item obj) {
        name.setText(obj.title);
        if (obj.icon > 0) {
            try {
                icon.setImageResource(obj.icon);
            } catch (Exception r) {
                DLog.handleException(r);
            }
        }
    }
}