package com.sudrives.sudrives.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.network_communication.NetworkConn;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public abstract class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public NetworkConn networkConn;
    private static List<Activity> sActivities = new ArrayList<Activity>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            if (SessionPref.getDataFromPrefLang(getApplicationContext(), SessionPref.LANGUAGE) != null) {
                setLocales(SessionPref.getDataFromPrefLang(getApplicationContext(), SessionPref.LANGUAGE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        sActivities.add(this);
        mErrorString = new SparseIntArray();

        networkConn = NetworkConn.getInstance();
        networkConn = new NetworkConn(BaseActivity.this, true);


    }

    private BroadcastReceiver mNetworkDetectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  checkInternetConnection();
        }
    };



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        sActivities.remove(this);
        // mLocationManager.removeUpdates(this);

        super.onDestroy();
    }

    public static void finishAllActivities() {
        if (sActivities != null) {
            for (Activity activity : sActivities) {
                activity.finish();
            }
        }
    }

    /*
     * get string from strings.xml
     * */
    public String getAppString(int id) {
        String str = "";
        if (!TextUtils.isEmpty(this.getResources().getString(id))) {
            str = this.getResources().getString(id);
        } else {
            str = "";
        }
        return str;
    }


    /*
     * get color from colors.xml
     * */
    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    /*
     * get string from edit text
     * */
    public String getEtString(EditText et) {
        return et.getText().toString().trim();
    }

    /*
     * get string from text view
     * */
    public String getTvString(TextView tv) {
        return tv.getText().toString().trim();
    }

    /*
     * isSeen registration page
     * only first time registration page seen after that show enter passcode screen
     * */
    public final String isRegistrationSeenKey = "is-seen-registration";
    public final String isRegistrationSeenValue = "is-seen-registration-value";



    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    // Method to manually check connection status
    public boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        AppApplication.getInstance().setConnectivityListener(this);

    }



    /*
     * if permission not granted return false
     * this method helping to check permissions granted or not
     * */
    private SparseIntArray mErrorString;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }

        // Log.v("runtime", "grantResults.length: " + grantResults.length);
        if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode);
        } else {

        }
    }


    public abstract void onPermissionsGranted(int requestCode);

    public void setLocales(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, lang);


    }
}


