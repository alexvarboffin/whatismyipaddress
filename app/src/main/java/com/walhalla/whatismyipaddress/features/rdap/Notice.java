package com.walhalla.whatismyipaddress.features.rdap;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walhalla.whatismyipaddress.whois.verisign.Link;

import java.util.List;

@Keep
public class Notice {

    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("description")
    @Expose
    public List<String> description;
    @SerializedName("links")
    @Expose
    public List<Link> links;

}