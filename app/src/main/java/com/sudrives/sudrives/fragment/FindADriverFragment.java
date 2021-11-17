package com.sudrives.sudrives.fragment;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.SocketListnerMethods.ListenersSocket;
import com.sudrives.sudrives.activity.HomeMenuActivity;
import com.sudrives.sudrives.activity.MainActivity;
import com.sudrives.sudrives.databinding.ActivityFindingArideBinding;
import com.sudrives.sudrives.utils.BaseFragment;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.emitter.Emitter;

public class FindADriverFragment extends BaseFragment implements OnMapReadyCallback {
    private ActivityFindingArideBinding mBinding;
    private GoogleMap mMap;
    private Activity mContext;
    private ErrorLayout mErrorLayout;
    private SessionPref mSessionPref;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private String mAddress = "", mVehicleType = "",mTripId="";
    private double mLat = 0.0, mLong = 0.0;
    private View rootView;

    FragmentManager mFragmentManager;
    RelativeLayout rel_progress;
    int countCheckData = 0;

    Marker markerDriver = null, markerLocation = null;

    public FindADriverFragment() {
        // Required empty public constructor
    }

    String mDriverResponse = "";

    LatLng fromLatlong = null, driverLatlong = null;
    Timer timer = new Timer();
    Handler handler1 = new Handler();
    Runnable runnable;
    int delay = 17*1000;
    String vehicle_image = "";
    @Override
    public void onResume() {
        super.onResume();
        String vehicle = mSessionPref.getDataFromPrefConfirm(mContext, mSessionPref.SELECTED_VEHICLE);


        if (SessionPref.getDataFromPrefConfirm(getActivity(), SessionPref.SELECTED_BOOKING) != null) {
            if (SessionPref.getDataFromPrefConfirm(getActivity(), SessionPref.SELECTED_BOOKING).equalsIgnoreCase(Config.DAILY)) {
                if (vehicle.equals(Config.AUTO_VEHICLE)) {
                    mBinding.tvSearchingForRentals.setText(getString(R.string.searching_for_drivers) + " " + getString(R.string.auto) + "...");
                } else {
                    mBinding.tvSearchingForRentals.setText(getString(R.string.searching_for_drivers) + " " + getString(R.string.cabs) + "...");
                }


            } else {
                mBinding.tvSearchingForRentals.setText(getString(R.string.searching_for_drivers) + " " + getString(R.string.cabs) + "...");

            }
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_finding_aride, container, false);
        rootView = mBinding.getRoot();
        mContext = getActivity();
        mSessionPref = new SessionPref(mContext);
        rel_progress = rootView.findViewById(R.id.rel_progress);
        rel_progress.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getControls(rootView);
    }

    private void getControls(View rootView) {


        mErrorLayout = new ErrorLayout(mContext, rootView.findViewById(R.id.error_layout));
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        getData();
        FontLoader.setHelBold(mBinding.tvFindARide);
        FontLoader.setHelRegular(mBinding.tvLocation, mBinding.tvSearchingForRentals);

        mErrorLayout = new ErrorLayout(mContext, rootView.findViewById(R.id.error_layout));
        mBinding.tvLocation.setText(mAddress);

        mBinding.btnCancelFindRide.setOnClickListener(this::cancelClick);

        if (checkConnection()) {

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        public void run() {
                            mBinding.progressBar.setProgress(progressStatus);
                          //  Log.e("111111111", "11111111111");

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(100);
                       // Log.e("222222222222", "22222222222");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void getData() {

        Bundle arg = getArguments();
        mAddress = arg.getString("mAddress");
        mLat = arg.getDouble("mLat");
        mLong = arg.getDouble("mLong");
        mVehicleType = arg.getString("vehicleType");
        mTripId = arg.getString("trip_id");

        fromLatlong = new LatLng(mLat, mLong);
        getTripStatus();
        Log.e("daataaaa", arg.toString());


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null)
            mMap.clear();
        mMap = googleMap;
        CameraUpdate cameraUpdate = null;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        /*try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.ola_style));

            if (!success) {
                Log.e("mapstyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("mapstyle", "Can't find style.", e);
        }*/


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (mLat != 0.0) {
            //cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLong), 12);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLong), 12));
            //LatLng sourceLatLng = new LatLng(mLat, mLong);

            markerLocation = mMap.addMarker(new MarkerOptions().position(fromLatlong).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from)));

            if (driverLatlong != null){
                markerDriver = mMap.addMarker(new MarkerOptions().position(driverLatlong).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_driver)));

            }

            if (markerLocation != null && markerDriver != null){
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    builder.include(markerLocation.getPosition());
                    builder.include(markerDriver.getPosition());
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
               // int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.moveCamera(cu);


            }else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(markerLocation.getPosition());
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                googleMap.animateCamera(cu);
               // googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));


            }

          //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromLatlong, 17.0f));

           /* final Circle circle = mMap.addCircle(new CircleOptions().center(fromLatlong)
                    .strokeColor(getResources().getColor(R.color.colorPrimary)).radius(300));

            ValueAnimator vAnimator = new ValueAnimator();
            vAnimator.setRepeatCount(ValueAnimator.INFINITE);
            vAnimator.setRepeatMode(ValueAnimator.RESTART);
            vAnimator.setIntValues(0, 4000);
            vAnimator.setDuration(1500);
            vAnimator.setEvaluator(new IntEvaluator());
            vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    // Log.e("", "" + animatedFraction);
                    circle.setRadius(animatedFraction * 4000);
                }
            });
            vAnimator.start();*/


        } else {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 5);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 12));

        }

        mMap.getUiSettings().setScrollGesturesEnabled(false);

    }

    private void cancelClick(View view) {
        rel_progress.setVisibility(View.VISIBLE);
        cancelRideRequest();


    }

    private void cancelRideRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("tripid", mTripId);
            jsonObject.put("typefor"    ,"user");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.CANCEL_RIDE_REQUEST, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonResponse) {
                            Log.e("cancelRideRequest", jsonResponse + "");
                            try {
                                int status = jsonResponse.getInt("status");
                                String message = jsonResponse.getString("message");
                                JSONObject jsonObject1 = jsonResponse.getJSONObject("result");
                                if (status == 1 && jsonObject1.getString("id").equalsIgnoreCase(mTripId)) {
                                    rel_progress.setVisibility(View.GONE);
                                  // startActivity(new Intent(getActivity(),HomeMenuActivity.class));

                                    ((HomeMenuActivity) getActivity()).homeCall();
                                   /* Fragment fragment = new HomeLocationSelectFragment();
                                    Bundle arg = new Bundle();
                                    arg.putString("status", "cancel_trip");
                                    fragment.setArguments(arg);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_content, fragment).addToBackStack(null).commit();
*/
                                } else {
                                    rel_progress.setVisibility(View.GONE);
                                    Log.e("changePaymentMethod", "failed");

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });


            VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    private void getTripStatus(){

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("trip_id", mTripId);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.APIURLPRODUCTION+"socketApi/get_trip_details", jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonResponse) {
                            Log.e("cancelRideRequest", jsonResponse + "");
                            try {
                                int status = jsonResponse.getInt("status");
                                String message = jsonResponse.getString("message");

                                if (status == 1) {

                                    JSONObject jsonObject1 = jsonResponse.getJSONObject("result");
                                    String vehicletype = jsonObject1.getString("vehicle_types");
                                    String lat = jsonObject1.getString("book_from_lat");
                                    String longi = jsonObject1.getString("book_from_long");
                                    vehicle_image = jsonObject1.getString("vehicle_marker_img");
                                    findNearByDriver(lat,longi,vehicletype);
                                    rel_progress.setVisibility(View.GONE);
                                    // startActivity(new Intent(getActivity(),HomeMenuActivity.class));
                                  //  ((HomeMenuActivity) getActivity()).homeCall();
                                   /* Fragment fragment = new HomeLocationSelectFragment();
                                    Bundle arg = new Bundle();
                                    arg.putString("status", "cancel_trip");
                                    fragment.setArguments(arg);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_content, fragment).addToBackStack(null).commit();
*/
                                } else {
                                    rel_progress.setVisibility(View.GONE);
                                    Log.e("changePaymentMethod", "failed");

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private void findNearByDriver(String lat, String longi, String vehicleId) {
       // GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);
        //clearMap();
        //if (vehicleId.length() != 0) {
        if (checkConnection()) {
            if (mBinding.pbHome.getVisibility() != View.VISIBLE) {
                mBinding.pbHome.setVisibility(View.VISIBLE);
            }
            try {
                if (SocketConnection.isConnected()) {
                    SocketConnection.attachSingleEventListener(Config.LISTENER_GET_DRIVER, getDriverListener);
                    try {




                        ListenersSocket.getDrivers(mContext, vehicleId, lat, longi, "1", "0", "0");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mContext != null) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.pbHome.setVisibility(View.GONE);
                            }
                        });
                    }
                    //clearDriverData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    Emitter.Listener getDriverListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("Get Driver List", (String) args[0]);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mBinding.pbHome.setVisibility(View.GONE);

                    if (countCheckData == 0){
                        mDriverResponse = (String) args[0];
                        Log.e("resDriver", mDriverResponse);

                        setDriverLatlong(mDriverResponse, countCheckData);

                    }


                    /*if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {

                        mBinding.rvhometrucks.setVisibility(View.VISIBLE);
                    }*/
                    //  mBinding.llSelectedImage.setVisibility(View.VISIBLE);
                    // mBinding.tvETA.setVisibility(View.VISIBLE);

                    //getDriverList(mDriverResponse);
                }
            });

        }
    };

    private void setDriverLatlong(String mres, int count){
        try {
            JSONObject jsonObject = new JSONObject(mres);

            JSONArray jsonArray = jsonObject.getJSONArray("result");
            JSONObject jsonObject1 = jsonArray.getJSONObject(countCheckData);
            driverLatlong = new LatLng(Double.valueOf(jsonObject1.getString("lat")), Double.valueOf(jsonObject1.getString("lang")));
            onMapReady(mMap);
            countCheckData++;
        }catch (JSONException e){
            e.printStackTrace();
        }

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                //do something
                try {

                    JSONObject jsonObject = new JSONObject(mres);

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    if (countCheckData < jsonArray.length()-1){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(countCheckData);
                        driverLatlong = new LatLng(Double.valueOf(jsonObject1.getString("lat")), Double.valueOf(jsonObject1.getString("lang")));
                        onMapReady(mMap);
                        countCheckData++;
                    }else {
                        countCheckData = 0;
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                handler.postDelayed(runnable, delay);
            }
        }, delay);







       /* timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //what you want to do
                try {


                    JSONObject jsonObject = new JSONObject(mres);
                    if (jsonObject.getJSONArray("result").length() - 1 > countCheckData){
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(countCheckData);
                        driverLatlong = new LatLng(Double.valueOf(jsonObject1.getString("lat")), Double.valueOf(jsonObject1.getString("lang")));
                        onMapReady(mMap);
                    }

                    countCheckData++;
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, 0, 10000);*/



    }

    @Override
    public void onPause() {
        countCheckData = 0;
        handler1.removeCallbacks(runnable);

        super.onPause();
    }
}



