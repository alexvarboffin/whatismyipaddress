
package com.walhalla.whatismyipaddress.whois.verisign;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//("jsonschema2pojo")
public class Entity {

    @SerializedName("objectClassName")
    @Expose
    public String objectClassName;
    @SerializedName("handle")
    @Expose
    public String handle;
    @SerializedName("roles")
    @Expose
    public List<String> roles;

    @SerializedName("publicIds")
    //@Expose
    public List<PublicId> publicIds;

//    @SerializedName("vcardArray")
//    @Expose
//    public List<String> vcardArray; //STRING_OR_ARRAY

    @SerializedName("entities")
    @Expose
    public List<Entity__1> entities;

}
