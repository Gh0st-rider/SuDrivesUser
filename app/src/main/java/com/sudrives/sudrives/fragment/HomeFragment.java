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
import android.location.LocationManager;
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.SocketListnerMethods.ListenersSocket;
import com.sudrives.sudrives.activity.BookOutStationActivity;
import com.sudrives.sudrives.activity.ConfirmPickupActivity;
import com.sudrives.sudrives.activity.HomeMenuActivity;
import com.sudrives.sudrives.activity.NotificationActivity;
import com.sudrives.sudrives.activity.OutstationStaticActivity;
import com.sudrives.sudrives.activity.RentACarActivity;
import com.sudrives.sudrives.adapter.RecyclerViewAdapter;
import com.sudrives.sudrives.databinding.FragmentHomeBinding;
import com.sudrives.sudrives.direction.GetDirectionDataTask;
import com.sudrives.sudrives.model.TruckBean;
import com.sudrives.sudrives.model.getpricesforvehicle.GetPricesForVehicelModel;
import com.sudrives.sudrives.model.homepricesmodel.Result;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseFragment;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GetLocation;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.GpsTrackerShashank;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.LatLngInterpolator;
import com.sudrives.sudrives.utils.MyGeocoder;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;


public class HomeFragment extends BaseFragment implements OnMapReadyCallback, RecyclerViewAdapter.AdapterCallback {

    public static final int REQUEST_CODE_RENTAL_RETURN = 4;
    private static final int REQUEST_CODE_FROM_LOC = 1;
    private static final int REQUEST_CODE_TO_LOC = 2;
    private static final int REQUEST_CODE_CONFIRM_RETURN = 3;
    private static final int REQUEST_CODE_OUTSTATION_RETURN = 5;
    private static final int REQUEST_CODE_RENTAL_FROM_LOC = 6;
    private static final String TAG = "fgcfgcgfh";
    double value = 1.0;
    TextView tv_fare;
    private int onMapCount = 0;
    /**
     * The formatted location address.
     */
    private boolean isSet = true, isSetLatLong = false;

    private String mDriverResponse = "", mStrSelectedCity = "", vehicle_city_id = "", mVehicleTypeId = "", mStrSelectedVehicleId = "",
            mStrSelectedImage = "", mToaddress = "", mStrSelectedVehicle = "", mETAmin = "", mStrEstimatedFare = "",
            mStrEstimatedFareSend = "", mFromLat = "0.0", mFromLong = "0.0", mFromaddress = "", mToLat = "0.0", mToLong = "0.0",
            mDistanceMatrixResponse = "", mFromCity = "", mToCity = "", value_distance = "", value_eta = "";

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
    private ArrayList<String> mArrayVehicleName, mArrayVehicleimage1, mArrayVehicleTypeId, mArraySelectedVehicle, mArrayVehicaleFare, mArrayCapacity, mArrayETA;
    private RecyclerViewAdapter mAdapter;

    private Map<String, Marker> driverMarkersMap;
    private TruckBean truckBean;
    // private SnapHelper snapHelper;
    // Location progress
    private ProgressDialog mprogressBooking;
    private View rootView;
    private SessionPref mSessionPref;
    private MarkerOptions markerOptions;
    private ErrorLayout mErrorLayout;

    private double mSourceLat = 0.0, mSourceLong = 0.0, mDesLat = 0.0, mDesLong = 0.0;

    //rotate marker
    boolean isRotating = false;

    boolean isCamerachanging = false;

    ArrayList<Result> cabPricesModels = new ArrayList<>();
    // ArrayList<Boolean> isClicked = new ArrayList<>();

    BottomSheetBehavior sheetBehavior;
    LocationManager locationManager;

    //new built gps tracker class
    GpsTrackerShashank gpsTracker;

    String mStrBaseFare, mStrChageKm, mStrWatingCharge, mStrTax, mStrTollTax;
    String totalDistanceforTrip = "";
    //LinearLayout ll_map;
    Marker source = null, destination = null;
    private boolean isclick = false;
    String vehicleID = "";
    int oldFirstPos = -1, oldLastPos = -1, totalItemsViewed = 0;

    List<com.sudrives.sudrives.model.getpricesforvehicle.Result> results = new ArrayList<>();

    public HomeFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCamerachanging = true;
        //getBuySubscriptionplan();

        if (Geocoder.isPresent()) {

            Log.d("Geocoder", "present");
            // Toast.makeText(getActivity(),"present",Toast.LENGTH_LONG).show();


        } else {
            //Toast.makeText(getActivity(),"not present",Toast.LENGTH_LONG).show();
            Log.d("Geocoder", "not present");

        }

        gpsTracker = new GpsTrackerShashank(getActivity());
        mFromLat = gpsTracker.getLatitude() + "";
        mFromLong = gpsTracker.getLongitude() + "";
        getAddressFromLatLong(mFromLatCurrent, mFromLongCurrent, new volleyCallback() {
            @Override
            public void onSuccessResponse(String result, String response) {
                Log.e("addressGeocode", result);
                Log.v("ressssssssss", response);
                mFromaddress = result;
                mBinding.etHomeFromAddress.setText(result);
                mFromCity = getCityResponse(response);


            }
        });

    }

    private String getCityResponse(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");


            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

            JSONArray jsonArray1 = jsonObject1.getJSONArray("address_components");
            for (int i = 0; i < jsonArray1.length(); i++) {

                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                JSONArray jsonArray2 = jsonObject2.getJSONArray("types");
                for (int j = 0; j < jsonArray2.length(); j++) {

                    if (jsonArray2.get(j).toString().equalsIgnoreCase("locality")) {
                        String str = jsonObject2.getString("long_name");

                        Log.e("citylocality", str.trim());
                        return str;
                    }

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "data";
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        rootView = mBinding.getRoot();


        return rootView;
    }


    private float pxtoDP(int dp) {
        return (dp * getResources().getDisplayMetrics().density);
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
        mArrayVehicaleFare = new ArrayList<>();
        mArrayETA = new ArrayList<>();
        mArrayCapacity = new ArrayList<>();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        //mapFragment.getMapAsync(this);


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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mBinding.rvhometrucks.setLayoutManager(mLayoutManager);
        mBinding.rvhometrucks.setHasFixedSize(true);
        mBinding.rvhometrucks.setNestedScrollingEnabled(false);


        mAdapter = new RecyclerViewAdapter(mContext, this, results);
        mBinding.rvhometrucks.setAdapter(mAdapter);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = 0;//getResources().getDimensionPixelSize(R.dimen._250sdp);

        params.setMargins(0, mBinding.topframe.getHeight(), 0, margin);
        mBinding.relMap.setLayoutParams(params);
        //vehicleListScroll();
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
        //ll_map = rootView.findViewById(R.id.RelMap);


        sheetBehavior = BottomSheetBehavior.from(mBinding.constBottom);
       /* mBinding.llBottomClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
*/


        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        // sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        //btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        int margin = getResources().getDimensionPixelSize(R.dimen._250sdp);

                        params.setMargins(0, mBinding.topframe.getHeight(), 0, margin);
                        mBinding.relMap.setLayoutParams(params);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:

                        //sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:


                        // ViewTreeObserver vto = mBinding.constBottom.getViewTreeObserver();
                       /* vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    mBinding.constBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    mBinding.constBottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                int width  = mBinding.constBottom.getMeasuredWidth();
                                int height = mBinding.constBottom.getMeasuredHeight();

                                System.out.println("Height and width"+width+" "+height);
                                if (mBinding.constBottom.getVisibility() == View.VISIBLE){

                                }else {
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    params.setMargins(0, mBinding.topframe.getHeight(), 0, 0);
                                    mBinding.relMap.setLayoutParams(params);
                                }

                            }
                        });*/

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }

    private void animateBottomSheetArrows(float slideOffset) {
        // Animate counter-clockwise
        //  arrowleft.setRotation(slideOffset * -180);
        // Animate clockwise
        // arrowright.setRotation(slideOffset * 180);
    }

    private void setLocClick(View view) {
        try {

            getDriverList(mDriverResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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


                isCamerachanging = true;
                mBinding.btnSetLocation.setVisibility(View.VISIBLE);
                mBinding.llBookMain.setVisibility(View.VISIBLE);
                mBinding.constBottom.setVisibility(View.GONE);
                mBinding.llBottom.setVisibility(View.GONE);
                ViewTreeObserver vto = mBinding.constBottom.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mBinding.constBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mBinding.constBottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int width = mBinding.constBottom.getMeasuredWidth();
                        int height = mBinding.constBottom.getMeasuredHeight();

                        System.out.println("Height and width" + width + " " + height);
                        if (mBinding.constBottom.getVisibility() == View.VISIBLE) {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, height);
                            mBinding.relMap.setLayoutParams(params);
                        } else {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 0);
                            mBinding.relMap.setLayoutParams(params);
                        }

                    }
                });
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                //call location based filter

                onDropClick = false;
                LatLng latLong;
                latLong = place.getLatLng();
                isSet = false;
                // mBinding.imgCrossHome.setVisibility(View.VISIBLE);
                FontLoader.setHelBold(mBinding.etHomeToAddress);
                FontLoader.setHelRegular(mBinding.etHomeFromAddress);
                mFromLat = String.valueOf(latLong.latitude);
                mFromLong = String.valueOf(latLong.longitude);
                mFromLatCurrent = latLong.latitude;
                mFromLongCurrent = latLong.longitude;
                // getAddressFromLocation(latLong.latitude, latLong.longitude);

                try {
                    List<Address> addresses = MyGeocoder.getFromLocation(latLong.latitude, latLong.longitude, 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");

                        for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        mFromaddress = strReturnedAddress.toString();
                        Log.e("My location address", strReturnedAddress.toString());
                    } else {
                        Log.e("My location address", "No Address returned!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("My location address", "Canont get Address!");
                }


                Log.e("addresssss", mFromaddress + "");
                // Toast.makeText(getActivity(), "ShashankLog: " + place.getAddress().toString(), Toast.LENGTH_LONG).show();

                //mToaddress = GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude);
                mBinding.etHomeFromAddress.setText(place.getAddress());
                mFromaddress = place.getAddress().toString();
                // Toast.makeText(getActivity(), "Address" + mToaddress, Toast.LENGTH_LONG).show();
                // Toast.makeText(getActivity(), "LatLong" + String.valueOf(latLong.latitude) + String.valueOf(latLong.longitude), Toast.LENGTH_LONG).show();

                mBinding.llPointIcon.setBackground(getResources().getDrawable(R.drawable.pointer_from));


                // getPricesforcab(mFromLat, mFromLong);
                // postNewComment();
                if (mDesLat != 0.0 && mSourceLat != 0.0) {

                    mArrayVehicaleFare.clear();
                    returnDistanceDuration();

                }


                if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY) || mBookingTypeStr.equalsIgnoreCase(Config.OUTSTATION)) {

                    //EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);

                } else {
                    if (mFromaddress.length() != 0) {
                        if (!mFromLong.equalsIgnoreCase("0.0") && !mFromLat.equalsIgnoreCase("0.0")) {
                            mBinding.llEstimated.setVisibility(View.GONE);
                        }
                    }
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLong.latitude, latLong.longitude), 8f));
                mSourceLat = latLong.latitude;
                mSourceLong = latLong.longitude;

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
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLong, 8f);
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


                        //  EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
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
                if (destination != null)
                    destination.remove();
                //clearMap();
                isCamerachanging = true;
                mBinding.btnSetLocation.setVisibility(View.VISIBLE);
                mBinding.llBookMain.setVisibility(View.VISIBLE);
                mBinding.constBottom.setVisibility(View.GONE);
                mBinding.llBottom.setVisibility(View.GONE);
                ViewTreeObserver vto = mBinding.constBottom.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mBinding.constBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mBinding.constBottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int width = mBinding.constBottom.getMeasuredWidth();
                        int height = mBinding.constBottom.getMeasuredHeight();

                        System.out.println("Height and width" + width + " " + height);
                        if (mBinding.constBottom.getVisibility() == View.VISIBLE) {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, height);
                            mBinding.relMap.setLayoutParams(params);
                        } else {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 0);
                            mBinding.relMap.setLayoutParams(params);
                        }

                    }
                });
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                //call location based filter

                onDropClick = true;
                LatLng latLong;
                latLong = place.getLatLng();
                isSet = false;
                //mBinding.imgCrossHome.setVisibility(View.VISIBLE);
                FontLoader.setHelBold(mBinding.etHomeToAddress);
                FontLoader.setHelRegular(mBinding.etHomeFromAddress);
                mToLat = String.valueOf(latLong.latitude);
                mToLong = String.valueOf(latLong.longitude);

                // getAddressFromLocation(latLong.latitude, latLong.longitude);

                try {
                    List<Address> addresses = MyGeocoder.getFromLocation(latLong.latitude, latLong.longitude, 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");

                        for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        mToaddress = strReturnedAddress.toString();
                        Log.e("My location address", strReturnedAddress.toString());
                    } else {
                        Log.e("My location address", "No Address returned!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("My location address", "Canont get Address!");
                }


                Log.e("addresssss", mToaddress + "");
                // Toast.makeText(getActivity(),"ShashankLog: "+place.getAddress().toString(),Toast.LENGTH_LONG).show();

                //mToaddress = GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude);
                mBinding.etHomeToAddress.setText(place.getAddress());
                mToaddress = place.getAddress().toString();
                // Toast.makeText(getActivity(), "Address" + mToaddress, Toast.LENGTH_LONG).show();
                // Toast.makeText(getActivity(), "LatLong" + String.valueOf(latLong.latitude) + String.valueOf(latLong.longitude), Toast.LENGTH_LONG).show();
                //clearMap();
                mBinding.llPointIcon.setBackground(getResources().getDrawable(R.drawable.pointer_red));

               /* try {
                    getPricesforcab(mToLat, mToLong);
                    // postNewComment();

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY) || mBookingTypeStr.equalsIgnoreCase(Config.OUTSTATION)) {

                    // EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);

                } else {
                    if (mToaddress.length() != 0) {
                        if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                            mBinding.llEstimated.setVisibility(View.GONE);
                        }
                    }
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLong.latitude, latLong.longitude), 8f));
                mDesLat = latLong.latitude;
                mDesLong = latLong.longitude;
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
            // mBinding.llEstimated.setVisibility(View.VISIBLE);
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
        //  mBinding.llSelectedImage.setVisibility(View.GONE);
        // mBinding.tvETA.setVisibility(View.GONE);
        // mBinding.rvhometrucks.setVisibility(View.GONE);
        //mBinding.rvhometrucks.smoothScrollToPosition(0);
        mAdapter.notifyDataSetChanged();
        // CODE FOR ADD MARGINS
        /*RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mBinding.relMap.getLayoutParams();
        relativeParams.setMargins(0, 75, 0, 250);  // left, top, right, bottom
        mBinding.relMap.setLayoutParams(relativeParams);*/
        Log.e("height", sheetBehavior.getPeekHeight() + "");
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
                //String kmoftrip[] = totalDistanceforTrip.split(" ");
                //if (Double.valueOf(kmoftrip[0]) < 100.0)
                String str1 = "";
                String str2 = "";
                if (totalDistanceforTrip.contains("km")) {
                    str1 = totalDistanceforTrip.replaceAll("km", "");
                    if (str1.contains(",")) {

                        str1 = str1.replaceAll(",", "");
                    }

                    Log.e("kmdistance", str1);
                }
                Log.e("kmdistance", str1);

                if (!str1.isEmpty()) {
                    if (Double.parseDouble(str1) < 100)
                        bookNowDailyClick();
                    else {
                        Intent i1 = new Intent(getActivity(), OutstationStaticActivity.class);
                        i1.putExtra("stringout", "Are you trying to book an Outstation Ride?\n" +
                                "Yes we are accepting Outstation rides offline.");
                        startActivity(i1);
                        //Toast.makeText(getActivity(), "Outstation or rental is not availiable please book daily within range < 100 km",Toast.LENGTH_LONG).show();

                    }

                    break;
                }

            case "rental":
                // bookNowRentalClick();
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

                           /* for (int i=0; i< truckBean.getResult().size();i++){
                                if (truckBean.getResult().get(i).getId().equalsIgnoreCase(vehicleID)) {
                                    mStrBaseFare = truckBean.getResult().get(i).getVehicleFare().get(0).getValuePair();
                                    mStrChageKm = truckBean.getResult().get(i).getVehicleFare().get(1).getValuePair();
                                    mStrWatingCharge = truckBean.getResult().get(i).getVehicleFare().get(6).getValuePair();
                                    mStrTax

                                }
                            }*/

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
                            intent.putExtra("mStrBaseFare", mStrBaseFare);
                            intent.putExtra("mStrChageKm", mStrChageKm);
                            intent.putExtra("mStrWatingCharge", mStrWatingCharge);
                            intent.putExtra("mStrTax", mStrTax);
                            intent.putExtra("mStrTollTax", mStrTollTax);
                            intent.putExtra("driver_search_ids", mDriverResponse);
                            intent.putExtra("mVehicleCityId", vehicle_city_id);
                            //  intent.putExtra("mShowCashOption", mCancelledTripsStatus);
                            startActivityForResult(intent, REQUEST_CODE_CONFIRM_RETURN);

                        } else {
                            mErrorLayout.showAlert(getString(R.string.fetching_estimated_fare), ErrorLayout.MsgType.Error, true);
                            // EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
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

        Intent i1 = new Intent(getActivity(), OutstationStaticActivity.class);
        i1.putExtra("stringout", "Are you trying to Book later?\n" +
                "Yes we are accepting book later rides offline.");
        startActivity(i1);

        /*KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.rlHomeMain);
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

        }*/

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
        onMapCount++;
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
        if (gpsTracker.getLatitude() != 0.0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 8f));

        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFromLatCurrent, mFromLongCurrent), 8f));

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (isCamerachanging) {
                    GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);


                    LatLng curentLocation = null;
                    // Log.d("Camera position change" + "", cameraPosition + "");

                    curentLocation = mMap.getCameraPosition().target;
                    if (!mFromLat.equalsIgnoreCase("0.0") && !mFromLong.equalsIgnoreCase("0.0")) {

                        mStrSelectedCity = getCityName(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong));
                    }
                    try {
                        if (onDropClick) {

                            mToLat = String.valueOf(curentLocation.latitude);
                            mToLong = String.valueOf(curentLocation.longitude);
                            //mToaddress = GlobalUtil.getAddress(mContext, curentLocation.latitude, curentLocation.longitude);
                            getAddressFromLatLong(curentLocation.latitude, curentLocation.longitude, new volleyCallback() {
                                @Override
                                public void onSuccessResponse(String result, String response) {
                                    Log.e("addressGeocode", result);
                                    mToaddress = result;
                                    mBinding.etHomeToAddress.setText(result);
                                    mToCity = getCityResponse(response);
                                }
                            });
                           /* GetLocationAsync locationAsync = new GetLocationAsync(curentLocation.latitude,
                                    curentLocation.longitude, getActivity(), new    GetLocationAsync.AsyncResponse() {
                                @Override
                                public void onProcessFinished(String fulladdress, String smallAddress, String state, String city, String country, String zipCode, String placeName) {
                                    Toast.makeText(getActivity(),fulladdress,Toast.LENGTH_LONG).show();
                                    mToaddress = fulladdress;
                                    mBinding.etHomeToAddress.setText(fulladdress);
                                }
                            });
                            locationAsync.execute();*/

                            //mBinding.etHomeToAddress.setText(mToaddress);
                            mDesLat = Double.valueOf(mToLat);
                            mDesLong = Double.valueOf(mToLong);
                            if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                                if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                    // EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                }
                            }

                        } else if (onSetCurrentLoc) {
                            clearMap();
                            onSetCurrentLoc = false;
                            mToLat = String.valueOf(placePickerToLat);
                            mToLong = String.valueOf(placePickerToLong);
                            mDesLat = Double.valueOf(mToLat);
                            mDesLong = Double.valueOf(mToLong);


                            getAddressFromLatLong(placePickerToLat, placePickerToLong, new volleyCallback() {
                                @Override
                                public void onSuccessResponse(String result, String response) {
                                    mToaddress = result;
                                    mBinding.etHomeToAddress.setText(mToaddress);
                                    mToCity = getCityResponse(response);
                                }
                            });


                           /* GetLocationAsync locationAsync = new GetLocationAsync(placePickerToLat,
                                    placePickerToLong, getActivity(), new    GetLocationAsync.AsyncResponse() {
                                @Override
                                public void onProcessFinished(String fulladdress, String smallAddress, String state, String city, String country, String zipCode, String placeName) {
                                    Toast.makeText(getActivity(),fulladdress,Toast.LENGTH_LONG).show();
                                    mToaddress = fulladdress;
                                    mBinding.etHomeToAddress.setText(fulladdress);
                                }
                            });
                            locationAsync.execute();*/

                            // mBinding.etHomeToAddress.setText(mToaddress);

                        } else {
                            mFromLat = String.valueOf(curentLocation.latitude);
                            mFromLong = String.valueOf(curentLocation.longitude);
                            //mToaddress = GlobalUtil.getAddress(mContext, curentLocation.latitude, curentLocation.longitude);
                            getAddressFromLatLong(curentLocation.latitude, curentLocation.longitude, new volleyCallback() {
                                @Override
                                public void onSuccessResponse(String result, String response) {
                                    Log.e("addressGeocode", result);
                                    mFromaddress = result;
                                    mBinding.etHomeFromAddress.setText(result);
                                    mFromCity = getCityResponse(response);
                                }
                            });


                            mSourceLat = Double.valueOf(mFromLat);
                            mSourceLong = Double.valueOf(mFromLong);
                            if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                                if (!mFromLong.equalsIgnoreCase("0.0") && !mFromLat.equalsIgnoreCase("0.0")) {
                                    // EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                }
                            }
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
                                    mDesLat = Double.valueOf(mToLat);
                                    mDesLong = Double.valueOf(mToLong);
                                    //mToaddress = GlobalUtil.getAddress(mContext, placePickerToLat, placePickerToLong);

                                    getAddressFromLatLong(placePickerToLat, placePickerToLong, new volleyCallback() {
                                        @Override
                                        public void onSuccessResponse(String result, String response) {
                                            mToaddress = result;
                                            mBinding.etHomeToAddress.setText(mToaddress);
                                            mToCity = getCityResponse(response);
                                        }
                                    });

                                    //mBinding.etHomeToAddress.setText(mToaddress);

                                } else {
                                    mToLat = String.valueOf(curentLocation.latitude);
                                    mToLong = String.valueOf(curentLocation.longitude);
                                    mDesLat = Double.valueOf(mToLat);
                                    mDesLong = Double.valueOf(mToLong);
                                    getAddressFromLatLong(curentLocation.latitude, curentLocation.longitude, new volleyCallback() {
                                        @Override
                                        public void onSuccessResponse(String result, String response) {
                                            mToaddress = result;
                                            mBinding.etHomeToAddress.setText(mToaddress);
                                            mToCity = getCityResponse(response);
                                        }
                                    });
                                   /* GetLocationAsync locationAsync = new GetLocationAsync(curentLocation.latitude,
                                            curentLocation.longitude, getActivity(), new    GetLocationAsync.AsyncResponse() {
                                        @Override
                                        public void onProcessFinished(String fulladdress, String smallAddress, String state, String city, String country, String zipCode, String placeName) {
                                            Toast.makeText(getActivity(),fulladdress,Toast.LENGTH_LONG).show();
                                            mToaddress = fulladdress;
                                            mBinding.etHomeToAddress.setText(fulladdress);
                                        }
                                    });
                                    locationAsync.execute();*/

                                    // mBinding.etHomeToAddress.setText(mToaddress);
                                }
                                if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                                    if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                        // EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                    }
                                }
                            } else {
                                if (onSetCurrentLoc) {
                                    clearMap();
                                    onSetCurrentLoc = false;
                                    mToLat = String.valueOf(placePickerToLat);
                                    mToLong = String.valueOf(placePickerToLong);
                                    mDesLat = Double.valueOf(mToLat);
                                    mDesLong = Double.valueOf(mToLong);
                                    //mToaddress = GlobalUtil.getAddress(mContext, placePickerToLat, placePickerToLong);
                                    getAddressFromLatLong(placePickerToLat, placePickerToLong, new volleyCallback() {
                                        @Override
                                        public void onSuccessResponse(String result, String response) {
                                            mToaddress = result;
                                            mBinding.etHomeToAddress.setText(mToaddress);
                                            mToCity = getCityResponse(response);
                                        }
                                    });                                  /*  GetLocationAsync locationAsync = new GetLocationAsync(placePickerToLat,
                                            placePickerToLong, getActivity(), new    GetLocationAsync.AsyncResponse() {
                                        @Override
                                        public void onProcessFinished(String fulladdress, String smallAddress, String state, String city, String country, String zipCode, String placeName) {

                                            Toast.makeText(getActivity(),fulladdress,Toast.LENGTH_LONG).show();
                                            mToaddress = fulladdress;
                                            mBinding.etHomeToAddress.setText(fulladdress);
                                        }
                                    });
                                    locationAsync.execute();*/
                                    //mBinding.etHomeToAddress.setText(mToaddress);
                                    if (mToaddress.length() != 0 && mFromaddress.length() != 0) {
                                        if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                            //EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                        }
                                    }
                                } else {
                                    mFromLat = String.valueOf(curentLocation.latitude);
                                    mFromLong = String.valueOf(curentLocation.longitude);
                                    mSourceLat = Double.valueOf(mFromLat);
                                    mSourceLong = Double.valueOf(mFromLong);
                                    // clearMap();
                                    if (checkConnection()) {
                                        clearVehicleData(mFromLat, mFromLong);

                                    } else {
                                        mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                                    }
                                    // mFromaddress = getAddressFromLocation(curentLocation.latitude, curentLocation.longitude);//GlobalUtil.getAddress(mContext, curentLocation.latitude, curentLocation.longitude);
                                    getAddressFromLatLong(curentLocation.latitude, curentLocation.longitude, new volleyCallback() {
                                        @Override
                                        public void onSuccessResponse(String result, String response) {
                                            mFromaddress = result;
                                            mBinding.etHomeFromAddress.setText(mFromaddress);
                                            mFromCity = getCityResponse(response);
                                        }
                                    });
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
                    mBinding.btnSetLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setLocClick(v);
                            if (mDesLat != 0.0 && mSourceLat != 0.0) {

                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                );
                                int margin = getResources().getDimensionPixelSize(R.dimen._250sdp);

                                params.setMargins(0, mBinding.topframe.getHeight(), 0, margin);
                                mBinding.relMap.setLayoutParams(params);
                                returnDistanceDuration();

                            }

                            clearMapandUpdate();
                        }
                    });
                }
            }
        });


        mMap.getUiSettings().setScrollGesturesEnabled(true);

        Bundle bundle5 = this.getArguments();
        if (bundle5 != null) {

            if (bundle5.getString("from").equals("text")) {
                //mBinding.llPointIcon.setBackground(getResources().getDrawable(R.drawable.pointer_from));
                mFromLatCurrent = gpsTracker.getLatitude();
                mFromLongCurrent = gpsTracker.getLongitude();
                getAddressFromLatLong(mFromLatCurrent, mFromLongCurrent, new volleyCallback() {
                    @Override
                    public void onSuccessResponse(String result, String response) {
                        Log.e("addressGeocode", result);
                        mFromaddress = result;
                        mBinding.etHomeFromAddress.setText(result);
                        mFromCity = getCityResponse(response);
                        //if (mBinding.etHomeToAddress.getText().toString().isEmpty())
                        //  homeToAddressClick(rootView);

                    }
                });


            }

        }

        Bundle bundle1 = this.getArguments();
        if (bundle1 != null) {

            if (bundle1.getString("from").equals("work")) {
                mFromLatCurrent = gpsTracker.getLatitude();
                mFromLongCurrent = gpsTracker.getLongitude();
                mToLat = bundle1.getString("Deslat");
                mToLong = bundle1.getString("Deslong");
                mDesLat = Double.valueOf(bundle1.getString("Deslat"));
                mDesLong = Double.valueOf(bundle1.getString("Deslong"));
                placePickerToLat = Double.valueOf(bundle1.getString("Deslat"));
                placePickerToLong = Double.valueOf(bundle1.getString("Deslong"));
                mSourceLat = gpsTracker.getLatitude();
                mSourceLong = gpsTracker.getLongitude();
                getAddressFromLatLong(mFromLatCurrent, mFromLongCurrent, new volleyCallback() {
                    @Override
                    public void onSuccessResponse(String result, String response) {
                        Log.e("addressGeocode", result);
                        mFromaddress = result;
                        mBinding.etHomeFromAddress.setText(result);
                        mFromCity = getCityResponse(response);
                    }
                });

                getAddressFromLatLong(mDesLat, mDesLong, new volleyCallback() {
                    @Override
                    public void onSuccessResponse(String result, String response) {
                        Log.e("addressGeocode", result);
                        mToaddress = result;
                        mBinding.etHomeToAddress.setText(result);
                        mToCity = getCityResponse(response);

                    }
                });
                if (mDesLat != 0.0 && mSourceLat != 0.0) {
                    returnDistanceDuration();
                }
                //getAddressFromLocation(mDesLat, mDesLong);

                // Log.e("destination",bundle1.getString("Deslat")+"long"+bundle1.getString("Deslong"));

                //mToaddress = GlobalUtil.getAddress(getActivity(), mDesLat, mDesLong);

               /* GetLocationAsync locationAsync1 = new GetLocationAsync(mDesLat,
                        mDesLong, getActivity(), new    GetLocationAsync.AsyncResponse() {
                    @Override
                    public void onProcessFinished(String fulladdress, String smallAddress, String state, String city, String country, String zipCode, String placeName) {
                        Toast.makeText(getActivity(),"Full add: "+fulladdress,Toast.LENGTH_LONG).show();


                    }
                });
                locationAsync1.execute();*/

                //mBinding.etHomeToAddress.setText(mToaddress);

                // clearMapandUpdate();
            }
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("from").equals("home")) {
                mFromLatCurrent = gpsTracker.getLatitude();
                mFromLongCurrent = gpsTracker.getLongitude();
                mToLat = bundle.getString("Deslat");
                mToLong = bundle.getString("Deslong");
                mDesLat = Double.valueOf(bundle.getString("Deslat"));
                mDesLong = Double.valueOf(bundle.getString("Deslong"));
                placePickerToLat = Double.valueOf(bundle.getString("Deslat"));
                placePickerToLong = Double.valueOf(bundle.getString("Deslong"));
                mSourceLat = gpsTracker.getLatitude();
                mSourceLong = gpsTracker.getLongitude();
                //mFromaddress = getAddressFromLocation(mFromLatCurrent, mFromLongCurrent);//GlobalUtil.getAddress(getActivity(), mFromLatCurrent, mFromLongCurrent);


                getAddressFromLatLong(mFromLatCurrent, mFromLongCurrent, new volleyCallback() {
                    @Override
                    public void onSuccessResponse(String result, String response) {
                        Log.e("addressGeocode", result);
                        mFromaddress = result;
                        mBinding.etHomeFromAddress.setText(result);
                        mFromCity = getCityResponse(response);

                    }
                });

                getAddressFromLatLong(mDesLat, mDesLong, new volleyCallback() {
                    @Override
                    public void onSuccessResponse(String result, String response) {
                        Log.e("addressGeocode", result);
                        mToaddress = result;
                        mBinding.etHomeToAddress.setText(result);
                        mToCity = getCityResponse(response);

                    }
                });


                if (mDesLat != 0.0 && mSourceLat != 0.0) {

                    returnDistanceDuration();
                    // postRequest();

                }
                clearMapandUpdate();


            }
        }


        Bundle bundle2 = this.getArguments();
        if (bundle2 != null) {
            if (bundle2.getString("from").equals("rental")) {
                mFromLat = bundle2.getString("lat");
                mFromLong = bundle2.getString("long");
                mBinding.constBottom.setVisibility(View.VISIBLE);
                mBinding.llBottom.setVisibility(View.VISIBLE);
                mBinding.btnSetLocation.setVisibility(View.GONE);
                mBinding.rlTopHome.setVisibility(View.GONE);
                tabRentalClick(rootView);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                int margin = getResources().getDimensionPixelSize(R.dimen._250sdp);

                params.setMargins(0, mBinding.topframe.getHeight(), 0, margin);
                mBinding.relMap.setLayoutParams(params);


            }
        }

        Bundle bundle3 = this.getArguments();
        if (bundle3 != null) {
            if (bundle3.getString("from").equals("outstation")) {

                mFromLat = bundle3.getString("lat");
                mFromLong = bundle3.getString("long");
                //mBinding.constBottom.setVisibility(View.VISIBLE);
                // mBinding.btnSetLocation.setVisibility(View.GONE);
                //mBinding.constTop.setVisibility(View.GONE);
                tabOutStationClick(rootView);

            }

        }


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

                    } else {
                        mBinding.llBottomButton.setVisibility(View.VISIBLE);
                    }


                    if (onMapCount == 1 && mBinding.upperLayout.getVisibility() == View.VISIBLE && mFromLatCurrent != 0.0) {
                        onMapReadyFunctionality(mMap);
                        mBinding.upperLayout.setVisibility(View.GONE);

                    }
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


        //gpsTracker = new GpsTrackerShashank(getActivity());
        //mFromLat = gpsTracker.getLatitude()+"";
        //mFromLong = gpsTracker.getLongitude()+"";

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
        /*try{
            if (!mFromaddress.isEmpty() && !mToaddress.isEmpty())
           // postRequest();

        }catch (Exception e){

        }*/
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
        //clearMap();
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
                                // EmitDistanceMatrixService(truckBean.getResult().get(pos).getVehicle_city_id(),truckBean.getResult().get(pos).getVehicle_type_id());
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
                                    //  EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                                }
                            }

                            //   Glide.with(mContext).load(mStrSelectedImage).into(mBinding.ivTruckSelected);
                            break;
                        case "rental":
                            // Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);

                            break;
                        case "outstation":
                            //  Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);
                            break;
                    }
                    mBinding.tvTruckSelected.setVisibility(View.GONE);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
                //mBinding.rvhometrucks.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                //int width = itemView.getMeasuredWidth();




               /* int height = mBinding.rvhometrucks.getMeasuredHeight();
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mBinding.relMap.getLayoutParams();
                relativeParams.setMargins(0, 75, 0, height);  // left, top, right, bottom
                mBinding.relMap.setLayoutParams(relativeParams);
                Log.e("height1",sheetBehavior.getPeekHeight()+"");*/


                //getEstimatedFare();
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
                        // Glide.with(mContext).load(mStrSelectedImage).into(mBinding.ivTruckSelected);
                        break;
                    case "rental":
                        //   Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);

                        break;
                    case "outstation":
                        //  Glide.with(mContext).load(R.drawable.sedancolor).into(mBinding.ivTruckSelected);
                        break;
                }
                mBinding.tvTruckSelected.setText(truckBean.getResult().get(pos).getVehicleName().trim());
                setCurrentPinPickUp();
                if (mToaddress != null) {

                    if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {
                        //  EmitDistanceMatrixService(vehicle_city_id, mVehicleTypeId);
                        /*for (int i = 1; i < 8; i++){
                            EmitDistanceMatrixService1(vehicle_city_id, i+"");
                        }*/
                        //  to show estimated fare
                    } else {
                        if (mToaddress.length() != 0 && mArrayDriverID.size() != 0) {
                            if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {
                                mBinding.llEstimated.setVisibility(View.GONE);
                            }
                        }
                    }
                }
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

                                            //   Log.e("Get driver_id 2      : ", driver_id);

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
                                            //    Log.e("driveridddd", driver_id + "2222222222");
                                            //   Log.e("driveridddd", lat + "2222222222" + longi);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(mFromLat), Double.valueOf(mFromLong)), 8f));

        getAddressFromLatLong(Double.valueOf(mFromLat), Double.valueOf(mFromLong), new volleyCallback() {
            @Override
            public void onSuccessResponse(String result, String response) {
                mFromaddress = result;
                mBinding.etHomeFromAddress.setText(mFromaddress);
                mFromCity = getCityResponse(response);

            }
        });

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
        com.sudrives.sudrives.model.getpricesforvehicle.Result result = results.get(pos);
        isclick = true;
        tv_fare = view.findViewById(R.id.tv_fare);
        LinearLayout ll_selected = view.findViewById(R.id.ll_selected);
        try {

            clearVehicleData(mFromLat, mFromLong);
            getVehichleandUpdateUi(pos);
            Log.e("posisionnnnnnn", pos + "1111");
            recyclerViewPos = pos;

            mStrEstimatedFareSend = result.getFareOfDistance();
            totalDistanceforTrip = result.getKilometers();
            //mBinding.rvhometrucks.smoothScrollToPosition(recyclerViewPos);
            mETAmin = result.getEta();
            vehicleID = result.getVehicleId();


        } catch (Exception e) {
            e.printStackTrace();

            recyclerViewPos = 0;
            if (truckBean != null) {
                //mBinding.rvhometrucks.smoothScrollToPosition(0);
                mStrSelectedVehicleId = result.getVehicleId();
                mStrSelectedVehicle = result.getVehicleName();
                mStrSelectedImage = result.getVehicleImage();
                vehicle_city_id = truckBean.getResult().get(0).getVehicle_city_id();
                mVehicleTypeId = truckBean.getResult().get(0).getVehicle_type_id();
                mAdapter.notifyDataSetChanged();
                /*RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mBinding.relMap.getLayoutParams();
                relativeParams.setMargins(0, 75, 0, 250);  // left, top, right, bottom
                mBinding.relMap.setLayoutParams(relativeParams);
                Log.e("height2",sheetBehavior.getPeekHeight()+"");*/

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
                // mBinding.llBookMain.setVisibility(View.VISIBLE);
                //mBinding.rltruckitems.setVisibility(View.GONE);
                // mBinding.rltrucks.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("errorsocket", e + "");
                e.printStackTrace();
            }
        }
    }

    public void onLocationUpdate(Location location) {
        //Log.e("Lat and Lng", location.getLatitude() + "   " + location.getLongitude());
        locationStatus(false);
        mFromLatCurrent = location.getLatitude();
        mFromLongCurrent = location.getLongitude();
        // mFromLat = location.getLatitude() + "";
        // mFromLong = location.getLongitude() + "";

        //  onMapReadyFunctionality(mMap);


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
        //  Log.e("onLocationEnabled","onLocationEnabled");
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
        //clearMap();
        if (mArrayDriverStatus != null) {
            mArrayDriverStatus.clear();
        }
        try {
            ((HomeMenuActivity) mContext).requestVehichleList(mBookingTypeStr, mFromLat, mFromLong);
            ((HomeMenuActivity) mContext).responseVehichleList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBinding.llBookMain.setVisibility(View.GONE);

    }

    private void ClearVehicleRelatedIds() {
        mVehicleTypeId = "";
        vehicle_city_id = "";
        mStrSelectedVehicleId = "";
        mStrSelectedVehicle = "";
        mStrSelectedImage = "";
        mStrEstimatedFare = "";
        // mStrEstimatedFareSend = "";
        mBinding.llEstimated.setVisibility(View.GONE);
        // mBinding.rvhometrucks.smoothScrollToPosition(0);

    }

    private void clearToLocData() {
        mBinding.etHomeToAddress.setText("");
        mToaddress = "";
        mToLat = "0.0";
        mToLong = "0.0";
        //mBinding.imgCrossHome.setVisibility(View.GONE);
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
            clearMapandUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearMapandUpdate() {

        if (mMap != null) {
            mMap.clear();

            if (mDesLat != 0.0 && mSourceLat != 0.0) {

                GetDirectionDataTask getDirectionDataTask = new GetDirectionDataTask(getActivity());
                getDirectionDataTask.execute(getDirectionDataTask.getgoogleDirectionUrl(mSourceLat + ","
                                + mSourceLong, mDesLat + "," + mDesLong, getAppString(R.string.direction_api_key)),
                        mSourceLat + "," + mSourceLong, mDesLat + "," + mDesLong);
            }
            isCamerachanging = false;
            mBinding.btnSetLocation.setVisibility(View.GONE);

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
                    //  mBinding.llSelectedImage.setVisibility(View.VISIBLE);
                    // mBinding.tvETA.setVisibility(View.VISIBLE);

                    getDriverList(mDriverResponse);
                    Log.e("Driverrresssss", mDriverResponse);
                }
            });

        }
    };
    /*Emitter.Listener getDistanceMatrixService = new Emitter.Listener() {
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
                                                        Log.e("dataaaaaa", mBaseModel.getResultObject() + "");
                                                        mStrEstimatedFare = getString(R.string.estimated_fare) + ": <font color=#f5220f>\u20B9 " + mBaseModel.getResultObject().getString("fare_of_distance") + "</font>";
                                                        // mStrEstimatedFareSend = mBaseModel.getResultObject().getString("fare_of_distance");
                                                        mStrBaseFare = mBaseModel.getResultObject().getString("base_fare");
                                                        mStrChageKm = mBaseModel.getResultObject().getString("charge_km");
                                                        mStrWatingCharge = mBaseModel.getResultObject().getString("waiting_charge");
                                                        mStrTax = mBaseModel.getResultObject().getString("tax");
                                                        mStrTollTax = mBaseModel.getResultObject().getString("toll_tax");
                                                        totalDistanceforTrip = mBaseModel.getResponseObject().optString("kilometers");

                                                        mBinding.tvEstimatefare.setText(Html.fromHtml(mStrEstimatedFare));
                                                        mBinding.tvEstimatefare.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
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
    };*/

    /*Emitter.Listener getDistanceMatrixService1 = new Emitter.Listener() {
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
                                                        // mStrEstimatedFare = getString(R.string.estimated_fare) + ": <font color=#f5220f>\u20B9 " + mBaseModel.getResultObject().getString("fare_of_distance") + "</font>";
                                                        // Log.e("estimated fare",mBaseModel.getResultObject().getString("fare_of_distance"));
                                                        mStrEstimatedFareSend = mBaseModel.getResultObject().getString("fare_of_distance");

                                                        Log.e("fare", mStrEstimatedFareSend);
                                                        *//*if (mBookingTypeStr.equalsIgnoreCase(Config.DAILY)) {
                                                            mBinding.llEstimated.setVisibility(View.VISIBLE);
                                                        }
                                                        mBinding.tvEstimatefare.setText(Html.fromHtml(mStrEstimatedFare));
                                                        mBinding.tvEstimatefare.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                                                        Log.e("timeeeeeeee",Html.fromHtml(getString(R.string.ett) + ": " + mBaseModel.getResultObject().getString("eta"))+"");
                                                        mBinding.tvEstimateTimeTravel.setText(Html.fromHtml(getString(R.string.ett) + ": " + mBaseModel.getResultObject().getString("eta")));
                                                        //if (tv_fare != null)
                                                        Log.e("price", mStrEstimatedFare);

                                                        //mArrayVehicaleFare.add(mStrEstimatedFare);
                                                        tv_fare.setText(Html.fromHtml(mStrEstimatedFare));*//*
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
    };*/

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

   /* private void EmitDistanceMatrixService(String vehicleCityId, String mStrVehicleTypeId) {
        GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);

        try {
            mBinding.llEstimated.setVisibility(View.GONE);
            mStrEstimatedFare = "";
            //mStrEstimatedFareSend = "";

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
    }*/


    /*private void EmitDistanceMatrixService1(String vehicleCityId, String mStrVehicleTypeId) {
        GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);

        try {
            mBinding.llEstimated.setVisibility(View.GONE);
            mStrEstimatedFare = "";
            mStrEstimatedFareSend = "";

            if (SocketConnection.isConnected()) {
                if (mToaddress.length() != 0) {
                    if (!mToLong.equalsIgnoreCase("0.0") && !mToLat.equalsIgnoreCase("0.0")) {

                        if (!mStrVehicleTypeId.equalsIgnoreCase("")) {
                            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_RESPONSE_DISTANCE_MATRIX_SERVICE, getDistanceMatrixService1);
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
    }*/

    /*
     * To find Near by available drivers
     * */

    private void findNearByDriver(String lat, String longi, String vehicleId) {
        GlobalUtil.avoidDoubleClicks(mBinding.btnBookNow);
        //clearMap();
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
                        System.out.println("getDriverrrrrrrrrrrrr:"+ ListenersSocket.getDrivers(mContext, vehicleId, lat, longi, mDriverDaily, mDriverRental, mDriverOutstation));


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
                // mBinding.llBookMain.setVisibility(View.VISIBLE);
                mBinding.llNoCab.setVisibility(View.GONE);
                //mBinding.rltruckitems.setVisibility(View.VISIBLE);

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
                                // mBinding.llBookMain.setVisibility(View.VISIBLE);
                                mBinding.llNoCab.setVisibility(View.GONE);
                                mBinding.tvETA.setVisibility(View.VISIBLE);
                                //mBinding.tvETA.setText(finalMETAmin);
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
                            //mBinding.tvETA.setVisibility(View.VISIBLE);
                            mBinding.rltruckitems.setVisibility(View.VISIBLE);
                            // mBinding.llBookMain.setVisibility(View.VISIBLE);
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

    private void getEstimatedFare() {

        if (SocketConnection.isConnected()) {
            for (int i = 0; i < 6; i++) {
                //EmitDistanceMatrixService(truckBean.getResult().get(i).getVehicle_city_id(), truckBean.getResult().get(i).getVehicle_type_id());
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    public void onDirectionResponse(PolylineOptions lineOptions, String origin, String destionation) {
        if (lineOptions != null) {
            String[] originArray = origin.split(",");
            LatLng mOriginLatLng = new LatLng(Double.parseDouble(originArray[0]), Double.parseDouble(originArray[1]));

            String[] destinationArray = destionation.split(",");
            LatLng mDestionationLatLng = new LatLng(Double.parseDouble(destinationArray[0]), Double.parseDouble(destinationArray[1]));

            mMap.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            // Log.e("Booking status", booking_status);
            mBinding.llBookMain.setVisibility(View.GONE);
            if (destination != null) {
                destination.remove();
            }

            if (source != null) {
                source.remove();
            }
            source = mMap.addMarker(new MarkerOptions().position(mOriginLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from)));
            destination = mMap.addMarker(new MarkerOptions().position(mDestionationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_red)));
            mBinding.constBottom.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = getResources().getDimensionPixelSize(R.dimen._250sdp);

            params.setMargins(0, mBinding.topframe.getHeight(), 0, margin);
            mBinding.relMap.setLayoutParams(params);
            mBinding.llBottom.setVisibility(View.VISIBLE);

            builder.include(mOriginLatLng);
            builder.include(mDestionationLatLng);
            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;

            int padding = (int) (width * 0.20);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            if (mMap != null) {
                mMap.animateCamera(cu);
            }


        }
    }

    /*private void getPricesforcab(final String ToLat, final String ToLong) throws JSONException {

        SessionPref sessionPref = new SessionPref(getActivity());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", sessionPref.user_userid);
        jsonObject.put("language", "english");
        jsonObject.put("token", sessionPref.token);

        jsonObject.put("book_from_address", mFromaddress);
        jsonObject.put("book_from_lat", mFromLat);
        jsonObject.put("book_from_long", mFromLong);

        jsonObject.put("book_to_address", mToaddress);
        jsonObject.put("book_to_lat", ToLat);
        jsonObject.put("book_to_long", ToLong);



        //jsonObject2 is the payload to server here you can use JsonObjectRequest

        String url = Config.APIURLPRODUCTION + "socketApi/distance_matrix_service_all";

        //String str = "{\"userid\":\"8\",\"language\":\"english\",\"token\":\"1136866456\",\"book_from_address\":\"\",\"book_from_lat\":\"28.602654\",\"book_from_long\":\"77.3272203\",\"book_to_address\":\"\",\"book_to_lat\":\"28.5145723\",\"book_to_long\":\"77.3754581\"}";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("resprice", response.toString());
                        try {
                            //TODO: Handle your response here
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.print(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();

                    }


                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue queue = Volley.newRequestQueue(getActivity());


        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {

            @Override

            public byte[] getBody() throws AuthFailureError {
                // String str = str;

                return jsonObject.toString().getBytes();
            }

            ;

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myReq);


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL = url;

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY1", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY2", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    Log.e("responsetring", responseString);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }*/

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Ski data from server - " + response);
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Ski error connect - " + error);
            }
        };
    }
    /*private void getBuySubscriptionplan(){
        try {
            NetworkConn networkConn = NetworkConn.getInstance();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userid", "8");
            jsonObject.put("language","english");
            jsonObject.put("token","1136866456");

            jsonObject.put("book_from_address", "");
            jsonObject.put("book_from_lat","28.602654");

            jsonObject.put("book_from_long","77.3272203");
            jsonObject.put("book_to_address","");

            jsonObject.put("book_to_lat","28.5145723");
            jsonObject.put("book_to_long","77.3754581");

            networkConn.makeRequest(getActivity(), networkConn.createRawDataRequest("http://www.iotnods.com/admin/api/socketApi/distance_matrix_service_all",jsonObject.toString()),"http://www.iotnods.com/admin/api/socketApi/distance_matrix_service_all",this,mErrorLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   /* private void postNewComment(){
        SessionPref sessionPref = new SessionPref(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST,"http://www.iotnods.com/admin/api/socketApi/distance_matrix_service_all", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("resprice",response);

                Toast.makeText(getActivity(),"Successfully buy subscription",Toast.LENGTH_LONG).show();
                // mPostCommentResponse.requestCompleted();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("resErr",error.toString());
                //mPostCommentResponse.requestEndedWithError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> jsonObject = new HashMap<String, String>();
                jsonObject.put("userid", sessionPref.user_userid);
                jsonObject.put("language","english");
                jsonObject.put("token",sessionPref.token);
                jsonObject.put("book_from_address", mFromaddress);
                jsonObject.put("book_from_lat",mFromLat);
                jsonObject.put("book_from_long",mFromLong);
                jsonObject.put("book_to_address",mToaddress);
                jsonObject.put("book_to_lat",mToLat);
                jsonObject.put("book_to_long",mToLong);

                return jsonObject;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("userid",sessionPref.user_userid);
                params.put("token",sessionPref.token);
                return params;
            }
        };
        queue.add(sr);
    }*/


 /*   @Override
    public void onResponse(String response, String eventType) {
        Log.e("requestRes", response);
    }

    @Override
    public void onNoNetworkConnectivity() {

    }

    @Override
    public void onRequestRetry() {

    }

    @Override
    public void onRequestFailed() {

    }
*/


    public void postRequest(String duration, String distance) throws IOException {
        //returnDistanceDuration();

        cabPricesModels.clear();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = Config.APIURLPRODUCTION + "socketApi/distance_matrix_service_all";

        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("token", mSessionPref.token);

            jsonObject.put("book_from_address", mFromaddress);
            jsonObject.put("book_from_lat", mFromLat);

            jsonObject.put("book_from_long", mFromLong);
            jsonObject.put("book_to_address", mToaddress);

            jsonObject.put("book_to_lat", mToLat);
            jsonObject.put("book_to_long", mToLong);
            jsonObject.put("book_from_city", mFromCity);
            jsonObject.put("book_to_city", mToCity);

            jsonObject.put("distance", distance);
            jsonObject.put("eta", duration);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.e("jsonObjectPriceparams", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = null;


        if (jsonObjectRequest != null)
            jsonObjectRequest.cancel();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        Log.e("cancelRideRequest", jsonResponse + "");
                        mArrayVehicaleFare.clear();
                        mArrayETA.clear();
                        mArrayCapacity.clear();
                        String mMessage = jsonResponse.toString();

                        results.clear();
                        GetPricesForVehicelModel getPricesForVehicelModel = new Gson().fromJson(jsonResponse.toString(), GetPricesForVehicelModel.class);
                        results.addAll(getPricesForVehicelModel.getResult());
                        /*try {
                            JSONObject jsonObject1 = new JSONObject(mMessage);
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            Log.e("jsonObjectPrice", jsonObject1.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                Log.e("fare", jsonObject2.getString("fare_of_distance"));
                                isclick = false;
                                if (i == 0 && !isclick) {
                                 //   EmitDistanceMatrixService(vehicle_city_id, "3");
                                    mStrEstimatedFareSend = jsonObject2.getString("fare_of_distance");
                                }

                                if (jsonObject2.getString("vehicle_name").equalsIgnoreCase("Wee")) {
                                    //capacity by ID
                                    mArrayCapacity.add("4");
                                    String str = jsonObject2.getString("eta");

                                    if (str.contains("hour") && !str.contains("day")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }

                                        if (str.contains("hours")) {

                                            str = str.replace("hours", "/");
                                        } else {

                                            str = str.replace("hour", "/");
                                        }

                                        String hourconvert[] = str.split("/");
                                        String hour = hourconvert[0].trim();
                                        String min = hourconvert[1].trim();

                                        int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, mintotal);
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    } else if (str.contains("min")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    }


                                    mArrayETA.add(str);


                                    mArrayVehicaleFare.add(jsonObject2.getString("fare_of_distance"));
                                    totalDistanceforTrip = jsonObject2.getString("kilometers");
                                }

                                if (jsonObject2.getString("vehicle_name").equalsIgnoreCase("Compact")) {
                                    //capacity by ID
                                    mArrayCapacity.add("4");
                                    String str = jsonObject2.getString("eta");

                                    if (str.contains("hour") && !str.contains("day")) {
                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        if (str.contains("hours")) {

                                            str = str.replace("hours", "/");
                                        } else {

                                            str = str.replace("hour", "/");
                                        }
                                        String hourconvert[] = str.split("/");
                                        String hour = hourconvert[0].trim();
                                        String min = hourconvert[1].trim();

                                        int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, mintotal);
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    } else if (str.contains("min")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    }


                                    mArrayETA.add(str);
                                    mArrayVehicaleFare.add(jsonObject2.getString("fare_of_distance"));
                                    totalDistanceforTrip = jsonObject2.getString("kilometers");


                                }
                                if (jsonObject2.getString("vehicle_name").equalsIgnoreCase("Sedan")) {
                                    //capacity by ID
                                    mArrayCapacity.add("4");
                                    String str = jsonObject2.getString("eta");

                                    if (str.contains("hour") && !str.contains("day")) {
                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        if (str.contains("hours")) {

                                            str = str.replace("hours", "/");
                                        } else {

                                            str = str.replace("hour", "/");
                                        }
                                        String hourconvert[] = str.split("/");
                                        String hour = hourconvert[0].trim();
                                        String min = hourconvert[1].trim();

                                        int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, mintotal);
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    } else if (str.contains("min")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    }


                                    mArrayETA.add(str);
                                    mArrayVehicaleFare.add(jsonObject2.getString("fare_of_distance"));
                                    totalDistanceforTrip = jsonObject2.getString("kilometers");


                                }
                                if (jsonObject2.getString("vehicle_name").equalsIgnoreCase("Luxury Coupe")) {
                                    //capacity by ID
                                    mArrayCapacity.add("4");
                                    String str = jsonObject2.getString("eta");

                                    if (str.contains("hour") && !str.contains("day")) {
                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        if (str.contains("hours")) {

                                            str = str.replace("hours", "/");
                                        } else {

                                            str = str.replace("hour", "/");
                                        }
                                        String hourconvert[] = str.split("/");
                                        String hour = hourconvert[0].trim();
                                        String min = hourconvert[1].trim();

                                        int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, mintotal);
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    } else if (str.contains("min")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    }

                                    mArrayETA.add(str);
                                    mArrayVehicaleFare.add(jsonObject2.getString("fare_of_distance"));
                                    totalDistanceforTrip = jsonObject2.getString("kilometers");


                                }
                                if (jsonObject2.getString("vehicle_name").equalsIgnoreCase("MPV")) {
                                    //capacity by ID
                                    mArrayCapacity.add("6");
                                    String str = jsonObject2.getString("eta");

                                    if (str.contains("hour") && !str.contains("day")) {
                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        if (str.contains("hours")) {

                                            str = str.replace("hours", "/");
                                        } else {

                                            str = str.replace("hour", "/");
                                        }
                                        String hourconvert[] = str.split("/");
                                        String hour = hourconvert[0].trim();
                                        String min = hourconvert[1].trim();

                                        int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, mintotal);
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    } else if (str.contains("min")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    }


                                    mArrayETA.add(str);
                                    mArrayVehicaleFare.add(jsonObject2.getString("fare_of_distance"));
                                    totalDistanceforTrip = jsonObject2.getString("kilometers");


                                }
                                if (jsonObject2.getString("vehicle_name").equalsIgnoreCase("Auto")) {
                                    //capacity by ID
                                    mArrayCapacity.add("3");
                                    String str = jsonObject2.getString("eta");

                                    if (str.contains("hour") && !str.contains("day")) {
                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        if (str.contains("hours")) {

                                            str = str.replace("hours", "/");
                                        } else {

                                            str = str.replace("hour", "/");
                                        }
                                        String hourconvert[] = str.split("/");
                                        String hour = hourconvert[0].trim();
                                        String min = hourconvert[1].trim();

                                        int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, mintotal);
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    } else if (str.contains("min")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    }


                                    mArrayETA.add(str);
                                    mArrayVehicaleFare.add(jsonObject2.getString("fare_of_distance"));
                                    totalDistanceforTrip = jsonObject2.getString("kilometers");


                                }
                                if (jsonObject2.getString("vehicle_name").equalsIgnoreCase("Bike")) {
                                    //capacity by ID
                                    mArrayCapacity.add("1");
                                    String str = jsonObject2.getString("eta");

                                    if (str.contains("hour") && !str.contains("day")) {
                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        if (str.contains("hours")) {

                                            str = str.replace("hours", "/");
                                        } else {

                                            str = str.replace("hour", "/");
                                        }
                                        String hourconvert[] = str.split("/");
                                        String hour = hourconvert[0].trim();
                                        String min = hourconvert[1].trim();

                                        int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, mintotal);
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    } else if (str.contains("min")) {

                                        if (str.contains("mins")) {
                                            str = str.replace("mins", "");

                                        } else if (str.contains("min")) {
                                            str = str.replace("min", "");

                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        System.out.println("Current Date = " + calendar.getTime());
                                        // Add 10 minutes to current date
                                        calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
                                        System.out.println("Updated Date = " + calendar.getTime());
                                        Date date = calendar.getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                        String formattedDate = dateFormat.format(date);
                                        str = formattedDate;
                                    }


                                    mArrayETA.add(str);
                                    mArrayVehicaleFare.add(jsonObject2.getString("fare_of_distance"));
                                    totalDistanceforTrip = jsonObject2.getString("kilometers");


                                }
                            }
                        } catch (Exception r) {
                            r.printStackTrace();

                        }*/

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    for (int i = 0; i < mArrayVehicaleFare.size(); i++) {
                                        System.out.println("Price>>>>>>>>>>>>>>>>>" + mArrayVehicaleFare.get(i));
                                    }
                                    mAdapter.row_index = 0;
                                    mAdapter.notifyDataSetChanged();

                                    Log.e("height3", mBinding.rvhometrucks.getHeight() + "");


                                    //final float scale = getContext().getResources().getDisplayMetrics().density;
                                    // int pixels = (int) (dps * scale + 0.5f);

                                    // Stuff that updates the UI

                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

    }

    private void getAddressFromLatLong(double lattitude, double longitude, final volleyCallback callback) {
        final String resultAddress;
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lattitude + "," + longitude + "&language=en&key=AIzaSyAVNsvTkS6bt2-q12RgQoL0S9g7o_VbxeQ";
        System.out.println("urlaaaaaaaa:"+ url);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("resGeocodeApi", response);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");


                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                    JSONArray jsonArray1 = jsonObject1.getJSONArray("address_components");
                    for (int i = 0; i < jsonArray1.length(); i++) {

                        JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                        JSONArray jsonArray2 = jsonObject2.getJSONArray("types");
                        for (int j = 0; j < jsonArray2.length(); j++) {

                            if (jsonArray2.get(j).toString().equalsIgnoreCase("locality")) {
                                String str = jsonObject2.getString("long_name");

                                Log.e("citylocality", str);

                            }

                        }


                    }

                    String address = jsonObject1.getString("formatted_address");

                    callback.onSuccessResponse(address, response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollyerror", String.valueOf(error));
            }
        });
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);

    }

    public interface volleyCallback {
        void onSuccessResponse(String result, String json);
    }

    //get data from directionApi for sending req to server
    private void returnDistanceDuration() {

        String GOOGLE_DIRECTIONURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + mSourceLat + ","
                + mSourceLong + "&destination=" + mDesLat + "," + mDesLong + "&key=" + getAppString(R.string.direction_api_key) + "&sensor=false&mode=driving&alternatives=true";
        System.out.println("getadirectionurl:" + GOOGLE_DIRECTIONURL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GOOGLE_DIRECTIONURL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("googleapires", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("routes");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray jsonArrayLegs = jsonObject.getJSONArray("legs");
                    JSONObject jsonObject1 = jsonArrayLegs.getJSONObject(0);
                    JSONObject jsonObjectDistance = jsonObject1.getJSONObject("distance");
                    String distance = jsonObjectDistance.getString("text");
                    JSONObject jsonObjectDuration = jsonObject1.getJSONObject("duration");
                    String duration = jsonObjectDuration.getString("text");
                    Log.e("dis", distance + " : " + duration + "duration on mins: " + getMins(duration));
                    //getMins(duration);

                    if (distance.contains("km")) {
                        distance = distance.replace("km", "");
                    } else if (distance.contains("m")) {
                        distance = distance.replace("m", "");

                    }
                    postRequest(getMins(duration), distance.trim());

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollyerror", String.valueOf(error));
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);

    }

    private String getMins(String str) {

        if (str.contains("hour") && !str.contains("day")) {
            if (str.contains("mins")) {
                str = str.replace("mins", "");

            } else if (str.contains("min")) {
                str = str.replace("min", "");

            }
            if (str.contains("hours")) {

                str = str.replace("hours", "/");
            } else {

                str = str.replace("hour", "/");
            }
            String hourconvert[] = str.split("/");
            String hour = hourconvert[0].trim();
            String min = hourconvert[1].trim();

            int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

            return String.valueOf(mintotal);

        } else if (str.contains("hour") && str.contains("day")) {

            String strDay[] = str.split("day");

            String strhour = strDay[1].trim();

            if (strhour.contains("mins")) {
                strhour = strhour.replace("mins", "");

            } else if (strhour.contains("min")) {
                strhour = strhour.replace("min", "");

            }
            if (strhour.contains("hours")) {

                strhour = strhour.replace("hours", "/");
            } else {

                strhour = strhour.replace("hour", "/");
            }
            String hourconvert[] = strhour.split("/");
            String hour = hourconvert[0].trim();
            //String min = hourconvert[1].trim();

            int mintotal = (Integer.valueOf(strDay[0].trim()) * 24 * 60) + (Integer.valueOf(hour) * 60);

            return String.valueOf(mintotal);

        } else if (str.contains("min")) {

            if (str.contains("mins")) {
                str = str.replace("mins", "");

            } else if (str.contains("min")) {
                str = str.replace("min", "");

            }
            return str;
        }

        return "";
    }

}

