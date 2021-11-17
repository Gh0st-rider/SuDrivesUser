package com.sudrives.sudrives.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.DrawableRes;
import androidx.databinding.DataBindingUtil;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityFindingArideBinding;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;

public class FindingARideActivity extends BaseActivity implements OnMapReadyCallback {
    private ActivityFindingArideBinding mBinding;
    private GoogleMap mMap;
    private Activity mContext;
    private ErrorLayout mErrorLayout;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_finding_aride);
        GlobalUtil.setStatusBarColor(FindingARideActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getControls();
    }

    private void getControls() {
        mContext = FindingARideActivity.this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FontLoader.setHelBold(mBinding.tvFindARide);
        FontLoader.setHelRegular(mBinding.tvLocation,mBinding.tvSearchingForRentals);

        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));

        mBinding.tvLocation.setText(getIntent().getStringExtra("mFromAddress"));
        if (checkConnection()) {

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            mBinding.progressBar.setProgress(progressStatus);

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            mContext, R.raw.ola_style));

            if (!success) {
                Log.e("mapstyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("mapstyle", "Can't find style.", e);
        }

        CameraUpdate cameraUpdate = null;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }




        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (getIntent().getStringExtra("mFromLat")!=null) {
             cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(getIntent().getStringExtra("mFromLat")), Double.parseDouble(getIntent().getStringExtra("mFromLong"))), 12);
             mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(getIntent().getStringExtra("mFromLat")), Double.parseDouble(getIntent().getStringExtra("mFromLong")))));

            markerOptions.position(new LatLng(Double.parseDouble(getIntent().getStringExtra("mFromLat")),Double.parseDouble(getIntent().getStringExtra("mFromLong"))));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from));


        }else{
             cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 5);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629)));
        }
        mMap.animateCamera(cameraUpdate);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }
}
