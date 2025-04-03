package com.walhalla.whatismyipaddress.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0;

public class SpeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ComplexAdapter.Callback2Menu mCallback;
    private final View icon;

    public TextView title;
    private final ImageView imageView;

    SpeedViewHolder(View view, ComplexAdapter.Callback2Menu callback) {
        super(view);
        title = view.findViewById(R.id.tv_label);
        icon = view.findViewById(R.id.icon);
        imageView = view.findViewById(R.id.icon01);

        mCallback = callback;

        view.findViewById(R.id.speedTestImage).setOnClickListener(this);
        this.imageView.setOnClickListener(this);
        this.title.setOnClickListener(this);
        this.icon.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void bind(ViewModel0 obj, Context context) {
        this.title.setText(obj.label);
        //this.content.setText(obj.content);
        if (null != obj.icon) {
            try {
                imageView.setImageResource(obj.icon);
            } catch (Exception r) {
                DLog.handleException(r);
            }
        }
    }

    @Override
    public void onClick(View v) {
        mCallback.menuItemSelected(getAdapterPosition());
    }
}