package com.sudrives.sudrives.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityReferNwinBinding;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;


public class ReferNWinActivity extends Activity {
    private ActivityReferNwinBinding mBinding;
    private Context mContext;
    private ErrorLayout mErrorLayout;
    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;
    private SessionPref mSessionPref;
    private String mReferralCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_refer_nwin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        getControls();
    }

    private void getControls() {
        mContext = ReferNWinActivity.this;
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        mSessionPref = new SessionPref(mContext);
        //mBinding.tvReferTitle.setText(getAppString(R.string.refer_n_win));
        mBinding.tvTitle.setTypeface(null, Typeface.BOLD);
        mBinding.tvrcCode.setTypeface(null, Typeface.BOLD);
        mBinding.ivReferLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.ivReferLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.rlShare.setOnClickListener(this::shareCode);
        mReferralCode = mSessionPref.referral_code;
        mBinding.tvrcCode.setText(mReferralCode);

        FontLoader.setHelBold(mBinding.tvShare, mBinding.tvTitle, mBinding.tvrcCode);
        FontLoader.setHelRegular(mBinding.tvPleaseVisitMovecabAgain, mBinding.tvRcText);

    }

    /**
     * Navigation to the Back screen
     *
     * @param view
     */
    private void navigationBackScreen(View view) {
        finish();
    }

    /**
     * Share
     *
     * @param view
     */
    private void shareCode(View view) {
        GlobalUtil.avoidDoubleClicks(mBinding.rlShare);

        String shareBody = "Hi,\n" + "Cheapest, Easiest & Fastest - Cabs booked through " + getResources().getString(R.string.app_name) + " mobile-app... #"+ getResources().getString(R.string.app_name) + " rocks!\n\nReferral Code : " + mReferralCode + "\n\n" +
                "http://play.google.com/store/apps/details?id=" + mContext.getPackageName();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Refer " + getResources().getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }
}
