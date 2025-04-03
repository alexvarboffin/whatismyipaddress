package com.walhalla.whatismyipaddress;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.walhalla.compat.ComV19;
import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.helper.DataHandler;
import com.walhalla.whatismyipaddress.helper.IPInfoLocal;

import java.io.IOException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;

public class AssetUtils {


    public static String loadFromAsset(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            DLog.handleException(e);
            return "";
        }
    }

    public static void shareString(String value, Context context) {
        Log.d("shareString", value);
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.TEXT", value);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public static void copyToClipboard(String value, Activity activity) {
        ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("IP Tools", value);
            clipboard.setPrimaryClip(clip);

            ComV19 comv19 = new ComV19();
            String tmp = String.format(activity.getString(R.string.data_to_clipboard), value);
            Toasty.custom(activity, tmp,
                    comv19.getDrawable(activity, R.drawable.ic_info),
                    ContextCompat.getColor(activity, R.color.colorPrimaryDark),
                    ContextCompat.getColor(activity, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
    }

    public static String getDataText(DataHandler helper, IPInfoLocal dataLocal, Context context) {
        StringBuilder sb = new StringBuilder();
        if (helper.dataRemote != null) {
            addIfNotEmpty(sb, context.getString(R.string.label_public_ip), helper.dataRemote.ip);
            addIfNotEmpty(sb, context.getString(R.string.label_hostname), helper.dataRemote.hostname);
            addIfNotEmpty(sb, context.getString(R.string.label_country), helper.dataRemote.country);

            addIfNotEmpty(sb, context.getString(R.string.label_city), helper.dataRemote.city);
            addIfNotEmpty(sb, context.getString(R.string.label_region), helper.dataRemote.region);

            addIfNotEmpty(sb, context.getString(R.string.label_loc), helper.dataRemote.loc);
            addIfNotEmpty(sb, context.getString(R.string.label_org), helper.dataRemote.Netname);

            addIfNotEmpty(sb, context.getString(R.string.label_postal), helper.dataRemote.postal);
            addIfNotEmpty(sb, context.getString(R.string.label_timezone), helper.dataRemote.timezone);


            addIfNotEmpty(sb, context.getString(R.string.label_description), helper.dataRemote.getDescription());
        }
        if (dataLocal != null) {

            addIfNotEmpty(sb, context.getString(R.string.label_local_ip), dataLocal.localIp);
            addIfNotEmpty(sb, context.getString(R.string.label_local_ipv6), dataLocal.localIPv6);
            addIfNotEmpty(sb, context.getString(R.string.label_default_gateway), dataLocal.gateway);
            addIfNotEmpty(sb, context.getString(R.string.label_mask), dataLocal.mask);
            addIfNotEmpty(sb, context.getString(R.string.label_dns1), dataLocal.dns1);
            addIfNotEmpty(sb, context.getString(R.string.label_dns2), dataLocal.dns2);
        }
        if (helper.dataConnection != null) {
            addIfNotEmpty(sb, context.getString(R.string.label_connection_type), helper.dataConnection.connection_type);
            addIfNotEmpty(sb, context.getString(R.string.label_connection_subtype), helper.dataConnection.connection_subtype);
            addIfNotEmpty(sb, context.getString(R.string.label_operator), helper.dataConnection.operator);
            addIfNotEmpty(sb, context.getString(R.string.label_ssid), helper.dataConnection.SSID);
        }
        return sb.toString();
    }

    private static void addIfNotEmpty(StringBuilder sb, String label, String value) {
        if (value != null && value.length() > 0) {
            sb.append(label);
            sb.append(" ")
                    .append(value)
                    .append((char) 10);
        }
    }

    public static String intToIp(int i) {
        return (i & 255) + "." + ((i >> 8) & 255)
                + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static void location(String content, Activity activity) {
        try {
            // Creates an Intent that will load a map of San Francisco
            Uri uri = Uri.parse("geo:" + content);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
