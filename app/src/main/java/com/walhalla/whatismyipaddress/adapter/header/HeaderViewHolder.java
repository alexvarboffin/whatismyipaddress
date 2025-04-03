package com.walhalla.whatismyipaddress.adapter.header;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.whatismyipaddress.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder { //Ping
    TextView name;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.code);
    }

    public void bind(HeaderItem dataModel) {
        name.setText(dataModel.title);
    }
}