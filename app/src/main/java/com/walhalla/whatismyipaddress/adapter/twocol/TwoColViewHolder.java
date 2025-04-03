package com.walhalla.whatismyipaddress.adapter.twocol;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.whatismyipaddress.R;

public class TwoColViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView value;

    public TwoColViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.code);
        value = itemView.findViewById(R.id.value);
    }

    public void bind(TwoColItem dataModel) {
        name.setText(dataModel.title);
        value.setText(dataModel.value);
        if (dataModel.color > 0) {
            name.setTextColor(name.getContext().getResources().getColor(dataModel.color));
        }

    }
}