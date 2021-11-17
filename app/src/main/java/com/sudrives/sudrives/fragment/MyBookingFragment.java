package com.sudrives.sudrives.fragment;

import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.HomeMenuActivity;
import com.sudrives.sudrives.activity.MainActivity;
import com.sudrives.sudrives.activity.NotificationActivity;
import com.sudrives.sudrives.adapter.MyBookingAdapter;
import com.sudrives.sudrives.adapter.UpComingBookingAdapter;
import com.sudrives.sudrives.databinding.ActivityMyBookingsBinding;
import com.sudrives.sudrives.model.HistoryListModel;
import com.sudrives.sudrives.utils.BaseFragment;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class MyBookingFragment extends BaseFragment {
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
    private View rootView;
    public static boolean tagLoadData = true;

    public MyBookingFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_my_bookings, container, false);
        rootView = mBinding.getRoot();

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getControls(rootView);
    }

    private void getControls(View rootView) {
        mContext = getActivity();
        mSessionPref = new SessionPref(mContext);
        mSessionPref = new SessionPref(mContext);
        mBaseModel = new BaseModel(mContext);

        mErrorLayout = new ErrorLayout(mContext, rootView.findViewById(R.id.error_layout));

        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBinding.rvMyBooking.setLayoutManager(mLayoutManager);
        mBinding.rvMyBooking.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBinding.rvUpComingBooking.setLayoutManager(mLayoutManager1);
        mBinding.rvUpComingBooking.setItemAnimator(new DefaultItemAnimator());


        mBinding.tvUpcomingBookings.setOnClickListener(this::tabUpComingClick);
        mBinding.tvPastBookings.setOnClickListener(this::tabPastBookingClick);


        FontLoader.setHelBold(mBinding.tvUpcomingBookings);
        FontLoader.setHelRegular(mBinding.tvPastBookings);
        FontLoader.setHelRegular(mBinding.toolBarBooking.tvTitle);

        ImageView iv_notification = (ImageView) getActivity().findViewById(R.id.iv_notification);
        TextView tv_notification_badge = getActivity().findViewById(R.id.tv_notification_badge);
        tv_notification_badge.setVisibility(View.GONE);
        iv_notification.setVisibility(View.GONE);



        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() != null)
                    startActivity(new Intent(((HomeMenuActivity) getActivity()), NotificationActivity.class));
            }
        });
        SetUpcomingSelected();
        //reloadData(true);


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

        mBinding.tvPastBookings.setBackgroundColor(getResources().getColor(R.color.colorGrayView));
        mBinding.tvUpcomingBookings.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        if (checkConnection()) {
            type = "history";
            userTripsMyBookingAPI(type);


        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void userTripsMyBookingAPI(String type) {
        tagLoadData=false;
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid,
                "trips_type", type, "lat", "", "lang", "", "token", mSessionPref.token, "typefor", "user");


        mBaseRequest.setBaseRequest(jsonObj, "SocketApi/user_trips", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                Log.e("API_Response","===>>>"+response);

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


    public void reloadData(boolean response) {
        if (response) {
            if (checkConnection()) {
                try {
                    userTripsMyBookingAPI(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        if (tagLoadData) {
            if (ConnectivityReceiver.isConnected()) {

                // type = "active";
                reloadData(true);


            } else {
                mErrorLayout.showAlert(getAppString(R
                        .string.no_internet_connection), ErrorLayout.MsgType.Error, true);

            }
        }
    }

}
