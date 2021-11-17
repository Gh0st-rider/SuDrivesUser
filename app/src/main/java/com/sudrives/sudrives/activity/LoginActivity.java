package com.sudrives.sudrives.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityLoginBinding;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.ValidationUtil;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding mBinding;
    private Context mContext;
    private String mMobileStr = "", mUserId = "", title = "";
    private ErrorLayout mErrorLayout;

    private BaseModel mBaseModel;
    private SessionPref mSessionPref;
    private BaseRequest mBaseRequest;
    private double lat = 0.0, longi = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);


        GlobalUtil.setStatusBarColor(LoginActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getData();
        getControls();

        KeyboardUtil.setupUI(mBinding.rlTop, this);

        if (!isLocationEnabled(mContext)) {
            checkLocationEnable();
        }




    }


    @Override
    public void onPermissionsGranted(int requestCode) {

        if (!isLocationEnabled(mContext)) {
            checkLocationEnable();
        }
    }

    private void getData() {
        lat = getIntent().getDoubleExtra("lat", 0.0);
        longi = getIntent().getDoubleExtra("long", 0.0);

    }

    /*
     * initialize all controls
     * */
    private void getControls() {
        mContext = LoginActivity.this;

        Config.DEVICE_TOKEN = mSessionPref.getDataFromPref(mContext, mSessionPref.KEY_DEVICE_TOKEN);
        if (Config.DEVICE_TOKEN.equals("")) {
            Config.DEVICE_TOKEN = FirebaseInstanceId.getInstance().getToken();
        }

        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        mBinding.btnLogin.setOnClickListener(this::loginBtnClick);
        mBinding.tvOtpWillBeSend.setTypeface(null, Typeface.NORMAL);
        // mBinding.tvTermsOfServices.setTypeface(null, Typeface.NORMAL);
        if (checkConnection()) {

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }

    }


    private void loginBtnClick(View view) {
        getValues();
        if (isValidAll()) {
            GlobalUtil.avoidDoubleClicks(mBinding.btnLogin);
            if (checkConnection()) {

                loginAPI();

            } else {

                dialogClickForAlert(false, getAppString(R.string.no_internet_connection), "");
            }
        }
    }

    /*
     * get all values
     * */
    private void getValues() {
        mMobileStr = getEtString(mBinding.etMobileNo);
        if (mMobileStr.matches("0(.*)")) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_valid_mobile_num), ErrorLayout.MsgType.Error, true);

        } else {
            System.out.println("in else");
        }
    }

    /*
     * check all validations
     * */
    private boolean isValidAll() {
        if (TextUtils.isEmpty(mMobileStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_mobile_num), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (!ValidationUtil.isValidPhone(mMobileStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_valid_mobile_num), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (mMobileStr.length() < 10) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_valid_mobile_num), ErrorLayout.MsgType.Error, true);
            return false;
        }

        return true;
    }


    /***************    Implement API    *************************/
    private void loginAPI() {
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("mobile", mMobileStr, "user_role", Config.TYPE_USER_VAl);
        mBaseRequest.setBaseRequest(jsonObj, "api/mobileNumberCheck", "", Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {
                Log.e("respomseLogin", response + "");
                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response.toString());
                        if (jsonObj.getString("error_code") != null) {

                            if (jsonObj.getString("error_code").equals("0")) {
                                Intent intent = new Intent(mContext, RegistrationActivity.class);
                                intent.putExtra("mobile", mMobileStr);
                                intent.putExtra("lat", lat);
                                intent.putExtra("long", longi);
                                startActivity(intent);


                            } else if (jsonObj.getString("error_code").equals("1")) {
                                mUserId = jsonObj.getString("userid").toString();
                                dialogClickForAlert(true, mBaseModel.Message, jsonObj.getString("error_code"));

                            }
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    dialogClickForAlert(false, mBaseModel.Message, "");
                }
            }

            @Override
            public void getError(Object response, String error) {
                if (!checkConnection()) {
                    dialogClickForAlert(false, getAppString(R.string.something_went_wrong), "");
                } else {
                    dialogClickForAlert(false, getAppString(R.string.something_went_wrong), "");
                }
            }
        }, false);


    }

    /**
     * Error dialog for the API error messages
     *
     * @param msg :- Message which we need to show.
     */
    public void dialogClickForAlert(boolean isSuccess, String msg, String error_code) {
        int layoutPopup;
        int drawable;
        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.confirmation) + "!";
            layoutPopup = R.layout.success_dialog;
        } else {
            drawable = R.drawable.failed_24dp;
            title = "";
            layoutPopup = R.layout.failure_dialog;
        }
        AppDialogs.singleButtonVersionDialog(LoginActivity.this, layoutPopup, title, drawable, msg,
                getString(R.string.ok),
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {
                        if (error_code.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(mContext, RegistrationActivity.class);
                            intent.putExtra("mobile", mMobileStr);
                            intent.putExtra("lat", lat);
                            intent.putExtra("long", longi);
                            startActivity(intent);


                        } else if (error_code.equalsIgnoreCase("1")) {
                            Intent intent = new Intent(mContext, VerificationActivity.class);
                            intent.putExtra("loginType", "login");
                            intent.putExtra("userid", mUserId);
                            intent.putExtra("mobile", mMobileStr);
                            intent.putExtra("lat", lat);
                            intent.putExtra("long", longi);
                            startActivity(intent);

                        }

                    }
                }, true);
    }


    /*
     * handle internet connection view
     * */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        super.onNetworkConnectionChanged(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE) != null) {
                String localeString = SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE);
                setupBasedOnLocale(localeString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        mBinding.etMobileNo.setHint(getAppString(R.string.enter_mobile_number));
        mBinding.btnLogin.setText(getAppString(R.string.login));

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

    }


    private void checkLocationEnable() {
        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.location_sevice_not_active);
            builder.setMessage(R.string.please_enable_location_services_and_gps);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;


        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

