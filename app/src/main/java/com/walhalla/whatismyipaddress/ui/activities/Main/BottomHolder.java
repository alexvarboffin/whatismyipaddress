package com.walhalla.whatismyipaddress.ui.activities.Main;

import static com.walhalla.ui.plugins.DialogAbout.aboutDialog;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.walhalla.ui.DLog;

import com.walhalla.compat.ComV19;
import com.walhalla.ui.plugins.Launcher;
import com.walhalla.ui.plugins.Module_U;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.fragment.BaseFragment;
import com.walhalla.whatismyipaddress.ui.fragment.DashboardFragment;
import com.walhalla.whatismyipaddress.ui.fragment.Fragment1;
import com.walhalla.whatismyipaddress.ui.fragment.home.HomeFragment;
import com.walhalla.whatismyipaddress.ui.fragment.MiscellaneousFragment;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BottomHolder extends AppCompatActivity
        implements MainActivityPresenter.View,
        Fragment1.Fragment1Callback, BaseFragment.FragmentCallback {

    //private RateAppModule mRateAppModule;

    private ViewPagerAdapter mPagerAdapter;
    private final List<Integer> indexToPage = new ArrayList<>();


    //private final List<Fragment> fragments = new ArrayList<>();
    private MainActivityPresenter presenter;
    private ViewPager2 viewPager2;
    private AdView adView;
    private ComV19 comv19;


//    final FragmentManager fm = getSupportFragmentManager();
//    Fragment active = fragment1;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {//requestData();
            return false;
        } else if (itemId == R.id.action_share) {//Tools.shareString(Tools.getDataText(this.mDatabaseManager, dataLocal, this), this);
            return false;
        } else if (itemId == R.id.action_copy) {//Tools.copyToClipboard(Tools.getDataText(this.mDatabaseManager, dataLocal, this), this);
            return false;
        } else if (itemId == R.id.action_about) {
            aboutDialog(this);
            return true;
        } else if (itemId == R.id.action_privacy_policy) {
            Launcher.openBrowser(this, getString(R.string.url_privacy_policy));
            return true;
        } else if (itemId == R.id.action_rate_app) {
            Launcher.rateUs(this);
            return true;
        } else if (itemId == R.id.action_share_app) {
            Module_U.shareThisApp(this);
            return true;
        } else if (itemId == R.id.action_discover_more_app) {
            Module_U.moreApp(this);
            return true;

//            case R.id.action_exit:
//                mNotificationManager.cancel(NOTIFICATION_ID);
//                this.finish();
//                return true;
        } else if (itemId == R.id.action_feedback) {
            Module_U.feedback(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        presenter = new MainActivityPresenter(this, this);

        boolean version1 = false;
        if (version1) {
            //setContentView(R.layout.activity_home);
        } else {
            setContentView(R.layout.bottom_holder);
            Toolbar too = findViewById(R.id.toolbar);
            setSupportActionBar(too);
        }


        AdRequest build = new AdRequest.Builder().build();
        adView = findViewById(R.id.adView);
        adView.loadAd(build);

        final BottomNavigationView navView = findViewById(R.id.bottomNavigation);
        viewPager2 = findViewById(R.id.mainViewPager);
        viewPager2.setUserInputEnabled(false);

        if (version1) {
//            // Passing each menu ID as a set of Ids because each
//            // menu should be considered as top level destinations.
//            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.navigation_home,
//                    R.id.navigation_dashboard,
//                    R.id.navigation_notifications
//            )
//                    .build();
//            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//            NavigationUI.setupWithNavController(navView, navController);
//            getSupportActionBar().hide();
//
//            navView.setSelectedItemId(R.id.navigation_dashboard);
//
//            navView.setOnNavigationItemReselectedListener(item -> {
//                // Do Nothing
//            });
        } else {


            indexToPage.add(R.id.navigation_home);
            indexToPage.add(R.id.navigation_dashboard);
            indexToPage.add(R.id.navigation_notifications);
            indexToPage.add(R.id.navigation_3);

//            if (getSupportActionBar() != null) {
//                getSupportActionBar().hide();
//            }

            navView.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

//   mainViewPager.a(this @SubdomainActivity)

            mPagerAdapter = new ViewPagerAdapter(this);//<== getChildFragmentManager(), 0
//            fragments.add(new Fragment1());
//            fragments.add(new MiscellaneousFragment());
//            fragments.add(new HomeFragment());
//            fragments.add(new DashboardFragment());
//            mPagerAdapter.addFragments(fragments);
            viewPager2.setAdapter(mPagerAdapter);
////            mainViewPager.post(this @SubdomainActivity::checkDeepLink)
//            viewPager2.setOffscreenPageLimit(
//                    (fragments.size() > 0) ? fragments.size() : ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
//            );

            viewPager2.setOffscreenPageLimit(4);
            //mainViewPager.setCurrentItem(2);

//            fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
//            fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
//            fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

        }


    }


    @SuppressLint("NonConstantResourceId")
    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        Integer position = indexToPage.indexOf(item.getItemId());
        if (position >= 0) {
            menuSelected(position);
            return true;
        }

//        switch (item.getItemId()) {
//            case R.id.navigation_home:
//                fm.beginTransaction().hide(active).show(fragment1).commit();
//                active = fragment1;
//                return true;
//
//            case R.id.navigation_dashboard:
//                fm.beginTransaction().hide(active).show(fragment2).commit();
//                active = fragment2;
//                return true;
//
//            case R.id.navigation_notifications:
//                fm.beginTransaction().hide(active).show(fragment3).commit();
//                active = fragment3;
//                return true;
//        }
        return false;
    };

    private void menuSelected(Integer position) {
        handleSelectedSuccess(position);
    }

    private void handleSelectedSuccess(int position) {
        if (viewPager2.getCurrentItem() != position) {
            setItem(position);
        }
    }

    private void setItem(Integer position) {
        viewPager2.setCurrentItem(position);
        //viewModel.push(position);
    }

//    private void handleSelectedSuccess(MainState.SelectedSuccess state){
//        if (mainViewPager.getCurrentItem() != state.position) setItem(state.position)
//    }


    @Override
    public void showProgressBar() {

    }

    @Override
    public void testNotify(String message) {
        if (presenter != null) {
            presenter.notifyMaker(this, BottomHolder.class, message);
        }
    }

    @Override
    public void errorToast(String err) {
        Toasty.custom(this,
                R.string.internet_connectivity_problem,
                comv19.getDrawable(this, R.drawable.ic_cancel),
                R.color.error,
                R.color.white, Toasty.LENGTH_SHORT, true, true).show();
    }

//    @Override
//    public void onBackPressed() {
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startMain);
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
////        if (mPublisherAdView != null) {
////            mPublisherAdView.pause();
////        }
//        if (mAdView != null) {
//            mAdView.pause();
//        }
//    }
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

//    @Override
//    public void onDestroy() {
//        if (adView != null) {
//            adView.destroy();
//        }
//        super.onDestroy();
//    }

    //    @Override
//    protected void onDestroy() {
////        if (mPublisherAdView != null) {
////            mPublisherAdView.destroy();
////        }
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
//        super.onDestroy();
//    }
    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }

//        if (mAdView == null) {
//            //
//            mAdView = createBanner(this, "ca-app-pub-5111357348858303/9993967729");
//
//            AdRequest.Builder builder = new AdRequest.Builder();
////            PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();
//
//            if (DEBUG) {
//                builder.addTestDevice("330FD453402153D99F79DD7C25317FEC");
//                builder.addTestDevice("D97101CD912C42082D0C57DF7EC70E26");
//                builder.addTestDevice("420B3C23A53A68C1F994B6E2043964AA");
//                builder.addTestDevice("1FFCD512BD8AE45F7647127CB80345E8");
//            }
//            mAdView.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    super.onAdLoaded();
//                    mAdView.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAdFailedToLoad(int errorCode) {
//                    // Code to be executed when an ad request fails.
//                }
//
//                @Override
//                public void onAdOpened() {
//                    // Code to be executed when an ad opens an overlay that
//                    // covers the screen.
//                }
//
//                @Override
//                public void onAdLeftApplication() {
//                    // Code to be executed when the user has left the app.
//                }
//
//                @Override
//                public void onAdClosed() {
//                    // Code to be executed when when the user is about to return
//                    // to the app after tapping on an ad.
//                }
//
//
//            });
//            mAdView.loadAd(builder.build());
//            mBinding.bottomBanner.addView(mAdView);
//        } else {
//            mAdView.resume();
//        }
    }


//    public static PublisherAdView createBanner(Context context, String banner_ad_unit_id) {
//        int id = UUID.randomUUID().hashCode();
//        PublisherAdView mAdView = new PublisherAdView(context);
//        //mPublisherAdView.setVisibility(View.GONE);
//        mAdView.setAdSizes(AdSize.BANNER);
//        mAdView.setAdUnitId(banner_ad_unit_id);
//        mAdView.setId(id);
//        return mAdView;
//    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        if (mRateAppModule != null) {
//            mRateAppModule.appReloadedHandler();
//        }
//        super.onSaveInstanceState(outState);
//    }

    private boolean doubleBackToExitPressedOnce = true;

    @Override
    public void onBackPressed() {

        //Pressed back => return to home screen
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(count > 0);
        }
        if (count > 0) {
            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {//count == 0


//                Dialog
//                new AlertDialog.Builder(this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Leaving this App?")
//                        .setMessage("Are you sure you want to close this application?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
            //super.onBackPressed();
            if (isFirstPage()) {
                if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;

                    // Move the task containing the SubdomainActivity to the back of the activity stack, instead of
                    // destroying it. Therefore, SubdomainActivity will be shown when the user switches back to the app.
                    moveTaskToBack(true);
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                //Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
                Toasty.custom(this, R.string.press_again_to_exit,
                        comv19.getDrawable(this, R.drawable.ic_info),
                        R.color.colorPrimaryDark,
                        R.color.white, Toasty.LENGTH_SHORT, true, true).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1200);
            } else {
                // Если текущий фрагмент не является первым, вернитесь на первый фрагмент
                viewPager2.setCurrentItem(0, true); // Установите первый фрагмент в ViewPager2
            }
        }
    }

    private boolean isFirstPage() {
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.viewPagerContainer);
//        return (currentFragment instanceof Fragment1);

        int currentPosition = viewPager2.getCurrentItem();
        return (currentPosition == 0);
    }

    @Override
    public void copyToBuffer(String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("IP Tools", value);
            clipboard.setPrimaryClip(clip);
            Toasty.custom(this, String.format(getString(R.string.data_to_clipboard), value).toUpperCase(),
                            comv19.getDrawable(this,
                                    R.drawable.ic_info), ContextCompat.getColor(this,
                                    R.color.colorPrimaryDark),
                            ContextCompat.getColor(this, R.color.white),
                            Toasty.LENGTH_SHORT, true, true)
                    .show();
        }
    }

    @Override
    public void shareText(String value) {
        Module_U.shareText(this, value, null);
    }
}