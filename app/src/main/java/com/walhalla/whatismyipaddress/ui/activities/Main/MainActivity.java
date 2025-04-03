//package com.walhalla.whatismyipaddress.ui.activities.Main;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.databinding.DataBindingUtil;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import com.google.android.gms.ads.AdRequest;
//import com.walhalla.ui.DLog;
//import com.walhalla.ui.Module_U;
//import com.walhalla.ui.observer.RateAppModule;
//import com.walhalla.compat.ComV19;
//
//import com.walhalla.whatismyipaddress.BuildConfig;
//import com.walhalla.whatismyipaddress.R;
//import com.walhalla.whatismyipaddress.ui.fragment.Fragment1;
//
//import com.walhalla.whatismyipaddress.databinding.Main0Binding;
//
//import es.dmoral.toasty.Toasty;
//
//
//public class SubdomainActivity extends AppCompatActivity
//        implements MainActivityPresenter.View,
//        Fragment1.Fragment1Callback {
//
//    private RateAppModule mRateAppModule;
//    private Main0Binding mBinding;
//    private MainActivityPresenter presenter;
//    private ComV19 comv19;
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        menu.add("Crash").setOnMenuItemClickListener(
////                new MenuItem.OnMenuItemClickListener() {
////                    @Override
////                    public boolean onMenuItemClick(MenuItem item) {
////                        throw new RuntimeException("Test Crash"); // Force a crash
////                    }
////                }
////        );
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.action_refresh:
//                //requestData();
//                return false;
//
//            case R.id.action_share:
//                //Tools.shareString(Tools.getDataText(this.mDatabaseManager, dataLocal, this), this);
//                return false;
//
//            case R.id.action_copy:
//                //Tools.copyToClipboard(Tools.getDataText(this.mDatabaseManager, dataLocal, this), this);
//                return false;
//
//
//            case R.id.action_about:
//                Module_U.aboutDialog(this);
//                return true;
//
//            case R.id.action_privacy_policy:
//                Module_U.openBrowser(this, getString(R.string.url_privacy_policy));
//                return true;
//
//            case R.id.action_rate_app:
//                Module_U.rateUs(this);
//                return true;
//
//            case R.id.action_share_app:
//                Module_U.shareThisApp(this);
//                return true;
//
//            case R.id.action_discover_more_app:
//                Module_U.moreApp(this);
//                return true;
//
////            case R.id.action_exit:
////                mNotificationManager.cancel(NOTIFICATION_ID);
////                this.finish();
////                return true;
//
//            case R.id.action_feedback:
//                Module_U.feedback(this);
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme_NoActionBar);
//        super.onCreate(savedInstanceState);
//        comv19 = new ComV19();
//        mBinding = DataBindingUtil.setContentView(this, R.layout.main0);
//        setSupportActionBar(mBinding.toolbar);
//        presenter = new MainActivityPresenter(this, this);
//
//        //Module_U.checkUpdate(this);
//        mRateAppModule = new RateAppModule(this);
//



////        if (BuildConfig.DEBUG) {
////            mBinding.adView.setVisibility(View.GONE);
////        } else {
//
//        AdRequest build = new AdRequest.Builder().build();
//        mBinding.adView.loadAd(build);
//
////        }
//
////this.adView.setAdListener(adListener);
////        this.adView.loadAd(new AdRequest.Builder()
////                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
////                //      .addTestDevice("28964E2506C9A8C6400A9E8FF42D3486")
////                .build());
//
//
//        if (savedInstanceState == null) {
//            Fragment fragment = TabHolder2Fragment.newInstance("", "");
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    //.addToBackStack(null)
//                    .add(R.id.container, fragment, fragment.getClass().getSimpleName())
//                    .commit();
//        }
//
//        //if (BuildConfig.DEBUG) {}
//        //mBinding.toolbar.setOnClickListener(v -> mRateAppModule.launchNow());
//    }
//
//
//
////    @Override
////    protected void onPause() {
////        super.onPause();
//////        if (mPublisherAdView != null) {
//////            mPublisherAdView.pause();
//////        }
////        if (mAdView != null) {
////            mAdView.pause();
////        }
////    }
//    @Override
//    public void onPause() {
//        if (mBinding.adView != null) {
//            mBinding.adView.pause();
//        }
//        super.onPause();
//    }
//
////    @Override
////    public void onDestroy() {
////        if (mBinding.adView != null) {
////            mBinding.adView.destroy();
////        }
////        super.onDestroy();
////    }
//
//    //    @Override
////    protected void onDestroy() {
//////        if (mPublisherAdView != null) {
//////            mPublisherAdView.destroy();
//////        }
////        if (mAdView != null) {
////            mAdView.destroy();
////        }
////        super.onDestroy();
////    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mBinding.adView != null) {
//            mBinding.adView.resume();
//        }
//
////        if (mAdView == null) {
////            //
////            mAdView = createBanner(this, "ca-app-pub-5111357348858303/9993967729");
////
////            AdRequest.Builder builder = new AdRequest.Builder();
//////            PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();
////
////            if (DEBUG) {
////                builder.addTestDevice("330FD453402153D99F79DD7C25317FEC");
////                builder.addTestDevice("D97101CD912C42082D0C57DF7EC70E26");
////                builder.addTestDevice("420B3C23A53A68C1F994B6E2043964AA");
////                builder.addTestDevice("1FFCD512BD8AE45F7647127CB80345E8");
////            }
////            mAdView.setAdListener(new AdListener() {
////                @Override
////                public void onAdLoaded() {
////                    super.onAdLoaded();
////                    mAdView.setVisibility(View.VISIBLE);
////                }
////
////                @Override
////                public void onAdFailedToLoad(int errorCode) {
////                    // Code to be executed when an ad request fails.
////                }
////
////                @Override
////                public void onAdOpened() {
////                    // Code to be executed when an ad opens an overlay that
////                    // covers the screen.
////                }
////
////                @Override
////                public void onAdLeftApplication() {
////                    // Code to be executed when the user has left the app.
////                }
////
////                @Override
////                public void onAdClosed() {
////                    // Code to be executed when when the user is about to return
////                    // to the app after tapping on an ad.
////                }
////
////
////            });
////            mAdView.loadAd(builder.build());
////            mBinding.bottomBanner.addView(mAdView);
////        } else {
////            mAdView.resume();
////        }
//    }
//
//

//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        if (mRateAppModule != null) {
//            mRateAppModule.appReloadedHandler();
//        }
//        super.onSaveInstanceState(outState);
//    }
//
//    private boolean doubleBackToExitPressedOnce;
//
//    @Override
//    public void onBackPressed() {
//
//        //Pressed back => return to home screen
//        int count = getSupportFragmentManager().getBackStackEntryCount();
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setHomeButtonEnabled(count > 0);
//        }
//        if (count > 0) {
//            getSupportFragmentManager()
//                    .popBackStack(getSupportFragmentManager()
//                                    .getBackStackEntryAt(0).getId(),
//                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        } else {//count == 0
//
//
////                Dialog
////                new AlertDialog.Builder(this)
////                        .setIcon(android.R.drawable.ic_dialog_alert)
////                        .setTitle("Leaving this App?")
////                        .setMessage("Are you sure you want to close this application?")
////                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                finish();
////                            }
////
////                        })
////                        .setNegativeButton("No", null)
////                        .show();
//            //super.onBackPressed();
//
//
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;
//            }
//
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
//
//            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 800);
//
//        }
//    }
//
//    @Override
//    public void showProgressBar() {
//
//    }
//
//    @Override
//    public void testNotify(String message) {
//        if (presenter != null) {
//            presenter.notifyMaker(this, SubdomainActivity.class, message);
//        }
//    }
//
//    @Override
//    public void errorToast(String err) {
////        Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
////        Toasty.custom(this, err,
////                Com19.getDrawable(this, R.drawable.ic_info),
////                ContextCompat.getColor(this, R.color.colorPrimaryDark),
////                white, Toasty.LENGTH_SHORT, true, true).show();
//
//        Toasty.custom(this,
//                R.string.internet_connectivity_problem,
//                comv19.getDrawable(this, R.drawable.ic_cancel),
//                R.color.error,
//                R.color.white, Toasty.LENGTH_SHORT, true, true).show();
//
//    }
//}
