package com.walhalla.whatismyipaddress.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.adapter.entity.ViewModel0;

public class MenuItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ComplexAdapter.Callback2Menu mCallback;
    private final TextView title;
    private final ImageView imageView;
    private final ImageButton icon;

    MenuItemViewHolder(View itemView, ComplexAdapter.Callback2Menu callback) {
        super(itemView);
        this.mCallback = callback;
        this.title = itemView.findViewById(R.id.tv_label);
        this.icon = itemView.findViewById(R.id.ib);
        this.imageView = itemView.findViewById(R.id.icon01);

  //@@      this.title.setOnClickListener(this);
  //@@       this.icon.setOnClickListener(this);
        this.itemView.setOnClickListener(this);
    }

    public void bind(ViewModel0 obj, Context context) {
//        this.icon.setFocusable(true);
//        this.icon.setClickable(true);
//        this.icon.setEnabled(true);

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
