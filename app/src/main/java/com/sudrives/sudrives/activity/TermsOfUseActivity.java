package com.sudrives.sudrives.activity;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityTermsOfUseBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TermsOfUseActivity extends BaseActivity {
    private ActivityTermsOfUseBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;
    private ErrorLayout mErrorLayout;
    private String avail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms_of_use);
        GlobalUtil.setStatusBarColor(TermsOfUseActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        mContext = TermsOfUseActivity.this;
        mSessionPref = new SessionPref(mContext);
        getControls();

    }

    private void getControls() {

        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));
        mBinding.toolBar.tvTitle.setText(getResources().getString(R.string.terms_of_use));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.btnCopy.setOnClickListener(this::copyClick);
        mBinding.toolBar.ibRight1.setOnClickListener(this::notificationClick);
        mBinding.tvToudesTitle.setText(getAppString(R.string.terms_and_condition) + ":");
        mBinding.tvToudesText.setMovementMethod(new ScrollingMovementMethod());
        avail = getIntent().getStringExtra("avail");
        if (getIntent().getStringExtra("text") != null) {
            mBinding.tvToudesText.setVisibility(View.VISIBLE);
            mBinding.svtextAreaScroller.setVisibility(View.VISIBLE);
            mBinding.tvToudesText.setText(Html.fromHtml(getIntent().getStringExtra("text").trim()));
        } else {
            mBinding.tvToudesText.setVisibility(View.GONE);
            mBinding.svtextAreaScroller.setVisibility(View.GONE);
        }
        mBinding.tvTouTitle.setText(Html.fromHtml(getAppString(R.string.coupon_code) + " - " + getIntent().getStringExtra("coupon")));

        if (getIntent().getStringExtra("validity") != null) {
            mBinding.tvTouText.setVisibility(View.VISIBLE);
            mBinding.tvTouText.setText(Html.fromHtml(getIntent().getStringExtra("validity")));
        } else {
            mBinding.tvTouText.setVisibility(View.GONE);
        }
        FontLoader.setHelRegular(mBinding.toolBar.tvTitle,mBinding.tvTermsOfUseR,mBinding.tvTouTitle,mBinding.tvTouText,mBinding.tvToudesTitle,mBinding.tvToudesText);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }
    /*click of notification to navigate */

    private void notificationClick(View view) {
        startActivity(new Intent(TermsOfUseActivity.this, NotificationActivity.class));
    }

    /**
     * Copy code o clip
     *
     * @param view
     */
    private void copyClick(View view) {

        if (avail.equalsIgnoreCase("0")) {
            int sdkVer = android.os.Build.VERSION.SDK_INT;

            clearDataFromClip();
            //For Older Android SDK versions
            if (getIntent().getStringExtra("coupon") != null) {
                if (sdkVer < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    @SuppressWarnings("deprecation")
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(getIntent().getStringExtra("coupon"));
                }

                //For Newer Versions
                else {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Message", getIntent().getStringExtra("coupon"));
                    clipboard.setPrimaryClip(clip);
                }
                mErrorLayout.showAlert(getAppString(R.string.copied), ErrorLayout.MsgType.Info, true);
                // startActivity(new Intent(mContext, ListOfCards.class));
            } else {
                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);


            }
        }else {
            mErrorLayout.showAlert(getAppString(R.string.you_have_already_used_this_coupon), ErrorLayout.MsgType.Error, true);

        }

    }

    private void clearDataFromClip() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            @SuppressWarnings("deprecation")
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("");
        }

        //For Newer Versions
        else {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Message", "");
            clipboard.setPrimaryClip(clip);
        }
    }

    /**
     * Navigation to the Back screen
     *
     * @param view
     */
    private void navigationBackScreen(View view) {
        finish();
    }

    ///////local broadcast//////
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
                                mBinding.toolBar.tvNotificationBadge.setVisibility(View.GONE);

                            } else {
                                mBinding.toolBar.tvNotificationBadge.setVisibility(View.VISIBLE);
                                mBinding.toolBar.tvNotificationBadge.setText(String.valueOf(i));

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
                    mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                    //   dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
                }

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (checkConnection()) {
            // callgetNotificationApiShowBadge(mContext);
            notificationListApi();

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
            //   dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
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
