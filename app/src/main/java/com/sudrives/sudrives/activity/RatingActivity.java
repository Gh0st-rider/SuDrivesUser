package com.sudrives.sudrives.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ActivityRatingBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.utils.AppDialogs;
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
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

public class RatingActivity extends BaseActivity {

    private ActivityRatingBinding mBinding;
    private Context mContext;
    private ErrorLayout mErrorLayout;
    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;
    private SessionPref mSessionPref;
    private String mRateStr = "", mRideId = "", driver_id = "", mCommentStr = "";
    private final String TAG = RatingActivity.class.getSimpleName();
    private String mMobileStr = "", mUserId = "", title = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rating);
        GlobalUtil.setStatusBarColor(RatingActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getControls();
        KeyboardUtil.setupUI(mBinding.rlRatingDetail, RatingActivity.this);


    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControls() {
        mContext = RatingActivity.this;
        mSessionPref = new SessionPref(mContext);

        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));

        mBinding.btnRating.setOnClickListener(this::SubmitClick);
        mBinding.toolBarRating.tvTitle.setText(getAppString(R.string.share_your_experience));
        mBinding.toolBarRating.ibLeft.setVisibility(View.GONE);
        mBinding.toolBarRating.ibLeft.setOnClickListener(this::backScreenClick);
        mBinding.toolBarRating.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBarRating.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBarRating.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBarRating.ibRight1.setOnClickListener(this::notificationClick);

        FontLoader.setHelBold(mBinding.tvRatedriver,mBinding.btnRating);
        FontLoader.setHelRegular(mBinding.tvRateTitle,mBinding.toolBarRating.tvTitle);



        try {
            mRideId = getIntent().getStringExtra("tripid");
            driver_id = getIntent().getStringExtra("driver_id");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /*click of notification to navigate */

    private void notificationClick(View view) {
        startActivity(new Intent(mContext, NotificationActivity.class));
    }

    /*
         * check all validations
         * */
    private boolean isValidAll() {

        if (TextUtils.isEmpty(mRateStr) || mRateStr.equals("") || mRateStr == null || mRateStr.equals("0.0")) {
            mErrorLayout.showAlert(getString(R.string.please_add_rating), ErrorLayout.MsgType.Error, true);
            return false;
        }
        return true;
    }

    /*
       * getValue
       * */
    private void getValues() {
        mRateStr = String.valueOf(mBinding.ratingBar.getRating());
        mCommentStr = mBinding.etRatingcomment.getText().toString().trim();
    }

    /**
     * Navigation to back screen.
     *
     * @param view
     */
    private void backScreenClick(View view) {
        finish();

    }

    /**
     * Navigation to Home screen.
     */
    private void SubmitClick(View view) {
        getValues();
        if (isValidAll()) {
            if (checkConnection()) {
                mBinding.btnRating.setEnabled(false);
                ratingApi();
            } else {
                dialogClickForAlert(false, getResources().getString(R.string.no_internet_connection));
            }
        }
    }

    public void dialogClickForAlert(boolean isSuccess, String msg) {
        int layoutPopup;
        int drawable;

        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.confirmation)+"!";
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
    /***************    Implement API    *************************/


    private void ratingApi() {


        mBaseRequest = new BaseRequest(mContext, true);


        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid, "rating_vote", mRateStr, "driverid", driver_id, "tripid", mRideId, "token", mSessionPref.token, "comment", mCommentStr);
        mBinding.btnRating.setEnabled(true);

        mBaseRequest.setBaseRequest(jsonObj, "api/rating_to_driver", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {

                startActivity(new Intent(mContext, ThankyouActivity.class));
                    finish();

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
    public void onBackPressed() {
        //  super.onBackPressed();
    }

    ///////local broadcast//////

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received


                String response = intent.getStringExtra("response");

                if (ConnectivityReceiver.isConnected()) {

                    new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBarRating.tvNotificationBadge);
                } else {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                }

            }

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectivityReceiver.isConnected()) {

            new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBarRating.tvNotificationBadge);
        }
        else {
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

}

