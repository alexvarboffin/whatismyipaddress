package com.walhalla.whatismyipaddress.ui.activities.splash;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WAKE_LOCK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


import com.walhalla.compat.ComV19;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.helper.EasyPermissions;
import com.walhalla.whatismyipaddress.onboarding.OnboardingManager;
import com.walhalla.whatismyipaddress.ui.activities.Main.BottomHolder;
import com.walhalla.whatismyipaddress.onboarding.OnboardingActivity;

import es.dmoral.toasty.Toasty;

public class Splash extends AppCompatActivity {

//    private static final long SPLASH_TIME_OUT = 1200;
//    private EasyPermissions permissionResolver;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_splash);
//        permissionResolver = new EasyPermissions(this);
//
//        try {
//            YoYo.with(Techniques.RotateInDownLeft)
//                    .duration(1500)
//                    .repeat(0)
//                    .playOn(findViewById(R.id.logo));
//
//            YoYo.with(Techniques.RotateInDownRight)
//                    .duration(1500)
//                    .repeat(0)
//                    .playOn(findViewById(R.id.logo1));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        if (!permissionResolver.hasPermissions()) {
//            permissionResolver.resolveAll();
//        } else {
//            new Handler().postDelayed(() -> startActivity(
//                    new Intent(Splash.this, BottomHolder.class)), SPLASH_TIME_OUT);
//        }
//
////        if (!resolve) {
////
////        } else {
////            new Handler().postDelayed(() -> startActivity(
////                    new Intent(Splash.this, BottomHolder.class)), SPLASH_TIME_OUT);
////        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        permissionResolver.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//
//
//        DLog.d(Arrays.toString(permissions));
//        DLog.d(Arrays.toString(grantResults));
//
//        if (EasyPermissions.PERMISSION_ALL_REQUEST_CODE == requestCode) {
//
//            // If request is cancelled, the result arrays are empty.
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                Toasty.custom(Splash.this,
//                        getString(R.string.welcome),
//                        Com19.getDrawable(Splash.this, R.drawable.ic_home),
//                        ContextCompat.getColor(Splash.this, R.color.colorPrimaryDark),
//                        ContextCompat.getColor(Splash.this, R.color.white),
//                        Toasty.LENGTH_SHORT, true, true).show();
//
//                new Handler().postDelayed(() -> {
//                    // close this activity
//                    launchApp();;
//                }, SPLASH_TIME_OUT);
//
//            } else {
//                Toasty.custom(Splash.this,
//                        getString(R.string.permission_not_granted), Com19.getDrawable(Splash.this, R.drawable.ic_cancel),
//                        ContextCompat.getColor(Splash.this, R.color.error), ContextCompat.getColor(Splash.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
//            }
//            return;
//        }
//    }

    private static final long SPLASH_TIME_OUT = 600;
    private static final String KEY_PERMISSION_DIALOG = "key.permission.dialog";

    private final int PERMISSION_ALL = 154;

    String[] PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE,
            READ_PHONE_STATE, //
            ACCESS_COARSE_LOCATION,
            WAKE_LOCK
    };

    private ComV19 comv19;
    private OnboardingManager manager;


//    private void checkAndOpenGooglePlay() {
//        try {
//            // Проверяем, установлен ли Google Play
//            getPackageManager().getPackageInfo(Module_U.PKG_NAME_VENDING, 0);
//
//            // Если Google Play установлен, открываем приложение
//            try {
//                Uri uri = Uri.parse(UConst.MARKET_CONSTANT + getPackageName());
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().startActivity(intent);
//            } catch (android.content.ActivityNotFoundException anfe) {
//                DLog.d("@@@@@@@@@@@@@@@@@@@@@@@mmmm");
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            DLog.d("@@@@@@@@@@@@@@@@@@@@@@@kkkk");
//            //startActivity(new Intent(this, SubdomainActivity.class));
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        manager = new OnboardingManager(this);

//        if (BuildConfig.ENABLELOADER) {
//            checkAndOpenGooglePlay();
//            return;
//        }


        try {
            YoYo.with(Techniques.RotateInDownLeft)
                    .duration(1500)
                    .repeat(0)
                    .playOn(findViewById(R.id.logo));

            YoYo.with(Techniques.RotateInDownRight)
                    .duration(1500)
                    .repeat(0)
                    .playOn(findViewById(R.id.logo1));
        } catch (Exception ex) {
            DLog.handleException(ex);
        }

        if (!hasPermissions(this, PERMISSIONS)) {
            SharedPreferences aa = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isSet = aa.getBoolean(KEY_PERMISSION_DIALOG, false);
            if (!isSet) {
                EasyPermissions.showHomePermissionDialog(this, (dialogInterface, i) ->
                {
                    aa.edit().putBoolean(KEY_PERMISSION_DIALOG, true).apply();
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                });
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        } else {
            new Handler().postDelayed(this::launchApp, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                //DLog.d(Arrays.toString(grantResults));
                //DLog.d(Arrays.toString(permissions));

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toasty.custom(Splash.this,
                            getString(R.string.welcome),
                            comv19.getDrawable(Splash.this, R.drawable.ic_home),
                            ContextCompat.getColor(Splash.this, R.color.colorPrimaryDark),
                            ContextCompat.getColor(Splash.this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true).show();

                    // close this activity
                    new Handler().postDelayed(this::launchApp, SPLASH_TIME_OUT);

                } else {
                    Toasty.custom(Splash.this,
                            getString(R.string.permission_not_granted), comv19.getDrawable(Splash.this, R.drawable.ic_cancel),
                            ContextCompat.getColor(Splash.this, R.color.error), ContextCompat.getColor(Splash.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                }
                return;
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void launchApp() {
        if (manager.isOnboarding()) {
            Intent intent = new Intent(this, BottomHolder.class).setFlags(335544320);
            startActivity(intent);
        } else {
            Intent openMainActivity = new Intent(this, OnboardingActivity.class).setFlags(335544320);
            startActivity(openMainActivity);
        }
        Splash.this.finish();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
