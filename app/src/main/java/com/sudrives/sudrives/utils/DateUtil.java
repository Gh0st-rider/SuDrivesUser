package com.sudrives.sudrives.utils;

import android.text.format.DateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by admin on 15/11/17.
 */

public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();


    /***************
     * Convert date format
     *******************/
    public static String dateFormat(String dateStr, String inputFormat, String outputFormat) {
        String finalDate = "";
        SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat);
        try {
            Date date = inputSDF.parse(dateStr);
            finalDate = outputSDF.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (Config.isLog) {
//            Log.v("ffiinalDDateeEE", "finalDate: " + finalDate);
        }

        return finalDate;
    }

    public static String UTCdateFormat(String date) {
        String UTCdate = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parceDate = null;
        try {
            parceDate = simpleDateFormat.parse(date);

            SimpleDateFormat parceDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            UTCdate = parceDateFormat.format(parceDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return UTCdate;
    }

    public static String UTCtimeFormat(String time) {
        String UTCtime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date parceDate = null;
        try {
            parceDate = simpleDateFormat.parse(time);
            SimpleDateFormat parceDateFormat = new SimpleDateFormat("hh:mm aa");
            // parceDateFormat.setTimeZone(TimeZone.getTimeZone("etc/UTC"));
            UTCtime = parceDateFormat.format(parceDate.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return UTCtime;
    }


    /*
     * get current date time of device in 24 hours format
     * */
    public static String getCurrentDateTime(String formate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                formate, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
