package com.walhalla.whatismyipaddress.adapter.singlcol;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.whatismyipaddress.R;

public class SingleItemViewHolder extends RecyclerView.ViewHolder {
    public TextView name;

    public SingleItemViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.code);
    }

    public void bind(SingleItem dataModel) {
        name.setText(dataModel.title);
        if (dataModel.color > 0) {
            name.setTextColor(name.getContext().getResources().getColor(dataModel.color));
        }
    }
}