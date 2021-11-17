package com.sudrives.sudrives.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.adapter.ReportIssueAdapter;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ItemListBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.model.ReportIssueListModel;
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
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ReportIssueListActivity extends BaseActivity {
    private Context mContext;
    private ErrorLayout mErrorLayout;
    private static final String ARG_LAYOUT = "layout";
    private ItemListBinding mBinding;
    private Calendar myCalendar;
    private ArrayList<ReportIssueListModel> myIssueListModel;
    private ReportIssueAdapter mAdapter;
    public LinearLayoutManager mLayoutManager;

    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;

    private SessionPref mSessionPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.item_list);
        GlobalUtil.setStatusBarColor(ReportIssueListActivity.this, getResources().getColor(R.color.colorPrimaryDark));

        getControls();

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControls() {
        mContext = ReportIssueListActivity.this;

        mSessionPref = new SessionPref(mContext);

        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));

        mBinding.toolBar.tvTitle.setText(getAppString(R.string.reported_issue));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.ibRight1.setOnClickListener(this::notificationClick);
        FontLoader.setHelRegular(mBinding.toolBar.tvTitle);

        mAdapter = new ReportIssueAdapter(mContext);
        myIssueListModel = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBinding.rvUserHistory.setLayoutManager(mLayoutManager);
        mBinding.rvUserHistory.setItemAnimator(new DefaultItemAnimator());

        myCalendar = Calendar.getInstance();


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mBinding.rvUserHistory.setLayoutManager(mLayoutManager);
        mBinding.rvUserHistory.setItemAnimator(new DefaultItemAnimator());
    }

    private void navigationBackScreen(View view) {
        finishAllActivities();
    }

    private void notificationClick(View view) {
        startActivity(new Intent(mContext, NotificationActivity.class));
    }



    /***************    Implement API    *************************/
    private void userTripsMyBookingAPI() {
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid);


        mBaseRequest.setBaseRequest(jsonObj, "api/get_report_issue", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN ,Config.DEVICE_TYPE,new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {


                    myIssueListModel.clear();
                    if (mBaseModel.getResultArray() != null) {
                        if (mBaseModel.getResultArray().length() > 0) {
                            mBinding.rvUserHistory.setVisibility(View.VISIBLE);
                            mBinding.tvMyBookingsActiveNoDataFound.setVisibility(View.GONE);

                            myIssueListModel.addAll(ReportIssueListModel.getMyIssueList(mBaseModel.getResultArray()));


                            mAdapter = new ReportIssueAdapter(mContext);
                            mAdapter.setList(myIssueListModel);

                            mBinding.rvUserHistory.setAdapter(mAdapter);

                        }else {
                            mBinding.rvUserHistory.setVisibility(View.GONE);
                            mBinding.tvMyBookingsActiveNoDataFound.setVisibility(View.VISIBLE);
                            mBinding.tvMyBookingsActiveNoDataFound.setText(mBaseModel.Message);
                            // mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                        }
                    }
                } else {
                    mBinding.rvUserHistory.setVisibility(View.GONE);
                    mBinding.tvMyBookingsActiveNoDataFound.setVisibility(View.VISIBLE);
                    mBinding.tvMyBookingsActiveNoDataFound.setText(mBaseModel.Message);

                    //mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                }
            }

            @Override
            public void getError(Object response, String error) {

                mBinding.rvUserHistory.setVisibility(View.GONE);
                mBinding.tvMyBookingsActiveNoDataFound.setVisibility(View.VISIBLE);

                if (!checkConnection()) {
                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

                } else {

                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
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

                if (ConnectivityReceiver.isConnected()) {
                    //notificationListApi();
                    new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);
                } else {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                }

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (checkConnection()) {
            userTripsMyBookingAPI();


        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

        }
        if (ConnectivityReceiver.isConnected()) {
            //notificationListApi();
            new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);
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




}


