package com.sudrives.sudrives.activity;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.sudrives.sudrives.R;

import com.sudrives.sudrives.databinding.ActivityRefundRequestBinding;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.DOBDate;
import com.sudrives.sudrives.utils.DateUtil;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

public class RefundRequestActivity extends BaseActivity {
    private ActivityRefundRequestBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;
    private ErrorLayout mErrorLayout;
    private String mCommentStr = "", mDateStr = "", mOrderIdStr = "", mTransactionAmtStr = "", mTransactionIdStr = "";
    private BaseModel mBaseModel;
    private BaseRequest mBaseRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_refund_request);

        GlobalUtil.setStatusBarColor(RefundRequestActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        mContext = RefundRequestActivity.this;
        mSessionPref = new SessionPref(mContext);
        getControls();
        KeyboardUtil.setupUI(mBinding.clTop, RefundRequestActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControls() {
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));

        mBinding.toolBar.tvTitle.setText(getAppString(R.string.refund_request));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibLeft.setOnClickListener(this::backScreenClick);
        mBinding.toolBar.ibRight1.setVisibility(View.GONE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBar.ibRight1.setOnClickListener(this::notificationClick);

        mBinding.tvSubmit.setOnClickListener(this::callRefundApi);
        mBinding.rlDate.setOnClickListener(this::selectDateClick);
        mBinding.tvDate.setOnClickListener(this::selectDateClick);

        FontLoader.setHelRegular(mBinding.toolBar.tvTitle, mBinding.tvOrderIdT, mBinding.tvOrderId, mBinding.tvTransactionIdT, mBinding.tvTransactionId, mBinding.tvdatetitle, mBinding.tvDate
                , mBinding.tvTransactionAmtT, mBinding.tvTransactionAmt, mBinding.tvCommentT, mBinding.etComment);
        FontLoader.setHelBold(mBinding.tvSubmit);

    }

    private void callRefundApi(View view) {
        //register button click

        getValues();
        if (isValidAlls()) {
            if (checkConnection()) {
                mBinding.tvSubmit.setEnabled(false);
                refundRequestAPI();

            } else {
                mBinding.tvSubmit.setEnabled(true);
                dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.tvSubmit.setEnabled(true);
    }

    /**
     * Error dialog for the API error messages
     *
     * @param msg :- Message which we need to show.
     */
    public void dialogClickForAlert(boolean isSuccess, String msg) {
        int layoutPopup;
        int drawable;
        String title;
        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.confirmation) + "!";
            layoutPopup = R.layout.success_dialog;
        } else {
            drawable = R.drawable.failed_24dp;
            title = "";
            layoutPopup = R.layout.failure_dialog;
        }


        AppDialogs.singleButtonVersionDialog(RefundRequestActivity.this, layoutPopup, title, drawable, msg,
                getString(R.string.ok),
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {


                    }
                }, true);
    }

    private void getValues() {
        mCommentStr = getEtString(mBinding.etComment);
        mDateStr = getEtString(mBinding.tvDate);
        mOrderIdStr = getTvString(mBinding.tvOrderId);
        mTransactionAmtStr = getTvString(mBinding.tvTransactionAmt);
        mTransactionIdStr = getTvString(mBinding.tvTransactionId);

    }

    /*
     * check all validations
     * */

    private boolean isValidAlls() {
        if (TextUtils.isEmpty(mOrderIdStr)) {
            mErrorLayout.showAlert(getAppString(R.string.please_enter_order_id), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mTransactionIdStr)) {
            mErrorLayout.showAlert(getAppString(R.string.please_enter_transaction_id), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mDateStr)) {
            mErrorLayout.showAlert(getAppString(R.string.please_select_date), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mTransactionAmtStr)) {
            mErrorLayout.showAlert(getAppString(R.string.please_transaction_amount), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mCommentStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_comment), ErrorLayout.MsgType.Error, true);
            return false;
        }
        return true;
    }


    /**
     * Navigation to back screen.
     *
     * @param view
     */
    private void backScreenClick(View view) {

        Intent intent = new Intent();
        intent.putExtra("flag", "false");
        setResult(RESULT_OK, intent);
        finish();
    }

    /*
      date click
      */
    private void selectDateClick(View view) {
        // Log.e(TAG, "cliclkkkkk");
        mBinding.rlDate.setEnabled(false);
        mBinding.tvDate.setEnabled(false);
        mBinding.etComment.setEnabled(false);
        mBinding.tvTransactionId.setEnabled(false);
        mBinding.tvTransactionAmt.setEnabled(false);
        mBinding.tvOrderId.setEnabled(false);
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.clTop);
        DOBDate.setEtDOBNew(mContext, mBinding.tvDate, new DOBDate.dobListener() {
            @Override
            public void returnDOB(String dob) {
                if (!TextUtils.isEmpty(dob)) {

                 //   Log.d("DOB==>> ", dob);
                    mBinding.tvDate.setEnabled(true);
                    mBinding.rlDate.setEnabled(true);
                    mBinding.etComment.setEnabled(true);
                    mBinding.tvTransactionId.setEnabled(true);
                    mBinding.tvTransactionAmt.setEnabled(true);
                    mBinding.tvOrderId.setEnabled(true);

                    mDateStr = dob;//DateUtil.dateFormat(dob, "yyyy-MM-dd", "dd/MM/yyyy");
                    mBinding.tvDate.setText("" + mDateStr);
                    mDateStr = DateUtil.dateFormat(dob, "dd-MMM-yyyy", "yyyy-MM-dd");

                }

            }

            @Override
            public void cancelDOB() {
                mBinding.tvDate.setEnabled(true);
                mBinding.rlDate.setEnabled(true);
                mBinding.etComment.setEnabled(true);
                mBinding.tvTransactionId.setEnabled(true);
                mBinding.tvTransactionAmt.setEnabled(true);
                mBinding.tvOrderId.setEnabled(true);

            }


        });
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.clTop);
    }
      /*click of notification to navigate */

    private void notificationClick(View view) {
        startActivity(new Intent(mContext, NotificationActivity.class));
    }


    /***************    Implement API    *************************/
    private void refundRequestAPI() {


        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid, "paytm_orderid", mOrderIdStr, "paytm_trxid",
                mTransactionIdStr, "amount", mTransactionAmtStr, "description", mCommentStr, "payment_date", mDateStr);

        mBaseRequest.setBaseRequest(jsonObj, "api/submit_paytm_report_issues", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {
                mBinding.tvSubmit.setEnabled(true);
                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {
                    mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Info, true);
                    mBinding.etComment.setText("");
                    mBinding.tvDate.setText("");
                    mBinding.tvOrderId.setText("");
                    mBinding.tvTransactionAmt.setText("");
                    mBinding.tvTransactionId.setText("");
                    mOrderIdStr = "";
                    mTransactionIdStr = "";
                    mTransactionAmtStr = "";
                    mCommentStr = "";
                    mDateStr = "";
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("flag", "true");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }, 2000);

                } else {
                    mBinding.tvSubmit.setEnabled(true);
                    mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);

                }
            }

            @Override
            public void getError(Object response, String error) {
                mBinding.tvSubmit.setEnabled(true);

                if (!checkConnection()) {
                    mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);

                } else {
                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

                }
            }
        }, false);


    }

}
