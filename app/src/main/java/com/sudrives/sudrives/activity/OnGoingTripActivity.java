package com.sudrives.sudrives.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityOnGoingTripBinding;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;

public class OnGoingTripActivity extends BaseActivity {
    private ActivityOnGoingTripBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;
    private ErrorLayout mErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_going_trip);
        getControl();
        GlobalUtil.setStatusBarColor(OnGoingTripActivity.this, getResources().getColor(R.color.colorPrimaryDark));

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControl() {
        mContext = OnGoingTripActivity.this;
        mSessionPref = new SessionPref(mContext);

        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));

        mBinding.toolBar.tvTitle.setText(getAppString(R.string.ongoing));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.help_24dp);
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.ibRight1.setOnClickListener(this::helpClick);

    }

    private void navigationBackScreen(View view) {
        finishAllActivities();
    }

    /*click of notification to navigate */

    private void helpClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.help_dialog, null);
        LinearLayout ll_EnableEmergencyMode = dialogLayout.findViewById(R.id.ll_EnableEmergencyMode);
        LinearLayout ll_SendRideStatus = dialogLayout.findViewById(R.id.ll_SendRideStatus);
        ImageView ivCross = dialogLayout.findViewById(R.id.ivCross);
        builder.setView(dialogLayout);
        AlertDialog testDialog = builder.create();


        ll_SendRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDialog.dismiss();
                startActivity(new Intent(mContext, SendRideStatusActivity.class));
            }
        });

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDialog.dismiss();
            }
        });


        testDialog.show();
    }
}
