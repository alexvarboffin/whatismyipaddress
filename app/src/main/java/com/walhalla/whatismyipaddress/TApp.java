package com.walhalla.whatismyipaddress;

import android.text.TextUtils;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.walhalla.domain.repository.from_internet.AdvertAdmobRepository;
import com.walhalla.domain.repository.from_internet.AdvertConfig;
import com.walhalla.ui.DLog;
import com.walhalla.wads.AppOpenManager5;
import com.walhalla.whatismyipaddress.features.websniffer.WebFragment;

import java.util.ArrayList;
import java.util.List;

public class TApp extends MultiDexApplication {

    //Whatismyipaddress-e16fb
    //whatismyipaddress-e16fb

    private static final String OAI = "5152a412-6aa7-4dd8-86a3-f5f96394b264";

    public static AdvertAdmobRepository repository;
    private AppOpenManager5 appOpenManager;


    @Override
    public void onCreate() {
        super.onCreate();
        List<String> list = new ArrayList<>();
        list.add(AdRequest.DEVICE_ID_EMULATOR);
        list.add("A8A2F7804653E219880030864C1F32E4");
        list.add("5D5A89BC6372A49242D138B9AC352894");
        list.add("A2A86E2966898F258CB671EE038C2703");

//        Object repository = /**/0x0_0___01f * 0_1.0__0 * 5_0__5_1__________________4L/**/;
//        DLog.d("@@@" + repository);

        RequestConfiguration requestConfiguration
                = new RequestConfiguration.Builder()
                .setTestDeviceIds(list)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        MobileAds.initialize(this, initializationStatus -> {
            //getString(R.string.app_id)
        });
        AdvertConfig config = AdvertConfig.newBuilder()
                .setAppId(getString(R.string.app_id))
                .setBannerId(getString(R.string.b2))
                .build();
        repository = AdvertAdmobRepository.getInstance(config);


//        SharedPref mm = SharedPref.getInstance(this);
//        if ((!mm.appRated())) {
//            if (BuildConfig.DEBUG) {
//                Toast.makeText(this, "" + mm.appRated(), Toast.LENGTH_SHORT).show();
//            }
            appOpenManager = new AppOpenManager5(this, R.string.appOpen);
//        }

        if (!TextUtils.isEmpty(OAI)) {
            // OneSignal Initialization
//            OneSignal.startInit(this)
//                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                    //.setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
//                    .unsubscribeWhenNotificationsAreDisabled(true)
//                    .autoPromptLocation(true)
//                    .init();

            // OneSignal Initialization
            OneSignal.initWithContext(this, OAI);
            // Enable verbose OneSignal logging to debug issues if needed.
            OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
            //OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
//            DLog.d("OneSignal Initialization");
//            OneSignal.startInit(this)
//                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                    //.setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
//                    //--.unsubscribeWhenNotificationsAreDisabled(true)
//                    //.autoPromptLocation(true)
//                    .init();

//            OneSignal.unsubscribeWhenNotificationsAreDisabled(false);
//            OSDeviceState device = OneSignal.getDeviceState();
//            if (device != null) {
//                String email = device.getEmailAddress();
//                String emailId = device.getEmailUserId();
//                String pushToken = device.getPushToken();
//                String userId = device.getUserId();
//
//                boolean enabled = device.areNotificationsEnabled();
//                boolean subscribed = device.isSubscribed();
//                boolean pushDisabled = device.isPushDisabled();
//
//                DLog.d("[" + enabled + "] " + subscribed + " " + pushDisabled);
//                DLog.d(String.valueOf(device.toJSONObject()));

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getApplicationContext(),
//                            new String[]{POST_NOTIFICATIONS}, 1);
//                }
//            }

            String id = OneSignal.getUser().getOnesignalId();
            //DLog.d("@@@@@@@@@@" + id);
        }
    }
}
