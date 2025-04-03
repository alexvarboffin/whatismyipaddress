package com.walhalla.whatismyipaddress.adapter.header;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

public class Header2Item extends ViewModel {

    @DrawableRes public final int icon;
    @StringRes int title;

    public Header2Item(@StringRes int title, @DrawableRes int icCert) {
        this.title = title;
        this.icon = icCert;

    }


    @Override
    public int getType() {
        return ViewModel.TYPE_ITEM_HEADER2;
    }
}
