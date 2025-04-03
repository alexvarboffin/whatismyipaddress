package com.walhalla.whatismyipaddress.adapter.items;

import androidx.annotation.Keep;

import com.walhalla.whatismyipaddress.R;

import java.io.Serializable;

//@Keep
public abstract class ViewModel implements Serializable {

    public static final int TYPE_ITEM_SINGLE = 1;
    public static final int TYPE_ITEM_2 = 2;
    public static final int TYPE_ITEM_HEADER = 3;
    public static final int TYPE_ITEM_LNK0 = 4;
    public static final int TYPE_ITEM_HEADER2 = 5;
    public static final int TYPE_ITEM_CHECKHOST = 6;
    public static final int TYPE_ITEM_CERT = 7;
    public abstract int getType();
}
