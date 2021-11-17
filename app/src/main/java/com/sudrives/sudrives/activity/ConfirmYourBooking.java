package com.sudrives.sudrives.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.sudrives.sudrives.R;

import com.sudrives.sudrives.databinding.ActivityConfirmYourBookingBinding;
import com.sudrives.sudrives.model.OutStationDataModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class ConfirmYourBooking extends BaseActivity {
    private ActivityConfirmYourBookingBinding mBinding;
    private Context mContext;
    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;
    private SessionPref mSessionPref;
    private ErrorLayout mErrorLayout;
    private String isOnlinePayment = "0";
    private String mToAddress = "", mFromAddress = "", mLeaveDate = "", mFromLat = "", mFromLong = "", mToLat = "", mToLong = "", mTripType = "",
            couponCode = "", mDistance = "", mTime = "", mAmount = "", mVehicleId = "", mLeaveDateSend = "", mVehicleCityId = "", mReturnDate = "", mReturnDateSend = "";
    private ProgressDialog progressDialogCreateBooking;
    private ProgressDialog progressDialogCancelledTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_your_booking);
        getControls();
        GlobalUtil.setStatusBarColor(ConfirmYourBooking.this, getResources().getColor(R.color.colorPrimaryDark));


    }

    private void getControls() {
        mContext = ConfirmYourBooking.this;
        mSessionPref = new SessionPref(mContext);

        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));
        getIntentData();

        progressDialogCancelledTrips = new ProgressDialog(mContext);
        progressDialogCancelledTrips.setMessage(getAppString(R.string.please_wait));
        progressDialogCancelledTrips.setCancelable(false);

        requestLastCancelledTrip();  // check last cancelled trips
        mBinding.toolBar.tvTitle.setText(getAppString(R.string.confirm_your_booking));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.GONE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.tvFareDetails.setOnClickListener(this::showHideClick);
        mBinding.btnConfirmAndBook.setOnClickListener(this::confirmClick);


        progressDialogCreateBooking = new ProgressDialog(mContext);
        progressDialogCreateBooking.setMessage(getAppString(R.string.please_wait));
        progressDialogCreateBooking.setCancelable(false);

        mBinding.sbConfirmYourBooking.scrollTo(0, 0);
        // Log.d("...","====>>"+getTvString(mBinding.tvFareDetails));
        isOnlinePayment = "0";
        mBinding.layoutPaymentType.llCard.setOnClickListener(this::cardClick);
        mBinding.layoutPaymentType.llCash.setOnClickListener(this::cashClick);
        mBinding.layoutPaymentType.llWallet.setOnClickListener(this::walletClick);
        mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);

        mBinding.layoutPaymentType.viewwallet.setVisibility(View.GONE);
        mBinding.layoutPaymentType.viewcash.setVisibility(View.GONE);
        FontLoader.setHelRegular(mBinding.toolBar.tvTitle, mBinding.tvVehicleName, mBinding.tvVehicleDetail, mBinding.tvOriginTrip, mBinding.tvDestinationTrip, mBinding.tvLeaveOn, mBinding.tvLeaveOnVal
                , mBinding.tvEstimated, mBinding.tvBaseFare, mBinding.tvBaseFareAmount, mBinding.tvDistance, mBinding.tvTaxesAndFeesVal, mBinding.tvNightCharges
                , mBinding.tvNightChargesVal, mBinding.tvTaxes, mBinding.tvTaxesVal, mBinding.tvEstimateFare, mBinding.tvCancelCharges, mBinding.tvCancelChargesVal,
                mBinding.tvEstimateFareAmount, mBinding.tvTripDistance, mBinding.rbOnline, mBinding.layoutPaymentType.tvCard, mBinding.layoutPaymentType.tvWallet);
        FontLoader.setHelBold(mBinding.btnConfirmAndBook, mBinding.tvEstimatedAmount, mBinding.rbCash, mBinding.layoutPaymentType.tvCash);


        if (checkConnection()) {


        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    private void getIntentData() {

        // Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getSerializableExtra("mOutStationDataModel") != null) {
                OutStationDataModel mOutStationDataModel = (OutStationDataModel) intent.getSerializableExtra("mOutStationDataModel");

                mFromAddress = mOutStationDataModel.getmFromAddress();
                mToAddress = mOutStationDataModel.getmToAddress();
                mFromLat = mOutStationDataModel.getmFromLat();
                mFromLong = mOutStationDataModel.getmFromLong();
                mToLat = mOutStationDataModel.getmToLat();
                mToLong = mOutStationDataModel.getmToLong();
                mTripType = mOutStationDataModel.getmTripType();
                mLeaveDate = mOutStationDataModel.getmLeaveDate();
                mLeaveDateSend = mOutStationDataModel.getmLeaveDateSend();
                mDistance = mOutStationDataModel.getDistance();
                mTime = mOutStationDataModel.getmTime();
                mAmount = mOutStationDataModel.getmAmount();
                mVehicleId = mOutStationDataModel.getmVehicleId();
                mVehicleCityId = mOutStationDataModel.getmVehicleCityId();
                mReturnDate = mOutStationDataModel.getmReturnDate();
                mReturnDateSend = mOutStationDataModel.getmReturnDateSend();

                if (!mReturnDate.trim().equalsIgnoreCase("")) {
                    mBinding.llReturnby.setVisibility(View.VISIBLE);
                    mBinding.tvReturnbyVal.setText(mReturnDate);
                } else {
                    mBinding.llReturnby.setVisibility(View.GONE);
                    mBinding.tvReturnbyVal.setText("");
                }
                mBinding.tvOriginTrip.setText(mFromAddress);
                mBinding.tvDestinationTrip.setText(mToAddress);
                mBinding.tvLeaveOnVal.setText(mLeaveDate);
                mBinding.tvVehicleName.setText(mOutStationDataModel.getmVehicleName());
                mBinding.tvVehicleDetail.setText(mOutStationDataModel.getmVehicleDescription());
                mBinding.tvEstimatedAmount.setText(Html.fromHtml("\u20B9 " + mOutStationDataModel.getmAmount()));
                mBinding.tvEstimateFareAmount.setText(Html.fromHtml("\u20B9 " + mOutStationDataModel.getmAmount()));
                mBinding.tvBaseFareAmount.setText(Html.fromHtml("\u20B9 " + mOutStationDataModel.getmBaseFare()));
                mBinding.tvDistance.setText(Html.fromHtml(mOutStationDataModel.getDistance() + ", " + mOutStationDataModel.getmTime()));
                mBinding.tvTaxesAndFeesVal.setText(Html.fromHtml("\u20B9 " + String.valueOf(String.format("%.2f",
                        Double.parseDouble(mOutStationDataModel.getmSgst()) + Double.parseDouble(mOutStationDataModel.getmCgst())
                                + Double.parseDouble(mOutStationDataModel.getmNightCharges())
                                + Double.parseDouble(mOutStationDataModel.getmCancelCharges())))));
                //   String.format("%.0f", 5.222)
                if (mOutStationDataModel.getmNightCharges().equalsIgnoreCase("0.00")) {
                    mBinding.tvNightChargesVal.setText(Html.fromHtml(getAppString(R.string.na)));

                } else {
                    mBinding.tvNightChargesVal.setText(Html.fromHtml("\u20B9 " + mOutStationDataModel.getmNightCharges()));

                }
                if (mOutStationDataModel.getmCancelCharges().equalsIgnoreCase("0.00")) {
                    mBinding.tvCancelChargesVal.setText(Html.fromHtml(getAppString(R.string.na)));

                } else {
                    mBinding.tvCancelChargesVal.setText(Html.fromHtml("\u20B9 " + mOutStationDataModel.getmCancelCharges()));

                }

                mBinding.tvTaxesVal.setText(Html.fromHtml("\u20B9 " + String.valueOf(String.format("%.2f", Double.parseDouble(mOutStationDataModel.getmSgst()) + Double.parseDouble(mOutStationDataModel.getmCgst())))));


                if (mTripType.equalsIgnoreCase("0")) {
                    mBinding.tvTripDistance.setText(getAppString(R.string.one_way_trip_of_about) + " " + mOutStationDataModel.getDistance());
                } else {
                    mBinding.tvTripDistance.setText(getAppString(R.string.round_trip_of_about) + " " + mOutStationDataModel.getDistance());
                }
            }
        }
    }

    private void showHideClick(View view) {

        if (mBinding.tvFareDetails.getText().toString().trim().equalsIgnoreCase(getAppString(R.string.see_fare_details))) {
            mBinding.tvFareDetails.setText(R.string.hide_fare_detalis);
            //  mBinding.llFareDetails.startAnimation(mMoveDownAnim);
            mBinding.llFareDetails.setVisibility(View.VISIBLE);

        } else {
            mBinding.llFareDetails.setVisibility(View.GONE);
            mBinding.tvFareDetails.setText(R.string.see_fare_details);
        }

    }

    private void cardClick(View view) {
        FontLoader.setHelBold(mBinding.layoutPaymentType.tvCard);
        FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCash, mBinding.layoutPaymentType.tvWallet);
        isOnlinePayment = "1";
        mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.VISIBLE);
        mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
        mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);

    }

    private void cashClick(View view) {
        FontLoader.setHelBold(mBinding.layoutPaymentType.tvCash);
        FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCard, mBinding.layoutPaymentType.tvWallet);
        isOnlinePayment = "0";
        mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);
        mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
        mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);

    }

    private void walletClick(View view) {

        mErrorLayout.showAlert(getAppString(R.string.coming_soon), ErrorLayout.MsgType.Error, true);

    }


    private void navigationBackScreen(View view) {
        Intent intent = new Intent();
        intent.putExtra("close", "false");
        setResult(RESULT_OK, intent);
        finish();
    }

    private void confirmClick(View view) {

        if (SocketConnection.isConnected()) {
            createBooking(mVehicleId);
        } else {
            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

        }
    }

    // Socket Emit
    Emitter.Listener getCreateBooking = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("Create Booking", (String) args[0]);
            String mConfirmBookingResponse = (String) args[0];
            getBookingResponse(mConfirmBookingResponse);

        }
    };

    public void createBooking(String vehicleId) {
        if (SocketConnection.isConnected()) {
            //  Log.e("I am connected", "I am connected");
            progressDialogCreateBooking.show();
            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_CREATE_BOOKING, getCreateBooking);

            JSONObject jsonObject = new JSONObject();
            try {
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

            }

            try {
                jsonObject.put("userid", mSessionPref.user_userid);
                jsonObject.put("token", mSessionPref.token);
                jsonObject.put("language", Config.SELECTED_LANG);
                jsonObject.put("vehicle_types", vehicleId);
                jsonObject.put("book_from_address", mFromAddress);
                jsonObject.put("book_from_lat", mFromLat);
                jsonObject.put("book_from_long", mFromLong);
                jsonObject.put("book_to_address", mToAddress);
                jsonObject.put("book_to_lat", mToLat);
                jsonObject.put("book_to_long", mToLong);
                jsonObject.put("book_coupon", couponCode);
                jsonObject.put("is_online_payment_accept", isOnlinePayment);
                jsonObject.put("booking_type", "1");
                jsonObject.put("type_of_booking", "2");
                jsonObject.put("round_trip", mTripType);
                jsonObject.put("booking_distance", mDistance);
                jsonObject.put("booking_total_times", mTime);
                jsonObject.put("booking_fee", mAmount);
                jsonObject.put("book_later_date_time", mLeaveDateSend);
                jsonObject.put("vehicle_city_id", mVehicleCityId);
                jsonObject.put("book_later_return_date_time", mReturnDateSend);


                // Log.d("booking", jsonObject.toString());

            } catch (JSONException e) {
                progressDialogCreateBooking.dismiss();
                e.printStackTrace();
            }


            SocketConnection.emitToServer(Config.EMIT_GET_CREATE_BOOKING, jsonObject);
        } else {
            progressDialogCreateBooking.dismiss();
            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
            //   Log.e("Create Booking=======>", "Error");
        }
    }

    private void getBookingResponse(String response) {
        BaseModel mBaseModel = new BaseModel(mContext);
        JSONObject obj = null;
        try {
            obj = new JSONObject(response.toString());

            if (response != null) {
                if (mBaseModel.isParse(response)) {
                    try {
                        if (obj.getString("status").equals("1")) {

                            JSONObject finalObj = obj;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        couponCode = "";
                                        GlobalUtil.clearClipBoard(mContext);
                                        SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, Config.OUTSTATION);

                                        progressDialogCreateBooking.dismiss();
                                        //Toast.makeText(mContext, finalObj.getString("message"), Toast.LENGTH_LONG).show();


                                        dialogClickForAlert(true, finalObj.getString("message"));

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


                                    progressDialogCreateBooking.dismiss();
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


                                progressDialogCreateBooking.dismiss();
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


                            progressDialogCreateBooking.dismiss();

                            try {
                                //  Toast.makeText(mContext, finalObj1.getString("message"), Toast.LENGTH_LONG).show();
                                dialogClickForAlert(false, finalObj1.getString("message"));

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                progressDialogCreateBooking.dismiss();
            }
        } catch (JSONException e) {
            progressDialogCreateBooking.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void dialogClickForAlert(boolean isSuccess, String msg) {
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


        AppDialogs.singleButtonVersionDialog(mContext, layoutPopup, title, drawable, msg,
                getString(R.string.ok),
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {
                        //  HomeMenuActivity.dataForDriver(mFromAddress, Double.parseDouble(mFromLat), Double.parseDouble(mFromLong));

                        Intent intent = new Intent();
                        intent.putExtra("close", "true");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, true);

    }

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
            //  Log.e("Get Cancelled Trip", (String) args[0]);


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

                                    String mShowCashOption = obj.getString("cancel_status");

                                    try {
                                       /* if (mShowCashOption.equalsIgnoreCase("0")) {
                                            mBinding.rbCash.setVisibility(View.VISIBLE);

                                        } else {
                                            mBinding.rbCash.setVisibility(View.GONE);
                                        }*/
                                        if (mShowCashOption.equalsIgnoreCase("0")) {
                                            mBinding.layoutPaymentType.llCash.setVisibility(View.VISIBLE);
                                            mBinding.layoutPaymentType.viewcash.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.viewwallet.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);
                                            FontLoader.setHelBold(mBinding.layoutPaymentType.tvCash);
                                            FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCard, mBinding.layoutPaymentType.tvWallet);
                                        } else {
                                            FontLoader.setHelBold(mBinding.layoutPaymentType.tvCard);
                                            FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCash, mBinding.layoutPaymentType.tvWallet);
                                            mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.VISIBLE);
                                            mBinding.layoutPaymentType.llCash.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.viewcash.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.viewwallet.setVisibility(View.GONE);
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

}
