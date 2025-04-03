package com.walhalla.whatismyipaddress.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IPInfoLocal {
    public String dns1;
    public String dns2;
    public String gateway;
    public String localIp;
    public String localIPv6;
    public String mask;

    public IPInfoLocal(Iterator<String> values) {
        setArray(values);
    }

    public IPInfoLocal() {

    }

    public List<String> getArray() {
        List<String> ret = new ArrayList<>();
        ret.add(this.localIp);
        ret.add(this.gateway);
        ret.add(this.mask);
        ret.add(this.dns1);
        ret.add(this.dns2);
        ret.add(this.localIPv6);
        return ret;
    }

    public void setArray(Iterator<String> values) {
        this.localIp = values.next();
        this.gateway = values.next();
        this.mask = values.next();
        this.dns1 = values.next();
        this.dns2 = values.next();
        this.localIPv6 = values.next();
    }
}
