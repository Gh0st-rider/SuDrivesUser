package com.sudrives.sudrives.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.AppSignatureHashHelper;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.GetLocation;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.socket.emitter.Emitter;
import pl.droidsonroids.gif.GifImageView;

public class Splash extends BaseActivity implements View.OnClickListener {
    public final int TIMEOUT = 2000;
    private SessionPref mSessionPref;
    private String from = "";
    private static final int INITIAL_REQUEST = 222;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE


    };
    public static final int PERMISSION_ALL = 134;
    private AlertDialog alertDialog;
    private Context mContext;
    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;

    private LocationManager locationManager;
    private Runnable runnable;
    private Handler handler;
    private VideoView VideoView;
    private boolean isBackground = false;
    private GetLocation getLocation;
    private Double CURRENT_LATITUDE = 0.0;
    private Double CURRENT_LONGITUDE = 0.0;

    GifImageView gif_1;

    int permissionCount = 0;
    private int versionCode = 13;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mSessionPref = new SessionPref(Splash.this);
        mContext = Splash.this;
        getLocation = new GetLocation(mContext);
        Config.OTP_HASH = getHashCode(mContext); // get hash key for OTP verification
        gif_1 = findViewById(R.id.gif_1);

        GlobalUtil.clearClipBoard(mContext);
        if (SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE) != null) {
            if (!SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE).equalsIgnoreCase("")) {
                String localeString = SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE);
                setupBasedOnLocale(localeString);
            } else {
                SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, "en");
                setupBasedOnLocale("en");

            }
        } else {
            SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, "en");
            setupBasedOnLocale("en");

        }
        try {
            Config.DEVICE_TOKEN = FirebaseInstanceId.getInstance().getToken();
            // Log.e("firebase", Config.DEVICE_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalUtil.setStatusBarColor(Splash.this, getResources().getColor(R.color.colorPrimaryDark));

    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackground = false;
        init();

    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackground = true;

    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(mContext, INITIAL_PERMS)) {
                ActivityCompat.requestPermissions((Activity) mContext, INITIAL_PERMS, PERMISSION_ALL);
                startCheckPermissions();
            } else {
                setValue();
            }
        } else {
            setValue();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void setupBasedOnLocale(String localeString) {

        if (localeString.equals("hi")) {
            setLocale(localeString);
            Config.SELECTED_LANG = getAppString(R.string.hindi);
        } else {
            setLocale(localeString);
            Config.SELECTED_LANG = getAppString(R.string.english);
        }
    }

    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, lang);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    //check permission
    void startCheckPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canAccessCoarseLocation() || !canAccessFineLocation()) {
                ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST);
            } else {

                setValue();

            }
        } else {
            setValue();
        }

    }

    public void checkPermission() {
        if (!hasPermissions(mContext, INITIAL_PERMS)) {
            ActivityCompat.requestPermissions((Activity) mContext, INITIAL_PERMS, PERMISSION_ALL);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setValue() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            getLocation.checkLocationPermission();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!canAccessCoarseLocation() || !canAccessFineLocation()) {
                    ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST);
                } else {

                    if (checkConnection()) {

                        callVersionCheckService();

                    } else {
                        AppDialogs.noNetworkConnectionDialog(Splash.this);

                    }
                }
            } else {
                if (checkConnection()) {

                    callVersionCheckService();

                } else {
                    AppDialogs.noNetworkConnectionDialog(Splash.this);

                }
            }


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings

            showSettingsAlert();

        }
        //  callVersionCheckService();

    }

    public void showSettingsAlert() {
        int layoutPopup = R.layout.duuble_button_dialog;
        AppDialogs.DoubleButtonWithCallBackVersionDialog(Splash.this, layoutPopup, true
                , false, 0, getString(R.string.gps_is_not_enabled), getString(R.string.gps_setting),
                getString(R.string.action_settings), getString(R.string.cancel),
                new AppDialogs.Doublebuttonpincallback() {
                    @Override
                    public void doublebuttonok(String from) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }, new AppDialogs.DoublebuttonCancelcallback() {
                    @Override
                    public void doublebuttonCancel(String from) {
                        //setValue();
                        Toast.makeText(Splash.this, getAppString(R.string.please_enable_location_services_and_gps_to_continue), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, new AppDialogs.Crosscallback() {


                    @Override
                    public void crossButtonCallback(String from) {

                    }


                });


    }


    private boolean canAccessExternalStorage() {
        return (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }


    private boolean canAccessFineLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCoarseLocation() {
        return (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST: {
                if (grantResults.length > 0) {

                    boolean value = false;

                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            value = true;
                        }
                    }

                    if (value) {
                        setValue();
                    } else {
                        init();
                    }

                } else {
                    permissionCount++;

                    if (permissionCount == 3){
                        //handler.removeCallbacks(runnable);
                        permissionCount = 0;
                        Intent i = new Intent(Splash.this, PermissionsActivity.class);
                        startActivity(i);
                    }
                    Toast.makeText(mContext, getResources().getString(R.string.permission_needed_to_continue), Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case GetLocation.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation.checkLocationPermission();
                    //  mMap.setMyLocationEnabled(true);

                } else {

                }
                break;

        }
    }

    private void redirectCall() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                    }
                    if (isBackground) {
                        return;
                    }

                    if (bundle != null) {
                        if (bundle.getString("title") != null) {
                            startActivity(new Intent(mContext, NotificationActivity.class));
                            finish();
                          //  overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
                        } else {
                            if (mSessionPref.user_userid != null && mSessionPref.user_userid != "") {

                                startActivity(new Intent(mContext, HomeMenuActivity.class).putExtra("lat", CURRENT_LATITUDE+"").putExtra("long", CURRENT_LONGITUDE+""));

                                finish();
                              //  overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
                            } else {
                                mSessionPref.clearSessionPrefUser(mContext);
                                isLanguageSelected();
                            }
                        }
                    } else {
                        if (mSessionPref.user_userid != null && mSessionPref.user_userid != "") {
                            startActivity(new Intent(mContext, HomeMenuActivity.class).putExtra("lat", CURRENT_LATITUDE+"").putExtra("long", CURRENT_LONGITUDE+""));
                            finish();
                          //  overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
                        } else {
                          /*  startActivity(new Intent(mContext, LoginActivity.class));
                            finish();*/
                            isLanguageSelected();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, TIMEOUT);
    }

    private void isLanguageSelected() {
        if (SessionPref.getDataFromPref(mContext, SessionPref.IS_LANGUAGE_SELECTED) != null) {
            if (SessionPref.getDataFromPref(mContext, SessionPref.IS_LANGUAGE_SELECTED).equalsIgnoreCase("true")) {
                Intent intent = new Intent(mContext, LoginActivity.class).putExtra("lat", getIntent().getStringExtra("lat")).putExtra("long", getIntent().getStringExtra("long"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            } else {
                Intent intent = new Intent(mContext, SelectLanguage.class).putExtra("lat", CURRENT_LATITUDE).putExtra("long", CURRENT_LONGITUDE);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        } else {
            Intent intent = new Intent(mContext, SelectLanguage.class).putExtra("lat", CURRENT_LATITUDE).putExtra("long", CURRENT_LONGITUDE);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Call Service to get the vserion
     */
    private void callVersionCheckService() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.APIURLPRODUCTION+"api/check_app_version", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("getversion", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {

                        PackageManager manager = getPackageManager();
                        PackageInfo info = null;
                        try {
                            info = manager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                       // Toast.makeText(getApplicationContext(),
                              //  "PackageName = " + info.packageName + "\nVersionCode = "
                                   //     + info.versionCode + "\nVersionName = "
                                     //   + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show();

                        Log.e("version", info.versionName+"\n"+info.versionCode);
                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        try {
                            if (jsonObject.getString("version_value") != null){
                                versionCode = Integer.valueOf(jsonObject.getString("version_value"));
                                if (versionCode > info.versionCode){
                                    alertNewVersion();
                                }else {
                                    redirectCall();
                                }
                            }else {
                                redirectCall();
                            }
                        }catch (Exception e){
                            redirectCall();
                        }


                       // String orderId = jsonObject.getString("order_id");
                        //String reciept_no = jsonObject.getString("receipt");

                        //startPayment(amount, orderId, reciept_no);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollyerror", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user_type", "User");
                return map;
            }

           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_type", "User");
                return params;
            }*/
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);



        /*try {
            mBaseRequest = new BaseRequest(mContext, false);
            // Log.e("dataaaa",mBaseRequest+"")
            Log.e("data","api/check_version"+"\n"+mSessionPref.user_userid+"\n"+Config.TIMEZONE+"\n"+Config.SELECTED_LANG+"\n"+Config.DEVICE_TOKEN+"\n"+Config.DEVICE_TYPE+"");
            mBaseRequest.setBaseRequest(new JsonObject(), "api/check_version", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
                @Override
                public void getResponse(Object response) {
                    //   Log.e("aaaaa",response+"");
                    mBaseModel = new BaseModel(mContext);


                    try {
                        JSONObject jsonObj = new JSONObject(response.toString());

                        int status = jsonObj.optInt("status");

                        if (status == 0) {
                            int error_code = jsonObj.getInt("error_code");
                            String message = jsonObj.optString("message", "");
                           *//* 467-major change
                            465-no change
                            466-minor update
                           *//*

                            switch (error_code) {
                                case 465:
                                    // isNewVersionAvailbe = true;
                                    redirectCall();
                                    break;

                                case 466: //// MINOR CHANGE
                                    //isNewVersionAvailbe = false;
                                    alertNewVersion(message, false);
                                    break;

                                case 467:  /// MAJOR CHANGE
                                    //isNewVersionAvailbe = false;
                                    alertNewVersion(message, true);
                                    break;
                            }
                        } else {
                            int error_code = jsonObj.getInt("error_code");
                            String message = jsonObj.optString("message", "");
                            switch (error_code) {
                                case 465:
                                    // isNewVersionAvailbe = true;

                                    redirectCall();

                                    break;

                                case 466: //// MINOR CHANGE
                                    //isNewVersionAvailbe = false;
                                    alertNewVersion(message, false);
                                    break;

                                case 467:  /// MAJOR CHANGE
                                    //isNewVersionAvailbe = false;
                                    alertNewVersion(message, true);
                                    break;
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                redirectCall();
                            }
                        }, TIMEOUT);
                    }

                }

                @Override
                public void getError(Object response, String error) {
                    Log.e("error", error + "");
                    Toast.makeText(mContext, getAppString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    if (!checkConnection()) {
                        // mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, false);
                    } else {
                        //   mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                    }
                }
            }, true);

        } catch (Exception e) {
            e.printStackTrace();

        }*/

    }

    public void alertNewVersion() {

        try {





               //Intent intent = new Intent(Intent.ACTION_VIEW);
            //                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
            //                                mContext.startActivity(intent);


            androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            LayoutInflater inflater = getLayoutInflater();
            View d = inflater.inflate(R.layout.dialog_version_update, null);
            alertDialog.setView(d);
            Dialog show = alertDialog.show();

            Button ok = d.findViewById(R.id.btn_update);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
                    mContext.startActivity(intent);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Emitter.Listener getLogoutRes = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //  Log.e("logout", (String) args[0]);
            String logout = (String) args[0];

            mSessionPref.clearSessionPrefUser(mContext);
          /*  startActivity(new Intent(HomeMenuActivity.this, LoginActivity.class));
            finish();*/
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
         //   overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
        }
    };

    public void onLocationUpdate(Location location) {
        // Log.e(" Splash Lat and Lng", location.getLatitude() + "   " + location.getLongitude());
        try {
            if (location != null) {
                if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                    CURRENT_LATITUDE = location.getLatitude();
                    CURRENT_LONGITUDE = location.getLongitude();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHashCode(Context context) {
        AppSignatureHashHelper appSignature = new AppSignatureHashHelper(context);
        Log.e(" getAppSignatures ", "" + appSignature.getAppSignatures());
        return appSignature.getAppSignatures().get(0);

    }

    @Override
    public void onClick(View v) {
    }
}
