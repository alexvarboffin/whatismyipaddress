package com.walhalla.whatismyipaddress.whois.verisign;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DomainInfo {
    @SerializedName("objectClassName")
    @Expose
    public String objectClassName;

    @SerializedName("links")
    @Expose
    public List<Link> links;
//    @SerializedName("status")
//    @Expose
//    public List<String> status;


    @SerializedName("secureDNS")
    @Expose
    public SecureDNS secureDNS;
    @SerializedName("rdapConformance")
    @Expose
    public List<String> rdapConformance;

}
