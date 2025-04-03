package com.walhalla.whatismyipaddress.adapter.twocol;

import androidx.annotation.ColorRes;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

public class TwoColItem extends ViewModel {


    //@ColorInt
    //@ColorRes
    
    public String title;
    public String value;
    public @ColorRes int color;

    public TwoColItem(String title, String value, @ColorRes int color) {
        this.title = title;
        this.value = value;
        this.color = color;
    }

    public TwoColItem(String title, String value) {
        this.title = title;
        this.value = value;
    }

    @Override
    public int getType() {
        return ViewModel.TYPE_ITEM_2;
    }
}
