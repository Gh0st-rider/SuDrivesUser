package com.sudrives.sudrives.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.sudrives.sudrives.R;
import com.sudrives.sudrives.adapter.CancelOrderListAdapter;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ActivityCancelBookingBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;

import com.sudrives.sudrives.fragment.LiveFragment;
import com.sudrives.sudrives.model.CancelOrderListModel;
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
import com.sudrives.sudrives.utils.server.SocketConnection;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;


public class CancelBookingActivity extends BaseActivity {
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private CancelOrderListAdapter mAdapter;
    private String tripId = "";
    private Context mContext;
    private ArrayList<CancelOrderListModel> mReportAnswerList;

    private LinearLayout llMain;


    private ErrorLayout mErrorLayout;
    private RelativeLayout rlMain;
    private ActivityCancelBookingBinding mBinding;
    private BaseRequest mBaseRequest;
    private SessionPref mSessionPref;
    private BaseModel mBaseModel;
    private boolean isSelected = false;
    private String reportId = "";
    private String reportText = "", mCancelResponse = "";
    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_booking);
        mContext = CancelBookingActivity.this;
        mSessionPref = new SessionPref(mContext);
        getControl();
        GlobalUtil.setStatusBarColor(CancelBookingActivity.this, getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControl() {

        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        KeyboardUtil.setupUI(mBinding.rlMainReport, CancelBookingActivity.this);


        tripId = getIntent().getStringExtra("tripId");
        mBinding.outerToolbar.tvTitle.setText(getAppString(R.string.cancel_order));
        mBinding.outerToolbar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.outerToolbar.ibRight1.setVisibility(View.VISIBLE);
        mBinding.outerToolbar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.outerToolbar.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.outerToolbar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.tvSubmit.setEnabled(true);
        mBinding.outerToolbar.ibRight1.setOnClickListener(this::notificationClick);
        mBinding.tvSubmit.setOnClickListener(this::attemtReportNow);
        mReportAnswerList = new ArrayList<>();


        mAdapter = new CancelOrderListAdapter(mContext, mReportAnswerList);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBinding.rvCancelOrder.setLayoutManager(mLayoutManager);

        FontLoader.setHelRegular(mBinding.etComment, mBinding.outerToolbar.tvTitle);
        FontLoader.setHelBold(mBinding.tvSubmit);

        //CALLING THE GET REPORT SERVICE.

        if (!checkConnection()) {
            mBinding.llMain.setVisibility(View.GONE);
            mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        } else {
            callingReportList();
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

    /*click of notification to navigate */

    private void notificationClick(View view) {
        startActivity(new Intent(mContext, NotificationActivity.class));
    }

    /**
     * Call Report List API
     */
    private void callingReportList() {


        mBaseRequest = new BaseRequest(mContext, true);

        JsonObject jsonObj = JsonElementUtil.getJsonObject("types", Config.CANCELORDERLIST);

        mBaseRequest.setBaseRequest(jsonObj, "CommonController/get_types", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {
                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {
                    mBinding.llMain.setVisibility(View.VISIBLE);
                    mBinding.tvNoDataFound.setVisibility(View.GONE);

                    try {
                        mReportAnswerList.addAll(CancelOrderListModel.getList(mContext, mBaseModel.getResultObject().getJSONArray("cancel_order")));


                        mAdapter.setList(mReportAnswerList);


                        mBinding.rvCancelOrder.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void getError(Object response, String error) {
                mBinding.llMain.setVisibility(View.GONE);
                mBinding.tvNoDataFound.setVisibility(View.VISIBLE);

                if (!checkConnection()) {

                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, false);
                } else {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Cll report API
     */
    private void attemtReportNow(View view) {

        comment = getEtString(mBinding.etComment);

        for (int i = 0; i < mReportAnswerList.size(); i++) {
            if (mReportAnswerList.get(i).isSelected) {

                isSelected = true;
                reportId = mReportAnswerList.get(i).getId();
                reportText = mReportAnswerList.get(i).getAnswer_report_type();
                break;
            } else {
                isSelected = false;
                reportId = "";
                reportText = "";
            }
        }

        if (isSelected) {

            if (TextUtils.isEmpty(comment)) {
                mBinding.tvSubmit.setEnabled(true);
                mErrorLayout.showAlert(getString(R.string.error_msg_enter_comment), ErrorLayout.MsgType.Error, true);

                return;
            } else {

                if (checkConnection()) {

                    CancelOrder();

                } else {
                    mBinding.tvSubmit.setEnabled(true);
                    mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                }
            }
        } else {

            mErrorLayout.showAlert(getString(R.string.Please_select_the_atleast_1_option), ErrorLayout.MsgType.Error, true);

        }
    }

    // ************************ Socket ************************************

    // Socket Emit
    Emitter.Listener getCreateBooking = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("Create Booking", (String) args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCancelResponse = (String) args[0];
                    getCancelOrderResponse(mCancelResponse);
                }
            });
        }
    };

    private void getCancelOrderResponse(String response) {
        // Log.e("cancel", response);
        BaseModel mBaseModel = new BaseModel(mContext);
        if (response != null) {
            mBinding.tvSubmit.setEnabled(true);
            if (mBaseModel.isParse(response)) {
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    // obj.getString("message");
                    if (obj.getString("status").equals("1")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                                    mBinding.tvSubmit.setEnabled(false);
                                    try {
                                        // MyBookingFragment.tagLoadData = true;
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }finally {
                                        onBackPressed();
                                    }
                                    try {
                                        LiveFragment.tagReLoadData = false;
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }finally {
                                        onBackPressed();
                                    }



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    } else {
                        mBinding.tvSubmit.setEnabled(true);
                        mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public void CancelOrder() {
        if (SocketConnection.isConnected()) {
            // Log.e("I am connected", "I am connected");
            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_TRIP_CANCEL, getCreateBooking);

            JSONObject jsonObject = new JSONObject();


            try {
                jsonObject.put("userid", mSessionPref.user_userid);
                jsonObject.put("token", mSessionPref.token);
                jsonObject.put("language", Config.SELECTED_LANG);
                jsonObject.put("typefor", "user");
                jsonObject.put("tripid", tripId);
                jsonObject.put("cancel_order_id", reportId);
                jsonObject.put("cancel_msg", comment);


                //   Log.d("cancel booking", jsonObject.toString());

            } catch (JSONException e) {
                mBinding.tvSubmit.setEnabled(true);
                e.printStackTrace();
            }


            SocketConnection.emitToServer(Config.EMIT_GET_TRIP_CANCEL, jsonObject);
        } else {
            // Log.e("Cancel Booking=======>", "Error");
        }
    }


    ///broadcast messaging


    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received
                String response = intent.getStringExtra("response");


            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectivityReceiver.isConnected()) {

            new NotificationApi(mContext, mSessionPref.user_userid, mBinding.outerToolbar.tvNotificationBadge);
        } else {
            mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(ConfigNotif.PUSH_NOTIFICATION_USER));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        mBinding.tvSubmit.setEnabled(true);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onStop();
    }
}
