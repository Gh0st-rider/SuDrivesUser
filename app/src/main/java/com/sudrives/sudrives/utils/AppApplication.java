package com.sudrives.sudrives.utils;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
/**
 * Created by Nikhat on 16/10/2018.
 */

public class AppApplication extends Application {


    private final String TAG = AppApplication.class.getSimpleName();
    public static AppApplication instance = null;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized AppApplication getInstance() {
        if (instance == null) {
            instance = new AppApplication();

        }
        return instance;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }


    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }



}
