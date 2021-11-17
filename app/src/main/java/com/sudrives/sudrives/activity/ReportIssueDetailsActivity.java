package com.sudrives.sudrives.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityReportIssueDetailsBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportIssueDetailsActivity extends BaseActivity {
    private ActivityReportIssueDetailsBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;
    private ErrorLayout mErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue_details);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_issue_details);

        GlobalUtil.setStatusBarColor(ReportIssueDetailsActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        mContext = ReportIssueDetailsActivity.this;
        mSessionPref = new SessionPref(mContext);
        getControls();
        KeyboardUtil.setupUI(mBinding.llreportIssuetop, ReportIssueDetailsActivity.this);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControls() {

        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        mBinding.toolBarRating.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBarRating.ibLeft.setOnClickListener(this::backScreenClick);
        mBinding.toolBarRating.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBarRating.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBarRating.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBarRating.ibRight1.setOnClickListener(this::notificationClick);
      //  mBinding.tvAmount.setTypeface(null, Typeface.BOLD);
       FontLoader.setHelRegular(mBinding.toolBarRating.tvTitle,mBinding.tvBookingId,mBinding.tvTruckType
               ,mBinding.tvOriginMyBooking,mBinding.tvDestinationMyBooking,mBinding.tvTransactionId,
               mBinding.tvDeliveryDateVal,mBinding.tvDescription,mBinding.tvDescriptionVal,mBinding.tvStatus);

       FontLoader.setHelBold(mBinding.tvBookingId,mBinding.tvAmount);
        getIntentData();
    }

    private void getIntentData() {

        mBinding.tvDeliveryDateVal.setText(getIntent().getStringExtra("date"));
        mBinding.tvDescriptionVal.setText(getIntent().getStringExtra("comment"));

        mBinding.tvAmount.setText(Html.fromHtml("\u20B9 " + getIntent().getStringExtra("amount")));

        if (getIntent().getStringExtra("show").equalsIgnoreCase("booking")) {
            mBinding.toolBarRating.tvTitle.setText(getAppString(R.string.report_issue_summary));
            mBinding.tvBookingId.setText(mContext.getString(R.string.trip_id) + " : " + getIntent().getStringExtra("booking_id"));
            showHide(false);
            mBinding.tvTruckType.setText(getIntent().getStringExtra("vehicle_name"));
            mBinding.tvOriginMyBooking.setText(getIntent().getStringExtra("fromAddress"));
            mBinding.sourceLocationLayout.etSourceAddress.setText(getIntent().getStringExtra("fromAddress"));
            mBinding.tvDestinationMyBooking.setText(getIntent().getStringExtra("toAddress"));

            if (getIntent().getStringExtra("toAddress")!=null){
               if (getIntent().getStringExtra("toAddress").length()!=0){
                   mBinding.lnrLocation.setVisibility(View.VISIBLE);
                   mBinding.rlOriginLocation.setVisibility(View.GONE);

               }else {
                   mBinding.lnrLocation.setVisibility(View.GONE);
                   mBinding.rlOriginLocation.setVisibility(View.VISIBLE);

               }
           }

            String status = getIntent().getStringExtra("status");
            switch (status) {
                case "Pending":
                    mBinding.tvStatus.setText(status);
                    mBinding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    mBinding.ivStatus.setImageResource(R.drawable.pending_24dp);

                    break;
                case "Processed":
                    mBinding.tvStatus.setText(status);
                    mBinding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    mBinding.ivStatus.setImageResource(R.drawable.selected_24dp);

                    break;
                case "Completed":
                    mBinding.tvStatus.setText(status);
                    mBinding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    mBinding.ivStatus.setImageResource(R.drawable.selected_24dp);

                    break;

            }
        } else {
            mBinding.toolBarRating.tvTitle.setText(getAppString(R.string.refund));

            showHide(true);

            mBinding.tvBookingId.setText(mContext.getString(R.string.order_id) + " : " + getIntent().getStringExtra("order_id"));
            mBinding.tvTransactionId.setText(mContext.getString(R.string.transaction_id) + " : " +getIntent().getStringExtra("transaction_id"));
            mBinding.tvDestinationMyBooking.setText(getIntent().getStringExtra("toAddress"));
            String status=getIntent().getStringExtra("status");
            switch (status) {
                case "Pending":
                    mBinding.tvTruckType.setText(status);

                    mBinding.tvTruckType.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    mBinding.ivTruckType.setImageResource(R.drawable.pending_24dp);

                    break;
                case "Processed":
                    mBinding.tvTruckType.setText(status);
                    mBinding.tvTruckType.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    mBinding.ivTruckType.setImageResource(R.drawable.selected_24dp);

                    break;
                case "Completed":
                    mBinding.tvTruckType.setText(status);
                    mBinding.tvTruckType.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    mBinding.ivTruckType.setImageResource(R.drawable.selected_24dp);

                    break;



            }


        }

    }

    /**
     * Navigation to back screen.
     *
     * @param view
     */
    private void backScreenClick(View view) {
        finish();

    }

    /*click of notification to navigate */

    private void notificationClick(View view) {
        startActivity(new Intent(mContext, NotificationActivity.class));
    }

    private void showHide(boolean transaction) {
        if (transaction) {
            mBinding.llTransactionId.setVisibility(View.VISIBLE);
            mBinding.lnrLocation.setVisibility(View.GONE);
            mBinding.llStatus.setVisibility(View.GONE);
            mBinding.viewAmount.setVisibility(View.GONE);
            mBinding.viewStatus.setVisibility(View.GONE);
        } else {
            mBinding.llTransactionId.setVisibility(View.GONE);
            mBinding.lnrLocation.setVisibility(View.VISIBLE);
            mBinding.llStatus.setVisibility(View.VISIBLE);
            mBinding.viewAmount.setVisibility(View.VISIBLE);
            mBinding.viewStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (checkConnection()) {
            // callgetNotificationApiShowBadge(mContext);
            notificationListApi();

        } else {
            mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
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

    private void notificationListApi() {
        String user_id = "";

        user_id = mSessionPref.user_userid;

        BaseRequest mBaseRequest = new BaseRequest(mContext, false);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid, "token", mSessionPref.token, "language", Config.SELECTED_LANG, "page", "");
        BaseModel mBaseModel = new BaseModel(mContext);
        String finalUser_id = user_id;

        //isLoadMore=false;
        mBaseRequest.setBaseRequest(jsonObj, "api/get_notifications", user_id, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                try {
                    if (response != null) {
                        JSONObject JsonRes = new JSONObject(response.toString());
                        if (JsonRes.getString("status").equals("1")) {
                            JSONArray arr = JsonRes.getJSONArray("result");


                            int i = Integer.parseInt(JsonRes.optString("notification_unread", ""));

                            if (i == 0) {
                                mBinding.toolBarRating.tvNotificationBadge.setVisibility(View.GONE);

                            } else {
                                mBinding.toolBarRating.tvNotificationBadge.setVisibility(View.VISIBLE);
                                mBinding.toolBarRating.tvNotificationBadge.setText(String.valueOf(i));

                            }




                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }

            @Override
            public void getError(Object response, String error) {
                if (!checkConnection()) {
                    //    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                } else {
                    //   mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


    }

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // checking for type intent filter
          //  Log.e("i m here", "i m here");
            // new push notification is received

            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received


                String response = intent.getStringExtra("response");

                if (checkConnection()) {
                    // callgetNotificationApiShowBadge(mContext);
                    notificationListApi();

                } else {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                }

            }

        }
    };
}

