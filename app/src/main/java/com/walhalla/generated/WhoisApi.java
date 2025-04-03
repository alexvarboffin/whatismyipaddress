package com.walhalla.generated;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.Const;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WhoisApi {

    //My key
    public static final String WHOISXMLAPI_KEY_ = "at_QqSJgQVuY0F8SEzN8svOnrOi2Ap2N";
    //public static final String WHOISXMLAPI_KEY_ = "at_IIcaZ7GoM0Qbj1zoG8mun4Cs8IcxX"; //mayson

    public static String ipNetBlock(String parameter) {
        String url = "https://ip-netblocks.whoisxmlapi.com/api/v2?apiKey=" +
                WHOISXMLAPI_KEY_
                + parameter + "&outputFormat=JSON";
        return url;
    }

    public static String ipDNSLookup(String ip) throws UnsupportedEncodingException {
        String url = "https://www.whoisxmlapi.com/whoisserver/DNSService?apiKey=" +
                WHOISXMLAPI_KEY_ +
                "&domainName=" + URLEncoder.encode(ip, "UTF-8") + "&outputFormat=JSON&type=_all";
        return url;
    }


    static class Wrp {
        String login;
        String password;

        public Wrp(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }

    //https://www.whoisxmlapi.com/whoisserver/WhoisService?apiKey=at_IIcaZ7GoM0Qbj1zoG8mun4Cs8IcxX&domainName=google.com&outputFormat=JSON
//    public static String whois0(String ip) throws UnsupportedEncodingException {
////        String url = "https://www.whoisxmlapi.com/whoisserver/WhoisService?apiKey=" +
////                WHOISXMLAPI_KEY_ +
////                "&domainName=" + URLEncoder.encode(ip, "UTF-8") + "&outputFormat=JSON";
//
//       //В Тренде ТипТоп
//        //  p-> at_ramTIsbkbQj3ojWZ3VSv7Bi5xoGv4
//        Wrp aa = new Wrp("danilabobrov82@gmail.com", "danilabobrov82@gmail.com");
//         Wrp bb = new Wrp("mayson188@gmail.com", "mayson188@gmail.com");
//
//        String url = "https://www.whoisxmlapi.com/whoisserver/WhoisService?domainName="
//                + URLEncoder.encode(ip, "UTF-8")
//                + "&username=" +
//                aa.login +
//                "&password=" +
//                aa.password +
//                "&outputFormat=JSON";
//
//        DLog.d("@@" + url);
//        return url;
//    }
}
