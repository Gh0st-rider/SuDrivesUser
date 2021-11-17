package com.sudrives.sudrives.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.HomeMenuActivity;
import com.sudrives.sudrives.activity.MainActivity;
import com.sudrives.sudrives.activity.OutstationStaticActivity;
import com.sudrives.sudrives.activity.Splash;
import com.sudrives.sudrives.adapter.RecentBookingAdapter;
import com.sudrives.sudrives.direction.GetDirectionDataTask;
import com.sudrives.sudrives.listeners.RecentSelect;
import com.sudrives.sudrives.model.RecentBookingModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GPSTracker;
import com.sudrives.sudrives.utils.GetLocation;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeLocationSelectFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, RecentSelect, GoogleMap.OnMarkerClickListener{

    TextView et_search, tv_suggestion1, tv_suggestion2;
    String lat = "", longi = "", deslat = "", deslong = "";
    public static final int REQUEST_CODE_HOME_ADDRESS = 1;
    public static final int REQUEST_CODE_WORK_ADDRESS = 2;
    Context mContext = getActivity();
    LinearLayout ll_rental, ll_daily, ll_outstation;

    RecyclerView recyclerRecent;
    RecentBookingAdapter recentBookingAdapter;
    public static ArrayList<RecentBookingModel> recentBookingModels;

    SessionPref mSessionPref;
    GoogleMap mMap;
    private static final String TAG = "fgcfgcgfh";
    private double mLat = 0.0, mLong = 0.0;

    FragmentTransaction fragmentTransaction;

    TextView tv_recent;
    androidx.appcompat.app.AlertDialog show;

    BottomSheetBehavior mBottomSheetBehavior;


    LocationManager locationManager;
    private GetLocation getLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragement_home_location, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSessionPref = new SessionPref(getActivity());
        getLocation = new GetLocation(getActivity());

        et_search = view.findViewById(R.id.et_search);

        tv_recent = view.findViewById(R.id.tv_recent);


        tv_suggestion1 = view.findViewById(R.id.tv_suggestion1);

        tv_suggestion2 = view.findViewById(R.id.tv_suggestion2);

        ll_daily = view.findViewById(R.id.ll_daily);

        ll_rental = view.findViewById(R.id.ll_rental);

        ll_outstation = view.findViewById(R.id.ll_outstation);





        if (!SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_ADDRESS).isEmpty()) {
            tv_suggestion2.setText(SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_ADDRESS));

        }

        if (!SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_HOME_ADDRESS).isEmpty()) {
            tv_suggestion1.setText(SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_HOME_ADDRESS));

        }

        et_search.setOnClickListener(this);
        tv_suggestion1.setOnClickListener(this);
        tv_suggestion2.setOnClickListener(this);
        ll_daily.setOnClickListener(this);
        ll_rental.setOnClickListener(this);
        ll_outstation.setOnClickListener(this);



        recyclerRecent = view.findViewById(R.id.recycler_recent_booking);
        recyclerRecent.setHasFixedSize(true);
        recentBookingModels = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerRecent.setLayoutManager(layoutManager);
        recyclerRecent.setNestedScrollingEnabled(false);
        recentBookingAdapter = new RecentBookingAdapter(mContext, recentBookingModels, this);
        recyclerRecent.setAdapter(recentBookingAdapter);

        getRecentBooking();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        final View bottomSheet = view.findViewById(R.id.bottom_sheet_home);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            getLocation.checkLocationPermission();
            mLat = (new GPSTracker(getActivity())).getLatitude();
            mLong = (new GPSTracker(getActivity())).getLongitude();

        } else {


            showSettingsAlert();

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search:
                Bundle bundle = new Bundle();
                bundle.putString("lat", lat);
                bundle.putString("from", "text");
                bundle.putString("long", longi);
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, homeFragment).addToBackStack("home").commit();
                ((HomeMenuActivity) getActivity()).fragment = homeFragment;

                break;

            case R.id.tv_suggestion1:
                if (SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_HOME_ADDRESS).isEmpty()) {
                    openAutocompleteActivity(REQUEST_CODE_HOME_ADDRESS);
                } else {

                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Deslat", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_HOME_LAT));
                    bundle1.putString("Deslong", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_HOME_LONG));
                    bundle1.putString("lat", lat);
                    bundle1.putString("long", longi);
                    bundle1.putString("from", "home");
                    HomeFragment homeFragment1 = new HomeFragment();
                    homeFragment1.setArguments(bundle1);
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, homeFragment1).addToBackStack("home").commit();
                    ((HomeMenuActivity) getActivity()).fragment = homeFragment1;

                }

                break;

            case R.id.tv_suggestion2:
                if (SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_ADDRESS).isEmpty()) {
                    openAutocompleteActivity(REQUEST_CODE_WORK_ADDRESS);
                } else {

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("Deslat", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_LAT));
                    bundle2.putString("Deslong", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_LONG));
                    bundle2.putString("lat", lat);
                    bundle2.putString("long", longi);
                    bundle2.putString("from", "work");
                    HomeFragment homeFragment2 = new HomeFragment();
                    homeFragment2.setArguments(bundle2);
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, homeFragment2).addToBackStack("home").commit();
                    ((HomeMenuActivity) getActivity()).fragment = homeFragment2;

                }

                break;

            case R.id.ll_daily:

                Bundle bundle3 = new Bundle();
                bundle3.putString("lat", lat);
                bundle3.putString("from", "text");
                bundle3.putString("long", longi);
                HomeFragment homeFragment3 = new HomeFragment();
                homeFragment3.setArguments(bundle3);
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, homeFragment3).addToBackStack("home").commit();
                ((HomeMenuActivity) getActivity()).fragment = homeFragment3;
                break;

            case R.id.ll_rental:

               /* Bundle bundle4 = new Bundle();
                bundle4.putString("lat", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_LAT));
                bundle4.putString("long", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_LONG));
                bundle4.putString("from", "rental");
                HomeFragment homeFragment4 = new HomeFragment();
                homeFragment4.setArguments(bundle4);
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, homeFragment4).addToBackStack("home").commit();
                ((HomeMenuActivity) getActivity()).fragment = homeFragment4;*/

                //popupService();

                //startActivity(new Intent(getActivity(), OutstationStaticActivity.class));
                Intent i1 =  new Intent(getActivity(), OutstationStaticActivity.class);
                i1.putExtra("stringout", "Are you trying to rent a car?\n" +
                        "Yes we are accepting rental rides offline.");
                startActivity(i1);
                break;

            case R.id.ll_outstation:
                Intent i =  new Intent(getActivity(), OutstationStaticActivity.class);
                i.putExtra("stringout", "Are you trying to book an Outstation Ride?\n" +
                        "Yes we are accepting Outstation rides offline.");
                startActivity(i);

               // popupService();
               /* Bundle bundle5 = new Bundle();
                bundle5.putString("lat", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_LAT));
                bundle5.putString("long", SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_LONG));
                bundle5.putString("from", "outstation");
                HomeFragment homeFragment5 = new HomeFragment();
                homeFragment5.setArguments(bundle5);
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, homeFragment5).addToBackStack("home").commit();
                ((HomeMenuActivity) getActivity()).fragment = homeFragment5;*/

                break;

        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_HOME_ADDRESS) {
            if (resultCode == getActivity().RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                //call location based filter

                LatLng latLong;
                latLong = place.getLatLng();
                String mToLat = String.valueOf(latLong.latitude);
                String mToLong = String.valueOf(latLong.longitude);
                String mToaddress = (String) place.getAddress();

                tv_suggestion1.setText(mToaddress);

                SessionPref.saveDataIntoSharedPref(getActivity(), SessionPref.KEY_HOME_ADDRESS, mToaddress);
                SessionPref.saveDataIntoSharedPref(getActivity(), SessionPref.KEY_HOME_LAT, mToLat);
                SessionPref.saveDataIntoSharedPref(getActivity(), SessionPref.KEY_HOME_LONG, mToLong);



            }


        }
        if (requestCode == REQUEST_CODE_WORK_ADDRESS) {
            if (resultCode == getActivity().RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                //call location based filter

                LatLng latLong;
                latLong = place.getLatLng();
                String mToLat = String.valueOf(latLong.latitude);
                String mToLong = String.valueOf(latLong.longitude);
                String mToaddress = (String) place.getAddress();

                tv_suggestion2.setText(mToaddress);
                SessionPref.saveDataIntoSharedPref(getActivity(), SessionPref.KEY_WORK_ADDRESS, mToaddress);
                SessionPref.saveDataIntoSharedPref(getActivity(), SessionPref.KEY_WORK_LAT, mToLat);
                SessionPref.saveDataIntoSharedPref(getActivity(), SessionPref.KEY_WORK_LONG, mToLong);


            }


        }

    }

    private void getRecentBooking() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FETCH_RECENT_BOOKING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // progressBar.setVisibility(View.GONE);
                Log.e("getRecentBooking", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {
                        recentBookingModels.clear();
                        JSONArray jsonArrayResult = jsonResponse.getJSONArray("result");
                        if (jsonArrayResult.length() != 0) {
                            tv_recent.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            if (jsonArrayResult.length() >= 5){
                                for (int i = 0; i < 5; i++) {
                                    RecentBookingModel recentBookingModel = gson.fromJson(String.valueOf(jsonArrayResult.get(i)), RecentBookingModel.class);
                                    recentBookingModels.add(recentBookingModel);
                                }
                            }else {
                                for (int i = 0; i < jsonArrayResult.length(); i++) {
                                    RecentBookingModel recentBookingModel = gson.fromJson(String.valueOf(jsonArrayResult.get(i)), RecentBookingModel.class);
                                    recentBookingModels.add(recentBookingModel);
                                }
                            }

                        } else {

                            tv_recent.setVisibility(View.GONE);

                        }

                    } else {

                        Log.e("nodata", message);
                    }

                    recentBookingAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressBar.setVisibility(View.GONE);
                Log.e("vollyerror", String.valueOf(error));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", mSessionPref.user_userid);
                params.put("token", mSessionPref.token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // googleMap.setPadding(left, top, right, bottom);
        googleMap.setOnMarkerClickListener(this);
        CameraUpdate cameraUpdate = null;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.ola_style));

            if (!success) {
                Log.e("mapstyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("mapstyle", "Can't find style.", e);
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (mLat != 0.0) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLong), 14);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLong), 14));
            LatLng sourceLatLng = new LatLng(mLat, mLong);

            mMap.addMarker(new MarkerOptions().position(sourceLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from)));


        } else {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 5);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 14));

        }



        mMap.getUiSettings().setScrollGesturesEnabled(false);



    }




    private void popupService() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_permission, null);
        alertDialog.setView(d);
        show = alertDialog.show();

        Button btnDone = d.findViewById(R.id.btn_allow_permission);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundleMap = new Bundle();
        bundleMap.putString("lat", lat);
        bundleMap.putString("from", "text");
        bundleMap.putString("long", longi);
        HomeFragment homeMap = new HomeFragment();
        homeMap.setArguments(bundleMap);
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, homeMap).addToBackStack("home").commit();
        ((HomeMenuActivity) getActivity()).fragment = homeMap;
        return false;
    }

    @Override
    public void onRecentClick(String fromLat, String fromLong, String toLat, String toLong) {

        Bundle bundle = new Bundle();
        bundle.putString("Deslat", toLat);
        bundle.putString("Deslong", toLong);
        bundle.putString("lat", lat);
        bundle.putString("long", longi);
        bundle.putString("from", "work");
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, homeFragment).addToBackStack("home").commit();
        ((HomeMenuActivity) getActivity()).fragment = homeFragment;
    }

    public void showSettingsAlert() {
        int layoutPopup = R.layout.duuble_button_dialog;
        AppDialogs.DoubleButtonWithCallBackVersionDialog(getActivity(), layoutPopup, true
                , false, 0, getString(R.string.gps_is_not_enabled), getString(R.string.gps_setting),
                getString(R.string.action_settings), getString(R.string.cancel),
                new AppDialogs.Doublebuttonpincallback() {
                    @Override
                    public void doublebuttonok(String from) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }, new AppDialogs.DoublebuttonCancelcallback() {
                    @Override
                    public void doublebuttonCancel(String from) {
                        //setValue();
                        Toast.makeText(getActivity(), R.string.please_enable_location_services_and_gps_to_continue, Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                }, new AppDialogs.Crosscallback() {


                    @Override
                    public void crossButtonCallback(String from) {

                    }


                });


    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeMenuActivity) getActivity()).flagLiveTripCall = true;
        if (!SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_ADDRESS).isEmpty()) {
            tv_suggestion2.setText(SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_WORK_ADDRESS));
            //edit_workLocation.setVisibility(View.VISIBLE);
        }

        if (!SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_HOME_ADDRESS).isEmpty()) {
            tv_suggestion1.setText(SessionPref.getDataFromPref(getActivity(), SessionPref.KEY_HOME_ADDRESS));
           // edit_homeLocation.setVisibility(View.VISIBLE);
        }


    }

}