package com.sudrives.sudrives.fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.maps.android.SphericalUtil;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.SocketListnerMethods.ListenersSocket;
import com.sudrives.sudrives.activity.CancelBookingActivity;
import com.sudrives.sudrives.activity.ChatActivity;
import com.sudrives.sudrives.activity.MyWalletActivity;
import com.sudrives.sudrives.activity.SendRideStatusActivity;
import com.sudrives.sudrives.databinding.ActivityLiveTripBinding;
import com.sudrives.sudrives.databinding.DialogSelectPaymentBinding;
import com.sudrives.sudrives.direction.GetDirectionDataTask;
import com.sudrives.sudrives.listeners.onlivetripScreenClick;
import com.sudrives.sudrives.model.LiveTripModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseFragment;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GPSTracker;
import com.sudrives.sudrives.utils.GetLocation;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.GpsTrackerShashank;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.LatLngInterpolator;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.emitter.Emitter;


public class LiveFragment extends BaseFragment implements OnMapReadyCallback, onlivetripScreenClick {
    private String mReferralCode = "", mReferralText = "", mtripId = "", mShowCashOption = "";
    private ActivityLiveTripBinding mBinding;
    private ErrorLayout mErrorLayout;
    private GoogleMap mMap;
    private Context mContext;
    private ArrayList<LiveTripModel> myBookingsListModel;
    private GetLocation getLocation;

    public LinearLayoutManager mLayoutManager;
    String tripFare = "0.0";
    private String vehicle_marker_img = "", live_marker_img = "", type_of_booking = "";
    private SessionPref mSessionPref;

    private PolylineOptions lineOptions;

    private LatLng originLatLng, destLatLng;
    private String newLatLong = "", originAddressLatLong = "", destinationAddress = "", updatedNewLatLong;
    private boolean flagToSHowPath = true;
    private double originLat = 0.0, originLong = 0.0;
    private double destinationLat = 0.0, destinationLong = 0.0;
    private int i = 0;

    private SupportMapFragment fragment;
    private String driver_id = "", driver_mobile = "", bookOriginAddress = "", booking_status = "", mToLat = "0.0", mToLong = "0.0", mToaddress = "", isOnlinePayment = "";
    private String drivername = "", driverVehicleNumber = "";
    private String notification_type, response, mTripResponse = "";
    private SupportMapFragment mapFragment;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 8000;
    private double mCurrentLong = 0.0, mCurrentLat = 0.0;

    private Marker marker;
    private TextView iv_help;
    public static boolean tagReLoadData = true;
    private View rootView;

    private static final int REQUEST_CODE_TO_LOC_LIVE = 5;
    private double placePickerToLat = 0.0, placePickerToLong = 0.0;
    private boolean isSet = true;
    private boolean onDropClick = false, onSetCurrentLoc = false;
    double oldlat;
    double oldlong;
    String bookingStatusdata = "";
    private DialogSelectPaymentBinding layoutBinder;
    private Dialog dialog;
    //Timer timer;
    int liveMarkerStatus = 0;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    androidx.appcompat.app.AlertDialog show;
    Polyline line = null;
   // GPSTracker gpsTrackerShashank;
    // TODO: Rename and change types and number of parameters
    public static LiveFragment newInstance() {
        LiveFragment fragment = new LiveFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_live_trip, container, false);
        rootView = mBinding.getRoot();

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Config.PATH_TRACKING = false;
        getControls(rootView);

        KeyboardUtil.setupUI(mBinding.rlTopLivetrip, getActivity());
        if (checkConnection()) {
        } else {

            //dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
        }
        if (fragment == null) {

            fragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map_view_live);

            fragment.getMapAsync(this);
        }
    }

    private void getControls(View view) {

       // gpsTrackerShashank = new GPSTracker(getActivity());
        mContext = getActivity();
        mSessionPref = new SessionPref(mContext);

        mErrorLayout = new ErrorLayout(mContext, view.findViewById(R.id.error_layout));
        getLocation = new GetLocation(mContext);
        getLocation.checkLocationPermission();
        mBinding.imgCross.setVisibility(View.GONE);
        mBinding.imgCross.setOnClickListener(this::liveToAddressClick);
        mBinding.tvDestinationTrip.setOnClickListener(this::liveToAddressClick);
        mBinding.tvDestinationTrip.setEnabled(false);

        mBinding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                /*This will be the actual content you wish you share.*/
                String shareBody = "Driver name "+drivername+"\nDriver number: "+driver_mobile+"\nVehicle number: "+driverVehicleNumber+"\nTrip id: "+mtripId+
                        "\nTrack link: " +Config.DOMAIN_URL+"trip_track/index.php/?tripid="+mtripId;
                /*The type of the content is text, obviously.*/
                intent.setType("text/plain");
                /*Applying information Subject and Body.*/
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey I am sharing my ride status");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                /*Fire!*/
                startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
            }
        });

        //  mBinding.liveTripBottom.ivCallDriver.setColorFilter(getResources().getColor(R.color.dark_yellow));

        if (checkConnection()) {
            if (SocketConnection.isConnected()) {
                emitTrips();

            } else {

                mBinding.rlTrips.setVisibility(View.GONE);
                mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
            }
        } else {
            mBinding.rlTrips.setVisibility(View.GONE);
            mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
        }

        iv_help = (TextView) getActivity().findViewById(R.id.iv_help);
        //iv_help.setVisibility(View.VISIBLE);

        mBinding.emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mtripId.trim().length() != 0) {
                    if (booking_status.equalsIgnoreCase("2")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.help_dialog, null);
                        LinearLayout ll_EnableEmergencyMode = dialogLayout.findViewById(R.id.ll_EnableEmergencyMode);
                        LinearLayout ll_SendRideStatus = dialogLayout.findViewById(R.id.ll_SendRideStatus);
                        LinearLayout ll_Call_police = dialogLayout.findViewById(R.id.ll_call_police_emergency);
                        ImageView ivCross = dialogLayout.findViewById(R.id.ivCross);
                        builder.setView(dialogLayout);
                        AlertDialog testDialog = builder.create();

                        ll_Call_police.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "100"));
                                startActivity(intent);
                            }
                        });


                        ll_SendRideStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                testDialog.dismiss();

                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                /*This will be the actual content you wish you share.*/
                                String shareBody = "Driver name "+drivername+"\nDriver number: "+driver_mobile+"\nVehicle number: "+driverVehicleNumber+"\nTrip id: "+mtripId+
                                        "\nTrack link: " +Config.DOMAIN_URL+"trip_track/index.php/?tripid="+mtripId;
                                /*The type of the content is text, obviously.*/
                                intent.setType("text/plain");
                                /*Applying information Subject and Body.*/
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey I am sharing my ride status");
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                /*Fire!*/
                                startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
                                /*Create an ACTION_SEND Intent*/

                               /* Intent intent = new Intent(getActivity(), SendRideStatusActivity.class);
                                intent.putExtra("Clatitude", mCurrentLat);
                                intent.putExtra("Clongitude", mCurrentLong);
                                intent.putExtra("tripId", mtripId);
                                startActivityForResult(intent, REQUEST_CODE_CONFIRM_RETURN);
*/
                            }
                        });

                        ll_EnableEmergencyMode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                testDialog.dismiss();
                                if (mtripId.length() != 0) {
                                    requestEmergencyContact(mtripId);
                                    SocketConnection.attachListener(Config.LISTENER_GET_CONTACT_SUPPORT, getEmergency);

                                } else {
                                    mErrorLayout.showAlert(getResources().getString(R.string.no_ride_available_to_send_ride_status), ErrorLayout.MsgType.Error, false);

                                }
                            }
                        });

                        ivCross.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                testDialog.dismiss();

                            }
                        });


                        testDialog.show();


                        //startActivity(new Intent(HomeMenuActivity.this, RefundRequestActivity.class));


                    } else {
                        mErrorLayout.showAlert(getResources().getString(R.string.no_ride_started_to_send_ride_status), ErrorLayout.MsgType.Error, false);

                    }
                } else {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_ride_available_to_send_ride_status), ErrorLayout.MsgType.Error, false);

                }
            }
        });
        iv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mtripId.trim().length() != 0) {
                    if (booking_status.equalsIgnoreCase("2")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.help_dialog, null);
                        LinearLayout ll_EnableEmergencyMode = dialogLayout.findViewById(R.id.ll_EnableEmergencyMode);
                        LinearLayout ll_SendRideStatus = dialogLayout.findViewById(R.id.ll_SendRideStatus);
                        LinearLayout ll_Call_police = dialogLayout.findViewById(R.id.ll_call_police_emergency);
                        ImageView ivCross = dialogLayout.findViewById(R.id.ivCross);
                        builder.setView(dialogLayout);
                        AlertDialog testDialog = builder.create();

                        ll_Call_police.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "100"));
                                startActivity(intent);
                            }
                        });


                        ll_SendRideStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                testDialog.dismiss();
                                /*Create an ACTION_SEND Intent*/

                               /* Intent intent = new Intent(getActivity(), SendRideStatusActivity.class);
                                intent.putExtra("Clatitude", mCurrentLat);
                                intent.putExtra("Clongitude", mCurrentLong);
                                intent.putExtra("tripId", mtripId);
                                startActivityForResult(intent, REQUEST_CODE_CONFIRM_RETURN);
*/
                            }
                        });

                        ll_EnableEmergencyMode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                testDialog.dismiss();
                                if (mtripId.length() != 0) {
                                    requestEmergencyContact(mtripId);
                                    SocketConnection.attachListener(Config.LISTENER_GET_CONTACT_SUPPORT, getEmergency);

                                } else {
                                    mErrorLayout.showAlert(getResources().getString(R.string.no_ride_available_to_send_ride_status), ErrorLayout.MsgType.Error, false);

                                }
                            }
                        });

                        ivCross.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                testDialog.dismiss();

                            }
                        });


                        testDialog.show();


                        //startActivity(new Intent(HomeMenuActivity.this, RefundRequestActivity.class));


                    } else {
                        mErrorLayout.showAlert(getResources().getString(R.string.no_ride_started_to_send_ride_status), ErrorLayout.MsgType.Error, false);

                    }
                } else {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_ride_available_to_send_ride_status), ErrorLayout.MsgType.Error, false);

                }


            }
        });

        mBinding.liveTripBottom.tvRide.setText(Html.fromHtml("<b>" + getAppString(R.string.ride_accepted) + "</b> : " + getAppString(R.string.your_ride_is_on_its_way)));
        mBinding.liveTripBottom.ivCallDriver.setOnClickListener(this::callDriver);
        mBinding.liveTripBottom.ivChatDriver.setOnClickListener(this::chatDriver);
        mBinding.liveTripBottom.btnCancelBooking.setOnClickListener(this::cancelBooking);
        mBinding.liveTripBottom.llSelectPaymentLive.setOnClickListener(this::selectPaymentClick);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view_live);

        mapFragment.getMapAsync(this);

        myBookingsListModel = new ArrayList<>();


        if (checkPlayServices()) {

        } else {
            Toast.makeText(mContext, getAppString(R.string.location_not_supported), Toast.LENGTH_SHORT).show();
        }
        if (mMap != null) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                  //  mapClick(rootView);
                }
            });
        }

        //emitTripsData(mtripId);



    }

    /*
    \\\\\
    */
    private void liveToAddressClick(View view) {

        if (type_of_booking.equalsIgnoreCase("0")) {
            if (checkConnection()) {

                openAutocompleteActivity(REQUEST_CODE_TO_LOC_LIVE);

            } else {
                mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

            }
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


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.rlTopLivetrip);
        if (mtripId.trim().length() != 0) {
            if (booking_status.equalsIgnoreCase("2") && type_of_booking.equalsIgnoreCase("0")) {
              //  iv_help.setVisibility(View.VISIBLE);
                // iv_help.setImageResource(R.mipmap.help);
                mBinding.tvDestinationTrip.setEnabled(true);
                mBinding.imgCross.setVisibility(View.VISIBLE);
            } else {
                emitTripsData(mtripId);
              //  iv_help.setVisibility(View.GONE);
                mBinding.tvDestinationTrip.setEnabled(false);
                mBinding.imgCross.setVisibility(View.GONE);
            }

        } else {
           // iv_help.setVisibility(View.GONE);
            mBinding.tvDestinationTrip.setEnabled(false);
            mBinding.imgCross.setVisibility(View.GONE);
        }
        if (tagReLoadData) {
            if (SocketConnection.isConnected()) {
                emitTrips();

            } else {
                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
            }
        }
    }

    /*call to driver*/
    private void callDriver(View view) {
        if (driver_mobile != null && driver_mobile != "") {
            int layoutPopup = R.layout.duuble_button_dialog;
            AppDialogs.DoubleButtonWithCallBackVersionDialog(mContext, layoutPopup, true, false, 0, "Call Driver",
                    mContext.getResources().getString(R.string.call_to), mContext.getResources().getString(R.string.call),
                    mContext.getResources().getString(R.string.cancel),
                    new AppDialogs.Doublebuttonpincallback() {
                        @Override
                        public void doublebuttonok(String from) {
                            callDriverPhone(mSessionPref.user_mobile, driver_mobile, mtripId);

                            //Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            //callIntent.setData(Uri.parse("tel:" + driver_mobile));
                            //startActivity(callIntent);


                        }
                    }, new AppDialogs.DoublebuttonCancelcallback() {
                        @Override
                        public void doublebuttonCancel(String from) {


                        }


                    }, new AppDialogs.Crosscallback() {


                        @Override
                        public void crossButtonCallback(String from) {

                        }


                    });


        } else {
            mErrorLayout.showAlert(getAppString(R.string.sorry_contact_number_not_available), ErrorLayout.MsgType.Error, true);
        }
    }
    private void callDriverPhone(String userphone, String driver, String tripid){

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.GET,"http://14.97.59.83:8081/SEATECHCCIVR/CallFromApp?driver="+driver+"&user="+userphone+"&trip_id="+tripid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("resPhoneString",response);
                try {


                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("1")){

                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + jsonObject1.getString("call")));
                        startActivity(callIntent);
                    }


                }catch (JSONException e){

                }


                // mPostCommentResponse.requestCompleted();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("res",error.toString());
                //mPostCommentResponse.requestEndedWithError(error);
            }
        });/*{
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid",AppPreference.loadStringPref(BuySubscriptionActivity.this, AppPreference.KEY_USER_ID));
                params.put("subscription_plan_id",id);
                params.put("online_payment_id", payment_id);
                params.put("online_payment_status",onlinePaymentStatus);
                params.put("online_payment_amount",onlinePaymentAmount);
                params.put("razorpay_order_id",razorpayOrderId);
                params.put("razorpay_signature",razorpaySignature);
                params.put("email",email);
                params.put("mobile",mobile);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("userid",AppPreference.loadStringPref(BuySubscriptionActivity.this, AppPreference.KEY_USER_ID));
                params.put("token",AppPreference.loadStringPref(BuySubscriptionActivity.this, AppPreference.KEY_TOKEN));
                return params;
            }
        };*/
        queue.add(sr);

    }
    private void chatDriver(View view) {
        emitTripsData(mtripId);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("driver_id", driver_id);
        intent.putExtra("trip_id",mtripId);
        startActivity(intent);

    }


    /*cancel trip*/
    private void cancelBooking(View view) {
        int layoutPopup;
        layoutPopup = R.layout.duuble_button_dialog;
        AppDialogs.DoubleButtonWithCallBackVersionDialog(mContext, layoutPopup, true, false, R.drawable.alert_24dp, getString(R.string.are_you_sure_you_want_to_cancel_the_booking),
                "", getString(R.string.yes),
                getString(R.string.no),
                new AppDialogs.Doublebuttonpincallback() {
                    @Override
                    public void doublebuttonok(String from) {
                        startActivity(new Intent(mContext, CancelBookingActivity.class).putExtra("tripId", mtripId));

                    }
                }, new AppDialogs.DoublebuttonCancelcallback() {
                    @Override
                    public void doublebuttonCancel(String from) {

                    }

                }, new AppDialogs.Crosscallback() {


                    @Override
                    public void crossButtonCallback(String from) {

                    }

                });


    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }


    /***************  API implementation ********************/

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

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 11);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629)));
        mMap.animateCamera(cameraUpdate);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               // mapClick(rootView);
            }
        });

    }

    //************ Socket Call
//*************** get Trip list
    Emitter.Listener getTripsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Log.e("Get  List", (String) args[0]);
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mTripResponse = (String) args[0];
                            parseTrips(mTripResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                            } catch (Exception e1) {

                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    };

    public void emitTrips() {
        tagReLoadData = false;
        if (SocketConnection.isConnected()) {
            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_LIVE_TRIPS, getTripsListener);

            JSONObject jsonObject = new JSONObject();
            try {
                mSessionPref = new SessionPref(mContext);

                jsonObject.put("userid", mSessionPref.user_userid);
                jsonObject.put("token", mSessionPref.token);
                jsonObject.put("language", Config.SELECTED_LANG);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SocketConnection.emitToServer(Config.EMIT_GET_LIVE_TRIPS, jsonObject);
        } else {

        }
    }

    // get and set trip data
    public void parseTrips(String response) {
        //Log.e("Response In Frag", response);
        JSONArray mTripList = new JSONArray();
        BaseModel mBaseModel = new BaseModel(mContext);
        parseTripsData("");
        if (response != null) {
            if (mBaseModel.isParse(response)) {
                mBinding.rlTrips.setVisibility(View.VISIBLE);
                mBinding.tvNoDataFound.setVisibility(View.GONE);

                mTripList = mBaseModel.getResultArray();
                if (mTripList != null && mTripList.length() > 0) {
                    mBinding.layoutTripTime.llTime.setVisibility(View.VISIBLE);
                    try {
                        mtripId = mTripList.getJSONObject(0).optString("booking_id");
                        if (mTripList.length() == 1) {

                           // mBinding.layoutTripTime.tvBookingTime.setText(mTripList.getJSONObject(0).optString("eta")+" to reach");
                            mBinding.layoutTripTime.llFirstLive.setVisibility(View.VISIBLE);
                            mBinding.layoutTripTime.llFirstLive.setBackground(getResources().getDrawable(R.drawable.button_shap));
                            mBinding.layoutTripTime.llSecondLive.setVisibility(View.GONE);

                        } else if (mTripList.length() == 2) {
                            mBinding.layoutTripTime.llFirstLive.setVisibility(View.VISIBLE);
                            mBinding.layoutTripTime.llFirstLive.setBackground(getResources().getDrawable(R.drawable.button_shap));
                            mBinding.layoutTripTime.llSecondLive.setVisibility(View.VISIBLE);
                            /*try {
                              //  mBinding.layoutTripTime.tvBookingTime.setText(mTripList.getJSONObject(0).optString("eta")+ " to reach");
                               // mBinding.layoutTripTime.tvBookingTime2.setText(mTripList.getJSONObject(1).optString("eta")+ " to reach");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                        }

                        if (SocketConnection.isConnected()) {
                            emitTripsData(mtripId);

                        } else {
                            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                            mBinding.rlTrips.setVisibility(View.GONE);
                            mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mBinding.rlTrips.setVisibility(View.GONE);
                        mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                    myBookingsListModel.clear();
                    //  for (int i = 0; i < mTripList.length(); i++) {

                    myBookingsListModel.addAll(LiveTripModel.getMyTripList(mTripList));
                    //  }


                    JSONArray finalMTripList = mTripList;
                    mBinding.layoutTripTime.llFirstLive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                mtripId = finalMTripList.getJSONObject(0).optString("booking_id");

                                mBinding.layoutTripTime.llFirstLive.setBackground(getResources().getDrawable(R.drawable.button_shap));
                                mBinding.layoutTripTime.llSecondLive.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                                mBinding.layoutTripTime.tvBookingTime.setTextColor(getResources().getColor(R.color.colorGrayDark));
                                mBinding.layoutTripTime.tvBookingTime2.setTextColor(getResources().getColor(R.color.colorGrayDark));

                                if (SocketConnection.isConnected()) {
                                    emitTripsData(finalMTripList.getJSONObject(0).optString("booking_id"));

                                } else {
                                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    mBinding.layoutTripTime.llSecondLive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finalMTripList.length() > 1) {

                                try {
                                    mtripId = finalMTripList.getJSONObject(1).optString("booking_id");

                                    mBinding.layoutTripTime.llFirstLive.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                                    mBinding.layoutTripTime.llSecondLive.setBackground(getResources().getDrawable(R.drawable.button_shap));

                                    mBinding.layoutTripTime.tvBookingTime.setTextColor(getResources().getColor(R.color.colorGrayDark));
                                    mBinding.layoutTripTime.tvBookingTime2.setTextColor(getResources().getColor(R.color.colorGrayDark));


                                    if (SocketConnection.isConnected()) {
                                        emitTripsData(finalMTripList.getJSONObject(1).optString("booking_id"));

                                    } else {
                                        mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });

                    //getRecyclerviewTrucks();

                } else {
                    mBinding.rlTrips.setVisibility(View.GONE);
                    mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                    mBinding.tvNoDataFound.setText(mBaseModel.Message);
                }


            } else {
                mBinding.rlTrips.setVisibility(View.GONE);
                mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                mBinding.tvNoDataFound.setText(mBaseModel.Message);

            }


        } else {
            mBinding.rlTrips.setVisibility(View.GONE);
            mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
        }
    }

    public void setDriverCurrentLocationData(String response) {
        // Log.e("driver location Live", response);
        try {
            JSONObject jObj = new JSONObject(response);

            int status = jObj.getInt("status");
            if (status == 1) {
                JSONObject resultObj = jObj.getJSONObject("result");

                Double lati = resultObj.getDouble("lat");
                Double longi = resultObj.getDouble("lang");
                String angle = resultObj.getString("angle");

                try {
                    oldlat = mCurrentLat;
                    oldlong = mCurrentLong;

                    mCurrentLat = lati;
                    mCurrentLong = longi;
                   // Toast.makeText(getActivity(),"getloc", Toast.LENGTH_SHORT).show();

                    if (oldlat != mCurrentLat && oldlong != mCurrentLong) {
                        Log.e("test",mCurrentLong+"");
                       // Toast.makeText(getActivity(),"getloc", Toast.LENGTH_SHORT).show();
                        updateMyLocation(new LatLng(oldlat, oldlong), new LatLng(mCurrentLat, mCurrentLong));
                       // GetDirectionDataTask getDirectionDataTask = new GetDirectionDataTask(getActivity());
                        //getDirectionDataTask.execute(getDirectionDataTask.getgoogleDirectionUrl(mCurrentLat + "," + mCurrentLong, destinationLat + "," + destinationLong, getAppString(R.string.direction_api_key)), mCurrentLat + "," + mCurrentLong, destinationLat + "," + destinationLong);


                        if (bookingStatusdata.equalsIgnoreCase("2")){
                             originAddressLatLong = String.valueOf(mCurrentLat+ "," + mCurrentLong);
                         GetDirectionDataTask getDirectionDataTask = new GetDirectionDataTask(getActivity());
                        getDirectionDataTask.execute(getDirectionDataTask.getgoogleDirectionUrl(originAddressLatLong, destinationAddress, getAppString(R.string.direction_api_key)), originAddressLatLong, destinationAddress);

                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                final Handler handler = new Handler();

                /*if (oldLatLng == null) {
                    oldLatLng = new LatLng(lati, longi);
                    return;
                }*/
                if (marker != null) {
                    final LatLng startPosition = marker.getPosition();
                    final LatLng finalPosition = new LatLng(lati, longi);

                    final long start = SystemClock.uptimeMillis();
                    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                    final float durationInMs = 3000;
                    final boolean hideMarker = false;


                    handler.post(new Runnable() {
                        long elapsed;
                        float t;
                        float v;

                        @Override
                        public void run() {
                            // Calculate progress using interpolator
                            elapsed = SystemClock.uptimeMillis() - start;
                            t = elapsed / durationInMs;
                            v = interpolator.getInterpolation(t);


                        }
                    });
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //*************** get Trip Data
    public void emitTripsData(String booking_id) {

        SocketConnection.attachSingleEventListener(Config.LISTENER_GET_TRIPS_DETAIL, getTripsDataListener);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("tripid", booking_id);
            jsonObject.put("typefor", "user");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        SocketConnection.emitToServer(Config.EMIT_GET_TRIP_DETAILS, jsonObject);


    }

    Emitter.Listener getTripsDataListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("Get Tripdata List", (String) args[0]);

            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String mTripDataResponse = (String) args[0];
                            parseTripsData(mTripDataResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                            } catch (Exception e1) {

                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                } catch (Exception e1) {

                }
            }
        }
    };


    Emitter.Listener getChangeAddressListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.e("Change Destination", (String) args[0]);
                        BaseModel mBaseModel = new BaseModel(mContext);
                        if ((String) args[0] != null) {
                            if (mBaseModel.isParse((String) args[0])) {
                                mBinding.tvDestinationTrip.setText(mToaddress);
                                mBinding.imgCross.setVisibility(View.VISIBLE);

                                //   Log.e("mtripId  ", mtripId);
                                if (SocketConnection.isConnected()) {
                                    emitTripsData(mtripId);

                                } else {
                                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                                    // Toast.makeText(mContext, getAppString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                mBinding.tvDestinationTrip.setEnabled(false);
                                mBinding.imgCross.setEnabled(false);
                                mBinding.imgCross.setVisibility(View.GONE);

                                mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                                // Toast.makeText(mContext, mBaseModel.Message, Toast.LENGTH_LONG).show();

                            }

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                } catch (Exception e1) {

                }
            }
        }
    };


    // get and set trip data
    public void parseTripsData(String response) {
        Log.e("Response In Frag", response);
        JSONObject mTripData = new JSONObject();
        BaseModel mBaseModel = new BaseModel(mContext);
        if (response != null) {
            if (mBaseModel.isParse(response)) {
                // Log.e("Json Response", response);

                mTripData = mBaseModel.getResultObject();

                try {
                    bookOriginAddress = mTripData.getString("book_from_address");
                    originLat = mTripData.getDouble("book_from_lat");
                    originLong = mTripData.getDouble("book_from_long");
                    destinationLat = mTripData.getDouble("book_to_lat");
                    destinationLong = mTripData.getDouble("book_to_long");
                    originAddressLatLong = mTripData.getString("book_from_lat") + "," + mTripData.getString("book_from_long");
                    newLatLong = mTripData.getString("book_from_lat") + "," + mTripData.getString("book_from_long");
                    originLatLng = new LatLng(mTripData.getDouble("book_from_lat"), mTripData.getDouble("book_from_long"));
                    String book_to_address = mTripData.getString("book_to_address");
                    destinationAddress = mTripData.getString("book_to_lat") + "," + mTripData.getString("book_to_long");
                    destLatLng = new LatLng(mTripData.getDouble("book_to_lat"), mTripData.getDouble("book_to_long"));
                    String booking_id = mTripData.getString("booking_id");
                    vehicle_marker_img = mTripData.getString("vehicle_marker_img");
                    String vehicle_image = mTripData.getString("vehicle_image");
                    live_marker_img = mTripData.getString("live_marker_img");
                    String driver_arrived_pickup = mTripData.getString("driver_arrived_pickup");
                    String minutes_toreach = mTripData.getString("minutes_to_reach");
                    bookingStatusdata = mTripData.getString("booking_status");
                    if (mTripData.getString("booking_status").equalsIgnoreCase("2"))
                        mBinding.layoutTripTime.tvBookingTime.setText(mTripData.getString("total_time")+" mins to reach");
                    else if(mTripData.getString("booking_status").equalsIgnoreCase("8"))
                        mBinding.layoutTripTime.tvBookingTime.setText("Driver arrived");
                    else
                        mBinding.layoutTripTime.tvBookingTime.setText("Arriving in: "+minutes_toreach);

                    if (driver_arrived_pickup.equalsIgnoreCase("1")){
                        mBinding.liveTripBottom.tvRide.setText("Driver Arrived at Location");
                        if (handler != null)
                        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

                        //timer.cancel();
                    }else {
                        if (handler != null)
                        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

                        handler.postDelayed(runnable = new Runnable() {
                            public void run() {
                                handler.postDelayed(runnable, delay);
                                emitTripsData(mtripId);
                            }
                        }, delay);
                    }
                     mShowCashOption = mTripData.getString("is_online_payment_accept");

                    if (mShowCashOption.equals("0")) {

                        mBinding.liveTripBottom.tvSelectedPaymentLive.setTextColor(getResources().getColor(R.color.colorGrayDark));
                        mBinding.liveTripBottom.ivSelectedPaymentLive.setImageDrawable(getResources().getDrawable(R.mipmap.cash));
                        mBinding.liveTripBottom.tvSelectedPaymentLive.setText(R.string.cash);

                    } else if (mShowCashOption.equals("1")) {

                        mBinding.liveTripBottom.tvSelectedPaymentLive.setTextColor(getResources().getColor(R.color.colorGrayDark));
                        mBinding.liveTripBottom.ivSelectedPaymentLive.setImageDrawable(getResources().getDrawable(R.mipmap.card));
                        mBinding.liveTripBottom.tvSelectedPaymentLive.setText(R.string.card);
                    } else {

                        mBinding.liveTripBottom.tvSelectedPaymentLive.setTextColor(getResources().getColor(R.color.colorGrayDark));
                        mBinding.liveTripBottom.ivSelectedPaymentLive.setImageDrawable(getResources().getDrawable(R.mipmap.wallet));
                        mBinding.liveTripBottom.tvSelectedPaymentLive.setText(R.string.wallet);

                    }


                    //  Log.e("livemarker", live_marker_img);
                    type_of_booking = mTripData.getString("type_of_booking");
                    String total_fare = mTripData.getString("total_fare");
                    tripFare = total_fare;
                    String rideStartOtp = mTripData.getString("start_otp");

                    lineOptions = new PolylineOptions();
                    if (mMap != null) {
                        mMap.clear();
                    }
                    lineOptions.add(originLatLng);

                    String eta = mTripData.getString("eta");
                    booking_status = mTripData.getString("booking_status");
                    if (booking_status.equalsIgnoreCase("4")){
                        getActivity().onBackPressed();
                        if (handler != null){
                            handler.removeCallbacks(runnable);
                        }
                    }

                    JSONObject driver_details = mTripData.getJSONObject("driver_details");


                    driver_id = driver_details.getString("driver_id");
                    String first_name = driver_details.getString("first_name");
                    String last_name = driver_details.getString("last_name");
                    drivername = first_name+" "+ last_name;



                    driver_mobile = driver_details.getString("mobile");
                    String profile_img = driver_details.getString("profile_img");
                    String vehicle_number = driver_details.getString("vehicle_number");
                    String avg_rating = driver_details.getString("avg_rating");
                    String vehicle_name = driver_details.getString("vehicle_name");
                    driverVehicleNumber = vehicle_number;
                    Double driver_lat = driver_details.getDouble("driver_lat");
                    Double driver_long = driver_details.getDouble("driver_lang");


                    JSONArray jsonArray = mTripData.getJSONArray("trip_path");
                    double latRaw = 0.0, longRaw = 0.0;
                    mBinding.tvDestinationTrip.setText(book_to_address);
                    mBinding.tvOriginTrip.setText(bookOriginAddress);
                    mBinding.liveTripBottom.tvName.setText(first_name + " " + last_name);
                    mBinding.liveTripBottom.tvVehicleNumber.setText(vehicle_number);
                    mBinding.liveTripBottom.tvVehicletype.setText(vehicle_name);
                    mBinding.liveTripBottom.ratingBar.setRating(Float.parseFloat(avg_rating));
                    mBinding.liveTripBottom.tvRatingText.setText(avg_rating);
                    mBinding.liveTripBottom.tvOtp.setText(rideStartOtp);
                    Glide.with(mContext).load(profile_img).placeholder(R.drawable.profile_placeholder).into(mBinding.liveTripBottom.ivDriverImg);

                    //live fragment java xml bas

                    mBinding.tvDname.setText(first_name + " " + last_name);
                    mBinding.tvVehicleNo.setText(vehicle_number);
                    mBinding.tvVehicleName.setText(vehicle_name);
                    mBinding.tvTripId.setText(booking_id);
                    Glide.with(mContext).load(profile_img).placeholder(R.drawable.profile_placeholder).into(mBinding.ivBeginDriverImg);


                    Glide.with(mContext)
                            .load(vehicle_image)
                            .placeholder(R.drawable.car_placeholder)
                            .override(40, 40)
                            .into(mBinding.liveTripBottom.ivVehicleImg);
                    mBinding.liveTripBottom.tvBookingId.setText(Html.fromHtml("<b>" + "TRIP ID- " + "</b>" + booking_id));
                    mBinding.liveTripBottom.tvEstimatedCash.setText(Html.fromHtml(getAppString(R.string.estimated_cash_to_be_paid) + " : \u20b9 " + total_fare));

                    mBinding.ratingBarBegin.setRating(Float.parseFloat(avg_rating));
                    mBinding.tvRatingTextBegin.setText(avg_rating);
                    mBinding.liveTripBottom.tvShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            /*This will be the actual content you wish you share.*/
                            String shareBody = "Driver name "+drivername+"\nDriver number: "+driver_mobile+"\nVehicle number: "+driverVehicleNumber+"\nTrip id: "+mtripId+
                                    "\nTrack link: " +Config.DOMAIN_URL+"trip_track/index.php/?tripid="+mtripId;
                            /*The type of the content is text, obviously.*/
                            intent.setType("text/plain");
                            /*Applying information Subject and Body.*/
                            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey I am sharing my ride status");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            /*Fire!*/
                            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
                        }
                    });

                    if (type_of_booking.equalsIgnoreCase("2")) {
                        mBinding.imgCross.setVisibility(View.GONE);
                        mBinding.tvDestinationTrip.setEnabled(false);


                    } else if (booking_status.equalsIgnoreCase("2") && type_of_booking.equalsIgnoreCase("0")) {
                        mBinding.imgCross.setVisibility(View.VISIBLE);
                        mBinding.tvDestinationTrip.setEnabled(true);
                    }
                    if (booking_status.equalsIgnoreCase("2")) {
                      //  iv_help.setVisibility(View.VISIBLE);

                        mBinding.cvTrip.setVisibility(View.VISIBLE);
                        mBinding.cvBottomCallDriver.setVisibility(View.GONE);
                        mBinding.cvBeginTripView.setVisibility(View.VISIBLE);
                        ViewTreeObserver vto = mBinding.rlTopTrip.getViewTreeObserver();
                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    mBinding.rlTopTrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    mBinding.rlTopTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                int width  = mBinding.rlTopTrip.getMeasuredWidth();
                                int height = mBinding.rlTopTrip.getMeasuredHeight();
                                Log.e("width", width+"");
                                Log.e("height", height+"");
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(0, 0, 0, height);
                                mBinding.llTopLayout.setLayoutParams(params);


                            }
                        });
                        if (jsonArray.length() > 0) {
                            // lineOptions = new PolylineOptions();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jArrayObj = jsonArray.getJSONObject(i);
                                // lineOptions.add(new LatLng(jArrayObj.getDouble("lat"), jArrayObj.getDouble("lang")));

                                latRaw = jArrayObj.getDouble("lat");
                                longRaw = jArrayObj.getDouble("lang");
                            }
                            if (longRaw != 0.0 || latRaw != 0.0) {


                                if (mMap != null) {


                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(new LatLng(latRaw, longRaw));

                                    Glide.with(mContext)
                                            .asBitmap().load(live_marker_img)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .override(40, 40)
                                            .into(new SimpleTarget<Bitmap>(100, 100) {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                                                    if (marker != null){
                                                        marker.remove();
                                                    }
                                                    marker = mMap.addMarker(markerOptions);
                                                    //marker.setRotation(180);

                                                }

                                            });
                                }
                            }
                        }

                        GetDirectionDataTask getDirectionDataTask = new GetDirectionDataTask(getActivity());
                        getDirectionDataTask.execute(getDirectionDataTask.getgoogleDirectionUrl(originAddressLatLong, destinationAddress, getAppString(R.string.direction_api_key)), originAddressLatLong, destinationAddress);

                    } else {
                       // iv_help.setVisibility(View.GONE);
                        mBinding.tvDestinationTrip.setEnabled(false);
                        mBinding.imgCross.setVisibility(View.GONE);
                        if (booking_status.equalsIgnoreCase("1") || booking_status.equalsIgnoreCase("8")) {
                            mBinding.cvTrip.setVisibility(View.VISIBLE);
                            mBinding.cvBottomCallDriver.setVisibility(View.VISIBLE);
                            mBinding.cvBeginTripView.setVisibility(View.GONE);
                            ViewTreeObserver vto = mBinding.rlTopTrip.getViewTreeObserver();
                            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        mBinding.rlTopTrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    } else {
                                        mBinding.rlTopTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    }
                                    int width  = mBinding.rlTopTrip.getMeasuredWidth();
                                    int height = mBinding.rlTopTrip.getMeasuredHeight();
                                    Log.e("width", width+"");
                                    Log.e("height", height+"");
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    params.setMargins(0, 0, 0, height);
                                    mBinding.llTopLayout.setLayoutParams(params);


                                }
                            });
                        }


                        mMap.addMarker(new MarkerOptions().position(new LatLng(originLat, originLong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(destinationLat, destinationLong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_red)));


                        MarkerOptions truckMarketOptions = new MarkerOptions();

                        LatLng driverLatlong = new LatLng(driver_lat, driver_long);
                        truckMarketOptions.position(driverLatlong);

                        //Log.e("Image Url", live_marker_img);

                        Glide.with(mContext).asBitmap()
                                .load(live_marker_img)
                                .override(40, 40)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.drawable.car_placeholder)
                                .into(new SimpleTarget<Bitmap>(100, 100) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        truckMarketOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                                        if (marker != null){
                                            marker.remove();
                                        }
                                        marker = mMap.addMarker(truckMarketOptions);

                                    }

                                });


                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(originLat, originLong)).zoom(12f).tilt(70).build();
                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));

                        GetDirectionDataTask getDirectionDataTask = new GetDirectionDataTask(getActivity());
                        getDirectionDataTask.execute(getDirectionDataTask.getgoogleDirectionUrl(driver_lat + "," + driver_long, originLat + "," + originLong, getAppString(R.string.direction_api_key)), driver_lat + "," + driver_long, originLat + "," + originLong);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onGetRideId(String rideid, int pos) {
        emitTripsData(mtripId);
    }


    public void onDirectionResponse(PolylineOptions lineOptions, String origin, String destionation) {
        if (lineOptions != null) {

            String[] originArray = origin.split(",");
            LatLng mOriginLatLng = new LatLng(Double.parseDouble(originArray[0]), Double.parseDouble(originArray[1]));

            String[] destinationArray = destionation.split(",");
            LatLng mDestionationLatLng = new LatLng(Double.parseDouble(destinationArray[0]), Double.parseDouble(destinationArray[1]));


            if (Config.PATH_TRACKING == false) {
                if (line != null)
                    line.remove();
                line = mMap.addPolyline(lineOptions);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                //Log.e("Booking status", booking_status);
                if (booking_status.equalsIgnoreCase("2")) {


                    MarkerOptions truckMarketOptions = new MarkerOptions();
                    truckMarketOptions.position(mOriginLatLng);
                    if (bookingStatusdata.equalsIgnoreCase("2") && liveMarkerStatus == 0){
                        Glide.with(mContext).asBitmap()
                            .load(live_marker_img)
                            .override(40, 40)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.car_placeholder)
                            .into(new SimpleTarget<Bitmap>(100, 100) {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    truckMarketOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                                    if (mMap != null) {
                                        if (marker != null){
                                            marker.remove();
                                        }
                                        marker = mMap.addMarker(truckMarketOptions);
                                        //float rotation = (float) SphericalUtil.computeHeading(new LatLng(oldlat,oldlong), new LatLng(mCurrentLat,mCurrentLong));
                                        //rotateMarker(marker, new LatLng(mCurrentLat,mCurrentLong), rotation);
                                    }
                                }

                            });
                        mMap.addMarker(new MarkerOptions().position(mDestionationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_red)));
                        liveMarkerStatus++;
                    } else if (!bookingStatusdata.equalsIgnoreCase("2")){
                        Glide.with(mContext).asBitmap()
                            .load(live_marker_img)
                            .override(40, 40)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.car_placeholder)
                            .into(new SimpleTarget<Bitmap>(100, 100) {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    truckMarketOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                                    if (mMap != null) {
                                        if (marker != null){
                                            marker.remove();
                                        }
                                        marker = mMap.addMarker(truckMarketOptions);
                                        //float rotation = (float) SphericalUtil.computeHeading(new LatLng(oldlat,oldlong), new LatLng(mCurrentLat,mCurrentLong));
                                        //rotateMarker(marker, new LatLng(mCurrentLat,mCurrentLong), rotation);
                                    }
                                }

                            });
                    mMap.addMarker(new MarkerOptions().position(mDestionationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_red)));

                    }


                    //marker =  mMap.addMarker(new MarkerOptions().position(mOriginLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from)));


                }
                builder.include(mOriginLatLng);
                builder.include(mDestionationLatLng);
                LatLngBounds bounds = builder.build();

                int width = getResources().getDisplayMetrics().widthPixels;

                int padding = (int) (width * 0.20);

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                if (mMap != null) {
                    mMap.animateCamera(cu);
                }
            } else {
        /*    newLatLong = updatedNewLatLong;
                mMap.addPolyline(lineOptions);
*/

            }


        }
    }

    public void drawOriginToDestination() {
        if (mMap != null) {
            mMap.clear();
        }
        lineOptions = new PolylineOptions();
        lineOptions.add(originLatLng);

        mBinding.cvTrip.setVisibility(View.VISIBLE);
        mBinding.cvBottomCallDriver.setVisibility(View.GONE);
        mBinding.cvBeginTripView.setVisibility(View.VISIBLE);
        ViewTreeObserver vto = mBinding.rlTopTrip.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mBinding.rlTopTrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mBinding.rlTopTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  = mBinding.rlTopTrip.getMeasuredWidth();
                int height = mBinding.rlTopTrip.getMeasuredHeight();
                Log.e("width", width+"");
                Log.e("height", height+"");
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, height);
                mBinding.llTopLayout.setLayoutParams(params);


            }
        });
        booking_status = "2";
        try {
           // iv_help.setVisibility(View.VISIBLE);

            if (type_of_booking.equalsIgnoreCase("2")) {
                mBinding.imgCross.setVisibility(View.GONE);
                mBinding.tvDestinationTrip.setEnabled(false);

            } else if (booking_status.equalsIgnoreCase("2") && type_of_booking.equalsIgnoreCase("0")) {
                mBinding.imgCross.setVisibility(View.VISIBLE);
                mBinding.tvDestinationTrip.setEnabled(true);
            }


            // iv_help.setImageResource(R.mipmap.help);
            MarkerOptions truckMarketOptions = new MarkerOptions();
            truckMarketOptions.position(new LatLng(originLatLng.latitude, originLatLng.longitude));

            Glide.with(mContext).asBitmap()
                    .load(live_marker_img)
                    .override(40, 40)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.car_placeholder)
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            truckMarketOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                            if (mMap != null) {
                                marker = mMap.addMarker(truckMarketOptions);
                                //float rotation = (float) SphericalUtil.computeHeading(new LatLng(oldlat,oldlong), new LatLng(mCurrentLat,mCurrentLong));
                                //rotateMarker(marker, new LatLng(mCurrentLat,mCurrentLong), rotation);
                            }
                        }

                    });

            GetDirectionDataTask getDirectionDataTask = new GetDirectionDataTask(getActivity());
            getDirectionDataTask.execute(getDirectionDataTask.getgoogleDirectionUrl(originAddressLatLong, destinationAddress, getAppString(R.string.direction_api_key)), originAddressLatLong, destinationAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTripEnd() {
        mtripId = "";
        booking_status = "";
       // iv_help.setVisibility(View.GONE);
        mBinding.tvDestinationTrip.setEnabled(false);
        mBinding.imgCross.setVisibility(View.GONE);
        mBinding.rlTrips.setVisibility(View.GONE);
        mBinding.tvNoDataFound.setVisibility(View.VISIBLE);


    }


    public void reloadLivetripData() {
        mtripId = "";
        booking_status = "";

        if (checkConnection()) {

            if (SocketConnection.isConnected()) {
                emitTrips();

            } else {
                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
            }
        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }

    }


    public void changeAddressToLocation(String trip_id, String ChangeFromAddress, String ChangeFromLat, String ChangeFromLong, String ChangeToAddress, String ChangeToLat, String ChangeToLong) {
        if (SocketConnection.isConnected()) {


            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_CHANGE_DESTINATION_ADDRESS, getChangeAddressListener);
            try {
                ListenersSocket.getChangeDestination(mContext, trip_id, ChangeFromAddress, ChangeFromLat, ChangeFromLong, ChangeToAddress, ChangeToLat, ChangeToLong);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void requestEmergencyContact(String tripId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("version", GlobalUtil.getAppVersion(mContext));
            //   Log.e("  ======   ", "" + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SocketConnection.emitToServer(Config.EMIT_EMERGENCY_CONTACT_SUPPORT, jsonObject);
    }

    Emitter.Listener getEmergency = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Get emergency response", (String) args[0]);
            try {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String mStarted = (String) args[0];
                        if (mStarted != null) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(mStarted.toString());

                                try {
                                    if (obj.getInt("status") == 1) {
                                        //  mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Info, true);
                                        if (obj.getString("result") != null) {
                                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                            callIntent.setData(Uri.parse("tel:" + obj.getString("result")));
                                            startActivity(callIntent);
                                        }
                                    } else {
                                        mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Info, true);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Info, true);

                                e.printStackTrace();
                            }


                        } else {
                            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Info, true);

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                } catch (Exception e1) {

                }
            }
        }
    };
    private int REQUEST_CODE_CONFIRM_RETURN = 12345;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONFIRM_RETURN) {
            if (resultCode == getActivity().RESULT_OK) {
                String str = data.getStringExtra("flag");
                // Log.e("clear ===== ", str);
                if (str.equals("true")) {
                    if (data.getStringExtra("msg") != null) {
                        Toast.makeText(mContext, data.getStringExtra("msg"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, getAppString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                    }
                }
            }
        }

        /*
        Edit Destination address
        */


        if (requestCode == REQUEST_CODE_TO_LOC_LIVE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                // TODO call location based filter
                onDropClick = true;
                LatLng latLong;


                latLong = place.getLatLng();
                mBinding.imgCross.setVisibility(View.VISIBLE);
                mBinding.tvDestinationTrip.setEnabled(true);
                mToLat = String.valueOf(latLong.latitude);
                mToLong = String.valueOf(latLong.longitude);
                mToaddress = GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude);

                //  Log.e("====", bookOriginAddress + " : " + originLat + " : " + originLong + " : " + mToLat + " : " + mToLong + " : " + mToaddress);

                String mNewFromAddress = mBinding.tvOriginTrip.getText().toString();//GlobalUtil.getAddress(mContext, mCurrentLat, mCurrentLong);

                // changeAddressToLocation(mtripId, bookOriginAddress, String.valueOf(originLat), String.valueOf(originLong), mToaddress, mToLat, mToLong);
                changeAddressToLocation(mtripId, mNewFromAddress, String.valueOf(mCurrentLat), String.valueOf(mCurrentLong), mToaddress, mToLat, mToLong);


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }


    }

    public void onLocationUpdate(Location location) {





        //Log.e("Lat and Lng", location.getLatitude() + "   " + location.getLongitude());
       /* try {
            oldlat = mCurrentLat;
            oldlong = mCurrentLong;

            mCurrentLat = location.getLatitude();
            mCurrentLong = location.getLongitude();

            if (oldlat != mCurrentLat && oldlong != mCurrentLong) {

                updateMyLocation(new LatLng(oldlat, oldlong), new LatLng(mCurrentLat, mCurrentLong));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*map click*/
    private void mapClick(View view) {
        if (booking_status.equalsIgnoreCase("2")) {
            if (mBinding.cvTrip.getVisibility() == View.VISIBLE) {
                mBinding.cvTrip.setVisibility(View.GONE);
                mBinding.cvBottomCallDriver.setVisibility(View.GONE);
                mBinding.cvBeginTripView.setVisibility(View.GONE);
                ViewTreeObserver vto = mBinding.rlTopTrip.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mBinding.rlTopTrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mBinding.rlTopTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int width  = mBinding.rlTopTrip.getMeasuredWidth();
                        int height = mBinding.rlTopTrip.getMeasuredHeight();
                        Log.e("width", width+"");
                        Log.e("height", height+"");
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, height);
                        mBinding.llTopLayout.setLayoutParams(params);


                    }
                });

            } else {
                mBinding.cvTrip.setVisibility(View.VISIBLE);
                mBinding.cvBottomCallDriver.setVisibility(View.GONE);
                mBinding.cvBeginTripView.setVisibility(View.VISIBLE);
                ViewTreeObserver vto = mBinding.rlTopTrip.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mBinding.rlTopTrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mBinding.rlTopTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int width  = mBinding.rlTopTrip.getMeasuredWidth();
                        int height = mBinding.rlTopTrip.getMeasuredHeight();
                        Log.e("width", width+"");
                        Log.e("height", height+"");



                    }
                });
            }

        } else if (booking_status.equalsIgnoreCase("1")) {
            if (mBinding.cvTrip.getVisibility() == View.VISIBLE) {
                mBinding.cvTrip.setVisibility(View.GONE);
                mBinding.cvBottomCallDriver.setVisibility(View.GONE);
                mBinding.cvBeginTripView.setVisibility(View.GONE);
                ViewTreeObserver vto = mBinding.rlTopTrip.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mBinding.rlTopTrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mBinding.rlTopTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int width  = mBinding.rlTopTrip.getMeasuredWidth();
                        int height = mBinding.rlTopTrip.getMeasuredHeight();
                        Log.e("width", width+"");
                        Log.e("height", height+"");
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, height);
                        mBinding.llTopLayout.setLayoutParams(params);


                    }
                });
               /* TranslateAnimation animate = new TranslateAnimation(0,0,0, mBinding.cvBottomCallDriver.getHeight());
                animate.setDuration(500);
                animate.setFillAfter(true);
                mBinding.cvBottomCallDriver.startAnimation(animate);
                mBinding.cvBottomCallDriver.setVisibility(View.GONE);*/

            } else {
                mBinding.cvTrip.setVisibility(View.VISIBLE);
                mBinding.cvBeginTripView.setVisibility(View.GONE);

            /*    TranslateAnimation animate = new TranslateAnimation(0, 0, mBinding.cvBottomCallDriver.getHeight(), 0);
                animate.setDuration(500);
                animate.setFillAfter(true);
                mBinding.cvBottomCallDriver.startAnimation(animate);*/
                mBinding.cvBottomCallDriver.setVisibility(View.VISIBLE);
                ViewTreeObserver vto = mBinding.rlTopTrip.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mBinding.rlTopTrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mBinding.rlTopTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int width  = mBinding.rlTopTrip.getMeasuredWidth();
                        int height = mBinding.rlTopTrip.getMeasuredHeight();
                        Log.e("width", width+"");
                        Log.e("height", height+"");
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, height);
                        mBinding.llTopLayout.setLayoutParams(params);


                    }
                });

            }
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

    private void updateMyLocation(LatLng old, LatLng newLa) {
        marker.setPosition(old);
        float rotation = (float) SphericalUtil.computeHeading(old, newLa);
        rotateMarker(marker, newLa, rotation);
    }

    private void selectPaymentClick(View view) {
        dialog = new Dialog(mContext, R.style.PaymentDialogTheme);
        layoutBinder = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.dialog_select_payment, null, false);
        //layoutBinder.setMain(this);
        dialog.setContentView(layoutBinder.getRoot());
        layoutBinder.tvclose.setOnClickListener(this::closeClick);
        if (mShowCashOption.equalsIgnoreCase("2")) {

            FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvWallet);
            layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llCard.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llCash.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llWallet.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.viewcash.setVisibility(View.GONE);


        } else if (mShowCashOption.equalsIgnoreCase("1")) {
            FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvCard);
            layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llCard.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llCash.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llWallet.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.viewcash.setVisibility(View.GONE);
        } else {


            FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvCash);
            layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llCard.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llCash.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.llWallet.setVisibility(View.VISIBLE);
            layoutBinder.layoutPaymentType.viewcash.setVisibility(View.VISIBLE);


        }


        switch (isOnlinePayment) {
            case "1":
                FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvCard);
                layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.VISIBLE);
                layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
                layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);
                FontLoader.setHelRegular(layoutBinder.layoutPaymentType.tvCash, layoutBinder.layoutPaymentType.tvWallet);
                break;

            case "0":
                FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvCash);
                layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);
                layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
                layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);
                FontLoader.setHelRegular(layoutBinder.layoutPaymentType.tvCard, layoutBinder.layoutPaymentType.tvWallet);

                break;


            case "2":
                FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvWallet);
                layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.VISIBLE);
                layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
                layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
                FontLoader.setHelRegular(layoutBinder.layoutPaymentType.tvCard, layoutBinder.layoutPaymentType.tvCash);
                break;
            default:
                break;

        }


        layoutBinder.layoutPaymentType.llWallet.setOnClickListener(this::WalletClick);
        layoutBinder.layoutPaymentType.llCard.setOnClickListener(this::cardClick);
        layoutBinder.layoutPaymentType.llCash.setOnClickListener(this::cashClick);
        dialog.show();


    }

    private void setCardSelected() {
        isOnlinePayment = "1";
        mBinding.liveTripBottom.tvSelectedPaymentLive.setText(R.string.card);
        mBinding.liveTripBottom.tvSelectedPaymentLive.setTextColor(getResources().getColor(R.color.colorGrayDark));
        mBinding.liveTripBottom.ivSelectedPaymentLive.setImageDrawable(getResources().getDrawable(R.mipmap.card));
        changePaymentMethod("1");

    }


    private void setCashSelected() {
        isOnlinePayment = "0";
        mBinding.liveTripBottom.tvSelectedPaymentLive.setText(R.string.cash);
        mBinding.liveTripBottom.tvSelectedPaymentLive.setTextColor(getResources().getColor(R.color.colorGrayDark));
        mBinding.liveTripBottom.ivSelectedPaymentLive.setImageDrawable(getResources().getDrawable(R.mipmap.cash));
        changePaymentMethod("0");

    }

    private void setWalletSelected() {
        isOnlinePayment = "2";
        mBinding.liveTripBottom.tvSelectedPaymentLive.setText(R.string.wallet);
        mBinding.liveTripBottom.tvSelectedPaymentLive.setTextColor(getResources().getColor(R.color.colorGrayDark));
        mBinding.liveTripBottom.ivSelectedPaymentLive.setImageDrawable(getResources().getDrawable(R.mipmap.wallet));
        changePaymentMethod("2");

    }

    private void cashClick(View view) {
        setCashSelected();
        FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvCash);
        FontLoader.setHelRegular(layoutBinder.layoutPaymentType.tvCard, layoutBinder.layoutPaymentType.tvWallet);
        layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);
        layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
        layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);
        dialog.dismiss();

    }

    private void cardClick(View view) {
        FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvCard);
        FontLoader.setHelRegular(layoutBinder.layoutPaymentType.tvCash, layoutBinder.layoutPaymentType.tvWallet);
        setCardSelected();
        layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.VISIBLE);
        layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
        layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);
        dialog.dismiss();

    }

    private void WalletClick(View view) {
        getWalletAmount();

    }


    private void closeClick(View view) {
        dialog.dismiss();
    }


    private void changePaymentMethod(String paymentType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("tripid", mtripId);
            jsonObject.put("payment_type", paymentType);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.CHANGE_PAYMENT_MODE, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonResponse) {
                            Log.e("changePaymentMethod", jsonResponse + "");
                            try {
                                int status = jsonResponse.getInt("status");
                                String message = jsonResponse.getString("message");

                                if (status == 1) {

                                    Log.e("changePaymentMethod", "success");

                                } else {

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

    @Override
    public void onPause() {
       // iv_help.setVisibility(View.GONE);
        if (handler !=  null)
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

        super.onPause();
    }
    private void getWalletAmount() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.BASE_URL_NEW + "fetchwalletamount?user_id=" + mSessionPref.user_userid +
                "&token=" + mSessionPref.token, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("walletResponse", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {
                        // Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        JSONObject jsonObjectData = jsonResponse.getJSONObject("data");

                        int id = jsonObjectData.optInt("id");
                        Double newAmount = 0.0;
                        Double tripAm = 0.0;
                        try {
                          newAmount = Double.parseDouble(jsonObjectData.optString("amount"));
                        } catch (NumberFormatException nfe) {
                            System.out.println("Could not parse " + nfe);
                        }

                        try {
                            tripAm = Double.parseDouble(tripFare);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Could not parse " + nfe);
                        }
                       // String amount = ;
                        if (newAmount < tripAm) {
                            dialog.dismiss();
                            showAddMoneyDialog();

                        } else {
                            setWalletSelected();
                            FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvWallet);
                            FontLoader.setHelRegular(layoutBinder.layoutPaymentType.tvCard, layoutBinder.layoutPaymentType.tvCash);
                            layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
                            layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
                            layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }

                        // int walletBalance = Integer.valueOf(amount);

                    } else {
                        //Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
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
    public void showAddMoneyDialog() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_add_money_trip_comp, null);
        alertDialog.setView(d);
        show = alertDialog.show();

        Button ok = d.findViewById(R.id.btn_go_to_wallet);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                startActivity(new Intent(mContext, MyWalletActivity.class));
                //getActivity()finish();
            }
        });
    }

}
