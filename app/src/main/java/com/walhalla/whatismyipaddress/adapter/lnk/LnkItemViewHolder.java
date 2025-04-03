package com.walhalla.whatismyipaddress.adapter.lnk;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.whatismyipaddress.R;

public class LnkItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView lnk;

    public LnkItemViewHolder(@NonNull View itemView) {
        super(itemView);
        lnk = itemView.findViewById(R.id.lnk);
    }


    public void bind(LnkItem dataModel) {
        lnk.setText(dataModel.title);
    }
}