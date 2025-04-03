package com.walhalla.whatismyipaddress;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class TPreferences {

    private final SharedPreferences preferences;

    private static TPreferences instance;

    private TPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public synchronized static TPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new TPreferences(context);
        }

        return instance;
    }

    public void setIPNetTaskKEY_MASK(String IPNetTaskKEY_MASK) {
        preferences.edit().putString(CheckKey.CH_IPNETTASKKEY_MASK, IPNetTaskKEY_MASK).apply();
    }

    public void setIPNetTaskKEY_IP(String IPNetTaskKEY_IP) {
        preferences.edit().putString(CheckKey.CH_IPNETTASKKEY_IP, IPNetTaskKEY_IP).apply();
    }

    public String getIPNetTaskKEY_IP() {
        return preferences.getString(CheckKey.CH_IPNETTASKKEY_IP, "8.8.8.8");
    }

    public String getIPNetTaskKEY_MASK() {
        return preferences.getString(CheckKey.CH_IPNETTASKKEY_MASK, "");
    }

    //
    public void setSubdomainDomain(String SubdomainDomain) {
        preferences.edit().putString(CheckKey.CH_SUBDOMAINDOMAIN, SubdomainDomain).apply();
    }

    public String getSubdomainDomain() {
        return preferences.getString(CheckKey.CH_SUBDOMAINDOMAIN, "google.com");
    }


    private static class CheckKey {
        public static final String CH_IPNETTASKKEY_MASK = "0UO+PnSxWMhtdC3qNBCW6w==";
        public static final String CH_IPNETTASKKEY_IP = "+xKUiSu6vXi4QCxCv6pUeA==";
        public static final String CH_SUBDOMAINDOMAIN = "s+di2X4Kt4YnzHGsq0iJKw==";
    }
}
