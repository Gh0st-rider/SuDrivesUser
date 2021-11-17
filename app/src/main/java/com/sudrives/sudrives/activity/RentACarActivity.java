package com.sudrives.sudrives.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.SocketListnerMethods.RentalListeners;
import com.sudrives.sudrives.adapter.RentalCabTypeAdapter;
import com.sudrives.sudrives.adapter.SelectPackageAdapter;
import com.sudrives.sudrives.databinding.ActivityRentAcarBinding;
import com.sudrives.sudrives.model.CabTypeModel;

import com.sudrives.sudrives.model.SelectPackageModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class RentACarActivity extends BaseActivity implements SelectPackageAdapter.AdapterCallback, RentalCabTypeAdapter.AdapterCabCallback {
    private ActivityRentAcarBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;

    private String isOnlinePayment = "2", mFromAddress = "", mToAddress = "", mFromLat = "", mFromLong = "", mToLat = "", mToLong = "",
            mPackageId = "", mVehicleId = "", couponCode = "", mIdSend = "", mPackageIdSend = "", mVehicleIdSend = "", mDatetime = "",mVehicleCityId="";
    private ErrorLayout mErrorLayout;
    private RentalCabTypeAdapter mAdapter;
    private SelectPackageAdapter mSelectPackageAdapter;
    private ArrayList<SelectPackageModel> mPackagelist;
    private ArrayList<CabTypeModel> mCablist;

    private ProgressDialog progressDialogPackage;
    private ProgressDialog progressDialogCabType;
    private ProgressDialog progressDialogCreateBooking;
    private BaseModel mBaseModel;
    private ProgressDialog progressDialogCancelledTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rent_acar);
        GlobalUtil.setStatusBarColor(RentACarActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getControl();

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    /*
     * initialize all controls
     * */
    private void getControl() {
        mContext = RentACarActivity.this;
        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        mPackagelist = new ArrayList<>();
        mCablist = new ArrayList<>();
        getIntentData();    // data get from previous activity
        progressDialogCancelledTrips = new ProgressDialog(mContext);
        progressDialogCancelledTrips.setMessage(getAppString(R.string.please_wait));
        progressDialogCancelledTrips.setCancelable(false);

        progressDialogPackage = new ProgressDialog(mContext);
        progressDialogPackage.setMessage(getAppString(R.string.please_wait));
        progressDialogPackage.setCancelable(false);

        progressDialogCabType = new ProgressDialog(mContext);
        progressDialogCabType.setMessage(getAppString(R.string.please_wait));
        progressDialogCabType.setCancelable(false);

        progressDialogCreateBooking = new ProgressDialog(mContext);
        progressDialogCreateBooking.setMessage(getAppString(R.string.please_wait));
        progressDialogCreateBooking.setCancelable(false);


        mBinding.toolBarRent.tvTitle.setText(getString(R.string.rent_a_car));
        mBinding.toolBarRent.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBarRent.ibRight1.setVisibility(View.GONE);
        mBinding.toolBarRent.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBarRent.tvNotificationBadge.setVisibility(View.GONE);
        mBinding.toolBarRent.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.tvSelectPackage.setOnClickListener(this::selectPackageClick);
        mBinding.btnConfirmRental.setOnClickListener(this::btnClick);
        FontLoader.setHelBold(mBinding.btnConfirmRental, mBinding.rbWallet, mBinding.layoutPaymentType.tvWallet);

        FontLoader.setHelRegular(mBinding.toolBarRent.tvTitle, mBinding.tvPickupLoc, mBinding.tvSelectPackage,
                mBinding.tvSelectCabType, mBinding.tvPaymentOption, mBinding.tvPickupLocVal, mBinding.layoutPaymentType.tvCash,
                mBinding.layoutPaymentType.tvCard);



        requestLastCancelledTrip();  // check last cancelled trips
        mBinding.rvCabType.setVisibility(View.GONE);
        mBinding.llChooseCab.setVisibility(View.GONE);

        mAdapter = new RentalCabTypeAdapter(mContext, "rental", this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mBinding.rvCabType.setLayoutManager(mLayoutManager);
        // mBinding.rvCabType.setAdapter(mAdapter);

        mSelectPackageAdapter = new SelectPackageAdapter(mContext);
        LinearLayoutManager mLayoutManagerPackage = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mBinding.rvPackage.setLayoutManager(mLayoutManagerPackage);
        //mBinding.rvPackage.setAdapter(mSelectPackageAdapter);
        mBinding.layoutPaymentType.llCard.setOnClickListener(this::cardClick);
        mBinding.layoutPaymentType.llCash.setOnClickListener(this::cashClick);
        mBinding.layoutPaymentType.llWallet.setOnClickListener(this::walletClick);

        isOnlinePayment = "2";
        mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.VISIBLE);
        mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
        mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);


        if (checkConnection()) {
            if (SocketConnection.isConnected()) {
                progressDialogPackage.show();

                RentalListeners.requestPackage(progressDialogPackage, mSessionPref.user_userid, mSessionPref.token,mFromLat,mFromLong);
                SocketConnection.attachSingleEventListener(Config.LISTENER_GET_RENTAL_PACKAGES, getPackage);

            } else {
                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
            }
        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
        }
    }

    private void cardClick(View view) {
        FontLoader.setHelBold(mBinding.layoutPaymentType.tvCard);
        FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCash, mBinding.layoutPaymentType.tvWallet);
        isOnlinePayment = "1";
        mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.VISIBLE);
        mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
        mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);

    }

    private void cashClick(View view) {
        FontLoader.setHelBold(mBinding.layoutPaymentType.tvCash);
        FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCard, mBinding.layoutPaymentType.tvWallet);
        isOnlinePayment = "0";
        mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);
        mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
        mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);

    }

    private void walletClick(View view) {
        FontLoader.setHelBold(mBinding.layoutPaymentType.tvWallet);
        FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCard, mBinding.layoutPaymentType.tvCash);
        isOnlinePayment = "2";
        mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
        mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
        mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.VISIBLE);

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        mFromAddress = bundle.getString("mFromaddress");
        mFromLat = bundle.getString("mFromaddressLat");
        mFromLong = bundle.getString("mFromaddressLong");
        mToAddress = bundle.getString("mToaddress");
        mToLat = bundle.getString("mToaddressLat");
        mToLong = bundle.getString("mToaddressLong");
        if (bundle.containsKey("datetime")) {
            mDatetime = bundle.getString("datetime");
        } else {
            mDatetime = "";
        }

        mBinding.tvPickupLocVal.setText(mFromAddress);

    }

    private void btnClick(View view) {
        if (mPackageIdSend.length() == 0) {
            mErrorLayout.showAlert(getAppString(R.string.please_select_package), ErrorLayout.MsgType.Error, true);
            return;
        }
        if (mIdSend.length() == 0) {
            mErrorLayout.showAlert(getAppString(R.string.please_select_cab_type), ErrorLayout.MsgType.Error, true);
            return;
        } else {
            if (SocketConnection.isConnected()) {
                createBooking(mIdSend, mPackageIdSend, mVehicleId);
            } else {
                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

            }
        }
    }

    /**
     * Navigation to the Back screen
     *
     * @param view
     */
    private void navigationBackScreen(View view) {
        Intent intent = new Intent();
        intent.putExtra("clear", "false");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(String mPackageName, String mPackageid, String _mVehicleCityId) {
        mPackageId = mPackageid;
        mVehicleCityId=  _mVehicleCityId;
        mBinding.tvSelectPackage.setText(Html.fromHtml("<font color=#9B9B9B>" + getAppString(R.string.selected_package) + " - </font><font color=#323643>" + mPackageName + "</font>"));

        // requestCabType(mPackageId);

        if (SocketConnection.isConnected()) {
            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_RENTAL_CAB_TYPE, getCabTypes);
            RentalListeners.requestCabType(progressDialogCabType, mPackageId, mSessionPref.user_userid, mSessionPref.token,_mVehicleCityId,mFromLat,mFromLong);
        } else {
            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

        }


    }

    /**
     * Click package
     *
     * @param view
     */
    private void selectPackageClick(View view) {
        mIdSend = "";
        mPackageIdSend = "";
        mVehicleId = "";
        if (mBinding.rvPackage.getVisibility() == View.VISIBLE) {

            // mSelectPackageAdapter.notifyDataSetChanged();
            // mBinding.rvPackage.setAdapter(null);
        } else {
            mBinding.rvPackage.setVisibility(View.VISIBLE);
            mBinding.llChooseCab.setVisibility(View.GONE);
            mBinding.rvCabType.setVisibility(View.GONE);
            mBinding.tvSelectPackage.setText(getAppString(R.string.select_package));
            mPackagelist.clear();
            mPackagelist.addAll(SelectPackageModel.getList(mContext, mBaseModel.getResultArray()));
            mSelectPackageAdapter.setList(mPackagelist, this);
            mSelectPackageAdapter.notifyDataSetChanged();


        }
    }

    @Override
    public void onClickCab(String id, String packageId, String vehicleId) {

        mIdSend = id;
        mPackageIdSend = packageId;
        mVehicleId = vehicleId;

    }
    // ************************ Socket ************************************

    // Socket Emit
    Emitter.Listener getPackage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
       //    Log.e("getPackage", (String) args[0]);
            String mPackageResponse = (String) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getPackageResponse(mPackageResponse);
                }
            });
        }
    };

    private void getPackageResponse(String response) {
        mBaseModel = new BaseModel(mContext);
        try {
            progressDialogPackage.dismiss();
            if (response != null) {
                if (mBaseModel.isParse(response)) {
                    try {
                        if (mBaseModel.getResultArray().length() > 0) {
                            mPackagelist.clear();
                            mPackagelist.addAll(SelectPackageModel.getList(mContext, mBaseModel.getResultArray()));
                            mSelectPackageAdapter.setList(mPackagelist, this);
                            mBinding.rvPackage.setAdapter(mSelectPackageAdapter);
                        }


                    } catch (Exception e) {
                        progressDialogPackage.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            progressDialogPackage.dismiss();
            e.printStackTrace();
        }
    }

    // Socket Emit
    Emitter.Listener getCabTypes = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           //  Log.e("getCabType", (String) args[0]);
            String mCabTypeResponse = (String) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getCabTypeResponse(mCabTypeResponse);
                }
            });
        }
    };


    private void getCabTypeResponse(String response) {
        Log.e("cabtypeTypeRental",response);
        BaseModel mBaseModel = new BaseModel(mContext);
        try {

            progressDialogCabType.dismiss();
            if (response != null) {
                if (mBaseModel.isParse(response)) {
                    Log.e("cabtypeTypeRental11111",response);
                    try {
                        mBinding.rvPackage.setVisibility(View.GONE);
                        mBinding.llChooseCab.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();

                        mBinding.rvCabType.setVisibility(View.VISIBLE);

                        if (mBaseModel.getResultArray().length() > 0) {
                            mCablist.clear();
                            mCablist.addAll(CabTypeModel.getList(mContext, mBaseModel.getResultArray()));
                            mAdapter.setList(mCablist, this);
                            mBinding.rvCabType.setAdapter(mAdapter);

                        }

                    } catch (Exception e) {
                        progressDialogCabType.dismiss();
                        e.printStackTrace();
                    }
                }else{

                    mErrorLayout.showAlert(getAppString(R.string.no_cab_available), ErrorLayout.MsgType.Error, true);

                }
            }
        } catch (Exception e) {
            progressDialogCabType.dismiss();
            e.printStackTrace();
        }
    }

    // Socket Emit
    Emitter.Listener getCreateBooking = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("Create Booking", (String) args[0]);
            String mConfirmBookingResponse = (String) args[0];
            getBookingResponse(mConfirmBookingResponse);

        }
    };

    public void createBooking(String id, String pakcageId, String vehicleId) {
        if (SocketConnection.isConnected()) {
            //  Log.e("I am connected", "I am connected");
            progressDialogCreateBooking.show();
            SocketConnection.attachSingleEventListener(Config.LISTENER_GET_CREATE_BOOKING, getCreateBooking);

            JSONObject jsonObject = new JSONObject();
            try {
                final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = clipboardManager.getPrimaryClip();
                // Get item count.
                int itemCount = clipData.getItemCount();
                if (itemCount > 0) {
                    // Get source text.
                    ClipData.Item item = clipData.getItemAt(0);
                    couponCode = item.getText().toString();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                jsonObject.put("userid", mSessionPref.user_userid);
                jsonObject.put("token", mSessionPref.token);
                jsonObject.put("language", Config.SELECTED_LANG);
                jsonObject.put("vehicle_types", vehicleId);
                jsonObject.put("book_from_address", mFromAddress);
                jsonObject.put("book_from_lat", mFromLat);
                jsonObject.put("book_from_long", mFromLong);
                jsonObject.put("book_from_long", mFromLong);
                jsonObject.put("book_coupon", couponCode);
                jsonObject.put("is_online_payment_accept", isOnlinePayment);
                jsonObject.put("vehicle_city_id", mVehicleCityId);

                if (mDatetime.trim().length() == 0) {
                    jsonObject.put("booking_type", "0");

                } else {
                    jsonObject.put("booking_type", "1");
                    jsonObject.put("book_later_date_time", mDatetime);
                }
                jsonObject.put("package_id", pakcageId);
                jsonObject.put("package_vehicle_id", id);

                jsonObject.put("type_of_booking", "1");

                Log.d("booking", jsonObject.toString());

            } catch (JSONException e) {
                progressDialogCreateBooking.dismiss();
                e.printStackTrace();
            }

            SocketConnection.emitToServer(Config.EMIT_GET_CREATE_BOOKING, jsonObject);

        } else {
            progressDialogCreateBooking.dismiss();
            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
            //   Log.e("Create Booking=======>", "Error");
        }
    }

    private void getBookingResponse(String response) {
        BaseModel mBaseModel = new BaseModel(mContext);
        JSONObject obj = null;
        try {
            obj = new JSONObject(response.toString());

            if (response != null) {
                if (mBaseModel.isParse(response)) {
                    try {
                        if (obj.getString("status").equals("1")) {

                            JSONObject finalObj = obj;
                            JSONObject finalObj4 = obj;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SessionPref.saveDataIntoSharedPrefConfirm(getApplicationContext(), SessionPref.SELECTED_BOOKING, Config.RENTAL);

                                    try {
                                        couponCode = "";
                                        GlobalUtil.clearClipBoard(mContext);

                                        progressDialogCreateBooking.dismiss();


                                        if (mDatetime.trim().length() == 0) {
                                            Toast.makeText(mContext, finalObj.getString("message"), Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent();
                                            intent.putExtra("clear", "true");
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        } else {
                                            dialogClickForAlert(true, mBaseModel.Message);
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        } else {
                            JSONObject finalObj3 = obj;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    progressDialogCreateBooking.dismiss();
                                    try {
                                        Toast.makeText(mContext, finalObj3.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            //  mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        JSONObject finalObj2 = obj;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                progressDialogCreateBooking.dismiss();
                                try {
                                    Toast.makeText(mContext, finalObj2.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } else {
                    //  mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                    JSONObject finalObj1 = obj;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            progressDialogCreateBooking.dismiss();

                            try {
                                //  Toast.makeText(mContext, finalObj1.getString("message"), Toast.LENGTH_LONG).show();

                                int layoutPopup;
                                int drawable;

                                drawable = R.drawable.failed_24dp;
                                String title = getAppString(R.string.failure);
                                layoutPopup = R.layout.failure_dialog;
                                AppDialogs.singleButtonVersionDialog(mContext, layoutPopup, title, drawable, finalObj1.getString("message"),
                                        getString(R.string.ok),
                                        new AppDialogs.SingleButoonCallback() {
                                            @Override
                                            public void singleButtonSuccess(String from) {


                                            }
                                        }, true);


                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                progressDialogCreateBooking.dismiss();
            }
        } catch (JSONException e) {
            progressDialogCreateBooking.dismiss();
            e.printStackTrace();
        }
    }

    public void dialogClickForAlert(boolean isSuccess, String msg) {
        int layoutPopup;
        int drawable;
        String title = "";
        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.ride_scheduled) + "!";
            layoutPopup = R.layout.success_dialog;
        } else {
            drawable = R.drawable.failed_24dp;
            title = "";
            layoutPopup = R.layout.failure_dialog;
        }
        String finalTitle = title;
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AppDialogs.singleButtonVersionDialog(mContext, layoutPopup, finalTitle, drawable, msg,
                            getString(R.string.ok),
                            new AppDialogs.SingleButoonCallback() {
                                @Override
                                public void singleButtonSuccess(String from) {
                                    Intent intent = new Intent();
                                    intent.putExtra("clear", "true");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }, true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void requestLastCancelledTrip() {
        JSONObject jsonObject = new JSONObject();
        progressDialogCancelledTrips.show();
        SocketConnection.attachSingleEventListener(Config.LISTENER_GET_CANCELLED_TRIP, getCancelledTripStatus);

        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);

        } catch (JSONException e) {
            e.printStackTrace();
            progressDialogCancelledTrips.dismiss();
        }


        SocketConnection.emitToServer(Config.EMIT_EMERGENCY_GET_CANCELLED_TRIP, jsonObject);
    }

    Emitter.Listener getCancelledTripStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          //  Log.e("Get Cancelled Trip", (String) args[0]);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialogCancelledTrips.dismiss();
                    String mStarted = (String) args[0];
                    if (mStarted != null) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(mStarted.toString());
                            if (obj.getString("status") != null) {
                                if (obj.getString("status").equalsIgnoreCase("0")) {


                                } else if (obj.getString("status").equalsIgnoreCase("1")) {

                                    String mShowCashOption = obj.getString("cancel_status");

                                    try {

                                        if (mShowCashOption.equalsIgnoreCase("0")) {
                                            mBinding.layoutPaymentType.llCash.setVisibility(View.VISIBLE);
                                            mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.VISIBLE);
                                            mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);
                                            FontLoader.setHelBold(mBinding.layoutPaymentType.tvCash);
                                            FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCard, mBinding.layoutPaymentType.tvWallet);
                                        }else if (mShowCashOption.equalsIgnoreCase("2")){
                                            mBinding.layoutPaymentType.llCash.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.VISIBLE);
                                            mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
                                            FontLoader.setHelBold(mBinding.layoutPaymentType.tvWallet);
                                            FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCard, mBinding.layoutPaymentType.tvCash);

                                        }else {
                                            FontLoader.setHelBold(mBinding.layoutPaymentType.tvCard);
                                            FontLoader.setHelRegular(mBinding.layoutPaymentType.tvCash, mBinding.layoutPaymentType.tvWallet);
                                            mBinding.layoutPaymentType.ivCardSuccess.setVisibility(View.VISIBLE);
                                            mBinding.layoutPaymentType.ivCashSuccess.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.ivWalletSuccess.setVisibility(View.GONE);
                                            mBinding.layoutPaymentType.llCash.setVisibility(View.GONE);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }


                            }


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
            });
        }
    };

}
