package com.sudrives.sudrives.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ActivityConfirmPickupNewBinding;
import com.sudrives.sudrives.databinding.DialogSelectPaymentBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.model.NotficationListModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class ConfirmPickupActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String ARG_LAYOUT = "layout";
    private ActivityConfirmPickupNewBinding mBinding;
    private Context mContext;
    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;
    private boolean flag = true;
    private int pageCount = 0;
    public boolean isLoadMore = false;
    private ErrorLayout mErrorLayout;
    private SessionPref mSessionPref;
    private ArrayList<NotficationListModel> mNotificationlist;
    private GoogleMap mMap;
    private String mMobileStr = "", mUserId = "", title = "";
    private String coupon_code = "";
    private Marker marker;
    private String mConfirmBookingResponse = "";
    private String mFromLat = "", mFromLong = "", mFromaddress = "", mToLat = "", mToLong = "", mToaddress = "", mETA = "", mShowCashOption = "", mVehicleType = "",
            mVehicleTypeId = "", mEstimatedFare = "", isOnlinePayment = "", mVehicleCityId = "", driver_search_ids = "";

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogCancelledTrips;
    private MarkerOptions markerOptions;
    private DialogSelectPaymentBinding layoutBinder;
    private Dialog dialog;

    AlertDialog show;
    String amount = "";
    double newAmount = 0.0, rideAmount = 0.0;

    int LAUNCH_SECOND_ACTIVITY = 1;

    String mStrBaseFare="",mStrChageKm="",mStrWatingCharge="",mStrTax="",mStrTollTax = "";
    JSONArray jsonArrayDriver = null;
    String arr[] = {};
    ArrayList<Integer> arrayDriverID = new ArrayList<>();
    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_pickup_new);
        GlobalUtil.setStatusBarColor(ConfirmPickupActivity.this, getResources().getColor(R.color.colorWhite));

        getControls();

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControls() {

        mContext = ConfirmPickupActivity.this;
        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));
        progressDialogCancelledTrips = new ProgressDialog(mContext);
        progressDialogCancelledTrips.setMessage(getAppString(R.string.please_wait));
        progressDialogCancelledTrips.setCancelable(false);
        requestLastCancelledTrip();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getAppString(R.string.please_wait));
        progressDialog.setCancelable(false);
        markerOptions = new MarkerOptions();

        getIntentData();

        mBinding.toolBar.tvTitle.setText(getString((R.string.confirm_booking)));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.ibRight1.setOnClickListener(this::notificationClick);
        mBinding.btnConfirmPickup.setOnClickListener(this::confirmNowClick);
        mBinding.llSelectPayment.setOnClickListener(this::selectPaymentClick);
        mBinding.ivPaymentDetails.setOnClickListener(this::popupFareDetail);

        mBinding.tvWaitingFee.setText(Html.fromHtml("\u20b9 " + getAppString(R.string.waiting_fee)));

        mBinding.llCoupon.setOnClickListener(this::applyCouponClick);


        FontLoader.setHelBold(mBinding.btnConfirmPickup);
        FontLoader.setHelRegular(mBinding.toolBar.tvTitle, mBinding.tvLocation, mBinding.etToAddress, mBinding.tvVehicleName, mBinding.tvWaitingFee);

        getWalletAmount();
        setCashSelected();
        if (SessionPref.getDataFromPref(ConfirmPickupActivity.this, SessionPref.PAYMENT_MODE_SELECTED).equalsIgnoreCase("0")){
            setCashSelected();
        }
        if (SessionPref.getDataFromPref(ConfirmPickupActivity.this, SessionPref.PAYMENT_MODE_SELECTED).equalsIgnoreCase("1")){
            setCardSelected();
        }
        if (SessionPref.getDataFromPref(ConfirmPickupActivity.this, SessionPref.PAYMENT_MODE_SELECTED).equalsIgnoreCase("2")){
            setWalletSelected();
        }

    }


    private void setCardSelected() {
        isOnlinePayment = "1";
        mBinding.tvSelectedPayment.setText(R.string.card);
        mBinding.tvSelectedPayment.setTextColor(getResources().getColor(R.color.colorGrayDark));
        mBinding.ivSelectedPayment.setImageDrawable(getResources().getDrawable(R.mipmap.card));
        SessionPref.saveDataIntoSharedPref(ConfirmPickupActivity.this, SessionPref.PAYMENT_MODE_SELECTED , "1");

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

    private void setCashSelected() {
        isOnlinePayment = "0";
        mBinding.tvSelectedPayment.setText(R.string.cash);
        mBinding.tvSelectedPayment.setTextColor(getResources().getColor(R.color.colorGrayDark));
        mBinding.ivSelectedPayment.setImageDrawable(getResources().getDrawable(R.mipmap.cash));

        SessionPref.saveDataIntoSharedPref(ConfirmPickupActivity.this, SessionPref.PAYMENT_MODE_SELECTED , "0");


    }

    private void setWalletSelected() {
        isOnlinePayment = "2";
        mBinding.tvSelectedPayment.setText(R.string.wallet);
        mBinding.tvSelectedPayment.setTextColor(getResources().getColor(R.color.colorGrayDark));
        mBinding.ivSelectedPayment.setImageDrawable(getResources().getDrawable(R.mipmap.wallet));

        SessionPref.saveDataIntoSharedPref(ConfirmPickupActivity.this, SessionPref.PAYMENT_MODE_SELECTED , "2");


    }

    private void WalletClick(View view) {
        setWalletSelected();
        FontLoader.setHelBold(layoutBinder.layoutPaymentType.tvWallet);
        FontLoader.setHelRegular(layoutBinder.layoutPaymentType.tvCard, layoutBinder.layoutPaymentType.tvCash);
        layoutBinder.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
        layoutBinder.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
        layoutBinder.layoutPaymentType.ivWalletSuccess.setVisibility(View.VISIBLE);
        dialog.dismiss();

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


    private void getIntentData() {
        mFromLat = getIntent().getStringExtra("mFromLat");
        mFromLong = getIntent().getStringExtra("mFromLong");
        mFromaddress = getIntent().getStringExtra("mFromaddress");
        mToLat = getIntent().getStringExtra("mToLat");
        mToLong = getIntent().getStringExtra("mToLong");
        mToaddress = getIntent().getStringExtra("mToaddress");
        mVehicleType = getIntent().getStringExtra("mVehicleType");
        mVehicleTypeId = getIntent().getStringExtra("mVehicleTypeId");
        mEstimatedFare = getIntent().getStringExtra("mEstimatedFare");
        mStrTollTax = getIntent().getStringExtra("mStrTollTax");
        driver_search_ids = getIntent().getStringExtra("driver_search_ids");
        Log.i("sendRideToDrivers", driver_search_ids);
        Log.e("sendRideToDrivers", driver_search_ids);
        try {
            JSONObject jsonObject = new JSONObject(driver_search_ids);
            jsonArrayDriver = jsonObject.getJSONArray("result");
            if (jsonArrayDriver.length()>0) {
                for (int i = 0; i < jsonArrayDriver.length(); i++) {
                    JSONObject jsonObject1 = jsonArrayDriver.getJSONObject(i);
                    Log.e("jsonArrayString", jsonObject1.getString("user_id"));

                    arrayDriverID.add(Integer.valueOf(jsonObject1.getString("user_id").trim()));
                    //arr[i] = jsonObject1.getString("user_id");
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        int n = jsonArrayDriver.length() + 1;

        for (int i=0; i< arrayDriverID.size(); i++){
            if (i==0)
                str = "["+arrayDriverID.get(i);
            else
                str += ","+arrayDriverID.get(i);
        }
        str = str+"]";

        Log.e("intarray", str);

        mStrBaseFare = getIntent().getStringExtra("mStrBaseFare");
        mStrChageKm = getIntent().getStringExtra("mStrChageKm");
        mStrWatingCharge = getIntent().getStringExtra("mStrWatingCharge");
        mStrTax = getIntent().getStringExtra("mStrTax");
        mVehicleCityId = getIntent().getStringExtra("mVehicleCityId");
        //mVehicleTypeId = "7";

        Log.e("fareeeDetil", mStrBaseFare + "111" + mStrChageKm+"22222"+mStrWatingCharge+"3333"+mStrTax+"");

        mBinding.tvLocation.setText(mFromaddress);
        mBinding.etToAddress.setText(mToaddress);
        mBinding.tvVehicleName.setText(mVehicleType);
        mBinding.tvVehicleRate.setText(Html.fromHtml("\u20b9 " + mEstimatedFare));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    private void confirmNowClick(View view) {
        try {
            if (SocketConnection.isConnected()) {
                if (checkConnection()) {
                    if (isOnlinePayment.length() != 0 && isOnlinePayment.equalsIgnoreCase("0") || isOnlinePayment.equalsIgnoreCase("1") || isOnlinePayment.equalsIgnoreCase("2")) {

                        if (isOnlinePayment.equalsIgnoreCase("2")) {


                            try {
                                newAmount = Double.parseDouble(amount);
                            } catch (NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }

                            try {
                                rideAmount = Double.parseDouble(mEstimatedFare);
                            } catch (NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }

                            Log.e("newwwww", newAmount + "" + "ssssss" + rideAmount + "");

                            if (newAmount < rideAmount) {


                                showAddMoneyDialog();

                            } else {

                                progressDialog.show();
                                new Handler().postDelayed(new Runnable() {    // if dialog is visible then automatic dismiss after 60 sec
                                    @Override
                                    public void run() {
                                        try {
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 1000 * 60);

                                createBooking("", "", isOnlinePayment);


                            }


                        } else {
                            progressDialog.show();
                            new Handler().postDelayed(new Runnable() {    // if dialog is visible then automatic dismiss after 60 sec
                                @Override
                                public void run() {
                                    try {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 1000 * 60);

                            createBooking("", "", isOnlinePayment);
                        }

                    } else {

                        mErrorLayout.showAlert(getAppString(R.string.please_choose_payment_option_to_continue), ErrorLayout.MsgType.Error, true);
                    }
                } else {

                    mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogClickForAlert(boolean isSuccess, String msg) {
        int layoutPopup;
        int drawable;

        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.confirmation) + "!";
            layoutPopup = R.layout.success_dialog;
        } else {
            drawable = R.drawable.failed_24dp;
            title = "";
            layoutPopup = R.layout.failure_dialog;
        }


        AppDialogs.singleButtonVersionDialog(ConfirmPickupActivity.this, layoutPopup, title, drawable, msg,
                getString(R.string.ok),
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {
                        progressDialog.dismiss();
                        SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_VEHICLE, mVehicleType);

                        Intent intent = new Intent();
                        intent.putExtra("clear", "true");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_VEHICLE, mVehicleType);

                Intent intent = new Intent();
                intent.putExtra("clear", "true");
                setResult(RESULT_OK, intent);
                finish();
            }
        }, 3000);

    }

    private void notificationClick(View view) {
        startActivity(new Intent(mContext, NotificationActivity.class));

    }

    private void navigationBackScreen(View view) {
        finishAllActivities();
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
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 5);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629)));
        mMap.animateCamera(cameraUpdate);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong)), 15f);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong))));
                    mMap.animateCamera(cameraUpdate);
                    markerOptions.position(new LatLng(Double.parseDouble(mFromLat), Double.parseDouble(mFromLong)));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from));
                    marker = mMap.addMarker(markerOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1500);

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

    private void closeClick(View view) {
        dialog.dismiss();
    }
    // ************************ Socket ************************************
    // Socket Emit

    Emitter.Listener getCreateBooking = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            mConfirmBookingResponse = (String) args[0];
            getBookingResponse(mConfirmBookingResponse);
            Log.e("Create Booking", (String) args[0]);

        }
    };

    public void createBooking(String paymentTransactionId, String paymentResponse, String onlinePayment) {
        if (SocketConnection.isConnected()) {
            Log.e("createbooking_connected", "I am connected");
            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_CREATE_BOOKING, getCreateBooking);

            JSONObject jsonObject = new JSONObject();
           /* try {
                final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
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
            }*/

            try {
                jsonObject.put("userid", mSessionPref.user_userid);
                jsonObject.put("token", mSessionPref.token);
                jsonObject.put("language", Config.SELECTED_LANG);
                jsonObject.put("booking_fee", "");
                jsonObject.put("book_reciever_name", "");
                jsonObject.put("book_reciever_mobile", "");
                jsonObject.put("vehicle_types", mVehicleTypeId);
                jsonObject.put("book_from_address", mFromaddress);
                jsonObject.put("book_from_lat", mFromLat);
                jsonObject.put("book_from_long", mFromLong);
                jsonObject.put("book_to_address", mToaddress);
                jsonObject.put("book_to_lat", mToLat);
                jsonObject.put("book_to_long", mToLong);
                jsonObject.put("book_coupon", coupon_code);
                jsonObject.put("is_online_payment_accept", onlinePayment);
                jsonObject.put("payment_responce_data", paymentResponse);
                jsonObject.put("payment_id", paymentTransactionId);
                jsonObject.put("book_later_date_time", "");
                jsonObject.put("booking_type", "0");
                jsonObject.put("type_of_booking", "0");
                jsonObject.put("vehicle_city_id", mVehicleCityId);
                //JSONArray jsonArray = new JSONArray();
                //jsonArray.put(jsonArrayDriver);


                jsonObject.put("driver_search_ids", str);
                //Log.d("booking", jsonObject.toString());
                Log.e("testJson", jsonObject.toString());
            } catch (JSONException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
            SocketConnection.emitToServer(Config.EMIT_GET_CREATE_BOOKING, jsonObject);
        } else {
            progressDialog.dismiss();
            //   Log.e("Create Booking=======>", "Error");
        }
    }

    private void getBookingResponse(String response) {
        // Log.e("getbookingResponse11111", response.toString());
        BaseModel mBaseModel = new BaseModel(mContext);
        JSONObject obj = null;
        try {

            obj = new JSONObject(response.toString());
            Log.e("getbookingResponse", response);
            if (response != null) {
                if (mBaseModel.isParse(response)) {
                    try {
                        if (obj.getString("status").equals("1")) {

                            JSONObject finalObj = obj;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        coupon_code = "";
                                        GlobalUtil.clearClipBoard(mContext);

                                        progressDialog.dismiss();
                                        // Toast.makeText(mContext, finalObj.getString("message"), Toast.LENGTH_LONG).show();
                                        SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_VEHICLE, mVehicleType);
                                        SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, Config.DAILY);

                                        Intent intent = new Intent();
                                        intent.putExtra("clear", "true");
                                        setResult(RESULT_OK, intent);
                                        finish();
                                        //dialogClickForAlert(true, obj.getString("message"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        } else {
                            JSONObject finalObj3 = obj;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressDialog.dismiss();
                                    try {
                                        Toast.makeText(mContext, finalObj3.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            //  mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        JSONObject finalObj2 = obj;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                try {
                                    Toast.makeText(mContext, finalObj2.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } else {
                    //  mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                    JSONObject finalObj1 = obj;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            progressDialog.dismiss();

                            try {
                                //  Toast.makeText(mContext, finalObj1.getString("message"), Toast.LENGTH_LONG).show();

                                int layoutPopup;
                                int drawable;

                                drawable = R.drawable.failed_24dp;
                                title = getAppString(R.string.failure);
                                layoutPopup = R.layout.failure_dialog;
                                AppDialogs.singleButtonVersionDialog(mContext, layoutPopup, title, drawable, finalObj1.getString("message"),
                                        getString(R.string.ok),
                                        new AppDialogs.SingleButoonCallback() {
                                            @Override
                                            public void singleButtonSuccess(String from) {


                                            }
                                        }, true);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                progressDialog.dismiss();
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(ConfigNotif.PUSH_NOTIFICATION_USER));


        if (ConnectivityReceiver.isConnected()) {
            // callgetNotificationApiShowBadge(mContext);
            new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);

        } else {
            //   dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onStop();
    }

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // checking for type intent filter
            // Log.e("i m here", "i m here");
            // new push notification is received

            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received


                String response = intent.getStringExtra("response");

                if (ConnectivityReceiver.isConnected()) {
                    // callgetNotificationApiShowBadge(mContext);
                    new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);

                } else {
                    //   dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
                }

            }

        }
    };

    public void requestLastCancelledTrip() {
        JSONObject jsonObject = new JSONObject();
        progressDialogCancelledTrips.show();
        SocketConnection.attachSingleEventListener(Config.LISTENER_GET_CANCELLED_TRIP, getCancelledTripStatus);

        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);

        } catch (JSONException e) {
            e.printStackTrace();
            progressDialogCancelledTrips.dismiss();
        }


        SocketConnection.emitToServer(Config.EMIT_EMERGENCY_GET_CANCELLED_TRIP, jsonObject);
    }

    Emitter.Listener getCancelledTripStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("Get Cancelled Trip", (String) args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialogCancelledTrips.dismiss();
                    String mStarted = (String) args[0];
                    if (mStarted != null) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(mStarted.toString());
                            if (obj.getString("status") != null) {
                                if (obj.getString("status").equalsIgnoreCase("0")) {


                                } else if (obj.getString("status").equalsIgnoreCase("1")) {

                                    mShowCashOption = obj.getString("cancel_status");

                                    try {

                                        if (mShowCashOption.equalsIgnoreCase("0")) {

                                           // setWalletSelected();
                                        } else {
                                            //setCardSelected();

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
                }
            });
        }
    };


    private void applyCouponClick(View view) {
        Intent i = new Intent(this, OffersActivity.class);
        i.putExtra("tripAmount", mEstimatedFare);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);

    }


    public void showAddMoneyDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
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
                finish();
            }
        });
    }

    private void popupFareDetail(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.myFullscreenAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_fare_details, null);
        alertDialog.setView(d);
        show = alertDialog.show();
        TextView tvBase =  d.findViewById(R.id.tv_baseFare_val);
        TextView tvChargeKm =  d.findViewById(R.id.tv_charge_km);
        TextView tvWatingCharge =  d.findViewById(R.id.tv_waiting_value);
        TextView tvTax =  d.findViewById(R.id.tv_taxes_value);
        TextView tvTotal =  d.findViewById(R.id.tv_total_charges_val);
        TextView tvTollTax =  d.findViewById(R.id.tv_tolltax_value);
        TextView tv_4 = d.findViewById(R.id.tv_4);

        tvBase.setText(mStrBaseFare+" \u20b9");
        tvChargeKm.setText(mStrChageKm+" \u20b9");
        tvWatingCharge.setText(mStrWatingCharge+" \u20b9");
        tvTax.setText(mStrTax+" \u20b9");
        tvTotal.setText(mEstimatedFare+" \u20b9");
        tvTollTax.setText(mStrTollTax+" \u20b9");
       /* if (mStrTollTax.equalsIgnoreCase("0.00")){
            tv_4.setVisibility(View.VISIBLE);
        }*/

        ImageView imgClose = d.findViewById(R.id.close_popup_fare_detail);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String coupon_title = data.getStringExtra("coupon_title");
                String coupon_discount = data.getStringExtra("coupon_discount");
                coupon_code = data.getStringExtra("coupon_code");
                String trip_amount = data.getStringExtra("trip_amount");
                String trip_discount_amount = data.getStringExtra("trip_discount_amount");
                mBinding.tvVehicleRate.setText(Html.fromHtml("\u20b9 " + trip_discount_amount));

                mBinding.applyCoupon.setText(coupon_code);
                popupCoupon(coupon_title, coupon_discount);
            }
        }
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
                        amount = jsonObjectData.optString("amount");
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

    private void popupCoupon(String title, String discount) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_coupon, null);
        alertDialog.setView(d);
        show = alertDialog.show();
        TextView tvTitle = d.findViewById(R.id.coupon_title);
        TextView tvDescription = d.findViewById(R.id.coupon_description);
        Button btnDone = d.findViewById(R.id.btn_ok_coupon);

        tvTitle.setText("'" + coupon_code + "'" + "applied");
        tvDescription.setText("You successfully applied your coupon max discount \u20B9" + discount + " for this ride");

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }
}



