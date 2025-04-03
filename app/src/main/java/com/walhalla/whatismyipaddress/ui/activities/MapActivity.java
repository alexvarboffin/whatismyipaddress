package com.walhalla.whatismyipaddress.ui.activities;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.walhalla.compat.ComV19;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.ui.fragment.home.HomeFragmentPresenter;

import es.dmoral.toasty.Toasty;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;

    Display display;
    Point size;
    int screenWidth;
    int screenHeight;
    TextView ip, latitude, longitude;
    Button back;
    private ComV19 comv19;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar too = findViewById(R.id.toolbar);
        setSupportActionBar(too);

        comv19 = new ComV19();
        back = findViewById(R.id.back);
        ip = findViewById(R.id.ip);
        latitude = findViewById(R.id.lat);
        longitude = findViewById(R.id.lng);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        back.setOnClickListener(view -> finish());
        ip.setText("Public IP: " + HomeFragmentPresenter.query);
        latitude.setText("Lat: " + HomeFragmentPresenter.lat);
        longitude.setText("Lng: " + HomeFragmentPresenter.lng);
        ip.setOnClickListener(view -> {
            copyToBuffer(HomeFragmentPresenter.query);
        });
        latitude.setOnClickListener(view -> {
            copyToBuffer(HomeFragmentPresenter.lat);
        });
        longitude.setOnClickListener(view -> {
            copyToBuffer(HomeFragmentPresenter.lng);
        });
    }

    public void copyToBuffer(String value) {
        ClipboardManager clipboard = (ClipboardManager) MapActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("IP Tools", value);
            clipboard.setPrimaryClip(clip);
            Toasty.custom(MapActivity.this, String.format(getString(R.string.data_to_clipboard), value).toUpperCase(),
                    comv19.getDrawable(MapActivity.this, R.drawable.ic_info), ContextCompat.getColor(MapActivity.this, R.color.colorPrimaryDark),
                    ContextCompat.getColor(MapActivity.this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.google_map));
            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        double dlat = mparseDouble(HomeFragmentPresenter.lat);
        double dlng = mparseDouble(HomeFragmentPresenter.lng);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dlat, dlng), 17f));

        LatLng hcmus = new LatLng(dlat, dlng);
        googleMap.addMarker(new MarkerOptions().title(HomeFragmentPresenter.asname).snippet(HomeFragmentPresenter.isp).position(hcmus).
                icon(BitmapDescriptorFactory.fromBitmap(resizeGoogleMapIcons("signs", screenHeight / 23, screenHeight / 23))));

    }

    double mparseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    public Bitmap resizeGoogleMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }
}
