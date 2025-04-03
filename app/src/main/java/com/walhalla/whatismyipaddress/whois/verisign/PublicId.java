
package com.walhalla.whatismyipaddress.whois.verisign;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//("jsonschema2pojo")
public class PublicId {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("identifier")
    @Expose
    public String identifier;

}
