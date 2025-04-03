
package com.walhalla.whatismyipaddress.whois.verisign;


import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Link {

    @SerializedName("value")
    @Expose
    public String value;
    @SerializedName("rel")
    @Expose
    public String rel;
    @SerializedName("href")
    @Expose
    public String href;
    @SerializedName("type")//Not contains in ErrorResponse
    @Expose
    public String type;

}
