package com.walhalla.whatismyipaddress.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityWrapper {


    public String connection_subtype;
    public String connection_type;
    public String operator;


    //NetworkInfo.java
    public String state;
    public String reason;
    public String extra;
    public boolean failover;
    public boolean available;
    public boolean roaming;


    //wifi
    public String SSID;
    public String BSSID;
    public int mRssi;
    public int mLinkSpeed;
    public int mTxLinkSpeed;
    public int mRxLinkSpeed;
    public int mFrequency;
    public int mNetworkId;

    public String mMacAddress = "";

    EntityWrapper(Iterator<String> values) {
        setArray(values);
    }

    public EntityWrapper() {

    }

    public List<String> getArray() {
        List<String> ret = new ArrayList<>();
        ret.add(this.connection_type);
        ret.add(this.connection_subtype);
        ret.add(this.SSID);
        ret.add(this.operator);
        return ret;
    }

    public void setArray(Iterator<String> values) {
        this.connection_type = values.next();
        this.connection_subtype = values.next();
        this.SSID = values.next();
        this.operator = values.next();
    }
}
