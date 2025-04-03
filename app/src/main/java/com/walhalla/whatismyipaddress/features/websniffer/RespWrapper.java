package com.walhalla.whatismyipaddress.features.websniffer;

import java.util.Map;

public class RespWrapper {

    public final Map<String, String> map0;
    public final Map<String, String> respHeaders;
    public final String html;
    public final String ip;



    public final int port;
    public final String url;
    public final String networkResponse;
    public final String host;
    public final int responseCode;

    public RespWrapper(String ip, int port, String host, String url0, String networkResponse, Map<String, String> map0, Map<String, String> map1, String html, int code) {
        this.map0 = map0;
        this.respHeaders = map1;
        this.html = html;
        this.ip = ip;
        this.port = port;

        this.url = url0;
        this.host = host;

        this.networkResponse = networkResponse;
        this.responseCode=code;
    }

    @Override
    public String toString() {
        return "{" +
                "map0=" + map0 +
                ", map1=" + respHeaders +
                //", html='" + html + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", url='" + url + '\'' +
                ", networkResponse='" + networkResponse + '\'' +
                ", host='" + host + '\'' +
                ", responseCode=" + responseCode +
                '}';
    }
}