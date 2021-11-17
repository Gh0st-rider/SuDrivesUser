package com.sudrives.sudrives.fragment;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.NotificationActivity;
import com.sudrives.sudrives.adapter.ReportIssueAdapterNew;
import com.sudrives.sudrives.databinding.FragmentReportIssueListFragementNewBinding;
import com.sudrives.sudrives.model.ReportIssueListModelNew;
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

public class ReportIssueListFragementNew extends BaseFragment {
    private Context mContext;
    private ErrorLayout mErrorLayout;
    private static final String ARG_LAYOUT = "layout";
    private FragmentReportIssueListFragementNewBinding mBinding;
    private Calendar myCalendar;
    private ArrayList<ReportIssueListModelNew> myIssueListModel;
    private ReportIssueAdapterNew mAdapter;
    public LinearLayoutManager mLayoutManager;

    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;

    private SessionPref mSessionPref;

    private View rootView;

    Context context;

    public static ReportIssueListFragementNew newInstance(int layout) {
        ReportIssueListFragementNew fragment = new ReportIssueListFragementNew();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_issue_list_fragement_new, container, false);
        rootView = mBinding.getRoot();
        //   AppApplication.getInstance().trackScreenView(getAppString(R.string.u_history));


        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getControls(rootView);
    }

    private void getControls(View view) {
        mContext = getActivity();

        mSessionPref = new SessionPref(mContext);

        mErrorLayout = new ErrorLayout(mContext, view.findViewById(R.id.error_layout));

        mAdapter = new ReportIssueAdapterNew(mContext);
        myIssueListModel = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mBinding.rvUserHistory.setLayoutManager(mLayoutManager);
        mBinding.rvUserHistory.setItemAnimator(new DefaultItemAnimator());

        myCalendar = Calendar.getInstance();


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvUserHistory.setLayoutManager(mLayoutManager);
        mBinding.rvUserHistory.setItemAnimator(new DefaultItemAnimator());

        if (checkConnection()) {
            userTripsMyBookingAPI();

        } else {
            mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

        }
        TextView tv_notification_badge = (TextView) getActivity().findViewById(R.id.tv_notification_badge);
        tv_notification_badge.setVisibility(View.GONE);
        ImageView iv_notification = (ImageView) getActivity().findViewById(R.id.iv_notification);
        iv_notification.setVisibility(View.GONE);

        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent(context, NotificationActivity.class));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
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


        mBaseRequest.setBaseRequest(jsonObj, "api/get_report_issue", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {


                    myIssueListModel.clear();
                    if (mBaseModel.getResultArray() != null) {
                        if (mBaseModel.getResultArray().length() > 0) {
                            mBinding.rvUserHistory.setVisibility(View.VISIBLE);
                            mBinding.tvMyBookingsActiveNoDataFound.setVisibility(View.GONE);

                            myIssueListModel.addAll(ReportIssueListModelNew.getMyIssueList(mBaseModel.getResultArray()));


                            mAdapter = new ReportIssueAdapterNew(mContext);
                            mAdapter.setList(myIssueListModel);

                            mBinding.rvUserHistory.setAdapter(mAdapter);

                        } else {
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

    private int REQUEST_CODE_CONFIRM_RETURN = 1245;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONFIRM_RETURN) {
            if (resultCode == getActivity().RESULT_OK) {
                String str = data.getStringExtra("flag");
                //  Log.e("clear ===== ", str);
                if (str.equals("true")) {
                    if (checkConnection()) {
                        userTripsMyBookingAPI();

                    } else {
                        mErrorLayout.showAlert(getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

                    }
                }
            }
        }
    }

}
