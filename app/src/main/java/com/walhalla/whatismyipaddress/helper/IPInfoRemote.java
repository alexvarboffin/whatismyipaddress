package com.walhalla.whatismyipaddress.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class IPInfoRemote {

    public String ip;
    public String hostname;
    public String city;
    public String region;
    public String regionName;

    public String continent;
    public String continentCode;
    public String country;

    public String loc;

    public String lat;
    public String lon;



    public String[] description;


    public String Netname;
    public String postal;
    public String timezone;


    public IPInfoRemote() {
    }

    public String getDescription() {
        StringBuilder descr = new StringBuilder();
        if (this.description != null && this.description.length > 0) {
            for (String aDescription : this.description) {
                if (descr.length() == 0) {
                    descr.append(aDescription);
                } else {
                    descr.append("\n").append(aDescription);
                }
            }
        }
        return descr.toString();
    }

    IPInfoRemote(Iterator<String> values) {
        setArray(values);
    }

    public List<String> getQArray() {
        List<String> list = new ArrayList<>();
        list.add(this.ip);
        list.add(this.hostname);
        list.add(this.country);
        list.add(this.Netname);
        list.add(this.postal);
        list.add(this.timezone);

        if (this.description == null || this.description.length <= 0) {
            list.add("0");
        } else {
            list.add("" + this.description.length);
            list.addAll(Arrays.asList(this.description));
        }
        return list;
    }


    public void setArray(Iterator<String> values) {
        this.ip = values.next();
        this.hostname = values.next();
        this.country = values.next();
        this.Netname = values.next();
        int count = Integer.parseInt(values.next());
        if (count > 0) {
            ArrayList<String> ls = new ArrayList<>();
            for (int idx = 0; idx < count; idx++) {
                ls.add(values.next());
            }
            this.description = ls.toArray(new String[0]);
        }
    }
}
