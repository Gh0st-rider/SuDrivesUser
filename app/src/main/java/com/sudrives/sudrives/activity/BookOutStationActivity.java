package com.sudrives.sudrives.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Handler;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.adapter.OutStationCabTypeAdapter;
import com.sudrives.sudrives.databinding.ActivityBookOutStationBinding;
import com.sudrives.sudrives.listeners.OutStationListeners;
import com.sudrives.sudrives.model.OutStationCabTypeModel;
import com.sudrives.sudrives.model.OutStationDataModel;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.DateUtil;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.SocketConnection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.socket.emitter.Emitter;

public class BookOutStationActivity extends BaseActivity implements OutStationCabTypeAdapter.AdapterCabCallback {
    private ActivityBookOutStationBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;

    private ErrorLayout mErrorLayout;
    private String mDateStr = "", mLeaveOnDate = "", mLeaveOnDateSend = "", mLeaveOnTimeSend = "",
            mReturnDateSend = "", mReturnTimeSend = "", mLeaveOnTime = "", mRoundTrip = "0",
            mFromAddress = "", mToAddress = "", mFromLat = "", mFromLong = "", mToLat = "", mToLong = "", mLeaveCompare = "", mVehicleCityId = "";
    private ProgressDialog mprogressBooking, progressDialogCabType;
    private ArrayList<OutStationCabTypeModel> mCablist;

    private OutStationCabTypeAdapter mAdapter;
    private OutStationDataModel mOutStationDataModel;
    private DateFormat dfDateSend, dfTimeSend;
    private SimpleDateFormat sfTimeSend, sfDateSend;
    private int REQUEST_CONFIRM_BOOKING = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_book_out_station);
        GlobalUtil.setStatusBarColor(BookOutStationActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getControl();


    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    /*
     * initialize all controls
     * */
    private void getControl() {

        mContext = BookOutStationActivity.this;
        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));

        mCablist = new ArrayList<>();
        mprogressBooking = new ProgressDialog(mContext);
        mprogressBooking.setMessage(getAppString(R.string.please_wait));
        mprogressBooking.setCancelable(false);

        progressDialogCabType = new ProgressDialog(mContext);
        progressDialogCabType.setMessage(getAppString(R.string.please_wait));
        progressDialogCabType.setCancelable(false);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mprogressBooking != null) {
                        mprogressBooking.dismiss();
                    }
                    if (progressDialogCabType != null) {
                        progressDialogCabType.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000 * 45);


        dfDateSend = new SimpleDateFormat("yyyy-MM-dd");
        sfDateSend = new SimpleDateFormat("yyyy-MM-dd");
        dfTimeSend = new SimpleDateFormat("HH:mm:ss");
        sfTimeSend = new SimpleDateFormat("HH:mm:ss");

        getIntentData();    // data get from previous activity
        setLeaveDate(false);   // show leave date (add 1 hr in current time)
        // show Return date (add 1 hr  after leave date)
        mBinding.toolBarOutstation.tvTitle.setText(getString(R.string.book_your_outstation_ride));
        mBinding.toolBarOutstation.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBarOutstation.ibRight1.setVisibility(View.GONE);
        mBinding.toolBarOutstation.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBarOutstation.tvNotificationBadge.setVisibility(View.GONE);
        mBinding.toolBarOutstation.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.tvLeaveonVal.setOnClickListener(this::leaveCalenderOpen);
        mBinding.tvReturnbyVal.setOnClickListener(this::returnCalenderOpen);

        FontLoader.setHelBold(mBinding.rbOneWay);
        FontLoader.setHelRegular(mBinding.toolBarOutstation.tvTitle);
        FontLoader.setHelRegular(mBinding.tvOriginTrip, mBinding.tvDestinationTrip, mBinding.tvSelectDateTime, mBinding.rbRoundTrip
                , mBinding.tvLeaveon, mBinding.tvLeaveonVal, mBinding.tvReturnby, mBinding.tvReturnbyVal, mBinding.tvSelectCabType);

        mAdapter = new OutStationCabTypeAdapter(mContext, "outstation", this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mBinding.rvCabType.setLayoutManager(mLayoutManager);
        // mBinding.rvCabType.setAdapter(mAdapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.sbOutStation.scrollTo(0, 0);

            }
        }, 100);

        mBinding.rbOneWay.setChecked(true);
        mBinding.llReturnBy.setVisibility(View.GONE);
        mBinding.radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbOneWay) {
                    mRoundTrip = "0";
                    setLeaveDate(true);   // show leave date (add 1 hr in current time)
                    FontLoader.setHelBold(mBinding.rbOneWay);
                    FontLoader.setHelRegular(mBinding.rbRoundTrip);
                    mBinding.llReturnBy.setVisibility(View.GONE);
                    mReturnDateSend = "";
                    mReturnTimeSend = "";
                    /*   mBinding.tvReturnbyVal.setText(R.string.select);

                     */
                    requestVehicles();
                } else if (checkedId == R.id.rbRoundTrip) {
                    mRoundTrip = "1";
                    setReturnDate(mLeaveOnDate, mLeaveOnTime,true);
                    FontLoader.setHelBold(mBinding.rbRoundTrip);
                    FontLoader.setHelRegular(mBinding.rbOneWay);
                    mBinding.llReturnBy.setVisibility(View.VISIBLE);

                   // requestVehicles();
                    // dfgdfgdfgfgdfg
                }

            }
        });

        requestVehicles();
    }

    private void requestVehicles() {
        try {
            if (SocketConnection.isConnected()) {
                SocketConnection.attachSingleEventListener(Config.LISTENER_GET_OUTSTATION_CAB_TYPE, getCabTypes);

                OutStationListeners.requestOutStationCabType(progressDialogCabType, mRoundTrip, mSessionPref.user_userid, mSessionPref.token,
                        mFromAddress, mFromLat, mFromLong, mToAddress, mToLat, mToLong, mLeaveOnDateSend + " " + mLeaveOnTimeSend,
                        mReturnDateSend + " " + mReturnTimeSend, mVehicleCityId);
            } else {
                mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setLeaveDate(boolean flag) { // show leave date (add 1 hr in current time)
        DateFormat dfDate = new SimpleDateFormat("dd-MMM");
        DateFormat dfTime = new SimpleDateFormat("hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Calendar.getInstance().getTime());
        calendar.add(Calendar.HOUR, 3);    // to show selected date


        String date = dfDate.format(calendar.getTime());
        String time = dfTime.format(calendar.getTime());

        mLeaveOnDate = date;
        mLeaveOnTime = time;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM, hh:mm a");
        Date dateRaw = null;
        String returntime = "";
        try {
            dateRaw = sdf.parse(mLeaveOnDate + ", " + mLeaveOnTime);
            Calendar calendarRaw = Calendar.getInstance();
            calendarRaw.setTime(dateRaw);
            calendarRaw.add(Calendar.HOUR, 1);
            mLeaveCompare = sfTimeSend.format(calendarRaw.getTimeInMillis());   // add 2 hrs in leave time for return time validation
          } catch (ParseException e) {
            e.printStackTrace();
        }

        //  mBinding.tvLeaveonVal.setText(date+", "+time);
        mLeaveOnDateSend = dfDateSend.format(calendar.getTime());
        mLeaveOnTimeSend = dfTimeSend.format(calendar.getTime());


        if (date.equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM"))) {
            mBinding.tvLeaveonVal.setText(getAppString(R.string.today) + ", " + time);
            mBinding.tvReturnbyVal.setText(getAppString(R.string.today) + ", " + returntime);

        } else {
            mBinding.tvLeaveonVal.setText(date + ", " + time);

        }
        setReturnDate(mLeaveOnDate, mLeaveOnTime, true);

    }

    private void setReturnDate(String leaveDate, String leaveTime, boolean flag) { // show leave date (add 1 hr in current time)
        DateFormat dfDate = new SimpleDateFormat("dd-MMM");
        DateFormat dfTime = new SimpleDateFormat("hh:mm a");
        // to show selected date
        String dateShow = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy, hh:mm a");
        Date dateRaw = null;
        String returntime = "";
        Calendar calendarRaw = Calendar.getInstance();
        try {
            int year = calendarRaw.get(Calendar.YEAR);
            dateRaw = sdf.parse(leaveDate+"-"+year+ ", " + leaveTime);
            calendarRaw.setTime(dateRaw);
            calendarRaw.add(Calendar.HOUR, 1);
            returntime = dfTime.format(calendarRaw.getTimeInMillis());
       //    if (flag) {
              // if (mReturnTimeSend.length()!=0){
                   mReturnTimeSend = sfTimeSend.format(calendarRaw.getTimeInMillis());
                   mReturnDateSend = sfDateSend.format(calendarRaw.getTime());
                   dateShow = dfDate.format(calendarRaw.getTime());


            if (dateShow.equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM"))) {
                mBinding.tvReturnbyVal.setText(getAppString(R.string.today) + ", " + returntime);
            } else {
                mBinding.tvReturnbyVal.setText(dateShow + ", " + returntime);

            }
             //  }
            requestVehicles();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void getIntentData() {

        Bundle bundle = getIntent().getExtras();
        mFromAddress = bundle.getString("mFromaddress");
        mFromLat = bundle.getString("mFromaddressLat");
        mFromLong = bundle.getString("mFromaddressLong");
        mToAddress = bundle.getString("mToaddress");
        mToLat = bundle.getString("mToaddressLat");
        mToLong = bundle.getString("mToaddressLong");
        mVehicleCityId = bundle.getString("mVehicleCityId");

        mBinding.tvOriginTrip.setText(bundle.getString("mFromaddress"));
        mBinding.tvDestinationTrip.setText(bundle.getString("mToaddress"));
    }

    /**
     * open calender and timer picker
     *
     * @param view
     */
    private void leaveCalenderOpen(View view) {
        mBinding.tvLeaveonVal.setEnabled(false);
        //  mBinding.tvReturnbyVal.setText(getAppString(R.string.select));
        selectDateClick("leaveDate");

    }

    /**
     * open calender and timer picker
     *
     * @param view
     */
    private void returnCalenderOpen(View view) {

        if (mBinding.tvLeaveonVal.getText().toString().equalsIgnoreCase(getAppString(R.string.select))) {

            mErrorLayout.showAlert(getAppString(R.string.please_select_leave_date_first), ErrorLayout.MsgType.Error, true);

        } else {
            mBinding.tvReturnbyVal.setEnabled(false);
            selectDateClick("returnDate");
        }
    }

    /*
  date click
  */
    private void selectDateClick(String selectedDate) {
        // Log.e(TAG, "cliclkkkkk");
        Calendar myCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat dateFormatterShow = new SimpleDateFormat("dd-MMM");
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDateStr = dateFormatter.format(newDate.getTime());
                if (selectedDate.equalsIgnoreCase("leaveDate")) {
                    mBinding.tvLeaveonVal.setEnabled(true);
                    getTimePicker(mContext, mDateStr, dateFormatterShow.format(newDate.getTime()), selectedDate);

                } else if (selectedDate.equalsIgnoreCase("returnDate")) {
                    mBinding.tvReturnbyVal.setEnabled(true);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
                    try {
                        if (!sdf.parse(mLeaveOnDate).after(sdf.parse(dateFormatterShow.format(newDate.getTime())))) {
                            getTimePicker(mContext, mDateStr, dateFormatterShow.format(newDate.getTime()), selectedDate);

                        } else {

                            // Toast.makeText(mContext, getString(R.string.txt_invalid_date), Toast.LENGTH_LONG).show();
                            mErrorLayout.showAlert(getAppString(R.string.txt_invalid_date), ErrorLayout.MsgType.Error, true);

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


            }


        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        datePickerDialog.setCancelable(false);
        datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    if (selectedDate.equalsIgnoreCase("leaveDate")) {
                        mBinding.tvLeaveonVal.setEnabled(true);

                    } else if (selectedDate.equalsIgnoreCase("returnDate")) {
                        mBinding.tvReturnbyVal.setEnabled(true);
                    }


                }
            }
        });
    }
    /*
     * Time Picker for book later functionality
     * */

    public void getTimePicker(final Context context, String mDateSelectedStr, String mDateSelectedShowStr, String selectedDate) {
        Calendar c = Calendar.getInstance();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timepickerdialog);
        dialog.show();
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.simpleTimePicker);
        timePicker.setIs24HourView(false);

        Date date = new Date();   // get current  date
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String mDateCurrentStr = dateFormatter.format(date.getTime());

        if (mDateSelectedStr.equals(mDateCurrentStr)) {

            if (selectedDate.equalsIgnoreCase("leaveDate")) {
                timePicker.setCurrentHour((c.get(Calendar.HOUR_OF_DAY)) + 3);   // on current date add 3 hr
            } else if (selectedDate.equalsIgnoreCase("returnDate")) {
                timePicker.setCurrentHour((c.get(Calendar.HOUR_OF_DAY)) + 4);
            }

        } else {

            if (selectedDate.equalsIgnoreCase("leaveDate")) {
                timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));


            } else if (selectedDate.equalsIgnoreCase("returnDate")) {
                timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY) + 1);

            }


        }
        TextView buttonOk = (TextView) dialog.findViewById(R.id.buttonOk);
        TextView buttonCancel = (TextView) dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDate.equalsIgnoreCase("leaveDate")) {
                    mBinding.tvLeaveonVal.setEnabled(true);

                } else if (selectedDate.equalsIgnoreCase("returnDate")) {
                    mBinding.tvReturnbyVal.setEnabled(true);
                }

                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    Calendar cSelected = Calendar.getInstance();    // selected time
                    cSelected.set(Calendar.HOUR_OF_DAY, (timePicker.getCurrentHour()));
                    cSelected.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                    SimpleDateFormat sdfTimeSend = new SimpleDateFormat("H:mm:s");

                    if (mDateSelectedStr.equals(mDateCurrentStr)) {    // compare selected date with current date
                        Calendar cValid = Calendar.getInstance();     // current  time

                        if (selectedDate.equalsIgnoreCase("leaveDate")) {
                            mBinding.tvLeaveonVal.setEnabled(true);
                            cValid.set(Calendar.HOUR_OF_DAY, (cValid.get(Calendar.HOUR_OF_DAY) + 3));   // current  time and add 3 hours on that

                        } else if (selectedDate.equalsIgnoreCase("returnDate")) {
                            mBinding.tvReturnbyVal.setEnabled(true);
                            cValid.set(Calendar.HOUR_OF_DAY, (cValid.get(Calendar.HOUR_OF_DAY) + 4));   // current  time and add 3 hours on that
                        }

                        cValid.set(Calendar.MINUTE, (cValid.get(Calendar.MINUTE)));

                        if (cValid.getTimeInMillis() <= cSelected.getTimeInMillis()) { // compare selected "time" and "current time +  hrs"
                            // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM, hh:mm a");

                            //sdf.parse("2019/01/02").after(sdf.parse("2019/01/03")
                            try {
                                dateCases(mContext, selectedDate, dialog, mDateSelectedShowStr, mDateSelectedStr, cSelected);

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            if (selectedDate.equalsIgnoreCase("leaveDate")) {
                                Toast.makeText(context, getAppString(R.string.booking_cant_be_done_before_1_hour), Toast.LENGTH_LONG).show();

                            } else if (selectedDate.equalsIgnoreCase("returnDate")) {
                                Toast.makeText(context, getAppString(R.string.booking_cant_be_done_before_2_hour), Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {


                        dateCases(mContext, selectedDate, dialog, mDateSelectedShowStr, mDateSelectedStr, cSelected);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    /**
     * Navigation to the Back screen
     *
     * @param view
     */
    private void navigationBackScreen(View view) {
        finish();
    }


    private void dateCases(Context context, String selectedDate, Dialog dialog, String mDateSelectedShowStr, String mDateSelectedStr, Calendar cSelected) {
        try {

            SimpleDateFormat sdfTimeShow = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM, hh:mm a");


            if (selectedDate.equalsIgnoreCase("returnDate")) {
                if (!sdf.parse(mLeaveOnDate + ", " + mLeaveOnTime)
                        .after(sdf.parse(mDateSelectedShowStr + ", " + sdfTimeShow.format(cSelected.getTimeInMillis())))) {

                    if (!sdfTimeShow.parse(mLeaveOnTime).equals(sdfTimeShow.parse(sdfTimeShow.format(cSelected.getTimeInMillis())))) {

                        mReturnDateSend = mDateSelectedStr;
                        mReturnTimeSend = sfTimeSend.format(cSelected.getTimeInMillis());
                        if (!sfTimeSend.parse(mLeaveCompare).after(sfTimeSend.parse(mReturnTimeSend))) {   // condition to select return time after 2hrs of leave time
                            // Toast.makeText(context, "true", Toast.LENGTH_LONG).show();

                            requestVehicles();
                            if (mDateSelectedShowStr.equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM"))) {
                                mBinding.tvReturnbyVal.setText(getAppString(R.string.today) + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                            } else {
                                mBinding.tvReturnbyVal.setText(mDateSelectedShowStr + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                            }
                            dialog.dismiss();
                        } else {
                            if (!mLeaveOnDate.equals(mDateSelectedShowStr)) {

                                mBinding.tvReturnbyVal.setText(mDateSelectedShowStr + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                                requestVehicles();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(context, getString(R.string.txt_invalid_time), Toast.LENGTH_LONG).show();
                            }
                        }


                    } else {
                        if (sdfDate.parse(mLeaveOnDate).equals(sdfDate.parse(mDateSelectedShowStr))) {
                            // Toast.makeText(context, "Invalid Time", Toast.LENGTH_LONG).show();
                            mReturnDateSend = mDateSelectedStr;
                            mReturnTimeSend = sfTimeSend.format(cSelected.getTimeInMillis());
                            requestVehicles();

                        } else {
                            mReturnDateSend = mDateSelectedStr;
                            mReturnTimeSend = sfTimeSend.format(cSelected.getTimeInMillis());
                            requestVehicles();
                            if (mDateSelectedShowStr.equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM"))) {
                                mBinding.tvReturnbyVal.setText(getAppString(R.string.today) + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                            } else {
                                mBinding.tvReturnbyVal.setText(mDateSelectedShowStr + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                            }


                        }
                        dialog.dismiss();

                    }
                } else if (sdfTimeShow.parse(mLeaveOnTime).equals(sdfTimeShow.parse(sdfTimeShow.format(cSelected.getTimeInMillis())))) { // compare leave time and selected return time
                    if (sdfDate.parse(mLeaveOnDate).equals(sdfDate.parse(mDateSelectedShowStr))) {
                        Toast.makeText(context, getString(R.string.txt_invalid_time), Toast.LENGTH_LONG).show();

                    } else {
                        requestVehicles();
                        if (mDateSelectedShowStr.equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM"))) {
                            mBinding.tvReturnbyVal.setText(getAppString(R.string.today) + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                        } else {
                            mBinding.tvReturnbyVal.setText(mDateSelectedShowStr + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                        }

                        dialog.dismiss();

                    }

                } else {

                    Toast.makeText(context, "Invalid Date/Time", Toast.LENGTH_LONG).show();


                }
            } else if (selectedDate.equalsIgnoreCase("leaveDate")) {
                mLeaveOnDate = mDateSelectedShowStr;
                mLeaveOnTime = sdfTimeShow.format(cSelected.getTimeInMillis());

                mLeaveOnTimeSend = sfTimeSend.format(cSelected.getTimeInMillis());
                mLeaveOnDateSend = mDateSelectedStr;

                Date date = sdf.parse(mLeaveOnDate + ", " + mLeaveOnTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, 1);
                mLeaveCompare = sfTimeSend.format(calendar.getTimeInMillis());   // add 2 hrs in leave time for return time validation


                if (mDateSelectedShowStr.equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM"))) {
                    mBinding.tvLeaveonVal.setText(getAppString(R.string.today) + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                } else {
                    mBinding.tvLeaveonVal.setText(mDateSelectedShowStr + ", " + sdfTimeShow.format(cSelected.getTimeInMillis()));

                }
                setReturnDate(mLeaveOnDate, mLeaveOnTime,true);
                dialog.dismiss();

            } else {


            }

        } catch (ParseException e1) {
            e1.printStackTrace();
        }


    }


    @Override
    public void onClickCab(String vehicleName, String vehicleDescription, String vehicleId, String distance,
                           String amount, String baseFare, String time, String sgst, String cgst, String nightCharges, String cancelCharge, String vehicleCityId) {
        if (amount.equalsIgnoreCase("0.00")) {
            mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

        } else {
            if (isValidAll()) {
                mOutStationDataModel = new OutStationDataModel();
                mOutStationDataModel.setmFromAddress(mFromAddress);
                mOutStationDataModel.setmFromLat(mFromLat);
                mOutStationDataModel.setmFromLong(mFromLong);
                mOutStationDataModel.setmToAddress(mToAddress);
                mOutStationDataModel.setmToLat(mToLat);
                mOutStationDataModel.setmToLong(mToLong);
                mOutStationDataModel.setmLeaveDate(mBinding.tvLeaveonVal.getText().toString());
                mOutStationDataModel.setmLeaveDateSend(mLeaveOnDateSend + " " + mLeaveOnTimeSend);
                mOutStationDataModel.setmVehicleName(vehicleName);
                mOutStationDataModel.setmVehicleDescription(vehicleDescription);
                mOutStationDataModel.setmTripType(mRoundTrip);
                mOutStationDataModel.setDistance(distance);
                mOutStationDataModel.setmAmount(amount);
                mOutStationDataModel.setmBaseFare(baseFare);
                mOutStationDataModel.setmTime(time);
                mOutStationDataModel.setmCgst(cgst);
                mOutStationDataModel.setmSgst(sgst);
                mOutStationDataModel.setmNightCharges(nightCharges);
                mOutStationDataModel.setmVehicleId(vehicleId);
                mOutStationDataModel.setmCancelCharges(cancelCharge);
                mOutStationDataModel.setmVehicleCityId(vehicleCityId);
                if (!mReturnTimeSend.equalsIgnoreCase("")) {
                    mOutStationDataModel.setmReturnDate(mBinding.tvReturnbyVal.getText().toString());
                    mOutStationDataModel.setmReturnDateSend(mReturnDateSend + " " + mReturnTimeSend);
                } else {
                    mReturnDateSend = "";
                    mReturnTimeSend = "";
                    mOutStationDataModel.setmReturnDate("");
                    mOutStationDataModel.setmReturnDateSend("");
                }


                Intent intent = new Intent(mContext, ConfirmYourBooking.class);
                intent.putExtra("mOutStationDataModel", mOutStationDataModel);
                startActivityForResult(intent, REQUEST_CONFIRM_BOOKING);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CONFIRM_BOOKING) {

                if (data != null) {

                    if (data.getStringExtra("close").equalsIgnoreCase("true")) {
                        Intent intent = new Intent();
                        intent.putExtra("clear", "true");
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {

                    }
                }
            }
        }
    }

    /*
     * check all validations
     * */
    private boolean isValidAll() {

        if (mRoundTrip.equalsIgnoreCase("0")) {
            if (mBinding.tvLeaveonVal.getText().toString().equalsIgnoreCase(getAppString(R.string.select))) {

                mErrorLayout.showAlert(getAppString(R.string.please_select_leave_date), ErrorLayout.MsgType.Error, true);
                return false;
            }
        } else if (mRoundTrip.equalsIgnoreCase("1")) {
            if (mBinding.tvLeaveonVal.getText().toString().equalsIgnoreCase(getAppString(R.string.select))) {

                mErrorLayout.showAlert(getAppString(R.string.please_select_leave_date), ErrorLayout.MsgType.Error, true);
                return false;
            }
            if (mBinding.tvReturnbyVal.getText().toString().equalsIgnoreCase(getAppString(R.string.select))) {

                mErrorLayout.showAlert(getAppString(R.string.please_select_return_date), ErrorLayout.MsgType.Error, true);
                return false;
            }
        }


        return true;
    }

    // Socket Emit
    Emitter.Listener getCabTypes = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("getCabType", (String) args[0]);

            try {
                String mCabTypeResponse = (String) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCabTypeResponse(mCabTypeResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    progressDialogCabType.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();

                }
            }
        }
    };

    private void getCabTypeResponse(String response) {
        Log.e("responseOut",response);
        BaseModel mBaseModel = new BaseModel(mContext);
        try {

            progressDialogCabType.dismiss();
            if (response != null) {
                if (mBaseModel.isParse(response)) {
                    try {


                        if (mBaseModel.getResultArray().length() > 0) {
                            mCablist.clear();
                            mCablist.addAll(OutStationCabTypeModel.getList(mContext, mBaseModel.getResultArray()));
                            mAdapter.setList(mCablist, this);
                            mBinding.rvCabType.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        progressDialogCabType.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            progressDialogCabType.dismiss();
            e.printStackTrace();
        }
    }

}
