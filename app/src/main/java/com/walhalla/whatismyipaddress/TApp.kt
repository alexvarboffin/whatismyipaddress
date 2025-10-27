package com.walhalla.whatismyipaddress

import android.content.Context
import android.os.Build
import android.os.StrictMode

import android.text.TextUtils
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.druk.servicebrowser.FavouritesManager
import com.druk.servicebrowser.RegTypeManager
import com.druk.servicebrowser.RegistrationManager
import com.github.druk.rx2dnssd.Rx2Dnssd
import com.github.druk.rx2dnssd.Rx2DnssdBindable
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.onesignal.OneSignal
import com.onesignal.OneSignal.initWithContext

import com.onesignal.debug.LogLevel

import com.walhalla.domain.repository.from_internet.AdvertAdmobRepository
import com.walhalla.domain.repository.from_internet.AdvertAdmobRepository.Companion.getInstance
import com.walhalla.domain.repository.from_internet.AdvertConfig.Companion.newBuilder
import com.walhalla.wads.AppOpenManager5

class TApp : MultiDexApplication() {
    private var appOpenManager: AppOpenManager5? = null


    override fun onCreate() {
        super.onCreate()
        val list: MutableList<String?> = ArrayList<String?>()
        list.add(AdRequest.DEVICE_ID_EMULATOR)
        list.add("A8A2F7804653E219880030864C1F32E4")
        list.add("5D5A89BC6372A49242D138B9AC352894")
        list.add("A2A86E2966898F258CB671EE038C2703")

        //        Object repository = /**/0x0_0___01f * 0_1.0__0 * 5_0__5_1__________________4L/**/;
//        DLog.d("@@@" + repository);
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(list)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(
            this,
            OnInitializationCompleteListener { initializationStatus: InitializationStatus? -> })
        val config = newBuilder()
            .setAppId(getString(R.string.app_id))
            .setBannerId(getString(R.string.b2))
            .build()
        repository = getInstance(config)


        //        SharedPref mm = SharedPref.getInstance(this);
//        if ((!mm.appRated())) {
//            if (BuildConfig.DEBUG) {
//                Toast.makeText(this, "" + mm.appRated(), Toast.LENGTH_SHORT).show();
//            }
        appOpenManager = AppOpenManager5(this, R.string.appOpen)

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

            initWithContext(this, OAI)
            // Enable verbose OneSignal logging to debug issues if needed.
            OneSignal.Debug.logLevel = LogLevel.VERBOSE

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
            val id: String = OneSignal.User.onesignalId
            //DLog.d("@@@@@@@@@@" + id);





            //scanner
            if (BuildConfig.DEBUG) {
                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork() // or .detectAll() for all detectable problems
                        .penaltyLog()
                        .build()
                )
                StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .build()
                )
            }
            mRxDnssd = createDnssd()
            mRegistrationManager = RegistrationManager()
            mRegTypeManager = RegTypeManager(this)
            mFavouritesManager = FavouritesManager(this)
        }



    }





    private var mRxDnssd: Rx2Dnssd? = null
    private lateinit var mRegistrationManager: RegistrationManager
    private var mRegTypeManager: RegTypeManager? = null
    private var mFavouritesManager: FavouritesManager? = null



    private fun createDnssd(): Rx2Dnssd {
        // https://developer.android.com/about/versions/12/behavior-changes-12#mdnsresponder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.i(TAG, "Using embedded version of dns sd")
            return Rx2DnssdEmbedded(this)
        } else {
            Log.i(TAG, "Using bindable version of dns sd")
            return Rx2DnssdBindable(this)
        }
    }

    companion object {

        //Whatismyipaddress-e16fb
        //whatismyipaddress-e16fb
        private const val OAI = "5152a412-6aa7-4dd8-86a3-f5f96394b264"


        lateinit var repository: AdvertAdmobRepository

        private const val TAG = "TApp"
        fun getApplication(context: Context): TApp? {
            return (context.applicationContext as TApp?)
        }

        @JvmStatic
        fun getRxDnssd(context: Context): Rx2Dnssd? {
            return (context.applicationContext as TApp).mRxDnssd
        }

        fun getRegistrationManager(context: Context): RegistrationManager {
            return (context.applicationContext as TApp).mRegistrationManager
        }

        @JvmStatic
        fun getRegTypeManager(context: Context): RegTypeManager? {
            return (context.applicationContext as TApp).mRegTypeManager
        }

        fun getFavouritesManager(context: Context): FavouritesManager? {
            return (context.applicationContext as TApp).mFavouritesManager
        }
    }
}
