package com.walhalla.whatismyipaddress.adapter.chekhostitem;

import androidx.annotation.ColorRes;

import com.walhalla.whatismyipaddress.adapter.items.ViewModel;
import com.walhalla.whatismyipaddress.features.checkhost.PingItem;

import java.util.List;

public class CheckHostItem extends ViewModel {


    //@ColorInt
    //@ColorRes

    public String countryCode;
    public String serverCountry;
    public @ColorRes int color;
    public String serverCity;
    public String serverIP;
    public String serverAS;

    public String getServerKey() {
        return serverKey;
    }

    public String serverKey;
    public List<PingItem> items;

    public CheckHostItem(String serverKey, @ColorRes int color) {
        this.serverKey = serverKey;
        this.color = color;
    }

    public CheckHostItem(String serverName, String value) {
        this.serverKey = serverName;
        this.serverCountry = value;
    }

    @Override
    public int getType() {
        return ViewModel.TYPE_ITEM_CHECKHOST;
    }

    public boolean isFirstLaunch() {
        return true;
    }


}
