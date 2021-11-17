package com.sudrives.sudrives.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sudrives.sudrives.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hemanth on 3/15/2016.
 */
public class DOBDate {
    static Calendar myCalendar;
    static dobListener listener;
    DatePickerDialog dialog;


    /*
    *
    * set DOB at create task
    * */
    public static void setDOB(Context mContext, TextView mDateTv, dobListener dobList) {
        Date calendarDate = null;
        listener = dobList;
        myCalendar = Calendar.getInstance();


        if (TextUtils.isEmpty(GlobalUtil.getText(mDateTv))) {

        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                calendarDate = sdf.parse(GlobalUtil.getText(mDateTv));
            } catch (Exception e) {
                e.printStackTrace();
            }
            myCalendar.setTime(calendarDate);
        }
        DatePickerDialog dialog = new DatePickerDialog(mContext, date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();

        //Set minimum date range on date picker and customize if change minimum date range
        String mday = "01";
        String mmonth = "00";//Show from first month
        String myear = "1901";
        Calendar minCalendar = Calendar.getInstance();
//        minCalendar.set(Integer.parseInt(myear), Integer.parseInt(mmonth), Integer.parseInt(mday));
        dialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
//        dialog.getDatePicker().setMaxDate(Calendar.getmDatabaseManager().getTimeInMillis());


        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    listener.cancelDOB();

                }
            }
        });

    }


    public static DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //  getDOB(listener);
        }

    };


    public static void setEtDOB(Context mContext, EditText mDateTv, dobListener dobList) {
        Date calendarDate = null;
        listener = dobList;
        myCalendar = Calendar.getInstance();


        if (TextUtils.isEmpty(GlobalUtil.getText(mDateTv))) {

        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                calendarDate = sdf.parse(GlobalUtil.getText(mDateTv));
            } catch (Exception e) {
                e.printStackTrace();
            }
            myCalendar.setTime(calendarDate);
        }
        DatePickerDialog dialog = new DatePickerDialog(mContext, date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();



    }
    public static void setEtDOBNew(Context mContext, EditText mDateTv, dobListener dobList) {
        listener = dobList;
        myCalendar = Calendar.getInstance();


        DatePickerDialog dialog = new DatePickerDialog(mContext, dateNew,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
        dialog.setCancelable(false);
        //Set minimum date range on date picker and customize if change minimum date range

        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add( Calendar.YEAR,0);
        long minDate = c.getTime().getTime();

        dialog.getDatePicker().setMaxDate(minDate);

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    listener.cancelDOB();

                }
            }
        });

    }


    /*
    * sdfformat only use for input format
    * */
    public static void setDOB(Context mContext, String dateStr, dobListener dobList, String sdfFormat, boolean isMinimum) {
        Date calendarDate = null;
        listener = dobList;
        myCalendar = Calendar.getInstance();


        if (TextUtils.isEmpty(dateStr)) {
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(sdfFormat);
            try {
                calendarDate = sdf.parse(dateStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            myCalendar.setTime(calendarDate);
        }
        DatePickerDialog dialog = new DatePickerDialog(mContext, date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();

        //Set minimum date range on date picker and customize if change minimum date range
        String mday = "01";
        String mmonth = "00";//Show from first month
        String myear = "1901";
        Calendar minCalendar = Calendar.getInstance();
        if (isMinimum) {
            dialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
        }
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    listener.cancelDOB();

                }
            }
        });
    }

    /************
     * getDOB set the date through interface
     **************/
    public static void getDOB(dobListener dobStr) {
        String myFormat = "dd-MMM-yyyy";//"yyyy-MM-dd"; //dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
       // Log.v("dateDOB", "setDate: " + sdf.format(myCalendar.getTime()));
        dobStr.returnDOB(sdf.format(myCalendar.getTime()));
    }


    /**************
     * Return selected DOB
     *****************/
    public interface dobListener {
        void returnDOB(String dob);

        void cancelDOB();
    }
    static DatePickerDialog.OnDateSetListener dateNew = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            getDOB(listener);
            if (Config.isLog) {
//                Log.v("SetDOB", "DOB selected: " + getDOB());
            }

        }

    };

}
