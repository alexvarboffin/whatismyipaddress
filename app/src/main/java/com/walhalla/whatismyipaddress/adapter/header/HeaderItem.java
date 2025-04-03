package com.walhalla.whatismyipaddress.adapter.header;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

public class HeaderItem extends ViewModel {
    String title;

    public HeaderItem(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return ViewModel.TYPE_ITEM_HEADER;
    }
}
