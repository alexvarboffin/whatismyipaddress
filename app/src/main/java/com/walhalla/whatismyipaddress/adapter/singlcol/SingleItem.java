package com.walhalla.whatismyipaddress.adapter.singlcol;

import androidx.annotation.ColorRes;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

public class SingleItem extends ViewModel {

    public String title;
    public int color;

    public SingleItem(String title, @ColorRes int color) {
        this.title = title;
        this.color = color;
    }

    public SingleItem(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return ViewModel.TYPE_ITEM_SINGLE;
    }
}
