package com.sudrives.sudrives.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ActivityBookingDetailBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;

public class BookingDetailActivity extends BaseActivity implements OnMapReadyCallback {

    private ActivityBookingDetailBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;

    GoogleMap mMap;
    String sourceLat, sourceLong, desLat, desLong;

    private BaseModel mBaseModel;
    private BaseRequest mBaseRequest;
    private ErrorLayout mErrorLayout;
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
                    //notificationListApi();
                   // new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);
                } else {
                    mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);

                }

            }

        }
    };
    private String invoice_link = "", tripId = "", booking_status = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_booking_detail);
        GlobalUtil.setStatusBarColor(BookingDetailActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        mContext = BookingDetailActivity.this;
        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout_home));

        if (checkConnection()) {
            mBinding.svBookingDetails.setVisibility(View.VISIBLE);
            getControl();

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
            mBinding.svBookingDetails.setVisibility(View.GONE);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sourceLat = getIntent().getStringExtra("latFrom");
        sourceLong = getIntent().getStringExtra("longFrom");
        desLat = getIntent().getStringExtra("latTo");
        desLong = getIntent().getStringExtra("longTo");


    }

    /*
     * initialize all controls
     * */
    private void getControl() {
        mBinding.toolBar.tvTitle.setText(getString(R.string.booking_Details));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.llDownloadInvoice.setOnClickListener(this::downLoadClick);
        mBinding.llEmailInvoice.setOnClickListener(this::emailClick);
        mBinding.toolBar.ibRight1.setVisibility(View.GONE);
        mBinding.toolBar.tvNotificationBadge.setVisibility(View.GONE);
        if (getIntent().getStringExtra("cancelbtnstatus") != null) {
            if (getIntent().getStringExtra("cancelbtnstatus").equals("true")) {
                if (getIntent().getStringExtra("arrived").equalsIgnoreCase("1")) {

                    mBinding.llCancelBooking.setVisibility(View.GONE);

                } else {
                    mBinding.llCancelBooking.setVisibility(View.VISIBLE);

                }
            } else {
                mBinding.llCancelBooking.setVisibility(View.GONE);
            }

        }


        mBinding.tvPaymentTypeBookingDetail.setText(getIntent().getStringExtra("payment_mode"));

         // Log.e("driverrrrr",getIntent().getStringExtra("driver_fname")+"222222");

        if (getIntent().getStringExtra("driver_fname") != null && !getIntent().getStringExtra("driver_fname") .isEmpty()) {

            mBinding.tvNameBookingDetail.setText(getIntent().getStringExtra("driver_fname")+" "+getIntent().getStringExtra("driver_lname"));

        } else {

            mBinding.tvNameBookingDetail.setText(getAppString(R.string.not_available));

        }


        mBinding.ratingBarBookingDetail.setRating(Float.parseFloat(getIntent().getStringExtra("driver_rating")));


        booking_status = getIntent().getStringExtra("status");

        if (booking_status.equals("4") || booking_status.equals("5") || booking_status.equals("7")) {

            mBinding.ivStatusCancelBookingDetail.setVisibility(View.VISIBLE);

            if (getIntent().getStringExtra("cancel_charge") != null) {
                mBinding.tvPriceBookingDetail.setText(Html.fromHtml("\u20B9 " + String.format("%.2f", Double.parseDouble(getIntent().getStringExtra("cancel_charge")))));
            } else {
                mBinding.tvPriceBookingDetail.setText(getAppString(R.string.not_available));
            }

        } else {

            mBinding.ivStatusCancelBookingDetail.setVisibility(View.GONE);

            if (getIntent().getStringExtra("total_charge") != null) {
                mBinding.tvPriceBookingDetail.setText(Html.fromHtml("\u20B9 " + String.format("%.2f", Double.parseDouble(getIntent().getStringExtra("total_charge")))));
            } else {
                mBinding.tvPriceBookingDetail.setText(getAppString(R.string.not_available));
            }


        }


        if (booking_status.equalsIgnoreCase("3")) {
            mBinding.llInvoiceDownloadMail.setVisibility(View.VISIBLE);

        } else {

            mBinding.llInvoiceDownloadMail.setVisibility(View.GONE);
        }

        if (getIntent().getStringExtra("invoice_link") != null) {
            invoice_link = getIntent().getStringExtra("invoice_link");
            tripId = getIntent().getStringExtra("tripid");
        }

        Log.e("driverrrrr",getIntent().getStringExtra("vehicleType")+"222222");

        if (!getIntent().getStringExtra("vehicleType").equals("null") &&getIntent().getStringExtra("vehicleType") != null && !getIntent().getStringExtra("vehicleType").isEmpty()) {

            mBinding.tvVehicleNameBookingDetail.setText(getIntent().getStringExtra("vehicleType"));

        } else {

            mBinding.tvVehicleNameBookingDetail.setText("Request not accepted");

        }




        mBinding.tvDateTimeBookingDetail.setText(getIntent().getStringExtra("time"));
        mBinding.tvTripIdBookingDetail.setText(getAppString(R.string.trip_id) + " : " + getIntent().getStringExtra("booking_id"));

        mBinding.tvOriginTrip.setText(getIntent().getStringExtra("from_address"));

        mBinding.sourceLocationLayout.etSourceAddress.setText(getIntent().getStringExtra("from_address"));

        try {
            mBinding.tvDestinationTrip.setText(getIntent().getStringExtra("to_address"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.ibRight1.setOnClickListener(this::notificationClick);
        mBinding.llCancelBooking.setOnClickListener(this::cancelBookingClick);


        if (getIntent().getStringExtra("latTo").length() == 0) {
            mBinding.rlsourceLocation.setVisibility(View.VISIBLE);
            mBinding.lnrBookingDetailsLocation.setVisibility(View.GONE);

        } else {
            mBinding.rlsourceLocation.setVisibility(View.GONE);
            mBinding.lnrBookingDetailsLocation.setVisibility(View.VISIBLE);

        }

    }

    private void cancelBookingClick(View view) {
        mBinding.llCancelBooking.setEnabled(false);
        Intent intent = new Intent(mContext, CancelBookingActivity.class);
        intent.putExtra("tripId", getIntent().getStringExtra("tripId"));
        startActivity(intent);
        finish();
    }

    private void notificationClick(View view) {
        //  startActivity(new Intent(mContext, NotificationActivity.class));
        startActivity(new Intent(mContext, NotificationActivity.class));
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    /**
     * Navigation to the Back screen
     *
     * @param view
     */
    private void navigationBackScreen(View view) {
        finish();
    }


    ///broadcast messaging

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.llCancelBooking.setEnabled(true);
        if (ConnectivityReceiver.isConnected()) {
            //notificationListApi();
          //  new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);
        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(ConfigNotif.PUSH_NOTIFICATION_USER));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onStop();
    }

    /*click of downLoadClick to navigate */

    private void downLoadClick(View view) {
        if (invoice_link != null) {
            if (invoice_link.toString().trim().length() != 0) {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse(invoice_link.toString().trim()));
                startActivity(httpIntent);

            } else {
                mErrorLayout.showAlert(getAppString(R.string.sorry_no_file_attached), ErrorLayout.MsgType.Error, false);

            }
        } else {
            mErrorLayout.showAlert(getAppString(R.string.sorry_no_file_attached), ErrorLayout.MsgType.Error, false);

        }
    }

    private void emailClick(View view) {
        // if (mSessionPref.user_email != null && mSessionPref.user_email != "") {
        AppDialogs.DoubleButtonWithCallBackVersionDialog(mContext, R.layout.duuble_button_dialog, true, false, 0, getString(R.string.are_you_sure_you_want_to_email_invoice),
                getString(R.string.email_invoice), getString(R.string.email),
                getString(R.string.cancel),
                new AppDialogs.Doublebuttonpincallback() {
                    @Override
                    public void doublebuttonok(String from) {
                        if (checkConnection()) {
                            emailApi(tripId);

                        } else {
                            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);


                        }
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

    private void emailApi(String tripid) {


        mBaseRequest = new BaseRequest(mContext, true);


        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid, "tripid", tripid);

        mBaseRequest.setBaseRequest(jsonObj, "SocketApi/invoice_email",
                mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
                    @Override
                    public void getResponse(Object response) {

                        mBaseModel = new BaseModel(mContext);
                        if (mBaseModel.isParse(response.toString())) {

                            mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Info, true);

                        } else {

                            mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                        }
                    }

                    @Override
                    public void getError(Object response, String error) {

                        if (!checkConnection()) {
                            mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, false);
                        } else {
                            mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                        }

                    }
                }, false);


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

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 11);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629)));
        mMap.animateCamera(cameraUpdate);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng mOriginLatLng = new LatLng(Double.parseDouble(sourceLat), Double.parseDouble(sourceLong));
        LatLng mDestionationLatLng = new LatLng(Double.parseDouble(desLat), Double.parseDouble(desLong));

        mMap.addMarker(new MarkerOptions().position(mOriginLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_from)));
        mMap.addMarker(new MarkerOptions().position(mDestionationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer_red)));

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


