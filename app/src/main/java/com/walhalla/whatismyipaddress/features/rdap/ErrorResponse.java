package com.walhalla.whatismyipaddress.features.rdap;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walhalla.whatismyipaddress.whois.verisign.Link;

import java.util.List;
@Keep
public class ErrorResponse {
    @SerializedName("links")
    @Expose
    public List<Link> links;
    @SerializedName("rdapConformance")
    @Expose
    public List<String> rdapConformance;
    @SerializedName("notices")
    @Expose
    public List<Notice> notices;
    @SerializedName("port43")
    @Expose
    public String port43;
    @SerializedName("errorCode")
    @Expose
    public Integer errorCode;
    @SerializedName("title")
    @Expose
    public String title;
}
