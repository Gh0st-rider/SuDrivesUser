package com.sudrives.sudrives.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityVerificationBinding;
import com.sudrives.sudrives.model.RegisterModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends BaseActivity {

    private BaseRequest mBaseRequest;
    private final String TAG = LoginActivity.class.getSimpleName();
    private ActivityVerificationBinding mBinding;
    private Context mContext;
    private String mMobileStr = "", mOtpStr = "", mState = "", mCity = "", mFirstName = "", mGender = "", mEmail = "", mUserId, mLoginType = "", title = "", mVerificationId = "";
    private ErrorLayout mErrorLayout;
    private SessionPref mSessionPref;
    private BaseModel mBaseModel;
    private Intent intent;
    private RegisterModel mRegisterModel;
    private int layoutPopup;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private double lat = 0.0, longi = 0.0;
    //firebase auth object
    private FirebaseAuth mAuth;
    public int counter = 60;

    // private OtpView otp_view;
    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private boolean flagShow = true;
    private LocationManager locationManager;
    private static final int INITIAL_REQUEST = 222;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verification);
        GlobalUtil.setStatusBarColor(VerificationActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getData();
        getControls();
        KeyboardUtil.setupUI(mBinding.rlTop, this);
    }

    private void getData() {

        lat = getIntent().getDoubleExtra("lat", 0.0);
        longi = getIntent().getDoubleExtra("long", 0.0);
        //   Log.e("verification request==>", lat+  " : "  +longi);

    }


    /*
     * initialize all controls
     * */
    private void getControls() {
        mContext = VerificationActivity.this;
        //initializing objects
        mAuth = FirebaseAuth.getInstance();

        mSessionPref = new SessionPref(mContext);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //  Config.DEVICE_TOKEN = FirebaseInstanceId.getInstance().getToken();
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        if (checkConnection()) {

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
        mBinding.btnVerify.setOnClickListener(this::registerBtnClick);
        mBinding.tvResendOtp.setOnClickListener(this::resendTextClick);

        mAuth.signOut();

        intent = getIntent();
        mLoginType = intent.getStringExtra("loginType");
        mUserId = intent.getStringExtra("userid");


        if (mLoginType.equals("register")) {
            if (intent.getSerializableExtra("registerModel") != null) {
                mRegisterModel = (RegisterModel) intent.getSerializableExtra("registerModel");
                mFirstName = mRegisterModel.getFirstname();
                mGender = mRegisterModel.getGender();
                mMobileStr = mRegisterModel.getMobileNumber();
                mState = mRegisterModel.getState();
                mEmail = mRegisterModel.getEmail();
                mCity = mRegisterModel.getCity();
            }
        } else {
            mFirstName = mSessionPref.user_fname;
            mMobileStr = intent.getStringExtra("mobile");
        }
        if (checkConnection()) {
            sendVerificationCode(mMobileStr);
        } else {
            dialogClickForAlert(false, getAppString(R.string.no_internet_connection), false);
        }

        mBinding.tvMobileNo.setText("to +91-"+mMobileStr);


    }

    private void registerBtnClick(View view) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            if (!hasPermissions(mContext, INITIAL_PERMS)) {

                showSettingsAlert();

            } else {
                // VerifyOTPAPI(true);
                mOtpStr = mBinding.otpView.getOTP();
                if (isValidAll()) {
                    if (checkConnection()) {
                       verifyVerificationCode(mOtpStr);


                    } else {
                        dialogClickForAlert(false, getAppString(R.string.no_internet_connection), false);
                    }

                }
            }


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings

            showSettingsAlert();

        }


    }


    private void VerifyOTPAPI(boolean staus) {
        getValues();
        if (isValidAll()) {
            if (checkConnection()) {

                VerifyOTPCALL(staus);

            } else {
                dialogClickForAlert(false, getAppString(R.string.no_internet_connection), false);
            }
        }
    }

    //Resend Text click
    private void resendTextClick(View view) {
        getValues();

        if (checkConnection()) {
            sendVerificationCode(mMobileStr);
        } else {
            dialogClickForAlert(false, getAppString(R.string.no_internet_connection), false);
        }

    }

    /*
     * get all values
     * */
    private void getValues() {
        //  mOtpStr = getEtString(mBinding.etOtp);
        mOtpStr = mBinding.otpView.getOTP();

    }


    /*
     * check all validations
     * */


    private boolean isValidAll() {
        if (TextUtils.isEmpty(mOtpStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_otp), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (mOtpStr.length() < 6) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_valid_otp), ErrorLayout.MsgType.Error, true);
            return false;

        }

        return true;
    }

    /***************    Implement API    *************************/
    private void VerifyOTPCALL(boolean status) {
        JsonObject jsonObj = null;
        mBaseRequest = new BaseRequest(mContext, status);

        jsonObj = JsonElementUtil.getJsonObject("userid", mUserId, "otp", "404040", "mobile", mMobileStr,
                "type", mLoginType, "user_role", Config.TYPE_USER_VAl, "firstname", mFirstName, "gender", mGender, "email", mEmail, "state_id", mState, "city_id", mCity);

         Log.e(TAG, "mSessionPref.useridddddd :::::::  " + Config.DEVICE_TOKEN);

        mBaseRequest.setBaseRequest(jsonObj, "api/otp_verify", "", Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {
                    Log.e(TAG, "ResLogin" + response.toString());
                    //store into session
                    mSessionPref.persistUser(mBaseModel.getResultObject());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogClickForAlert(true, mBaseModel.Message, true);

                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialogClickForAlert(false, mBaseModel.Message, true);
                        }
                    });
                }
            }

            @Override
            public void getError(Object response, String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogClickForAlert(false, error, false);
                    }
                });
            }
        }, false);


    }

    /**
     * Error dialog for the API error messages
     *
     * @param msg :- Message which we need to show.
     */
    public void dialogClickForAlert(boolean isSuccess, String msg, boolean flagfinish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int drawable;

                if (isSuccess) {
                    drawable = R.drawable.success_24dp;
                    title = getString(R.string.confirmation);
                    layoutPopup = R.layout.success_dialog;
                } else {
                    drawable = R.drawable.failed_24dp;
                    title = "";
                    layoutPopup = R.layout.failure_dialog;
                }
                try {

                    AppDialogs.singleButtonVersionDialog(VerificationActivity.this, layoutPopup, title, drawable, msg,
                            getString(R.string.ok),
                            new AppDialogs.SingleButoonCallback() {
                                @Override
                                public void singleButtonSuccess(String from) {
                                    if (isSuccess) {
                                        // finish();
                                        if (flagfinish) {
                                            finishAllActivities();
                                            Intent intent = new Intent(mContext, HomeMenuActivity.class);
                                            intent.putExtra("lat", lat);
                                            intent.putExtra("long", longi);
                                            startActivity(intent);
                                        }
                                    } else {
                                        // finish();
                                    }

                                }
                            }, true);
                } catch (Exception e) {
                    e.printStackTrace();

                    finishAllActivities();
                    Intent intent = new Intent(mContext, HomeMenuActivity.class);
                    startActivity(intent);

                }
            }
        });
    }



//Resend otp for Register user

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                //  System.out.println(message);

                // Toast.makeText(VerificationActivity.this,"Message: "+message.trim(),Toast.LENGTH_LONG).show();
                mBinding.otpView.setOTP(message.trim());

               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {*/
                otpReciever();
                   /* }
                }, 2500);*/

            }
        }
    };


    private void otpReciever() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            if (!hasPermissions(mContext, INITIAL_PERMS)) {

                showSettingsAlert();

            } else {
                VerifyOTPAPI(false);
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings

            showSettingsAlert();

        }


    }


    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));

        if (flagShow) {
            init();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void init() {

        if (!hasPermissions(mContext, INITIAL_PERMS)) {
          /*  ActivityCompat.requestPermissions((Activity) mContext, INITIAL_PERMS, PERMISSION_ALL);
            startCheckPermissions();
          */
            showSettingsAlert();
        } else {
            //
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


    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }


    public void showSettingsAlert() {
        /*GlobalUtil.ShowLocationPopUp(mContext, getString(R.string.gps_setting), getString(R.string.gps_is_not_enabled),
                getString(R.string.action_settings), getString(R.string.cancel));*/
        flagShow = false;

        int layoutPopup = R.layout.duuble_button_dialog;
        AppDialogs.DoubleButtonWithCallBackVersionDialog(VerificationActivity.this, layoutPopup, true
                , false, 0, getString(R.string.gps_is_not_enabled), getString(R.string.gps_setting),
                getString(R.string.action_settings), getString(R.string.cancel),
                new AppDialogs.Doublebuttonpincallback() {
                    @Override
                    public void doublebuttonok(String from) {
                        flagShow = false;
                        AppDialogs.dialogDismiss();

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }, new AppDialogs.DoublebuttonCancelcallback() {
                    @Override
                    public void doublebuttonCancel(String from) {
                        flagShow = true;
                        //  VerifyOTPAPI(true);
                    }
                }, new AppDialogs.Crosscallback() {


                    @Override
                    public void crossButtonCallback(String from) {
                        flagShow = true;
                    }


                });


    }

////////////// OTP through FCM //////////////////////////

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);


        mAuth.signOut();
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            Toast.makeText(mContext, getString(R.string.otp_sent), Toast.LENGTH_LONG).show();

            //Getting the code sent by SMS
            mOtpStr = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (mOtpStr != null) {
                Log.e("verification code", mOtpStr);
                mBinding.otpView.setOTP(mOtpStr);
                //verifying the code
               verifyVerificationCode(mOtpStr);

            }else {
                mOtpStr = "123456";
                mBinding.otpView.setOTP(mOtpStr);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.e("FirebaseException", e.getMessage());
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            counter = 60;
            mBinding.tvResendOtpIn.setText("");
            mBinding.tvResendOtpIn.setVisibility(View.GONE);
            mBinding.tvResendOtp.setEnabled(true);
            mBinding.tvResendOtp.setTextColor(getResources().getColor(R.color.colorGrayLight));
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    counter--;
                    mBinding.tvResendOtpIn.setVisibility(View.VISIBLE);
                    mBinding.tvResendOtpIn.setText(getString(R.string.resend_otp_in) + " " + String.valueOf(counter) + " " + getString(R.string.sec));
                    mBinding.tvResendOtp.setTextColor(getResources().getColor(R.color.translucent));
                    mBinding.tvResendOtp.setEnabled(false);

                }

                @Override
                public void onFinish() {
                    counter = 60;
                    mBinding.tvResendOtpIn.setText("");
                    mBinding.tvResendOtpIn.setVisibility(View.GONE);
                    mBinding.tvResendOtp.setEnabled(true);
                    mBinding.tvResendOtp.setTextColor(getResources().getColor(R.color.colorBlack));

                }
            }.start();
            //storing the verification id that is sent to the user
            mVerificationId = s;
            //   mSessionPref.saveDataIntoSharedPrefLang(mContext, "verificationid", mVerificationId);
            // Toast.makeText(mContext, "OTP Sent", Toast.LENGTH_LONG).show();
            Log.e("mVerificationId", mVerificationId);
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential

        try {
            if (mVerificationId != null && mVerificationId.length() != 0) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                //signing the user
                signInWithPhoneAuthCredential(credential);


            } else {
                mAuth.signOut();
                Toast.makeText(mContext, getAppString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            // Toast.makeText(mContext, "Successfull", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            VerifyOTPAPI(true);
                        } else {

                            Toast.makeText(mContext, "Invalid code entered", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

}
