package com.walhalla.whatismyipaddress.features.checkhost;

public class PingItem {
    public String pingResult;
    public Double pingTime = 0.0;
    public String pingIp;

    public PingItem(String pingResult, Double pingTime, String pingIp) {
        this.pingResult = pingResult;
        this.pingTime = pingTime;
        this.pingIp = pingIp;
    }

    public PingItem(String pingResult, Double pingTime) {
        this.pingResult = pingResult;
        this.pingTime = pingTime;
    }
}
