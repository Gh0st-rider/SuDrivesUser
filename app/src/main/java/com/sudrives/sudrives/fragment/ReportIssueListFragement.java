package com.sudrives.sudrives.fragment;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.NotificationActivity;
import com.sudrives.sudrives.adapter.ReportIssueAdapter;

import com.sudrives.sudrives.databinding.ItemListBinding;
import com.sudrives.sudrives.model.ReportIssueListModel;
import com.sudrives.sudrives.utils.BaseFragment;
import com.sudrives.sudrives.utils.Config;

import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ReportIssueListFragement extends BaseFragment {
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
    private View rootView;
    public static ReportIssueListFragement newInstance(int layout) {
        ReportIssueListFragement fragment = new ReportIssueListFragement();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.item_list, container, false);
         rootView = mBinding.getRoot();


        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getControls(rootView);
        if (checkConnection()) {
            userTripsMyBookingAPI();


        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

        }
    }

    private void getControls(View view) {
        mContext = getActivity();

        mSessionPref = new SessionPref(mContext);

        mErrorLayout = new ErrorLayout(mContext, view.findViewById(R.id.error_layout));

        mAdapter = new ReportIssueAdapter(mContext);
        myIssueListModel = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mBinding.rvUserHistory.setLayoutManager(mLayoutManager);
        mBinding.rvUserHistory.setItemAnimator(new DefaultItemAnimator());

        myCalendar = Calendar.getInstance();


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvUserHistory.setLayoutManager(mLayoutManager);
        mBinding.rvUserHistory.setItemAnimator(new DefaultItemAnimator());
        ImageView iv_notification = (ImageView) getActivity().findViewById(R.id.iv_notification);
        iv_notification.setVisibility(View.VISIBLE);

        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(mContext, NotificationActivity.class));


            }
        });}


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

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
}
