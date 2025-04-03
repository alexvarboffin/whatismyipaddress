package com.walhalla.whatismyipaddress.helper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WAKE_LOCK;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.BuildConfig;
import com.walhalla.whatismyipaddress.R;

import java.util.LinkedList;
import java.util.List;

public class EasyPermissions {

    public static int PERMISSION_ALL_REQUEST_CODE = 1434;

    private static final String[] DEFAULT_PERMISSIONS = {
            INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE,
            android.Manifest.permission.READ_PHONE_STATE, //
            ACCESS_COARSE_LOCATION,//
            WAKE_LOCK
    };

    private final Activity activity;

    public EasyPermissions(Activity a) {
        this.activity = a;
    }

    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity) {
        if (requestCode != PERMISSION_ALL_REQUEST_CODE) return;
        boolean granted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                DLog.d("Not Granted: => " + permissions[i] + ": " + grantResults[i] + " "+i+" " + permissions.length);
                granted = false;
                break;
            }
        }
        if (granted) return;


        //Not Work in API level 30
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
            //Show permission explanation dialog...
            showPermissionDialog(activity);
        } else {
            //Never ask again selected, or device policy prohibits the app from having that permission.
            //So, disable that feature, or fall back to another situation...
        }

    }
    public static void showHomePermissionDialog(Activity activity, DialogInterface.OnClickListener aa) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.alert_home_title)
                .setMessage(R.string.alert_home_body)
                .setPositiveButton(R.string.alert_home_btn, aa)
                .create()
                .show();
    }

    public static void showPermissionDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_perm_body)
                .setTitle(R.string.alert_perm_title)
                .setPositiveButton(R.string.alert_perm_btn, (dialogInterface, i) -> activity.startActivity(new Intent()
                        .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                ))
                .create()
                .show();
    }

    public boolean hasPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity!= null && DEFAULT_PERMISSIONS != null) {
            for (String permission : DEFAULT_PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private String[] getUnmetPermissions() {
        List<String> unmet_permissions = new LinkedList<String>();
        try {
//            List<String> def = Arrays.asList(DEFAULT_PERMISSIONS);
//            PackageInfo info = activity.getPackageManager()
//                    .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = DEFAULT_PERMISSIONS; //info.requestedPermissions;

            for (String perm : permissions) {
                //DLog.d("[+] Permissions: " + perm);
                //if (def.contains(perm)) continue;
                if (
                    //activity.checkSelfPermission(perm)
                    //ContextCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED
                        ActivityCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED
                ) {
                    continue;
                }
                unmet_permissions.add(perm);
            }
        } catch (Exception e /*PackageManager.NameNotFoundException e*/) {
            DLog.handleException(e);
            return new String[0];
        }

        if (unmet_permissions.size() < 1) return new String[0];

        String[] arr = new String[unmet_permissions.size()];
        unmet_permissions.toArray(arr);
        return arr;
    }

    public boolean resolveAll() {
        if (Build.VERSION.SDK_INT < 23) return true;
        //activity.requestPermissions(DEFAULT_PERMISSIONS, PERMISSION_ALL_REQUEST_CODE);
            ActivityCompat.requestPermissions(activity, DEFAULT_PERMISSIONS, PERMISSION_ALL_REQUEST_CODE);
        return false;
    }
//    public boolean resolve() {
//        if (Build.VERSION.SDK_INT < 23) return true;
//
//        String[] unmet_permissions = getUnmetPermissions();
//        if (unmet_permissions.length < 1) return true;
//
//        activity.requestPermissions(unmet_permissions, PERMISSION_ALL_REQUEST_CODE);
//        //    ActivityCompat.requestPermissions(activity, unmet_permissions, REQUEST_CODE);
//        return false;
//    }

}
