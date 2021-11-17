package com.sudrives.sudrives.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;

import java.util.Locale;


/**
 * Created by admin on 08/11/17.
 */

public abstract class BaseFragment extends Fragment {

    public SQLiteDatabase mSqLiteDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            if (SessionPref.getDataFromPrefLang(getActivity(), SessionPref.LANGUAGE) != null) {
                setLocales(SessionPref.getDataFromPrefLang(getActivity(), SessionPref.LANGUAGE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        //Log.e("fragmeeee", "oncreateeeeee");
        mErrorString = new SparseIntArray();
    }

    public void setLocales(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        SessionPref.saveDataIntoSharedPrefLang(getActivity(), SessionPref.LANGUAGE, lang);


    }

    public String getAppString(int id) {
        String str = "";
        try {
            if (!TextUtils.isEmpty(this.getResources().getString(id))) {
                str = this.getResources().getString(id);
            } else {
                str = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }



    /*
     * get color from colors.xml
     * */
    public int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
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
            Snackbar.make(((Activity) getContext()).findViewById(android.R.id.content), getAppString(R.string.This_app_needs_phone_permission),
                    Snackbar.LENGTH_LONG).setAction(R.string.enable,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);
                        }
                    }).show();
        }
    }

    public void requestAppPermissions(final String[] requestedPermissions,
                                      final int stringId, final int requestCode) {
        mErrorString.put(requestCode, stringId);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRequestPermissionRationale = true;// false;
        for (String permission : requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(getActivity(), permission);
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
        }

        // Log.v("runtime", "permissionCheck: " + permissionCheck + "   conditions : " + (permissionCheck != PackageManager.PERMISSION_GRANTED));

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getAppString(R.string.This_app_needs_phone_permission), //"aa "+ stringId,
                        Snackbar.LENGTH_LONG).setAction(R.string.grant,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //  ActivityCompat.requestPermissions((Activity) getContext(), requestedPermissions, requestCode);

                                Intent i = new Intent();
                                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                i.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                getActivity().startActivity(i);

                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions((Activity) getContext(), requestedPermissions, requestCode);
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), getAppString(R.string.This_app_needs_phone_permission), //"aa "+ stringId,
                        Snackbar.LENGTH_LONG).setAction(R.string.grant,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent();
                                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                i.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                getActivity().startActivity(i);

                            }
                        });


                snackbar.show();

            }
        } else {
            onPermissionsGranted(requestCode);

        }
    }


    public abstract void onPermissionsGranted(int requestCode);

}
