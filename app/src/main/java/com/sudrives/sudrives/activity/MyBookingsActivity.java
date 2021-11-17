package com.sudrives.sudrives.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sudrives.sudrives.R;

import com.sudrives.sudrives.adapter.MyBookingAdapter;
import com.sudrives.sudrives.adapter.UpComingBookingAdapter;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ActivityMyBookingsBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.model.HistoryListModel;

import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
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


public class  MyBookingsActivity extends BaseActivity {
    private ActivityMyBookingsBinding mBinding;
    private Context mContext;
    private ErrorLayout mErrorLayout;
    private BaseRequest mBaseRequest;
    private MyBookingAdapter mAdapter;
    private UpComingBookingAdapter mUpComingAdapter;
    private BaseModel mBaseModel;
    private SessionPref mSessionPref;
    private LinearLayoutManager mLayoutManager;
    private String type = "active";
    private ArrayList<HistoryListModel> myBookingsListModel = new ArrayList<>();
    public static boolean tagCancelBooking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_bookings);
        GlobalUtil.setStatusBarColor(MyBookingsActivity.this, getResources().getColor(R.color.colorPrimaryDark));

        getControls();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }


    private void getControls() {
        mContext = MyBookingsActivity.this;
        mSessionPref = new SessionPref(mContext);
        mBaseModel = new BaseModel(mContext);
        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));

        mBinding.toolBarBooking.tvTitle.setText(getAppString(R.string.my_bookings));
        mBinding.toolBarBooking.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBarBooking.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBarBooking.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBarBooking.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBarBooking.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBarBooking.ibRight1.setOnClickListener(this::notificationClick);

        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBinding.rvMyBooking.setLayoutManager(mLayoutManager);
        mBinding.rvMyBooking.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBinding.rvUpComingBooking.setLayoutManager(mLayoutManager1);
        mBinding.rvUpComingBooking.setItemAnimator(new DefaultItemAnimator());


        mBinding.tvUpcomingBookings.setOnClickListener(this::tabUpComingClick);
        mBinding.tvPastBookings.setOnClickListener(this::tabPastBookingClick);


        FontLoader.setHelBold(mBinding.tvUpcomingBookings);
        FontLoader.setHelRegular(mBinding.tvPastBookings, mBinding.toolBarBooking.tvTitle);
        SetUpcomingSelected();


        if (checkConnection()) {
            //type = "active";
            //  userTripsMyBookingAPI(type);

            if (SocketConnection.isConnected()) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }

    }


    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received


                String response = intent.getStringExtra("response");

                if (ConnectivityReceiver.isConnected()) {

                    new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBarBooking.tvNotificationBadge);
                } else {
                    mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);

                }

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectivityReceiver.isConnected()) {

            // type = "active";
            userTripsMyBookingAPI(type);

            new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBarBooking.tvNotificationBadge);
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


    private void tabUpComingClick(View view) {
        SetUpcomingSelected();


    }

    private void SetUpcomingSelected() {
        mBinding.tvUpcomingBookings.setActivated(true);
        mBinding.tvPastBookings.setActivated(false);
        FontLoader.setHelBold(mBinding.tvUpcomingBookings);
        FontLoader.setHelRegular(mBinding.tvPastBookings);

        mBinding.tvUpcomingBookings.setBackgroundColor(getResources().getColor(R.color.colorGrayView));
        mBinding.tvPastBookings.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        if (checkConnection()) {
            type = "active";
            userTripsMyBookingAPI(type);


        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    private void tabPastBookingClick(View view) {
        mBinding.tvPastBookings.setActivated(true);
        mBinding.tvUpcomingBookings.setActivated(false);
        FontLoader.setHelBold(mBinding.tvPastBookings);
        FontLoader.setHelRegular(mBinding.tvUpcomingBookings);

        mBinding.tvPastBookings.setBackgroundColor(getResources().getColor(R.color.colorGrayLight));
        mBinding.tvUpcomingBookings.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        if (checkConnection()) {
            type = "history";
            userTripsMyBookingAPI(type);


        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }

    }

    /**
     * Navigation to the Back screen
     *
     * @param view
     */
    private void navigationBackScreen(View view) {
        finishAllActivities();
    }

    /*click of notification to navigate */

    private void notificationClick(View view) {
        startActivity(new Intent(mContext, NotificationActivity.class));
    }

    /***************    Implement API    *************************/


    private void userTripsMyBookingAPI(String type) {
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid,
                "trips_type", type, "lat", "", "lang", "", "token", mSessionPref.token, "typefor", "user");


        mBaseRequest.setBaseRequest(jsonObj, "SocketApi/user_trips", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                Log.e("my_booking_res",response+"");

                if (mBaseModel.isParse(response.toString())) {

                    if (mBaseModel.getResultArray() != null) {
                        myBookingsListModel.clear();
                        if (mBaseModel.getResultArray().length() > 0) {
                            mBinding.rvMyBooking.setVisibility(View.VISIBLE);
                            mBinding.tvNoDataFound.setVisibility(View.GONE);


                            myBookingsListModel.addAll(HistoryListModel.getMyBookingsList(mBaseModel.getResultArray()));

                            if (type.equals("active")) {
                                mBinding.rvMyBooking.setVisibility(View.GONE);
                                mUpComingAdapter = new UpComingBookingAdapter(mContext);
                                mUpComingAdapter.setList(myBookingsListModel);
                                mBinding.rvUpComingBooking.setAdapter(mUpComingAdapter);
                                mBinding.rvUpComingBooking.setVisibility(View.VISIBLE);

                            } else {
                                mBinding.rvUpComingBooking.setVisibility(View.GONE);

                                mAdapter = new MyBookingAdapter(mContext);
                                mAdapter.setList(myBookingsListModel);
                                mBinding.rvMyBooking.setAdapter(mAdapter);
                                mBinding.rvMyBooking.setVisibility(View.VISIBLE);
                            }

                        } else {
                            mBinding.rvMyBooking.setVisibility(View.GONE);
                            mBinding.rvUpComingBooking.setVisibility(View.GONE);
                            mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                            mBinding.tvNoDataFound.setText(mBaseModel.Message);

                        }
                    } else {
                        mBinding.rvMyBooking.setVisibility(View.GONE);
                        mBinding.rvUpComingBooking.setVisibility(View.GONE);
                        mBinding.tvNoDataFound.setVisibility(View.VISIBLE);
                        mBinding.tvNoDataFound.setText(mBaseModel.Message);


                    }
                } else {
                    mBinding.rvMyBooking.setVisibility(View.GONE);
                    mBinding.rvUpComingBooking.setVisibility(View.GONE);
                    mBinding.tvNoDataFound.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void getError(Object response, String error) {

                mBinding.rvMyBooking.setVisibility(View.GONE);
                mBinding.rvUpComingBooking.setVisibility(View.GONE);
                mBinding.tvNoDataFound.setVisibility(View.VISIBLE);

                if (!checkConnection()) {
                    // dialogClickForAlert(false,getAppString(R.string.something_went_wrong), "");
                } else {
                    // dialogClickForAlert(false,getAppString(R.string.something_went_wrong), "");
                }
            }
        }, false);

    }
}
