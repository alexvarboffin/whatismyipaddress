package com.walhalla.whatismyipaddress.adapter.lnk;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

public class LnkItem extends ViewModel {
    public final String title;
    public final String href;

    public LnkItem(String title, String value) {
        this.title = title;
        this.href = value;
    }

    @Override
    public int getType() {
        return ViewModel.TYPE_ITEM_LNK0;
    }
}
