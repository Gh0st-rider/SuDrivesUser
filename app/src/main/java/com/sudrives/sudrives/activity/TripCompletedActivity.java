package com.sudrives.sudrives.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ActivityTripCompletedBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
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
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.socket.emitter.Emitter;

public class TripCompletedActivity extends BaseActivity implements PaymentResultWithDataListener {
    private final String TAG = TripCompletedActivity.class.getSimpleName();
    private ActivityTripCompletedBinding mBinding;
    private Context mContext;
    private ErrorLayout mErrorLayout;
    private SessionPref mSessionPref;
    AlertDialog showFeed,showAddMoney,showFailed,showFare;
    ProgressBar progressFeedback;

    RatingBar rating;
    private String mRateStr = "", mCommentStr = "", mRideId = "";
    private String title = "";
    EditText feedBack;

    private BaseModel mBaseModel;
    private BaseRequest mBaseRequest;

    String amount = "";
    String paymentAmount = "";
    double newAmount = 0.0, rideAmount = 0.0;

    Checkout checkout;

    String mStrBaseFare,mStrChageKm,mStrWatingCharge,mStrTax,mStrTollTax;

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received
                if (ConnectivityReceiver.isConnected()) {
                    new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBarTripcompleted.tvNotificationBadge);
                } else {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                }

            }

        }
    };
    private String total_charge = "", driver_id = "", payment_id = "";
    private String is_online_payment_accept = "";
    Emitter.Listener getTripsDataListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("Get Tripdata List", (String) args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mTripDataResponse = (String) args[0];
                    parseTripsData(mTripDataResponse);
                }
            });
        }
    };
    private ProgressDialog progress;
    Emitter.Listener getPaymentListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //   Log.e("Get Payment List", (String) args[0]);
            //   Toast.makeText(mContext, (String) args[0], Toast.LENGTH_LONG).show();
            if (progress.isShowing()) {
                progress.dismiss();
            }
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String mTripDataResponse = (String) args[0];
                        BaseModel mBaseModel = new BaseModel(mContext);
                        if (mTripDataResponse != null) {
                            if (mBaseModel.isParse(mTripDataResponse)) {
                                showFeedbackDialog();
                                //startActivity(new Intent(TripCompletedActivity.this, RatingActivity.class).putExtra("tripid", getIntent().getStringExtra("tripid")));
                                //finish();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /*click of notification to navigate */

    public static String getRandomNumber() {
        Random rand = new Random();
        int num = rand.nextInt(9000000) + 1000000;

        return "" + num;

    }

    /*click of done to navigate */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trip_completed);
        GlobalUtil.setStatusBarColor(TripCompletedActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        mContext = TripCompletedActivity.this;
        mSessionPref = new SessionPref(mContext);
        getControls();

        mRideId = getIntent().getStringExtra("tripid");

        getWalletAmount();

        checkout = new Checkout();
        Checkout.preload(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void getControls() {
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        progress = new ProgressDialog(mContext);
        mBinding.toolBarTripcompleted.tvTitle.setText(getAppString(R.string.trip_complete));
        mBinding.toolBarTripcompleted.ibLeft.setVisibility(View.GONE);
        mBinding.toolBarTripcompleted.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBarTripcompleted.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBarTripcompleted.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBarTripcompleted.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBarTripcompleted.ibRight1.setOnClickListener(this::notificationClick);
        mBinding.btnDone.setOnClickListener(this::doneBtnClick);
        mBinding.llFareBreakupTripComp.setOnClickListener(this::clickFare);
        FontLoader.setHelBold(mBinding.tvPayableAmount, mBinding.btnDone, mBinding.toolBarTripcompleted.tvTitle);
        FontLoader.setHelRegular(mBinding.tvPayable, mBinding.tvTotalDistance, mBinding.tvBookingIdv, mBinding.tvTruckType, mBinding.tvVehicleNo);


        if (getIntent().getStringExtra("tripid") != null) {
            if (checkConnection()) {
                if (SocketConnection.isConnected()) {

                    emitTripsData(getIntent().getStringExtra("tripid"));
                } else {
                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                }
            } else {
                mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
            }

        }


    }

    private void notificationClick(View view) {
        startActivity(new Intent(TripCompletedActivity.this, NotificationActivity.class));
    }


    ///////local broadcast//////

    private void doneBtnClick(View view) {

        try {
      /*      if (payment_id.equals("")) {
                if (is_online_payment_accept.equals("1")) {
                    if (checkConnection()) {
                        progress.setTitle("Loading");
                        progress.setMessage("Wait while loading...");
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();
                        //generateCheckSum();
                        getOrderID();

                    } else {
                        //mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                        Toast.makeText(mContext, getAppString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {

                    Log.e("paymentModed", is_online_payment_accept);
                    if (is_online_payment_accept.equalsIgnoreCase("2")) {

                        try {
                            newAmount = Double.parseDouble(amount);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Could not parse " + nfe);
                        }

                        try {
                            rideAmount = Double.parseDouble(total_charge);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Could not parse " + nfe);
                        }

                        if (newAmount < rideAmount) {

                            showAddMoneyDialog();

                        } else {

                            showFeedbackDialog();

                        }


                    } else {

                        showFeedbackDialog();

                    }
                }
            } else {

                if (is_online_payment_accept.equalsIgnoreCase("2")) {

                    try {
                        newAmount = Double.parseDouble(amount);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }

                    try {
                        rideAmount = Double.parseDouble(total_charge);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }


                    if (newAmount < rideAmount) {

                        showAddMoneyDialog();
                    }


                } else {

                    showFeedbackDialog();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
      showFeedbackDialog();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        finishAllActivities();
    }

    // get and set trip data
    @SuppressLint("SetTextI18n")
    public void parseTripsData(String response) {
        Log.e("Response In Frag", response);
        JSONObject mTripData = new JSONObject();
        BaseModel mBaseModel = new BaseModel(mContext);
        if (response != null) {
            if (mBaseModel.isParse(response)) {
                try {
                    mTripData = mBaseModel.getResultObject();
                    String book_reciever_name = mTripData.getString("book_reciever_name");
                    String book_to_address = mTripData.getString("book_to_address");
                    String booking_id = mTripData.getString("booking_id");
                    String eta = mTripData.getString("eta");
                    String total_distance = mTripData.getString("total_distance");
                    payment_id = mTripData.getString("payment_id");
                    total_charge = mTripData.getString("total_fare").replaceAll(",", "");

                    mStrBaseFare = mTripData.getString("base_fare");
                    mStrChageKm = mTripData.getString("charge_km");
                    mStrWatingCharge = mTripData.getString("waiting_charge");
                    mStrTax = mTripData.getString("tax");
                    mStrTollTax = mTripData.getString("toll_tax");


                    is_online_payment_accept = mTripData.getString("is_online_payment_accept");
                    JSONObject driver_details = mTripData.getJSONObject("driver_details");
                    driver_id = driver_details.getString("driver_id");
                    String first_name = driver_details.getString("first_name");
                    String last_name = driver_details.getString("last_name");
                    String mobile = driver_details.getString("mobile");
                    String profile_img = driver_details.getString("profile_img");
                    String vehicle_number = driver_details.getString("vehicle_number");
                    int avg_rating = driver_details.getInt("avg_rating");
                    String vehicle_name = driver_details.getString("vehicle_name");





                    mBinding.tvPayableAmount.setText(Html.fromHtml("\u20B9" + total_charge));
                    mBinding.tvTruckType.setText(vehicle_name);
                    mBinding.tvVehicleNo.setText(vehicle_number);


                    mBinding.tvName.setText(first_name + " " + last_name);
                    mBinding.tvTruckType.setText(vehicle_name);

                    mBinding.tvBookingIdv.setText(getAppString(R.string.trip_id) + " : " + booking_id);


                    mBinding.tvTotalDistance.setText(getAppString(R.string.total_distance) + ": " + total_distance);

                    mBinding.ratingBar.setRating(avg_rating);
                    // if (!TripSummaryActivity.this.isFinishing()) {
                    Glide.with(mContext).load(profile_img).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(mBinding.ivOrderdetailsImg);
                    //}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectivityReceiver.isConnected()) {

            new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBarTripcompleted.tvNotificationBadge);
        } else {
            // mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

            finish();
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
//**************** Socket *******************************************

    //Paypal
   /* private void generateCheckSum() {

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                total_charge,
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID,
                mSessionPref.user_mobile,
                mSessionPref.user_email,
                Constants.M_KEY
        );


        HashMap<String, String> map = new HashMap<>();
        map.put("merchantMid", Constants.M_ID);
        map.put("merchantKey", Constants.M_KEY);
        map.put("custId",paytm.getCustId() + mSessionPref.user_userid);
        map.put("website", paytm.getWebsite());
        map.put("channelId", paytm.getChannelId());
        map.put("mobileNo", mSessionPref.user_mobile);
        map.put("txnAmount", total_charge);
        map.put("industryTypeId", paytm.getIndustryTypeId());
        map.put("email", mSessionPref.user_email);
        String orderId = paytm.getOrderId() + mSessionPref.user_userid + "-" + getRandomNumber();
        map.put("orderId", orderId);
        map.put("callbackUrl", paytm.getCallBackUrl() + orderId);


        //   Log.d("map", "r" + map + "     " + mSessionPref.user_userid + " " + GlobalUtil.getAppVersion(this) + "  " + mSessionPref.token + "  " + Config.TIMEZONE + "  " + Config.SELECTED_LANG + "  " + Config.DEVICE_TOKEN + "  " + Config.DEVICE_TYPE);


        apiService.getChecksum(map, "application/x-www-form-urlencoded", mSessionPref.user_userid, GlobalUtil.getAppVersion(this), mSessionPref.token, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE).enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {

                try {

                    initializePaytmPayment(response.body().getResult().getPaytmChecksum(), response.body().getResult().getPaytmParams());
                } catch (Exception e) {
                    e.printStackTrace();
                                }


            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                //   Log.d("error", "" + call);

            }
        });


    }*/

   /* private void initializePaytmPayment(String checksumHash, Checksum.Result.PaytmParams paytm) {

        PaytmPGService Service = PaytmPGService.getProductionService();    // for live account


        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", Constants.M_ID);
// Key in your staging and production MID available in your dashboard
        paramMap.put("ORDER_ID", paytm.getORDERID());
        paramMap.put("CUST_ID", paytm.getCUSTID());
        paramMap.put("MOBILE_NO", paytm.getMOBILENO());
        paramMap.put("EMAIL", paytm.getEMAIL());
        paramMap.put("CHANNEL_ID", paytm.getCHANNELID());
        paramMap.put("TXN_AMOUNT", total_charge);
        paramMap.put("WEBSITE", paytm.getWEBSITE());
// This is the staging value. Production value is available in your dashboard
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getINDUSTRYTYPEID());
// This is the staging value. Production value is available in your dashboard
        paramMap.put("CALLBACK_URL", paytm.getCALLBACKURL());
        paramMap.put("CHECKSUMHASH", checksumHash);


        //  Log.e("fgfg", "fgfgfgf: " + paramMap);
        //creating a paytm order object using the hashmap
        PaytmOrder Order = new PaytmOrder(paramMap);
        // PaytmClientCertificate Certificate = new PaytmClientCertificate("123456", "test");

        //intializing the paytm service
        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {
                if (inResponse.get("STATUS").equals("TXN_SUCCESS")) {
                    if (!inResponse.get("TXNID").equals(null)) {
                        if (checkConnection()) {
                            //  Toast.makeText(mContext, inResponse.get("TXNID").toString(), Toast.LENGTH_LONG).show();

                            emitPaymentData(inResponse.toString(), getIntent().getStringExtra("tripid"), total_charge, inResponse.get("TXNID").toString());
                        } else {
                            if (progress.isShowing()) {
                                progress.dismiss();
                            }
                            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                        }
                    } else {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                        mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                    }
                } else {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    try {
                        if (inResponse.get("STATUS").toString().equalsIgnoreCase("PENDING")) {
                            mErrorLayout.showAlert(getAppString(R.string.payment_failed), ErrorLayout.MsgType.Error, true);
                        } else {
                            try {
                                if (inResponse.get("RESPMSG").toString().length() != 0) {

                                    mErrorLayout.showAlert(inResponse.get("RESPMSG").toString(), ErrorLayout.MsgType.Error, true);

                                } else {
                                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                    }
                }
            }

            @Override
            public void networkNotAvailable() {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);

            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mErrorLayout.showAlert(getAppString(R.string.authentication_failed), ErrorLayout.MsgType.Error, true);

            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mErrorLayout.showAlert(inErrorMessage, ErrorLayout.MsgType.Error, true);

            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mErrorLayout.showAlert(getAppString(R.string.error_in_loading_page), ErrorLayout.MsgType.Error, true);

            }

            @Override
            public void onBackPressedCancelTransaction() {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mErrorLayout.showAlert(getAppString(R.string.transaction_cancelled), ErrorLayout.MsgType.Error, true);

            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mErrorLayout.showAlert(inErrorMessage, ErrorLayout.MsgType.Error, true);

            }
        });
    }*/

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

    //*************** get Payment
    public void emitPaymentData(String transactiondata,String payment_id,String order_id,String payment_signature,String payment_status) {
        try {
            SocketConnection.attachSingleEventListener(Config.LISTNER_GET_PAYMENT, getPaymentListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("tripid", mRideId);
            jsonObject.put("amount", total_charge);
            jsonObject.put("payment_responce_data", transactiondata);
            jsonObject.put("payment_id", payment_id);
            jsonObject.put("online_payment_order_id", order_id);
            jsonObject.put("online_payment_signature", payment_signature);
            jsonObject.put("online_payment_status", payment_status);

        } catch (JSONException e) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            e.printStackTrace();
        }

        try {
            SocketConnection.emitToServer(Config.EMIT_GET_PAYMENT, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showFeedbackDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_rating, null);
        alertDialog.setView(d);
        showFeed = alertDialog.show();
        //show.setCancelable(false);

        progressFeedback = d.findViewById(R.id.progress_feedback);
        final TextView average = d.findViewById(R.id.average_rating);
        rating = d.findViewById(R.id.user_rating);
        Button btnSend = d.findViewById(R.id.btn_rating);
        feedBack = d.findViewById(R.id.et_Ratingcomment);
        showFeed.setCancelable(false);
        //float rat = rating.getRating();

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String rate = Float.toString(rating);
                average.setText(rate);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();
                if (isValidAll()) {
                    if (checkConnection()) {
                        rating.setEnabled(false);
                        ratingApi();
                    } else {
                        dialogClickForAlert(false, getResources().getString(R.string.no_internet_connection));
                    }
                }
            }
        });
    }

    private void ratingApi() {
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid, "rating_vote", mRateStr, "driverid", driver_id, "tripid", mRideId, "token", mSessionPref.token, "comment", mCommentStr);
        rating.setEnabled(true);

        mBaseRequest.setBaseRequest(jsonObj, "api/rating_to_driver", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {

                    showFeed.dismiss();
                    //startActivity(new Intent(mContext, ThankyouActivity.class));
                    finish();

                } else {

                    //Log.e("errrr1111",mBaseModel.Message+ErrorLayout.MsgType.Error);
                    mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                }
            }

            @Override
            public void getError(Object response, String error) {

                Log.e("errrr2222", error.toString() + ErrorLayout.MsgType.Error);
                if (!checkConnection()) {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, false);
                } else {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


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


        AppDialogs.singleButtonVersionDialog(mContext, layoutPopup, title, drawable, msg,
                getString(R.string.ok),
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {

                    }
                }, true);
    }

    private boolean isValidAll() {
        if (TextUtils.isEmpty(mRateStr) || mRateStr.equals("") || mRateStr == null || mRateStr.equals("0.0")) {
            mErrorLayout.showAlert(getString(R.string.please_add_rating), ErrorLayout.MsgType.Error, true);
            return false;
        }
        return true;
    }

    private void getValues() {
        Log.e("rateeeeee", rating.getRating() + "");
        mRateStr = String.valueOf(rating.getRating());
        mCommentStr = feedBack.getText().toString().trim();
    }


    private void getWalletAmount() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.BASE_URL_NEW + "fetchwalletamount?user_id=" + mSessionPref.user_userid +
                "&token=" + mSessionPref.token, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollyerror", String.valueOf(error));
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }


    public void showAddMoneyDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_add_money_trip_comp, null);
        alertDialog.setView(d);
        showAddMoney = alertDialog.show();
        showAddMoney.setCancelable(false);

        Button ok = d.findViewById(R.id.btn_go_to_wallet);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMoney.dismiss();
                startActivity(new Intent(mContext, MyWalletActivity.class));
                finish();
            }
        });
    }


    private void getOrderID() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GENERATE_ORDER_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("getOrderID", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {

                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        paymentAmount = jsonObject.getString("amount");
                        String orderId = jsonObject.getString("order_id");
                        String reciept_no = jsonObject.getString("receipt");

                        startPayment(amount, orderId, reciept_no);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollyerror", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("payment_type", "Razorpay");
                map.put("amount", total_charge);
                map.put("using_type", "Ride Amount");
                Log.e("saveAmount", map + "");
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", mSessionPref.user_userid);
                params.put("token", mSessionPref.token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void startPayment(final String amount, final String orderId, final String receipt) {
        progress.dismiss();
        checkout.setKeyID("rzp_live_Qo2n35b2sGbJKK");
        /**
         * Instantiate Checkout
         */

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.app_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put("name", "SuDrives");
            options.put("description", "Wallet" + receipt);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderId);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount);//pass amount in currency subunits
            options.put("prefill.email", mSessionPref.user_email);
            options.put("prefill.contact", mSessionPref.user_mobile);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        emitPaymentData(paymentData.toString(),paymentData.getPaymentId(),paymentData.getOrderId(),paymentData.getSignature(),"Success");

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        if (paymentData!=null){

            emitPaymentData(paymentData.toString(),paymentData.getPaymentId(),paymentData.getOrderId(),paymentData.getSignature(),"Failed");

        }else {
          /*  try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject jsonObject1 = jsonObject.getJSONObject("error");
                String error = jsonObject1.getString("description");

            }catch (JSONException e){
                e.printStackTrace();
            }*/
            showFailedDialog();
        }

    }
    public void showFailedDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_payment_fail, null);
        alertDialog.setView(d);
        showFailed = alertDialog.show();

        Button ok = d.findViewById(R.id.btn_popup_failed);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFailed.dismiss();
            }
        });
    }

    private void clickFare(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext,R.style.myFullscreenAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_fare_details, null);
        alertDialog.setView(d);
        showFare = alertDialog.show();

        TextView tvBase =  d.findViewById(R.id.tv_baseFare_val);
        TextView tvChargeKm =  d.findViewById(R.id.tv_charge_km);
        TextView tvWatingCharge =  d.findViewById(R.id.tv_waiting_value);
        TextView tvTax =  d.findViewById(R.id.tv_taxes_value);
        TextView tvTotal =  d.findViewById(R.id.tv_total_charges_val);
        TextView tvTollTax =  d.findViewById(R.id.tv_tolltax_value);

        tvBase.setText(mStrBaseFare+" \u20b9");
        tvChargeKm.setText(mStrChageKm+" \u20b9");
        tvWatingCharge.setText(mStrWatingCharge+" \u20b9");
        tvTax.setText(mStrTax+" \u20b9");
        tvTotal.setText(total_charge+" \u20b9");
        tvTollTax.setText(mStrTollTax+" \u20b9");

        ImageView imgClose = d.findViewById(R.id.close_popup_fare_detail);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFare.dismiss();
            }
        });
    }

}