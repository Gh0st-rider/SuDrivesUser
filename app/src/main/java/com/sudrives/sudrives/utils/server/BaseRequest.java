package com.sudrives.sudrives.utils.server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.LoginActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;

import com.sudrives.sudrives.utils.apiloader.ApiLoaderDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.socket.emitter.Emitter;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created
 */

public class BaseRequest<T> {
    private Context mContext;

    /*
     * isLoader = false means not required to show loader
     * */
    private boolean inBackground = false;
    private DialogFragment mDialogLoaderView;
    private ApiInterface apiService;
    private BaseModel baseModel;
    private CallBackResponse mCallback;

    private JsonObject jsonObj;
    private String remainURL = "", mLanguage = "", mTimezone = "";
    private String userid = "";
    private FragmentManager fragmentManager;
    private String mAppVersion = "1.0";
    private String mTokenStr = "";
    private SessionPref mSessionPref;
    private String mFcmtoken = "";
    private String mDevicetype = "";
    private boolean loaderShow;

    public BaseRequest(Context context, boolean loaderShow) {
        this.mContext = context;
        this.loaderShow = loaderShow;
        try {
            mSessionPref = new SessionPref(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (loaderShow) {
            setDefaultLoader();
        }
    }


    private void showLoader(boolean tag) {

        if (!tag) {
            if ((mDialogLoaderView != null)) {
                mDialogLoaderView.dismiss();
            }
        }
    }

    public void setBaseRequest(JsonObject jsonObj, String remainURL, String userid, String timezone, String language, String fcmtoken, String devicetype, CallBackResponse listener, boolean inBackground) {
//        apiService = ApiClientBuilder.getClient().create(ApiInterface.class);
        apiService = ApiBuilderSingleton.getInstance();
        baseModel = new BaseModel(mContext);
        this.jsonObj = jsonObj;
        this.remainURL = remainURL;
        this.userid = userid;
        this.mCallback = listener;
        this.mTimezone = timezone;
        this.mLanguage = language;
        this.mFcmtoken = fcmtoken;
        this.mDevicetype = devicetype;
        this.inBackground = inBackground;
        mAppVersion = GlobalUtil.getAppVersion(mContext);
        try {
            if (!TextUtils.isEmpty(mSessionPref.token)) {
                mTokenStr = mSessionPref.token;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestAPI();
    }

    public void setBaseRequest(String remainURL, String userid, String timezone, String language, String fcmtoken, String devicetype, CallBackResponse listener, boolean inBackground) {
//        apiService = ApiClientBuilder.getClient().create(ApiInterface.class);
        apiService = ApiBuilderSingleton.getInstance();
        baseModel = new BaseModel(mContext);
        this.remainURL = remainURL;
        this.userid = userid;
        this.mCallback = listener;
        this.inBackground = inBackground;
        this.mTimezone = timezone;
        this.mLanguage = language;

        this.mDevicetype = devicetype;

        mAppVersion = GlobalUtil.getAppVersion(mContext);
        try {
            if (!TextUtils.isEmpty(mSessionPref.token)) {
                mTokenStr = mSessionPref.token;
            }
            if (Config.DEVICE_TOKEN.equals("") || Config.DEVICE_TOKEN == null) {
                Config.DEVICE_TOKEN = mSessionPref.getDataFromPref(mContext, mSessionPref.KEY_DEVICE_TOKEN);
                this.mFcmtoken = Config.DEVICE_TOKEN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestAPI();
    }


    /*
     * set loader to show views on load API
     * */
    public void setLoader(DialogFragment view) {
        if (view != null) {
            mDialogLoaderView = view;
        }
    }

    // Method to manually check connection status
    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }

    /*
     * set loader view
     * */
    private void setDefaultLoader() {
        try {
            fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            mDialogLoaderView = ApiLoaderDialog.newInstance();
        } catch (ClassCastException e) {
            //  Log.e("", "Can't get fragment manager");
        }
    }


    /*
     * request to server to call API
     * */
    public void requestAPI() {
        // System.out.println(remainURL+ jsonObj+userid + mAppVersion + mTokenStr);
        retrofit2.Call<JsonElement> call = apiService.postJson(remainURL, jsonObj, userid, mAppVersion, mTokenStr, mTimezone, mLanguage, mFcmtoken, mDevicetype);
        if (jsonObj != null) {
            logFullResponse(jsonObj.toString(), "INPUT");
        }

        if (checkConnection()) {
        } else {
            //Show error messages
            call.cancel();
        }
        if (inBackground == false) {
            if (loaderShow) {

                if ((mDialogLoaderView != null || mDialogLoaderView.isVisible()) && inBackground == false) {
                    mDialogLoaderView.setCancelable(false);
                    mDialogLoaderView.show(fragmentManager, "dialog");
                }
            }


        } else {

        }

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(retrofit2.Call<JsonElement> call, Response<JsonElement> response) {
                String jsonResponse = "";
                if (loaderShow) {
                    if ((mDialogLoaderView != null || mDialogLoaderView.isVisible()) && inBackground == false) {
                        try {
                            mDialogLoaderView.dismissAllowingStateLoss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                boolean isSuccess;

                if (response.code() == 200) {
                    JsonElement jsonElement = response.body();
                    if (Config.isLog) {
                        // Log.v("result", "result: " + jsonElement.toString() + " res.code: " + response.code());
                    }
                    try {
                        JSONObject jsonObj = new JSONObject(jsonElement.toString());
                        // ResponseCode = jsonObj.optString("ResponseCode", "");
                        String error_code = jsonObj.optString("error_code", "");
                        if (jsonObj.optString("status").equals("0")) {
                            if (error_code.equals("461")) {
                                try {
                                    if (SocketConnection.isConnected()) {

                                        SocketConnection.emitToServer(Config.EMIT_DISCONNECT, mSessionPref.user_userid);
                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("userid", mSessionPref.user_userid);
                                            jsonObject.put("language", Config.SELECTED_LANG);
                                            jsonObject.put("token", mSessionPref.token);

                                            SocketConnection.emitToServer(Config.EMIT_LOGOUT, jsonObject);

                                            SocketConnection.attachSingleEventListener(Config.LISTNER_GET_LOGOUT, getLogoutRes);


                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                        //    SocketConnection.emitToServer(Config.EMIT_LOGOUT, mSessionPref.user_userid,Config.SELECTED_LANG,Config.DEVICE_TOKEN);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                               /* Intent intent = new Intent(mContext, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ((Activity) mContext).startActivity(intent);
                                ((Activity) mContext).finish();*/
                            }
                        }
                    } catch (Exception e) {

                    }
                    try {
                        mCallback.getResponse(jsonElement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        jsonResponse = "" + response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Config.isLog) {
                        //{"status":false,"error":"Unauthorized"} res.code: 401
                        //    Log.v("result", "resulterrorbody: " + jsonResponse.toString() + " res.code: " + response.code());
                    }
                    logFullResponse(jsonResponse, "Output");
                    if (response.code() == 401) {
                        //Show dialog if user unauthorized
//                        try {
                           /* AppDialog.OkDialog(mContext, "", new JSONObject(jsonResponse).optString("error", "Unauthorized"), new AppDialog.OnButtonClickListener() {
                                @Override
                                public void onButtonOneClick() {
                                    if (!TextUtils.isEmpty(mSessionParam.session_key)) {
                                        mSessionParam.clearSession();
                                        Intent intent = new Intent(mContext, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        ((Activity) mContext).startActivity(intent);
                                        ((Activity) mContext).finish();
                                    }
                                }

                                @Override
                                public void onButtonTwoClick() {

                                }
                            });*/
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    } else {
                        mCallback.getResponse(jsonResponse);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonElement> call, Throwable t) {
                if (call.isCanceled() && checkConnection() == false) {
                    mCallback.getError(call, mContext.getResources().getString(R.string.No_Internet_Connection_Detected));
                } else {
                    mCallback.getError(call, "");
                }
                if (loaderShow) {
                    if ((mDialogLoaderView != null || mDialogLoaderView.isVisible()) && inBackground == false) {
                        mDialogLoaderView.dismissAllowingStateLoss();
                    }
                }
                //Log.d("Error", t.getMessage() + " msg: ");
            }
        });


    }


    /*
     * print input/output logcat
     * */
    public void logFullResponse(String response, String inout) {
        final int chunkSize = 3000;

        if (null != response && response.length() > chunkSize) {
            int chunks = (int) Math.ceil((double) response.length()
                    / (double) chunkSize);

            for (int i = 1; i <= chunks; i++) {
                if (i != chunks) {
               /*     Log.i("BASE REQ",
                            "JSON " + inout + " : " + response.substring((i - 1) * chunkSize, i
                                    * chunkSize));*/
                } else {
                  /*  Log.i("BASE REQ",
                            "JSON " + inout + " : "
                                    + response.substring((i - 1) * chunkSize,
                                    response.length()));
               */
                }
            }
        } else {

            try {
                JSONObject jsonObject = new JSONObject(response);
                //  Log.d("BASE REQ", "JSON " + inout + " : " + jsonObject.toString(jsonObject.length()));

            } catch (JSONException e) {
                e.printStackTrace();
                // Log.d("BASE REQ", "JSON " + inout + " : " + response);
            }

        }
    }


    /*
     * print error
     * */
    private String readStreamFully(long len, InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        long readCount = 0;
        int progress = 0, prevProgress = 0;

        String currLine = null;
        try {


            /* Read until all response is read */
            while ((currLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(currLine + "\n");
                readCount += currLine.length();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    Emitter.Listener getLogoutRes = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // Log.e("logout", (String) args[0]);
            String logout = (String) args[0];

            mSessionPref.clearSessionPrefUser(mContext);
          /*  startActivity(new Intent(HomeMenuActivity.this, LoginActivity.class));
            finish();*/
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ((Activity) mContext).startActivity(intent);
            ((Activity) mContext).finish();
        }
    };

}
