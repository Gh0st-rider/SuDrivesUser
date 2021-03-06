package com.sudrives.sudrives.fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.api.Status;
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
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.SocketListnerMethods.ListenersSocket;
import com.sudrives.sudrives.activity.BookOutStationActivity;
import com.sudrives.sudrives.activity.ConfirmPickupActivity;
import com.sudrives.sudrives.activity.HomeMenuActivity;
import com.sudrives.sudrives.activity.NotificationActivity;
import com.sudrives.sudrives.activity.RentACarActivity;
import com.sudrives.sudrives.adapter.RecyclerViewAdapter;
import com.sudrives.sudrives.databinding.FragmentHomeBinding;
import com.sudrives.sudrives.model.TruckBean;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseFragment;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GetLocation;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.LatLngInterpolator;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.socket.emitter.Emitter;


public class HomeFragment1 extends BaseFragment implements OnMapReadyCallback, RecyclerViewAdapter.AdapterCallback {

    public static final int REQUEST_CODE_RENTAL_RETURN = 4;
    private static final int REQUEST_CODE_FROM_LOC = 1;
    private static final int REQUEST_CODE_TO_LOC = 2;
    private static final int REQUEST_CODE_CONFIRM_RETURN = 3;
    private static final int REQUEST_CODE_OUTSTATION_RETURN = 5;
    private static final int REQUEST_CODE_RENTAL_FROM_LOC = 6;
    private static final String TAG = "";
    double value = 1.0;
    /**
     * The formatted location address.
     */
    private boolean isSet = true, isSetLatLong = false;

    private String mDriverResponse = "", mStrSelectedCity = "", vehicle_city_id = "", mVehicleTypeId = "", mStrSelectedVehicleId = "",
            mStrSelectedImage = "", mToaddress = "", mStrSelectedVehicle = "", mETAmin = "", mStrEstimatedFare = "",
            mStrEstimatedFareSend = "", mFromLat = "0.0", mFromLong = "0.0", mFromaddress = "", mToLat = "0.0", mToLong = "0.0",
            mDistanceMatrixResponse = "";

    private String mDateStr = "", mBookingTypeStr = "", mBookingTypeId = "", mDriverDaily = "1", mDriverRental = "0", mDriverOutstation = "0";

    private double placePickerToLat = 0.0, placePickerToLong = 0.0, mFromLatCurrent = 0.0, mFromLongCurrent = 0.0;
     private int recyclerViewPos = 0; // private GPSTracker gps;
      private int newScrollState = 0;
    private int pos = 0;

    private long mLastClickTime = 0;
    private boolean onDropClick = false, onSetCurrentLoc = false;

    private GetLocation getLocation;
    private FragmentHomeBinding mBinding;
    private GoogleMap mMap;
    private Activity mContext;

    // Driver List
    private ArrayList<String> mArrayDriverETA = new ArrayList<>();
    private ArrayList<String> mArrayDriverStatus = new ArrayList<>();
    private ArrayList<Double> mArrayDriverLat = new ArrayList<>();
    private ArrayList<String> mArrayDriverID = new ArrayList<>();
    private ArrayList<Double> mArrayDriverLong = new ArrayList<>();
    private ArrayList<String> mArrayVehicleName, mArrayVehicleimage1, mArrayVehicleTypeId, mArraySelectedVehicle;
    private RecyclerViewAdapter mAdapter;

    private Map<String, Marker> driverMarkersMap;
    private TruckBean truckBean;
    private SnapHelper snapHelper;
    // Location progress
    private ProgressDialog mprogressBooking;
    private View rootView;
    private SessionPref mSessionPref;
    private MarkerOptions markerOptions;
    private ErrorLayout mErrorLayout;


    //rotate marker
    boolean isRotating = false;


    public HomeFragment1() {

    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment1 newInstance(String param1, String param2) {
        HomeFragment1 fragment = new HomeFragment1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        driverMarkersMap = new HashMap<>();
        getControls(rootView);
        requestVehichle(Config.DAILY);
    }

    @Override
    public void onPermissionsGranted(int remapquestCode) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home1, container, false);
        rootView = mBinding.getRoot();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.rlHomeMain);
        KeyboardUtil.hideSoftKeyboard(mContext);
    }

    private void getControls(View rootView) {
        mContext = getActivity();
        getLocation = new GetLocation(mContext);
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.rlHomeMain);
        getLocation.checkLocationPermission();
        mSessionPref = new SessionPref(mContext);

        ImageView iv_notification = (ImageView) getActivity().findViewById(R.id.iv_notification);
        iv_notification.setVisibility(View.VISIBLE);
        TextView iv_help = (TextView) getActivity().findViewById(R.id.iv_help);
        iv_help.setVisibility(View.GONE);
        //iv_help.setImageResource(R.drawable.notifications_black_24dp);
        iv_notification.setImageResource(R.drawable.notification_icon);
        // Create a new Places client instance.
        // PlacesClient placesClient = Places.createClient(mContext);

        //mBinding.rltrucks.setVisibility(View.GONE);
        locationStatus(true);
        mBookingTypeStr = Config.DAILY;
        markerOptions = new MarkerOptions();
        mErrorLayout = new ErrorLayout(mContext, rootView.findViewById(R.id.error_layout));
        mArrayVehicleName = new ArrayList<>();
        mArrayVehicleimage1 = new ArrayList<>();
        mArrayVehicleTypeId = new ArrayList<>();
        mArraySelectedVehicle = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                onMapReadyFunctionality(googleMap);// Rest of the stuff you need to do with the map
            }
        });


        mapFragment.getMapAsync(this);
        mBinding.btnSetLoc.setOnClickListener(this::setLocClick);
        mBinding.btnBookNow.setOnClickListener(this::bookNowClick);
        mBinding.btnBookLater.setOnClickListener(this::bookLaterClick);
        mBinding.tvDaily.setOnClickListener(this::tabDailyClick);
        mBinding.tvRental.setOnClickListener(this::tabRentalClick);
        mBinding.tvOutStation.setOnClickListener(this::tabOutStationClick);

        mBinding.imgCrossHome.setOnClickListener(this::crossClick);
        mBinding.etHomeFromAddress.setOnClickListener(this::homeFromAddressClick);
        mBinding.rentalPickupLayout.etRentalFromAddress.setOnClickListener(this::rentalFromAddressClick);
        mBinding.etHomeToAddress.setOnClickListener(this::homeToAddressClick);
        mBinding.btnContinueBooking.setOnClickListener(this::bookNowOutStationClick);

        mBinding.tvDaily.setActivated(true);
        mBinding.tvDaily.setBackgroundResource(R.drawable.shape_home_tab_button);
        mBinding.tvRental.setBackground(null);
        mBinding.tvOutStation.setBackground(null);


        FontLoader.setHelBold(mBinding.tvDaily, mBinding.btnBookNow,
                mBinding.btnContinueBooking, mBinding.etHomeFromAddress, mBinding.rentalPickupLayout.etRentalFromAddress);
        FontLoader.setHelRegular(mBinding.etHomeToAddress, mBinding.tvRental, mBinding.tvOutStation, mBinding.tvNoCabs);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvhometrucks.setLayoutManager(mLayoutManager);
        mBinding.rvhometrucks.setHasFixedSize(true);
        int padding = (getScreenWidth() / 5) * 2;

        mBinding.rvhometrucks.setOnDragListener(null);
        mBinding.rvhometrucks.setPadding(padding, 0, padding, 0);
        snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);

                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));

                if (newScrollState == 1) {
                    //  Log.e("Target Pos", "" + targetPosition);
                    getVehichleandUpdateUi(targetPosition);
                    recyclerViewPos = targetPosition;

                }
                return targetPosition;
            }


        };

        snapHelper.attachToRecyclerView(mBinding.rvhometrucks);
       // mAdapter = new RecyclerViewAdapter(mContext, mArrayVehicleName, mArrayVehicleimage1, this);
        mBinding.rvhometrucks.setAdapter(mAdapter);

         vehicleListScroll();
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent(mContext, NotificationActivity.class));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setLocClick(View view) {
        try {
            mToLat = String.valueOf(placePickerToLat);
            mToLong = String.valueOf(placePickerToLong);

            if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {

                mStrSelectedCity = getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong));
            }
            //System.out.println(mFromLatCurrent + "  :  " + mFromLongCurrent);
            if (mFromLatCurrent != 0.0 && mFromLongCurrent != 0.0) {
                onSetCurrentLoc = true;
                mFromLat = String.valueOf(mFromLatCurrent);
                mFromLong = String.valueOf(mFromLongCurrent);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFromLatCurrent, mFromLongCurrent), 12f));
                try {
                    if (checkConnection()) {
                        if (!mStrSelectedCity.equalsIgnoreCase(getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong)))) {
                            try {
                                ClearVehicleRelatedIds();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            clearVehicleData(mFromLat, mFromLong);
                        }
                    } else {
                        mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                FontLoader.setHelBold(mBinding.etHomeFromAddress, mBinding.rentalPickupLayout.etRentalFromAddress);
                FontLoader.setHelRegular(mBinding.etHomeToAddress);
                mBinding.llPointIcon.setBackground(getResources().getDrawable(R.drawable.pointer_from));
                if (mFromLatCurrent != 0.0 && mFromLongCurrent != 0.0) {
                    mFromaddress = GlobalUtil.getAddress(mContext, mFromLatCurrent, mFromLongCurrent);
                    mBinding.etHomeFromAddress.setText(mFromaddress);
                    mBinding.rentalPickupLayout.etRentalFromAddress.setText(mFromaddress);
                }
                if (placePickerToLat != 0.0 && placePickerToLong != 0.0) {
                    mToaddress = GlobalUtil.getAddress(mContext, placePickerToLat, placePickerToLong);
                    mBinding.etHomeToAddress.setText(mToaddress);
                }
                onDropClick = false;
                if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY) || mBookingTypeStr.equalsIgnoreCase(Config.OUTSTATION)) {
                    EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                } else {
                    if (mToaddress.length() != 0) {
                        if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                            mBinding.llEstimated.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vehicleListScroll() {
        clearMap();
        mBinding.rvhometrucks.setNestedScrollingEnabled(false);

        mBinding.rvhometrucks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                newScrollState = newState;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View centerView = snapHelper.findSnapView(((LinearLayoutManager) recyclerView.getLayoutManager()));

                    pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).getPosition(centerView);

                    getVehichleandUpdateUi(pos);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.e("I am scrolled", "I am scrolled");
            }
        });
    }

    /* From places  click
     */

    private void homeFromAddressClick(View view) {
        if (checkConnection()) {
            GlobalUtil.avoidDoubleClicks(mBinding.etHomeFromAddress);
            openAutocompleteActivity(REQUEST_CODE_FROM_LOC);
        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    /* From places rental click
     */
    private void rentalFromAddressClick(View view) {
        if (checkConnection()) {
            mBinding.rentalPickupLayout.etRentalFromAddress.setEnabled(false);
            openAutocompleteActivity(REQUEST_CODE_RENTAL_FROM_LOC);
        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    /* To location clear
     */
    private void crossClick(View view) {
        mBinding.llEstimated.setVisibility(View.GONE);
        clearToLocData();
    }

    private void homeToAddressClick(View view) {
        if (checkConnection()) {
            GlobalUtil.avoidDoubleClicks(mBinding.etHomeToAddress);
            if (!mFromLong.equals("0.0") && !mFromLat.equals("0.0")) {
                openAutocompleteActivity(REQUEST_CODE_TO_LOC);
            } else {
                Toast.makeText(mContext, getString(R.string.please_enter_pick_up_location), Toast.LENGTH_SHORT).show();
            }
        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
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

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KeyboardUtil.hideSoftKeyboard(mContext);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_FROM_LOC) {
            if (resultCode == getActivity().RESULT_OK) {
                //Get the user's selected place from the Intent.
                onDropClick = false;
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                // TODO call location based filter
                LatLng latLong = place.getLatLng();
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLong, 12f);
                mMap.moveCamera(cameraUpdate);

                if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {

                    mStrSelectedCity = getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong));
                }
                mFromLat = String.valueOf(latLong.latitude);
                mFromLong = String.valueOf(latLong.longitude);

                try {
                    if (mArrayDriverLat.size() != 0) {
                        mMap.clear();
                        for (int i = 0; i < mArrayDriverLat.size(); i++) {
                            // Setting the position for the marker
                            markerOptions.position(new LatLng(mArrayDriverLat.get(i), mArrayDriverLong.get(i)));
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_placeholder));
                              // Log.e("latlong1111", mArrayDriverLat + "" + mArrayDriverLong + "111111");
                            //  marker = mMap.addMarker(markerOptions);
                        }
                    }
                    mBinding.etHomeFromAddress.setText(GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude));
                    FontLoader.setHelBold(mBinding.etHomeFromAddress);
                    FontLoader.setHelRegular(mBinding.etHomeToAddress);
                    mFromaddress = GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude);
                    mBinding.llPointIcon.setBackground(getResources().getDrawable(R.drawable.pointer_from));
                    //drawCircle(new LatLng(latLong.latitude, latLong.longitude));
                    try {
                        if (checkConnection()) {
                            if (!mStrSelectedCity.equalsIgnoreCase(getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong)))) {
                                try {
                                    ClearVehicleRelatedIds();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                clearVehicleData(mFromLat, mFromLong);
                                //Log.e("latlong2222", mFromLat + mFromLong + "222222");
                            }
                        } else {
                            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                        }
                        if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY) || mBookingTypeStr.equalsIgnoreCase(Config.OUTSTATION)) {
                            EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                        } else {
                            if (mToaddress.length() != 0) {
                                if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                    mBinding.llEstimated.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if (requestCode == REQUEST_CODE_RENTAL_FROM_LOC) {
            if (resultCode == getActivity().RESULT_OK) {
                //Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                //TODO call location based filter
                LatLng latLong = place.getLatLng();
                mBinding.rentalPickupLayout.etRentalFromAddress.setEnabled(true);

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //ActivityCompat#requestPermissions
                    //here to request the missing permissions, and then overriding
                    //public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //int[] grantResults)
                    //to handle the case where the user grants the permission. See the documentation
                    //for ActivityCompat#requestPermissions for more details.
                    return;
                }

                //   mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLong, 12f);
                mMap.moveCamera(cameraUpdate);

                if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {

                    mStrSelectedCity = getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong));
                }
                mFromLat = String.valueOf(latLong.latitude);
                mFromLong = String.valueOf(latLong.longitude);

                try {
                    if (mArrayDriverLat.size() != 0) {
                        mMap.clear();

                        for (int i = 0; i < mArrayDriverLat.size(); i++) {
                            // Setting the position for the marker
                            markerOptions.position(new LatLng(mArrayDriverLat.get(i), mArrayDriverLong.get(i)));
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_placeholder));
                            //  marker = mMap.addMarker(markerOptions);

                        }
                    }
                    //mBinding.etHomeAddress.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
                    mBinding.rentalPickupLayout.etRentalFromAddress.setText(GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude));
                    mFromaddress = GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude);
                    FontLoader.setHelBold(mBinding.rentalPickupLayout.etRentalFromAddress);
                    FontLoader.setHelBold(mBinding.etHomeFromAddress);
                    FontLoader.setHelRegular(mBinding.etHomeToAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (checkConnection()) {
                        if (!mStrSelectedCity.equalsIgnoreCase(getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong)))) {
                            try {
                                ClearVehicleRelatedIds();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            clearVehicleData(mFromLat, mFromLong);
                            //Log.e("latlong333", mFromLat + mFromLong + "44444444");
                        }
                    } else {
                        mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                    }
                    if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY) || mBookingTypeStr.equalsIgnoreCase(Config.OUTSTATION)) {
                        EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                    } else {
                        if (mToaddress.length() != 0) {
                            if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                mBinding.llEstimated.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if (requestCode == REQUEST_CODE_TO_LOC) {
            if (resultCode == getActivity().RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(mContext, data);
                //call location based filter

                onDropClick = true;
                LatLng latLong;
                latLong = place.getLatLng();
                isSet = false;
                mBinding.imgCrossHome.setVisibility(View.VISIBLE);
                FontLoader.setHelBold(mBinding.etHomeToAddress);
                FontLoader.setHelRegular(mBinding.etHomeFromAddress);
                mToLat = String.valueOf(latLong.latitude);
                mToLong = String.valueOf(latLong.longitude);
                mToaddress = GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude);
                mBinding.etHomeToAddress.setText(mToaddress);
                mBinding.llPointIcon.setBackground(getResources().getDrawable(R.drawable.pointer_red));

                if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY) || mBookingTypeStr.equalsIgnoreCase(Config.OUTSTATION)) {
                    EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                } else {
                    if (mToaddress.length() != 0) {
                        if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                            mBinding.llEstimated.setVisibility(View.GONE);
                        }
                    }
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLong.latitude, latLong.longitude), 12f));
                // mMap.animateCamera(cameraUpdate);
                placePickerToLat = latLong.latitude;
                placePickerToLong = latLong.longitude;

            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
        if (requestCode == REQUEST_CODE_CONFIRM_RETURN) {
            if (resultCode == getActivity().RESULT_OK) {
                String strEditText = data.getStringExtra("clear");
                if (strEditText.equals("true")) {
                    ListenersSocket.requestBookingStatus(mContext);
                    clearToLocData();
                }
            }
        }
        if (requestCode == REQUEST_CODE_RENTAL_RETURN) {
            try {
                if (data != null) {
                    if (data.getStringExtra("clear").equalsIgnoreCase("true")) {
                        clearToLocData();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_CODE_OUTSTATION_RETURN) {
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    if (data != null) {
                        if (data.getStringExtra("clear").equalsIgnoreCase("true")) {
                            clearToLocData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tabDailyClick(View view) {
        GlobalUtil.avoidDoubleClicks(mBinding.tvDaily);
        mStrEstimatedFareSend = "";
        mBinding.tvDaily.setActivated(true);
        mBinding.tvRental.setActivated(false);
        mBinding.tvOutStation.setActivated(false);
        FontLoader.setHelBold(mBinding.tvDaily);
        FontLoader.setHelRegular(mBinding.tvRental, mBinding.tvOutStation);
        mBinding.tvDaily.setBackgroundResource(R.drawable.shape_home_tab_button);
        mBinding.tvRental.setBackground(null);
        mBinding.tvOutStation.setBackground(null);
        mBinding.lnrBookingDetailsLocation.setVisibility(View.VISIBLE);
        mBinding.llBottomButton.setVisibility(View.VISIBLE);
        mBinding.btnContinueBooking.setVisibility(View.GONE);
        mBinding.rlRentalPickup.setVisibility(View.GONE);
        mBookingTypeStr = Config.DAILY;

        setBookingType("1", "0", "0");
        requestVehichle(Config.DAILY);

        if (!mStrEstimatedFareSend.equalsIgnoreCase("")) {
            mBinding.llEstimated.setVisibility(View.VISIBLE);
        } else {

        }
    }

    private void tabRentalClick(View view) {
        GlobalUtil.avoidDoubleClicks(mBinding.tvRental);
        mBinding.tvRental.setActivated(true);
        mBinding.tvDaily.setActivated(false);
        mBinding.tvOutStation.setActivated(false);
        FontLoader.setHelBold(mBinding.tvRental, mBinding.etHomeFromAddress);
        FontLoader.setHelRegular(mBinding.tvDaily, mBinding.tvOutStation, mBinding.etHomeToAddress);
        mBinding.tvRental.setBackgroundResource(R.drawable.shape_home_tab_button);
        mBinding.tvDaily.setBackground(null);
        mBinding.tvOutStation.setBackground(null);
        mBinding.lnrBookingDetailsLocation.setVisibility(View.GONE);

        mStrSelectedVehicleId = "";
        mStrSelectedVehicle = "";
        mStrSelectedImage = "";
        vehicle_city_id = "";
        mVehicleTypeId = "";
        mBinding.llEstimated.setVisibility(View.GONE);
        clearToLocData();
        clearMap();
        mBinding.llBottomButton.setVisibility(View.VISIBLE);
        mBinding.btnContinueBooking.setVisibility(View.GONE);
        mBinding.rlRentalPickup.setVisibility(View.VISIBLE);
        mBookingTypeStr = Config.RENTAL;
        setCurrentPinPickUp();
        setBookingType("0", "1", "0");

        requestVehichle(Config.RENTAL);
        mBinding.tvTruckSelected.setText("");

    }

    private void tabOutStationClick(View view) {
        GlobalUtil.avoidDoubleClicks(mBinding.tvOutStation);
        mBinding.tvOutStation.setActivated(true);
        mBinding.tvRental.setActivated(false);
        mBinding.tvDaily.setActivated(false);
        FontLoader.setHelBold(mBinding.tvOutStation);
        FontLoader.setHelRegular(mBinding.tvRental, mBinding.tvDaily);
        mBinding.tvOutStation.setBackgroundResource(R.drawable.shape_home_tab_button);
        mBinding.tvRental.setBackground(null);
        mBinding.tvDaily.setBackground(null);
        mBinding.llBottomButton.setVisibility(View.GONE);
        mBinding.btnContinueBooking.setVisibility(View.VISIBLE);
        mBinding.llEstimated.setVisibility(View.GONE);

        mBinding.lnrBookingDetailsLocation.setVisibility(View.VISIBLE);

        mBinding.rlRentalPickup.setVisibility(View.GONE);
        mStrSelectedVehicleId = "";
        mStrSelectedVehicle = "";
        mStrSelectedImage = "";
        vehicle_city_id = "";
        mVehicleTypeId = "";
        mStrEstimatedFare = "";
        mStrEstimatedFareSend = "";

        setBookingType("0", "0", "1");
        mBookingTypeStr = Config.OUTSTATION;
        requestVehichle(Config.OUTSTATION);
        mBinding.tvTruckSelected.setText("");

    }


    private void setBookingType(String daily, String rental, String outstation) {
        mDriverDaily = daily;
        mDriverRental = rental;
        mDriverOutstation = outstation;
    }

    private void requestVehichle(String vehicleFor) {
        mBinding.tvTruckSelected.setVisibility(View.GONE);
//        mBinding.llSelectedImage.setVisibility(View.GONE);
        mBinding.tvETA.setVisibility(View.GONE);
       // mBinding.rvhometrucks.setVisibility(View.GONE);
       mBinding.rvhometrucks.smoothScrollToPosition(0);
        mAdapter.notifyDataSetChanged();

        try {
            if (checkConnection()) {
                clearVehicleData(String.valueOf(mFromLat), String.valueOf(mFromLong));
            } else {
                mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void bookNowClick(View view) {
        // To hide soft keyboard
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        switch (mBookingTypeStr) {
            case "daily":
                bookNowDailyClick();
                break;
            case "rental":
                bookNowRentalClick();
                break;
            case "outstation":
                break;

        }
    }

    private void bookNowDailyClick() {
        if (mFromaddress.length() == 0) {
            mErrorLayout.showAlert(getString(R.string.please_enter_pick_up_location), ErrorLayout.MsgType.Error, false);
            return;

        } else if (mToaddress.length() == 0) {
            mErrorLayout.showAlert(getString(R.string.please_enter_drop_off_location), ErrorLayout.MsgType.Error, false);
            return;
        } else if (mStrSelectedVehicleId.length() == 0) {
            if (mStrSelectedVehicle.equalsIgnoreCase("Auto")) {
                mErrorLayout.showAlert(getResources().getString(R.string.no_auto_available), ErrorLayout.MsgType.Error, false);

            } else {

                mErrorLayout.showAlert(getResources().getString(R.string.no_cab_available), ErrorLayout.MsgType.Error, false);
            }

            return;

        } else if (mArrayDriverStatus.size() == 0) {
            if (mStrSelectedVehicle.equalsIgnoreCase("Auto")) {
                mErrorLayout.showAlert(getResources().getString(R.string.no_auto_available), ErrorLayout.MsgType.Error, false);

            } else {

                mErrorLayout.showAlert(getResources().getString(R.string.no_cab_available), ErrorLayout.MsgType.Error, false);
            }

        } else {
            try {
                if (checkConnection()) {
                    if (mBinding.rltrucks.getVisibility() == View.GONE) {
                        clearVehicleData(String.valueOf(mFromLat), String.valueOf(mFromLong));

                    } else {
                        GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);

                        if (!mStrEstimatedFareSend.equalsIgnoreCase("") && !mStrEstimatedFareSend.equalsIgnoreCase("0.0")) {

                            Intent intent = new Intent(getActivity(), ConfirmPickupActivity.class);
                            intent.putExtra("mFromLat", mFromLat);
                            intent.putExtra("mFromLong", mFromLong);
                            intent.putExtra("mFromaddress", mFromaddress);
                            intent.putExtra("mToLat", mToLat);
                            intent.putExtra("mToLong", mToLong);
                            intent.putExtra("mToaddress", mToaddress);
                            intent.putExtra("mETA", mETAmin);
                            intent.putExtra("mVehicleType", mStrSelectedVehicle);
                            intent.putExtra("mVehicleTypeId", mStrSelectedVehicleId);
                            intent.putExtra("mEstimatedFare", mStrEstimatedFareSend);
                            intent.putExtra("mVehicleCityId", vehicle_city_id);
                            //  intent.putExtra("mShowCashOption", mCancelledTripsStatus);
                            startActivityForResult(intent, REQUEST_CODE_CONFIRM_RETURN);

                        } else {

                            mErrorLayout.showAlert(getString(R.string.fetching_estimated_fare), ErrorLayout.MsgType.Error, true);
                            EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                        }
                    }

                } else {
                    mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bookNowRentalClick() {

        if (mFromaddress.length() == 0) {
            mErrorLayout.showAlert(getString(R.string.please_enter_pick_up_location), ErrorLayout.MsgType.Error, false);
            return;

        } else if (mArrayDriverStatus.size() == 0) {
            mErrorLayout.showAlert(getResources().getString(R.string.no_cab_available), ErrorLayout.MsgType.Error, false);
            // return;

        } else {
            try {

                GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);
                Intent intent = new Intent(getActivity(), RentACarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mFromaddress", mFromaddress);
                bundle.putString("mFromaddressLat", mFromLat);
                bundle.putString("mFromaddressLong", mFromLong);
                bundle.putString("mToaddress", mToaddress);
                bundle.putString("mToaddressLat", mToLat);
                bundle.putString("mToaddressLong", mToLong);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_RENTAL_RETURN);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bookNowOutStationClick(View view) {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        switch (mBookingTypeStr) {
            case "outstation":

                if (mFromaddress.length() == 0) {
                    mErrorLayout.showAlert(getString(R.string.please_enter_pick_up_location), ErrorLayout.MsgType.Error, false);
                    return;

                } else if (mToaddress.length() == 0) {
                    mErrorLayout.showAlert(getString(R.string.please_enter_drop_off_location), ErrorLayout.MsgType.Error, false);
                    return;
                } else if (mArrayDriverStatus.size() == 0) {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_cab_available), ErrorLayout.MsgType.Error, false);
                    //return;

                } else {
                    try {
                        GlobalUtil.avoidDoubleClicks(mBinding.btnContinueBooking);
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(getActivity(), BookOutStationActivity.class);
                        bundle.putString("mFromaddress", mFromaddress);
                        bundle.putString("mFromaddressLat", mFromLat);
                        bundle.putString("mFromaddressLong", mFromLong);
                        bundle.putString("mToaddress", mToaddress);
                        bundle.putString("mToaddressLat", mToLat);
                        bundle.putString("mToaddressLong", mToLong);
                        bundle.putString("mVehicleCityId", vehicle_city_id);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_CODE_OUTSTATION_RETURN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                break;
        }
    }

    /*
          Book Later click
          */
    private void bookLaterClick(View view) {

        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.rlHomeMain);
        // to hide soft keyboard
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        switch (mBookingTypeStr) {
            case "daily":
                mBookingTypeId = "0";
                if (mFromaddress.length() == 0) {
                    mErrorLayout.showAlert(getString(R.string.please_enter_pick_up_location), ErrorLayout.MsgType.Error, false);
                    return;

                } else if (mToaddress.length() == 0) {
                    mErrorLayout.showAlert(getString(R.string.please_enter_drop_off_location), ErrorLayout.MsgType.Error, false);
                    return;
                } else if (mStrSelectedVehicleId.length() == 0) {
                    if (mStrSelectedVehicle.equalsIgnoreCase("Auto")) {
                        mErrorLayout.showAlert(getResources().getString(R.string.no_auto_available), ErrorLayout.MsgType.Error, false);
                    } else {
                        mErrorLayout.showAlert(getResources().getString(R.string.no_cab_available), ErrorLayout.MsgType.Error, false);
                    }
                    return;
                } else {
                    if (checkConnection()) {
                        if (mBinding.rltrucks.getVisibility() == View.GONE) {
                            clearVehicleData(String.valueOf(mFromLat), String.valueOf(mFromLong));

                        } else {
                            GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);
                            GlobalUtil.avoidDoubleClicks(mBinding.btnBookLater);

                            if (!mStrEstimatedFareSend.equalsIgnoreCase("") || !mStrEstimatedFareSend.equalsIgnoreCase("0.0")) {

                                selectDateClick();
                            } else {
                                Toast.makeText(mContext, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                            }
                        }

                    } else {
                        mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                    }
                }
                break;

            case "rental":
                mBookingTypeId = "1";
                if (mFromaddress.length() == 0) {
                    mErrorLayout.showAlert(getString(R.string.please_enter_pick_up_location), ErrorLayout.MsgType.Error, false);
                    return;
                } else if (mArrayDriverStatus.size() == 0) {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_cab_available), ErrorLayout.MsgType.Error, false);

                } else {
                    GlobalUtil.avoidDoubleClicks(mBinding.btnBookLater);
                    selectDateClick();
                }
                break;

        }

    }

    /*
      date click
      */
    private void selectDateClick() {
        // Log.e(TAG, "cliclkkkkk");
        Calendar myCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDateStr = dateFormatter.format(newDate.getTime());
                getTimePicker(mContext, mDateStr);
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        datePickerDialog.setCancelable(false);
        datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                }
            }
        });
    }

    public void dialogClickForAlert(boolean isSuccess, String msg) {
        int layoutPopup;
        int drawable;
        String title = "";
        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.ride_scheduled) + "!";
            layoutPopup = R.layout.success_dialog;
        } else {
            drawable = R.drawable.failed_24dp;
            title = "";
            layoutPopup = R.layout.failure_dialog;
        }
        String finalTitle = title;
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AppDialogs.singleButtonVersionDialog(mContext, layoutPopup, finalTitle, drawable, msg,
                            getString(R.string.ok),
                            new AppDialogs.SingleButoonCallback() {
                                @Override
                                public void singleButtonSuccess(String from) {
                                    clearToLocData();
                                }
                            }, true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        onMapReadyFunctionality(googleMap);
    }

    private void onMapReadyFunctionality(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.ola_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 12f));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);


                LatLng curentLocation = null;
                // Log.d("Camera position change" + "", cameraPosition + "");

                curentLocation = cameraPosition.target;
                if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {

                    mStrSelectedCity = getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong));
                }
                try {
                    if (onDropClick) {

                        mToLat = String.valueOf(curentLocation.latitude);
                        mToLong = String.valueOf(curentLocation.longitude);
                        mToaddress = GlobalUtil.getAddress(mContext, curentLocation.latitude, curentLocation.longitude);
                        mBinding.etHomeToAddress.setText(mToaddress);

                        if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                            if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                            }
                        }

                    } else if (onSetCurrentLoc) {
                        clearMap();
                        onSetCurrentLoc = false;
                        mToLat = String.valueOf(placePickerToLat);
                        mToLong = String.valueOf(placePickerToLong);
                        mToaddress = GlobalUtil.getAddress(mContext, placePickerToLat, placePickerToLong);
                        mBinding.etHomeToAddress.setText(mToaddress);

                    } else {
                        clearMap();
                        mFromLat = String.valueOf(curentLocation.latitude);
                        mFromLong = String.valueOf(curentLocation.longitude);

                    }
                    if (mArrayDriverLat.size() != 0) {

                        isSet = false;
                        for (int i = 0; i < mArrayDriverLat.size(); i++) {
                            //   Log.e("Current Loc", "loc");
                            // Setting the position for the marker
                            final int currentPos = i;
                            try {
                                if (truckBean.getResult().size() != 0) {
                                    if (truckBean.getResult().get(pos).getVehicleMarkerImg().length() != 0) {
                                        Glide.with(mContext).asBitmap()
                                                .load(truckBean.getResult().get(pos).getVehicleMarkerImg().trim())
                                                .override(50, 50)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(new SimpleTarget<Bitmap>(100, 100) {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        try {
                                                            if (mArrayDriverLat.size() != 0) {
                                                                Runnable helloRunnable = new Runnable() {
                                                                    public void run() {
                                                                        Log.e("driverlat", mArrayDriverLong.get(currentPos).toString());
                                                                        markerOptions.position(new LatLng(mArrayDriverLat.get(currentPos), mArrayDriverLong.get(currentPos)));
                                                                    }
                                                                };

                                                                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                                                                executor.scheduleAtFixedRate(helloRunnable, 0, 10, TimeUnit.SECONDS);
                                                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                });
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // Log.e("map  id: =======>", mStrSelectedVehicleId + "   " + mFromLat + "   " + mFromLong);
                    if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {
                        Handler mhandler = new Handler();
                        mhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //  Log.e("lat long =======>", mFromLat + " " + mFromLong + " " + mStrSelectedVehicleId);
                                if (!onDropClick) {
                                    if (checkConnection()) {

                                        try {
                                            if (!mStrSelectedCity.equalsIgnoreCase(getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong)))) {
                                                ClearVehicleRelatedIds();
                                                clearVehicleData(String.valueOf(mFromLat), String.valueOf(mFromLong));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            clearVehicleData(String.valueOf(mFromLat), String.valueOf(mFromLong));
                                        }
                                    } else {
                                        mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                                    }
                                } else {
                                }

                            }
                        }, 500);


                    } else {
                        mBinding.btnBookNow.setEnabled(true);
                    }
                    if (isSetLatLong) {

                        if (onDropClick) {

                            if (onSetCurrentLoc) {
                                clearMap();
                                onSetCurrentLoc = false;
                                mToLat = String.valueOf(placePickerToLat);
                                mToLong = String.valueOf(placePickerToLong);
                                mToaddress = GlobalUtil.getAddress(mContext, placePickerToLat, placePickerToLong);
                                mBinding.etHomeToAddress.setText(mToaddress);

                            } else {
                                mToLat = String.valueOf(curentLocation.latitude);
                                mToLong = String.valueOf(curentLocation.longitude);
                                mToaddress = GlobalUtil.getAddress(mContext, curentLocation.latitude, curentLocation.longitude);
                                mBinding.etHomeToAddress.setText(mToaddress);
                            }
                            if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                                if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                    EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                }
                            }
                        } else {
                            if (onSetCurrentLoc) {
                                clearMap();
                                onSetCurrentLoc = false;
                                mToLat = String.valueOf(placePickerToLat);
                                mToLong = String.valueOf(placePickerToLong);
                                mToaddress = GlobalUtil.getAddress(mContext, placePickerToLat, placePickerToLong);
                                mBinding.etHomeToAddress.setText(mToaddress);
                                if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                                    if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                        EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                    }
                                }
                            } else {
                                mFromLat = String.valueOf(curentLocation.latitude);
                                mFromLong = String.valueOf(curentLocation.longitude);
                                clearMap();
                                if (checkConnection()) {
                                    clearVehicleData(mFromLat, mFromLong);

                                } else {
                                    mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                                }
                                mFromaddress = GlobalUtil.getAddress(mContext, curentLocation.latitude, curentLocation.longitude);
                                mBinding.etHomeFromAddress.setText(mFromaddress);
                            }
                        }
                        mBinding.rentalPickupLayout.etRentalFromAddress.setText(mFromaddress);
                    } else {
                        isSetLatLong = true;
                    }

                } catch (Exception e) {
                    mBinding.btnBookNow.setEnabled(true);
                    e.printStackTrace();
                }

            }

        });
        mMap.getUiSettings().setScrollGesturesEnabled(true);

    }

    private void locationStatus(boolean showLoader) {
        if (showLoader) {
            mBinding.upperLayout.setVisibility(View.VISIBLE);
            mBinding.llBottomButton.setVisibility(View.GONE);
            mBinding.rltrucks.setVisibility(View.GONE);
            //  mBinding.rlbottom.setVisibility(View.GONE);

        } else {

            try {
                if (SocketConnection.isConnected()) {
                    if (mBookingTypeStr.equalsIgnoreCase(Config.OUTSTATION)) {
                        mBinding.llBottomButton.setVisibility(View.GONE);
                        mBinding.btnContinueBooking.setVisibility(View.VISIBLE);

                    } else if (mBookingTypeStr.equalsIgnoreCase("self_drive")) {
                        mBinding.llBottomButton.setVisibility(View.GONE);
                        mBinding.btnContinueBooking.setVisibility(View.VISIBLE);
                    } else if (mBookingTypeStr.equalsIgnoreCase("carpool")) {
                        mBinding.llBottomButton.setVisibility(View.GONE);
                        mBinding.btnContinueBooking.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.llBottomButton.setVisibility(View.VISIBLE);
                    }
                    mBinding.upperLayout.setVisibility(View.GONE);
                    // mBinding.rlbottom.setVisibility(View.VISIBLE);


                } else {
                    mBinding.llBottomButton.setVisibility(View.GONE);
                    mBinding.btnContinueBooking.setVisibility(View.GONE);
                    mBinding.upperLayout.setVisibility(View.GONE);
                   // mBinding.rltrucks.setVisibility(View.GONE);
                    // mBinding.rlbottom.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.rlHomeMain);
        KeyboardUtil.hideKeyboard(mContext);
        mSessionPref = new SessionPref(mContext);
        mBinding.etHomeFromAddress.setEnabled(true);
        mBinding.rentalPickupLayout.etRentalFromAddress.setEnabled(true);
        mBinding.etHomeToAddress.setEnabled(true);
        mBinding.btnBookNow.setEnabled(true);
        mBinding.pbHome.setVisibility(View.GONE);

        if (SessionPref.getDataFromPrefBooking(mContext, "dropLocCheck") != null && !SessionPref.getDataFromPrefBooking(mContext, "dropLocCheck").equals("")) {
            if (SessionPref.getDataFromPrefBooking(mContext, "dropLocCheck").equals("true")) {
                clearToLocData();
                SessionPref.saveDataIntoSharedPrefBooking(mContext, "dropLocCheck", "false");

            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }
//data
    public void setVehicleData(String response, String vehicle_id, String vehicle_name, String image, String vehicle_payload, String
            vehicle_size, String fare_charges, String surcharge, String mvehicle_city_id, String vehicle_type_id, String status) {

      //  Log.e("===>Res_vehicle", response + "11111111");
        mArrayVehicleName.clear();
        mArrayVehicleimage1.clear();
        mArrayVehicleTypeId.clear();
        mArraySelectedVehicle.clear();
       // mBinding.rltrucks.setVisibility(View.GONE);

        clearMap();
        if (response != null) {
            BaseModel mBaseModel = new BaseModel(mContext);
            if (mBaseModel.isParse(response)) {
                Gson gson = new Gson();
                Log.e("loggg", "aaaaaa mTruckList: " + response);
                truckBean = gson.fromJson(response, TruckBean.class);

                int maxLogSize = 1000;
                for (int i = 0; i <= response.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > response.length() ? response.length() : end;
                    Log.e(TAG, response.substring(start, end));

                }

                mBinding.rltrucks.setVisibility(View.VISIBLE);
                try {
                    if (mBaseModel.getResultArray().length() != 0) {

                        for (int i = 0; i < truckBean.getResult().size(); i++) {

                            try {
                                mArrayVehicleName.add(truckBean.getResult().get(i).getVehicleName());
                                //isClicked.add(false);
                                mArrayVehicleimage1.add(truckBean.getResult().get(i).getVehicleImg());
                                mArrayVehicleTypeId.add(truckBean.getResult().get(i).getVehicle_type_id());
                                mArraySelectedVehicle.add(truckBean.getResult().get(i).getVehicleSelImg());
                                if (mStrSelectedVehicleId.length() != 0 && mStrSelectedVehicle.length() != 0) {
                                    if (mArraySelectedVehicle.get(i).equalsIgnoreCase(mStrSelectedVehicleId)) {
                                        mStrSelectedImage = truckBean.getResult().get(pos).getVehicleSelImg();
                                        vehicle_city_id = truckBean.getResult().get(pos).getVehicle_city_id();
                                        mVehicleTypeId = truckBean.getResult().get(pos).getVehicle_type_id();
                                        mStrSelectedVehicleId = truckBean.getResult().get(pos).getId();
                                        mStrSelectedVehicle = truckBean.getResult().get(pos).getVehicleName();

                                    }
                                } else {
                                    switch (mBookingTypeStr) {
                                        case "daily":

                                            mStrSelectedVehicleId = truckBean.getResult().get(0).getId();
                                            mStrSelectedVehicle = truckBean.getResult().get(0).getVehicleName();
                                            mStrSelectedImage = truckBean.getResult().get(0).getVehicleSelImg();
                                            vehicle_city_id = truckBean.getResult().get(0).getVehicle_city_id();
                                            mVehicleTypeId = truckBean.getResult().get(0).getVehicle_type_id();

                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Log.e("vehicle name", mStrSelectedVehicle + "      " + mStrSelectedVehicleId);
                try {

                    switch (mBookingTypeStr) {
                        case "daily":
                            if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                                if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                    EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                }
                            }

//                              Glide.with(mContext).load(mStrSelectedImage).into(mBinding.ivTruckSelected);
                            break;
                        case "rental":
                            // Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);

                            break;
                        case "outstation":
                           // Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);
                            break;
                    }
                    mBinding.tvTruckSelected.setVisibility(View.GONE);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();

                if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {
                    if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {
                        findNearByDriver(mFromLat, mFromLong, mStrSelectedVehicleId);

                    } else {

                        findNearByDriver(mFromLat, mFromLong, mStrSelectedVehicleId);

                    }
                }
            } else {
                setVehicleVisibility(false);
            }
        } else {
            setVehicleVisibility(false);
        }
    }

    private void getVehichleandUpdateUi(int pos) {
        try {
            if (truckBean.getResult().size() != 0) {
                mStrSelectedImage = truckBean.getResult().get(pos).getVehicleSelImg().trim();
                vehicle_city_id = truckBean.getResult().get(pos).getVehicle_city_id();
                mVehicleTypeId = truckBean.getResult().get(pos).getVehicle_type_id();
                mStrSelectedVehicleId = truckBean.getResult().get(pos).getId().trim();
                mStrSelectedVehicle = truckBean.getResult().get(pos).getVehicleName().trim();

                switch (mBookingTypeStr) {
                    case "daily":
                      //   Glide.with(mContext).load(mStrSelectedImage).into(mBinding.ivTruckSelected);
                        break;
                    case "rental":
                         //  Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);

                        break;
                    case "outstation":
                       //  Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);
                        break;
                }
                mBinding.tvTruckSelected.setText(truckBean.getResult().get(pos).getVehicleName().trim());
                setCurrentPinPickUp();
              /*  if (mToaddress != null) {

                    if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {
                        EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);     //  to show estimated fare
                    } else {
                        if (mToaddress.length() != 0 && mArrayDriverID.size() != 0) {
                            if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                mBinding.llEstimated.setVisibility(View.GONE);
                            }
                        }
                    }
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDriverAngle(String driver_id, String angle, Double lat, Double longi) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!angle.equalsIgnoreCase("0")) {


                            if (driverMarkersMap.containsKey(driver_id)) {

                                //  Log.e("driver angle 2    ", driver_id  +"  :   "+angle);

                                Marker marker = driverMarkersMap.get(driver_id);

                                if (marker != null) {
                                    final LatLng startPosition = marker.getPosition();
                                    final LatLng finalPosition = new LatLng(lat, longi);

                                    final long start = SystemClock.uptimeMillis();
                                    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                                    final float durationInMs = 3000;
                                    final boolean hideMarker = false;
                                    final Handler handler = new Handler();
                                    handler.post(new Runnable() {
                                        long elapsed;
                                        float t;
                                        float v;

                                        @Override
                                        public void run() {

                                            Log.e("Get driver_id 2      : ", driver_id);

                                            // Calculate progress using interpolator
                                            elapsed = SystemClock.uptimeMillis() - start;
                                            t = elapsed / durationInMs;
                                            v = interpolator.getInterpolation(t);

                                            LatLng currentPosition = new LatLng(
                                                    startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                                                    startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                                            marker.setPosition(currentPosition);
                                            marker.setFlat(true);
                                            marker.setRotation(Float.valueOf(angle));

                                            // Repeat till progress is complete.
                                            if (t < 1) {
                                                // Post again 12ms later.
                                                handler.postDelayed(this, 12);
                                            } else {
                                                if (hideMarker) {
                                                    marker.setVisible(false);
                                                } else {
                                                    marker.setVisible(true);
                                                }
                                            }
                                            Log.e("driveridddd", driver_id + "2222222222");
                                            Log.e("driveridddd", lat + "2222222222" + longi);
                                            //animateMarkerNew(driver_id,marker);
                                        }
                                    });
                                    driverMarkersMap.put(driver_id, marker);
                                }
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setCurrentPinPickUp() {
        onDropClick = false;
        mBinding.llPointIcon.setBackground(getResources().getDrawable(R.drawable.pointer_from));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(mFromLat), Double.valueOf(mFromLong)), 12f));

        mBinding.etHomeFromAddress.setText(GlobalUtil.getAddress(mContext, Double.valueOf(mFromLat), Double.valueOf(mFromLong)));
        mBinding.rentalPickupLayout.etRentalFromAddress.setText(GlobalUtil.getAddress(mContext, Double.valueOf(mFromLat), Double.valueOf(mFromLong)));
        FontLoader.setHelBold(mBinding.etHomeFromAddress, mBinding.rentalPickupLayout.etRentalFromAddress);
        FontLoader.setHelRegular(mBinding.etHomeToAddress);

    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        return width;
    }

    /* *
    Vehicle Items on click
    */

    @Override
    public void onClick(int pos, View view) {
        try {

           // LinearLayout llview = view.findViewById(R.id.llView);

            //llview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.circle_yellow_filled_boarder));

            //TruckBean.Result resulData = truckBean.getResult().get(pos);
          //  resulData.setClicked(true);

         /*   for(int i=0;i<mArrayVehicleimage1.size();i++){
                if (i==pos){

                    isClicked.add(true);
                    resulData.setClicked(true);

                }else {
                    isClicked.add(false);
                    resulData.setClicked(false);
                  //  llview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.gradient_circle));
                }
            }*/


        /*    for(int j=0;j<truckBean.getResult().size();j++){

                if (truckBean.getResult().get(j).isClicked()){

                    llview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.circle_yellow_filled_boarder));

                }else {

                    llview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.gradient_circle));
                }

            }*/



            getVehichleandUpdateUi(pos);
            Log.e("posisionnnnnnn",pos+"1111");
             recyclerViewPos = pos;
            mBinding.rvhometrucks.smoothScrollToPosition(recyclerViewPos);

        } catch (Exception e) {
            e.printStackTrace();

             recyclerViewPos = 0;
            if (truckBean != null) {
                mBinding.rvhometrucks.smoothScrollToPosition(0);
                mStrSelectedVehicleId = truckBean.getResult().get(0).getId();
                mStrSelectedVehicle = truckBean.getResult().get(0).getVehicleName();
                mStrSelectedImage = truckBean.getResult().get(0).getVehicleSelImg();
                vehicle_city_id = truckBean.getResult().get(0).getVehicle_city_id();
                mVehicleTypeId = truckBean.getResult().get(0).getVehicle_type_id();

                mAdapter.notifyDataSetChanged();


                if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {
                    if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {
                        findNearByDriver(mFromLat, mFromLong, mStrSelectedVehicleId);

                    } else {

                        findNearByDriver(mFromLat, mFromLong, mStrSelectedVehicleId);

                    }
                }
            }
        }

    }

    private void setVehicleVisibility(boolean flag) {
        if (!flag) {
            try {
                mBinding.llBookMain.setVisibility(View.VISIBLE);
                mBinding.rltruckitems.setVisibility(View.GONE);
                // mBinding.rltrucks.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("errorsocket", e + "");
                e.printStackTrace();
            }
        }

    }

    public void onLocationUpdate(Location location) {
        // Log.e("Lat and Lng", location.getLatitude() + "   " + location.getLongitude());
        locationStatus(false);
        mFromLatCurrent = location.getLatitude();
        mFromLongCurrent = location.getLongitude();
        if (isSet) {
            try {
                mBinding.etHomeFromAddress.setText(GlobalUtil.getAddress(mContext, location.getLatitude(), location.getLongitude()));
                mBinding.rentalPickupLayout.etRentalFromAddress.setText(GlobalUtil.getAddress(mContext, location.getLatitude(), location.getLongitude()));
                try {
                    if (location != null) {
                        mFromaddress = GlobalUtil.getAddress(mContext, location.getLatitude(), location.getLongitude());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                isSet = false;
                try {
                    if (latLng != null) {
                        if (mMap != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GetLocation.PERMISSION_REQUEST_CODE:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        getLocation.checkLocationPermission();
                        //  mMap.setMyLocationEnabled(true);

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void onLocationEnabled() {
        getLocation.checkLocationPermission();
    }


    private void getTimePicker(final Context context, String mDateSelectedStr) {
        Calendar c = Calendar.getInstance();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timepickerdialog);
        dialog.show();
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.simpleTimePicker);
        timePicker.setIs24HourView(false);

        Date date = new Date();   // get current  date
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String mDateCurrentStr = dateFormatter.format(date.getTime());

        if (mDateSelectedStr.equals(mDateCurrentStr)) {
            timePicker.setCurrentHour((c.get(Calendar.HOUR_OF_DAY)) + 4);

        } else {
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        }
        TextView buttonOk = (TextView) dialog.findViewById(R.id.buttonOk);
        TextView buttonCancel = (TextView) dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar cSelected = Calendar.getInstance();    // selected time
                    cSelected.set(Calendar.HOUR_OF_DAY, (timePicker.getCurrentHour()));
                    cSelected.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                    Calendar cValid = Calendar.getInstance();     // current  time
                    cValid.set(Calendar.HOUR_OF_DAY, (cValid.get(Calendar.HOUR_OF_DAY) + 4));   // current  time and add 4 hours on that
                    cValid.set(Calendar.MINUTE, (cValid.get(Calendar.MINUTE)));

                    String myFormatToSendTime = "H:mm:s";   // formate to post time on server
                    SimpleDateFormat sdfTimeSend = new SimpleDateFormat(myFormatToSendTime);

                    if (mDateSelectedStr.equals(mDateCurrentStr)) {    // compare selected date with current date
                        if (cValid.getTimeInMillis() <= cSelected.getTimeInMillis()) { // compare selected "time" and "current time + 4 hrs"
                            // sdfTimeSend.format(cSelected.getTimeInMillis());

                            //Toast.makeText(context, "valid" + sdf.format(cSelected.getTimeInMillis()), Toast.LENGTH_SHORT).show();
                            mprogressBooking = new ProgressDialog(mContext);
                            mprogressBooking.setMessage(getString(R.string.please_wait));
                            mprogressBooking.setCancelable(false);
                            if (mBookingTypeId.equalsIgnoreCase("0")) {
                                createBooking("", "", "0", mDateStr + "    " + sdfTimeSend.format(cSelected.getTimeInMillis()), mBookingTypeId);
                                dialog.dismiss();

                            } else if (mBookingTypeId.equalsIgnoreCase("1")) {
                                dialog.dismiss();
                                try {
                                    callRentACar(mFromaddress, mFromLat, mFromLong, mToaddress, mToLat, mToLong, mDateStr, sdfTimeSend.format(cSelected.getTimeInMillis()));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(context, getString(R.string.booking_cant_be_done), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mprogressBooking = new ProgressDialog(mContext);
                        mprogressBooking.setMessage(getString(R.string.please_wait));
                        mprogressBooking.setCancelable(false);
                        // Toast.makeText(context, "valid date" + sdf.format(cSelected.getTimeInMillis()), Toast.LENGTH_SHORT).show();
                        if (mBookingTypeId.equalsIgnoreCase("0")) {
                            createBooking("", "", "0", mDateStr + "    " + sdfTimeSend.format(cSelected.getTimeInMillis()), mBookingTypeId);
                            dialog.dismiss();

                        } else if (mBookingTypeId.equalsIgnoreCase("1")) {
                            dialog.dismiss();
                            try {
                                callRentACar(mFromaddress, mFromLat, mFromLong, mToaddress, mToLat, mToLong, mDateStr, sdfTimeSend.format(cSelected.getTimeInMillis()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callRentACar(String mFromaddress, String mFromLat, String mFromLong, String mToaddress, String mToLat, String mToLong, String mDateStr, String mtimeStr) {
        try {
            Intent intent = new Intent(getActivity(), RentACarActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("mFromaddress", mFromaddress);
            bundle.putString("mFromaddressLat", mFromLat);
            bundle.putString("mFromaddressLong", mFromLong);
            bundle.putString("mToaddress", mToaddress);
            bundle.putString("mToaddressLat", mToLat);
            bundle.putString("mToaddressLong", mToLong);
            bundle.putString("datetime", mDateStr + "    " + mtimeStr);

            intent.putExtras(bundle);

            startActivityForResult(intent, REQUEST_CODE_RENTAL_RETURN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCityName(double lat, double longi) {
        String cityName = "";
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;

            if (geocoder != null) {
                addresses = geocoder.getFromLocation(lat, longi, 1);
                cityName = addresses.get(0).getLocality();
                if (cityName == null) {
                    try {
                        cityName = addresses.get(0).getFeatureName();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

    /*
     * *
     Clear already saved data
     */
    private void clearDriverData() {
        mArrayDriverETA.clear();
        if (mArrayDriverStatus != null) {
            mArrayDriverStatus.clear();
        }
        mArrayDriverLat.clear();
        mArrayDriverLong.clear();
        mArrayDriverID.clear();
        clearMap();

    }

    /* *
        Clear already saved vehicle data
        */
    private void clearVehicleData(String mFromLat, String mFromLong) {
        clearMap();
        if (mArrayDriverStatus != null) {
            mArrayDriverStatus.clear();
        }
        try {
            ((HomeMenuActivity) mContext).requestVehichleList(mBookingTypeStr, mFromLat, mFromLong);
            ((HomeMenuActivity) mContext).responseVehichleList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ClearVehicleRelatedIds() {
        mVehicleTypeId = "";
        vehicle_city_id = "";
        mStrSelectedVehicleId = "";
        mStrSelectedVehicle = "";
        mStrSelectedImage = "";
        mStrEstimatedFare = "";
        mStrEstimatedFareSend = "";
        mBinding.llEstimated.setVisibility(View.GONE);
        mBinding.rvhometrucks.smoothScrollToPosition(0);

    }

    private void clearToLocData() {
        mBinding.etHomeToAddress.setText("");
        mToaddress = "";
        mToLat = "0.0";
        mToLong = "0.0";
        mBinding.imgCrossHome.setVisibility(View.GONE);
        if (mToaddress.length() != 0 && mArrayDriverID.size() != 0) {
            if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                mBinding.llEstimated.setVisibility(View.GONE);
            }
        }
        setCurrentPinPickUp();
        onDropClick = false;
        onSetCurrentLoc = false;
    }

    private void clearMap() {
        try {
            if (mMap != null) {
                mMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ************************ Socket ************************************
    // Socket Emit
    Emitter.Listener getCreateBooking = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Log.e("Create Booking", (String) args[0]);
            String mConfirmBookingResponse = (String) args[0];
            getBookingResponse(mConfirmBookingResponse);

        }
    };
    Emitter.Listener getDriverListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("Get Driver List", (String) args[0]);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.pbHome.setVisibility(View.GONE);
                    mDriverResponse = (String) args[0];
                    if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {

                        mBinding.rvhometrucks.setVisibility(View.VISIBLE);
                    }
                   // mBinding.llSelectedImage.setVisibility(View.VISIBLE);
                    mBinding.tvETA.setVisibility(View.VISIBLE);

                    getDriverList(mDriverResponse);
                }
            });

        }
    };
    Emitter.Listener getDistanceMatrixService = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("getDistanceMatrix", (String) args[0]);

            try {
                mDistanceMatrixResponse = (String) args[0];
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseModel mBaseModel = new BaseModel(mContext);
                        if (mDistanceMatrixResponse != null) {
                            if (mBaseModel.isParse(mDistanceMatrixResponse)) {
                                try {
                                    if (mBaseModel.getResultObject().length() != 0) {
                                        try {
                                            if (mToaddress.length() != 0) {
                                                if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                                    if (!mBaseModel.getResultObject().getString("fare_of_distance").equalsIgnoreCase("0.00")) {
                                                        mStrEstimatedFare = getString(R.string.estimated_fare) + ": <font color=#f5220f>\u20B9 " + mBaseModel.getResultObject().getString("fare_of_distance") + "</font>";
                                                        //Log.e("estimated fare",mBaseModel.getResultObject().getString("fare_of_distance"));
                                                        mStrEstimatedFareSend = mBaseModel.getResultObject().getString("fare_of_distance");
                                                        if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {
                                                            mBinding.llEstimated.setVisibility(View.VISIBLE);
                                                        }
                                                        mBinding.tvEstimatefare.setText(Html.fromHtml(mStrEstimatedFare));
                                                        mBinding.tvEstimatefare.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                                                        Log.e("timeeeeeeee",Html.fromHtml(getString(R.string.ett) + ": " + mBaseModel.getResultObject().getString("eta"))+"");
                                                        mBinding.tvEstimateTimeTravel.setText(Html.fromHtml(getString(R.string.ett) + ": " + mBaseModel.getResultObject().getString("eta")));

                                                    } else {
                                                        mErrorLayout.showAlert(getString(R.string.fetching_estimated_fare), ErrorLayout.MsgType.Warning, true);
                                                    }
                                                } else {
                                                    mBinding.llEstimated.setVisibility(View.GONE);
                                                }
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (mToaddress.length() != 0 && mArrayDriverID.size() != 0) {
                                    if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                        mBinding.llEstimated.setVisibility(View.GONE);
                                    }
                                }
                            }

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void createBooking(String paymentTransactionId, String paymentResponse, String onlinePayment, String dateTime, String bookingType) {
        String couponCode = "";
        if (SocketConnection.isConnected()) {
            //Log.e("I am connected", "I am connected");
            mprogressBooking.setCancelable(false);
            mprogressBooking.show();
            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_CREATE_BOOKING, getCreateBooking);
            JSONObject jsonObject = new JSONObject();
            try {
                final ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = clipboardManager.getPrimaryClip();
                // Get item count.
                int itemCount = clipData.getItemCount();
                if (itemCount > 0) {
                    // Get source text.
                    ClipData.Item item = clipData.getItemAt(0);
                    couponCode = item.getText().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                jsonObject.put("userid", mSessionPref.user_userid);
                jsonObject.put("token", mSessionPref.token);
                jsonObject.put("language", Config.SELECTED_LANG);
                jsonObject.put("booking_fee", "");
                jsonObject.put("book_reciever_name", "");
                jsonObject.put("book_reciever_mobile", "");
                jsonObject.put("vehicle_types", mStrSelectedVehicleId);
                jsonObject.put("book_from_address", mFromaddress);
                jsonObject.put("book_from_lat", mFromLat);
                jsonObject.put("book_from_long", mFromLong);
                jsonObject.put("book_coupon", couponCode);
                jsonObject.put("is_online_payment_accept", onlinePayment);
                jsonObject.put("payment_responce_data", paymentResponse);
                jsonObject.put("payment_id", paymentTransactionId);
                jsonObject.put("book_later_date_time", dateTime);
                jsonObject.put("booking_type", "1");
                jsonObject.put("type_of_booking", bookingType);
                jsonObject.put("vehicle_city_id", vehicle_city_id);

                if (bookingType.equalsIgnoreCase("0")) {
                    jsonObject.put("book_to_address", mToaddress);
                    jsonObject.put("book_to_lat", mToLat);
                    jsonObject.put("book_to_long", mToLong);

                } else {

                }
                //Log.d("booking", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                mprogressBooking.dismiss();
            }

            SocketConnection.emitToServer(Config.EMIT_GET_CREATE_BOOKING, jsonObject);
        } else {
            mErrorLayout.showAlert(getString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, false);
            // Log.e("Create Booking=======>", "Error");
        }
    }

    private void getBookingResponse(String response) {
        BaseModel mBaseModel = new BaseModel(mContext);
        mprogressBooking.dismiss();
        if (response != null) {
            if (mBaseModel.isParse(response)) {
                try {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GlobalUtil.clearClipBoard(mContext);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                dialogClickForAlert(true, mBaseModel.Message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });
            }
        }
    }
    /*
     * To show Estimated fare
     * */

    private void EmitDistanceMatrixService(String vehicleCityId, String mStrVehicleTypeId) {
        GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);

        try {
            mBinding.llEstimated.setVisibility(View.GONE);
            mStrEstimatedFare = "";
            mStrEstimatedFareSend = "";

            if (SocketConnection.isConnected()) {
                if (mToaddress.length() != 0) {
                    if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {

                        if (!mStrVehicleTypeId.equalsIgnoreCase("")) {
                            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_RESPONSE_DISTANCE_MATRIX_SERVICE, getDistanceMatrixService);
                            ListenersSocket.getDistanceMatrix(mContext, mStrVehicleTypeId, mFromLat, mFromLong, mToLat, mToLong, vehicleCityId);
                        } else {
                            // mErrorLayout.showAlert(getResources().getString(R.string.please_select_vehicle_type), ErrorLayout.MsgType.Error, false);
                        }
                    }
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * To find Near by available drivers
     * */

    private void findNearByDriver(String lat, String longi, String vehicleId) {
        GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);
        clearMap();
        //if (vehicleId.length() != 0) {
        if (checkConnection()) {
            if (mBinding.pbHome.getVisibility() != View.VISIBLE) {
                mBinding.pbHome.setVisibility(View.VISIBLE);
            }
            try {
                if (SocketConnection.isConnected()) {
                    mStrSelectedVehicleId = vehicleId;
                    SocketConnection.attachSingleEventListener(Config.LISTENER_GET_DRIVER, getDriverListener);
                    try {

                        switch (mBookingTypeStr) {
                            case "daily":
                                vehicleId = vehicleId;
                                break;
                            case "rental":
                                vehicleId = TextUtils.join(",", mArrayVehicleTypeId);

                                break;
                            case "outstation":
                                vehicleId = TextUtils.join(",", mArrayVehicleTypeId);
                                break;
                        }


                        ListenersSocket.getDrivers(mContext, vehicleId, lat, longi, mDriverDaily, mDriverRental, mDriverOutstation);


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
                    clearDriverData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    // get driver list
    private void getDriverList(String response) {

        BaseModel mBaseModel = new BaseModel(mContext);
        if (response != null) {
            clearDriverData();

            if (mBaseModel.isParse(response)) {

                mBinding.btnBookNow.setText(getString(R.string.book_now));
                mBinding.llBookMain.setVisibility(View.VISIBLE);
                mBinding.llNoCab.setVisibility(View.GONE);
                mBinding.rltruckitems.setVisibility(View.VISIBLE);

                if (mBaseModel.getResultArray() != null) {
                    for (int i = 0; i < mBaseModel.getResultArray().length(); i++) {
                        try {
                            JSONObject jsonObj = mBaseModel.getResultArray().getJSONObject(i);
                            mArrayDriverETA.add(jsonObj.optString("estimated_time"));
                            mArrayDriverStatus.add(jsonObj.optString("driver_status"));
                            Log.e("drivers's lat", +jsonObj.optDouble("lat") + "\n" + jsonObj.optDouble("lang"));
                            mArrayDriverLat.add(jsonObj.optDouble("lat"));
                            mArrayDriverLong.add(jsonObj.optDouble("lang"));
                            mArrayDriverID.add(jsonObj.optString("user_id"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mETAmin = mArrayDriverETA.get(0);
                    String finalMETAmin = mETAmin;

                    if (mContext != null) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("latlong", mArrayDriverLat + "" + mArrayDriverLong + "11111");
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                mBinding.llBookMain.setVisibility(View.VISIBLE);
                                mBinding.llNoCab.setVisibility(View.GONE);
                                mBinding.tvETA.setVisibility(View.VISIBLE);
                                mBinding.tvETA.setText(finalMETAmin);
                                try {
                                    if (mArrayDriverLat.size() != 0) {
                                        clearMap();
                                        driverMarkersMap.clear();
                                        for (int i = 0; i < mArrayDriverLat.size(); i++) {
                                            // Setting the position for the marker
                                            final int currentPos = i;
                                            try {
                                                if (truckBean.getResult().size() != 0) {
                                                    if (truckBean.getResult().get(pos).getVehicleMarkerImg().length() != 0) {
                                                        try {
                                                            Glide.with(mContext).asBitmap()
                                                                    .load(truckBean.getResult().get(pos).getVehicleMarkerImg().trim())
                                                                    .placeholder(R.drawable.car_placeholder)
                                                                    .override(50, 50)
                                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                    .into(new SimpleTarget<Bitmap>(100, 100) {
                                                                        @Override
                                                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                            try {
                                                                                markerOptions.position(new LatLng(mArrayDriverLat.get(currentPos), mArrayDriverLong.get(currentPos)));
                                                                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                                                                                final Marker marker = mMap.addMarker(markerOptions);
                                                                                driverMarkersMap.put(String.valueOf(mArrayDriverID.get(currentPos)), marker);
                                                                                updateMyLocation(marker, new LatLng(mArrayDriverLat.get(currentPos), mArrayDriverLong.get(currentPos)), new LatLng(mArrayDriverLat.get(currentPos) + 0.00005, mArrayDriverLong.get(currentPos) + 0.00005));
                                                                                // rotateMarker(marker,180);
                                                                                Log.e("latlong1", mArrayDriverLat.get(currentPos) + "" + mArrayDriverLong.get(currentPos) + "7777777");

                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }

                                                                    });

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            } else {
                if (mContext != null) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearDriverData();
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            mBinding.tvETA.setVisibility(View.INVISIBLE);
                            mBinding.rltruckitems.setVisibility(View.VISIBLE);
                            mBinding.llBookMain.setVisibility(View.VISIBLE);
                            mBinding.llNoCab.setVisibility(View.GONE);
                            clearMap();
                        }
                    });
                }
            }
        } else {
            mBinding.rltruckitems.setVisibility(View.VISIBLE);
            clearDriverData();
        }
    }


    private void rotateMarker(final Marker marker, final LatLng destination, final float rotation) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private void updateMyLocation(Marker marker, LatLng old, LatLng newLa) {
        marker.setPosition(old);
        float rotation = (float) SphericalUtil.computeHeading(old, newLa);
        rotateMarker(marker, newLa, rotation);
    }

}

