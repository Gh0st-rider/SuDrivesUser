package com.sudrives.sudrives.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.BaseFragment;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GetLocation;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelectLocationSourceDesFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    double latitude, longitude;
    GoogleMap mMap;

    TextView et_pickup, et_destination;
    String mFromLong = "0.0", mFromLat = "0.0", mToLat = "0.0", mToLong = "0.0", mToaddress = "",mFromaddress = "";

    public static final int REQUEST_CODE_RENTAL_RETURN = 4;
    private static final int REQUEST_CODE_FROM_LOC = 1;
    private static final int REQUEST_CODE_TO_LOC = 2;
    private static final int REQUEST_CODE_CONFIRM_RETURN = 3;
    private static final int REQUEST_CODE_OUTSTATION_RETURN = 5;
    private static final int REQUEST_CODE_RENTAL_FROM_LOC = 6;

    Context mContext = getActivity();

    double placePickerToLat= 0.0, placePickerToLong = 0.0;

    MarkerOptions markerOptionsSource;
    MarkerOptions markerOptionsDestination;

    Marker markerSource, markerDestination;
    //Define list to get all latlng for the route
    List<LatLng> path = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_location_source_des, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void init(View view) {

        et_pickup = view.findViewById(R.id.et_pickup);
        et_destination = view.findViewById(R.id.et_destination);

        et_pickup.setOnClickListener(this);
        et_destination.setOnClickListener(this);

    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = lat;
                longitude = longi;
                Toast.makeText(getActivity(), "Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude, Toast.LENGTH_LONG).show();

                SupportMapFragment mapFragment1 = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                if (mapFragment1 != null) {
                    mapFragment1.getMapAsync(this);
                }
                //SupportMapFragment mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                //      .findFragmentById(R.id.map);
                //mMapFragment.getMapAsync(this);
                // showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
            } else {
                Toast.makeText(getActivity(), "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.ola_style));

            if (!success) {
                Log.e("", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("", "Can't find style.", e);
        }


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17f));
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        setupMap();
    }

    private void setupMap() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        markerSource = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Source"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17f));


    }

    private void homeFromAddressClick(View view) {
        if (checkConnection()) {
            GlobalUtil.avoidDoubleClicks(et_pickup);
            openAutocompleteActivity(REQUEST_CODE_FROM_LOC);
        } else {
            //mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    private void homeToAddressClick(View view) {
        if (checkConnection()) {
            GlobalUtil.avoidDoubleClicks(et_destination);
            if (!mFromLong.equals("0.0") && !mFromLat.equals("0.0")) {
                openAutocompleteActivity(REQUEST_CODE_TO_LOC);
            } else {
                Toast.makeText(getActivity(), getString(R.string.please_enter_pick_up_location), Toast.LENGTH_SHORT).show();
            }
        } else {
            // mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void openAutocompleteActivity(int request_code) {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            LatLngBounds latLngBounds = new LatLngBounds(new LatLng(22.719568, 75.857727),
                    new LatLng(22.719568, 75.857727));

            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("IN")
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(latLngBounds)
                    .setFilter(autocompleteFilter)
                    .build(getActivity());
            startActivityForResult(intent, request_code);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.et_pickup:

                homeFromAddressClick(v);

                break;

            case R.id.et_destination:

                homeToAddressClick(v);

                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        KeyboardUtil.hideSoftKeyboard(getActivity());


        if (requestCode == REQUEST_CODE_FROM_LOC) {
            if (resultCode == getActivity().RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                LatLng latLong;
                latLong = place.getLatLng();
                mFromLat = String.valueOf(latLong.latitude);
                mFromLong = String.valueOf(latLong.longitude);
                mFromaddress = GlobalUtil.getAddress(getActivity(), latLong.latitude, latLong.longitude);
                et_pickup.setText(mFromaddress);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLong.latitude, latLong.longitude), 17f));
                placePickerToLat = latLong.latitude;
                placePickerToLong = latLong.longitude;

                setSourceMarker(mMap, latLong);
            }
        }

        if (requestCode == REQUEST_CODE_TO_LOC) {
            if (resultCode == getActivity().RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                LatLng latLong;
                latLong = place.getLatLng();
                mToLat = String.valueOf(latLong.latitude);
                mToLong = String.valueOf(latLong.longitude);
                mToaddress = GlobalUtil.getAddress(getActivity(), latLong.latitude, latLong.longitude);
                et_destination.setText(mToaddress);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLong.latitude, latLong.longitude), 17f));
                placePickerToLat = latLong.latitude;
                placePickerToLong = latLong.longitude;

                setDestinationMarker(mMap, latLong);
            }
        }


    }

    private void setSourceMarker(GoogleMap googleMap, LatLng latLng){

        if (markerSource != null)
            markerSource.remove();


        markerSource = googleMap.addMarker(new MarkerOptions().position(latLng).title("Source"));


    }


    private void setDestinationMarker(GoogleMap googleMap, LatLng latLng){

        if (markerDestination != null)
            markerDestination.remove();

        markerDestination = googleMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));


    }

}
