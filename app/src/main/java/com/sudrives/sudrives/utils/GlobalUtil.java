package com.sudrives.sudrives.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.sudrives.sudrives.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hemanth on 9/27/2016.
 */

public class GlobalUtil {



    public static String getAddress(Context mContext, double latitude, double longitude) {
       /*StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
           // Log.e("tag", e.getMessage());
        }


        return result.toString();*/
         String fulladdress = "";
         String smallAddress = "";
         String city = "";
         String state = "";
         String country = "";
         String zipcode = "";
         String placeName = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            StringBuilder sb = new StringBuilder();
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                fulladdress = "";
                smallAddress = "";
                placeName = "";
                if (sb.append(address.getAddressLine(0)) != null) {
                    smallAddress = address.getAddressLine(0);
                    fulladdress = smallAddress;
                }
                if (address.getLocality() != null) {
                    city = address.getLocality();
                    if (!fulladdress.contains(city))
                        fulladdress = fulladdress + " " + city;
                }
                if (address.getFeatureName() != null) {
                    placeName = address.getFeatureName();
                }
                if (address.getAdminArea() != null) {
                    state = address.getAdminArea();
                    if (!fulladdress.contains(state))
                        fulladdress = fulladdress + " " + state;
                }
                if (address.getPostalCode() != null) {
                    zipcode = address.getPostalCode();
                    if (!fulladdress.contains(zipcode))
                        fulladdress = fulladdress + " " + zipcode;
                }
                if (address.getCountryName() != null) {
                    country = address.getCountryName();
                    if (!fulladdress.contains(country))
                        fulladdress = fulladdress + " " + country;
                };
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       // Toast.makeText(mContext, "globalutil"+fulladdress+smallAddress+placeName, Toast.LENGTH_LONG).show();

        return fulladdress;

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorWhite));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.colorWhite));

                  }
    }

    /***************
     * Get text from textview
     *********************/
    public static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    //Set enable / disable view  - true means enable else false means disable
    public static void setSelection(View view, boolean visibility) {
        view.setEnabled(visibility);
        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            View child = ((ViewGroup) view).getChildAt(i);
            if (child instanceof ViewGroup) {
                setSelection(child, visibility);
            } else {
                child.setEnabled(visibility);
            }
        }
    }


    public static void setStatusBarColor(Activity activity, int Color){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color);
        }else{

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            activity.getWindow().setStatusBarColor(Color);
        }else{


        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds*1000);
        return formatter.format(calendar.getTime());
    }


    public static void clearClipBoard(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            @SuppressWarnings("deprecation")
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("");
        }

        //For Newer Versions
        else {
            ClipboardManager clipboard = (ClipboardManager)context. getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Message", "");
            clipboard.setPrimaryClip(clip);
        }
    }
    /***
     * To prevent from double clicking the row item and so prevents overlapping fragment.
     * **/
    public static void avoidDoubleClicks(final View view) {
        final long DELAY_IN_MS = 900;
        if (!view.isClickable()) {
            return;
        }
        view.setClickable(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, DELAY_IN_MS);
    }
    public static String getAppVersion(Context context) {
        String version = Config.VERSION_CODE;   // TODO (last updated date  02-08-2019)

        return version;
    }

}
