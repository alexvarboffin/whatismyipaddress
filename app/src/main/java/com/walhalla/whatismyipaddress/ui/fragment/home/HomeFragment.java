package com.walhalla.whatismyipaddress.ui.fragment.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.walhalla.whatismyipaddress.Helpers0;
import com.walhalla.whatismyipaddress.adapter.ListAdapter;
import com.walhalla.whatismyipaddress.adapter.items.ViewModel;

import com.walhalla.netdiscover.NonScrollListView;

import com.walhalla.ui.DLog;
import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.AssetUtils;
import com.walhalla.whatismyipaddress.adapter.twocol.TwoColItem;
import com.walhalla.whatismyipaddress.databinding.FragmentHomeBinding;
import com.walhalla.whatismyipaddress.ui.activities.MapActivity;
import com.walhalla.whatismyipaddress.ui.activities.SpeedTest;
import com.walhalla.whatismyipaddress.ui.fragment.BaseFragment;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends
        BaseFragment implements OnMapReadyCallback,
        HomeFragmentPresenter.HomeView, ListAdapter.OnItemClickListener {


    //ArrayList<String> values = new ArrayList<>();

    NonScrollListView mListView;
    SpinKitView spinKitView;

    SupportMapFragment mapFragment;
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;
    Boolean isMyIp = false;

    private SharedPreferences pref;


    private LinearLayoutManager layoutManager;
    private ComV19 comv19;
    private HomeFragmentPresenter presenter;
    private ListAdapter m;

    private FragmentHomeBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comv19 = new ComV19();
        layoutManager = new LinearLayoutManager(getActivity());
        Handler handler = new Handler(Looper.getMainLooper());
        presenter = new HomeFragmentPresenter(getContext(), this, handler);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        DLog.d("[AAAA] " + this.hashCode());

        pref = getContext().getSharedPreferences("IP Tools", 0);

        mListView = root.findViewById(R.id.listView);
        m = new ListAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(m);
        m.setOnItemClickListener(this);

        spinKitView = root.findViewById(R.id.spin_kit);

        display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        loadPreferences();

        binding.search.setOnClickListener(view -> {
            Helpers0.hideKeyboard(getActivity());
            if (AssetUtils.isNetworkAvailable(getActivity())) {
                if (binding.ip != null
                        && !binding.ip.getText().toString().equals("")
                        && !binding.ip.getText().toString().equals(" ")
                        && !binding.ip.getText().toString().equals("  ")
                        && !binding.ip.getText().toString().equals("   ")
                        && !binding.ip.getText().toString().equals("    ")) {
                    isMyIp = false;
                    String ip = prepareIp(isMyIp);
                    presenter.getIpInfo(isMyIp, ip);
                } else {
                    Toasty.custom(getContext(), "PROVIDE IP OR WEB ADDRESS",
                            comv19.getDrawable(getContext(), R.drawable.ic_cancel), ContextCompat.getColor(getContext(), R.color.error), ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                }
            } else {
                Toasty.custom(getContext(),
                        getString(R.string.internet_connectivity_problem), comv19.getDrawable(getContext(), R.drawable.ic_cancel), ContextCompat.getColor(getContext(), R.color.error), ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            }
        });

        binding.myIp.setOnClickListener(view -> {
            Helpers0.hideKeyboard(getActivity());
            isMyIp = true;
            String ip = prepareIp(isMyIp);
            presenter.getIpInfo(isMyIp, ip);
        });


        binding.speedTest.setOnClickListener(view -> startActivity(new Intent(getContext(), SpeedTest.class)));
        if (savedInstanceState == null) {
            if (AssetUtils.isNetworkAvailable(getActivity())) {
                if (binding.ip != null
                        && !binding.ip.getText().toString().equals("")
                        && !binding.ip.getText().toString().equals(" ")
                        && !binding.ip.getText().toString().equals("  ")
                        && !binding.ip.getText().toString().equals("   ")
                        && !binding.ip.getText().toString().equals("    ")) {
                    isMyIp = false;
                    String ip = prepareIp(isMyIp);
                    presenter.getIpInfo(isMyIp, ip);
                } else {
                    Toasty.custom(getContext(), "PROVIDE IP OR WEB ADDRESS", comv19.getDrawable(getContext(), R.drawable.ic_cancel), ContextCompat.getColor(getContext(), R.color.error), ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
                }
            } else {
                Toasty.custom(getContext(),
                        getString(R.string.internet_connectivity_problem),
                        comv19.getDrawable(getContext(), R.drawable.ic_cancel),
                        ContextCompat.getColor(getContext(), R.color.error),
                        ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
            }
        }

        //@  Tools.hideKeyboard(getActivity());

//        mInterstitialAd = new InterstitialAd(getContext());
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() { if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    } else {
//                        Log.d("TAG", "The interstitial wasn't loaded yet.");
//                    }
//                    }
//                });
//            }
//        }, 6000);
    }

    private String prepareIp(Boolean isMyIp) {
        return (isMyIp) ? "" : binding.ip.getText().toString().trim();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }


//                            Snackbar snackbar = Snackbar
//                                    .make(layout, "Copy \"" + values.get(i) + "\" to Clipboard?", Snackbar.LENGTH_LONG)
//                                    .setAction("Copy", view1 -> {
//                                        ddd(getContext(), values.get(i));
//                                    });
//
//                            snackbar.show();

    private void loadPreferences() {
        if (pref.contains("ip")) {
            binding.ip.setText(pref.getString("ip", "www.google.com"));
        } else {
            binding.ip.setText(R.string.www_google);
        }
    }


    public Bitmap resizeGoogleMapIcons(String iconName, int width, int height) {
        //DLog.d("@@@@" + iconName);
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getContext().getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    @Override
    public void showProgress() {
        spinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        spinKitView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void receivedErrorMessage(String message) {
        if (isAdded()) {
            binding.mapView.setVisibility(View.GONE);
            Toasty.custom(getContext(), message.toUpperCase(), comv19.getDrawable(getContext(), R.drawable.ic_info), ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), ContextCompat.getColor(getContext(), R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
    }

    @Override
    public void successResult(ArrayList<ViewModel> models) {
        m.swap(models);
    }

    @Override
    public void setMap(String lat, String lng, String asname, String isp, String query) {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        binding.mapView.setVisibility(View.VISIBLE);

        mapFragment.getMapAsync(mMap -> {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.google_map));
                if (!success) {
                    Log.e("MapsActivityRaw", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("MapsActivityRaw", "Can't find style.", e);
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 17f));

            LatLng hcmus = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            mMap.addMarker(new MarkerOptions().title(asname).snippet(isp).position(hcmus).
                    icon(BitmapDescriptorFactory.fromBitmap(resizeGoogleMapIcons("signs", screenHeight / 23, screenHeight / 23))));

            mMap.setOnMapClickListener(latLng -> startActivity(new Intent(getContext(), MapActivity.class)));

            mMap.setOnMarkerClickListener(marker -> {
                startActivity(new Intent(getContext(), MapActivity.class));
                return false;
            });
        });
        pref.edit().putString("ip", binding.ip.getText().toString().trim()).apply();
        if (isMyIp) {
            binding.ip.setText(query);
        }
    }

    @Override
    public void onListItemClick(ViewModel dataModel) {
        if (dataModel instanceof TwoColItem) {
            String value = ((TwoColItem) dataModel).value;
            callback.copyToBuffer(value);
        }
    }

    @Override
    public void copyToBuffer(String commonName) {
        //none
    }
}