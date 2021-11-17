package com.sudrives.sudrives.utils.network_communication;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ProgressBar;


import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.apiloader.ApiLoaderDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by krishnapal on 9/28/17.
 */

public class NetworkConn {
    private static final NetworkConn ourInstance = new NetworkConn();
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    private Context mContext;
    private DialogFragment mDialogLoaderView;
 //   private AppDialog appDialogs = new AppDialog();
 private boolean loaderShow;
    private boolean inBackground = false;
    public static NetworkConn getInstance() {
        return ourInstance;
    }

    private NetworkConn() {

    }
    public NetworkConn(Context context, boolean loaderShow) {
        this.mContext = context;
        this.loaderShow = loaderShow;
        mSessionPref = new SessionPref(mContext);
        if (loaderShow) {
            setDefaultLoader();
        }
    }
    private void setDefaultLoader() {
        try {
            fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            mDialogLoaderView = ApiLoaderDialog.newInstance();
        } catch (ClassCastException e) {
          //  Log.e("", "Can't get fragment manager");
        }
    }
    private onRequestResponse requestResponse;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    public void makeRequest(final Context context, Request request, final String eventType,
                            final onRequestResponse requestResponse, ErrorLayout mErrorLayout) {
        //   requestResponse = (onRequestResponse) context;
        Log.e("request", eventType.toString());
        if (checkNetworkConnectivity(context) == true) {
         //   setDefaultLoader();
         //   final Dialog loaderDialog = appDialogs.showLoader(context);
            if (inBackground == false) {
                if (loaderShow) {
                    mDialogLoaderView.setCancelable(false);
                    mDialogLoaderView.show(fragmentManager, "dialog");

                }


            } else {

            }
            new OkHttpClient.Builder()
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           if (loaderShow) {
                               if ((mDialogLoaderView != null || mDialogLoaderView.isVisible()) && inBackground == false) {
                                    mDialogLoaderView.dismissAllowingStateLoss();
                                }
                            }
           //                 appDialogs.hideLoader(loaderDialog);
                            requestResponse.onRequestFailed();
                        }
                    });
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String stringResponse = response.body().string();
                    response.body().close();
                    Log.e("response", stringResponse);
                    ((Activity) context).runOnUiThread(() -> {
                        try {
               //             appDialogs.hideLoader(loaderDialog);
                            if (loaderShow) {
                              if ((mDialogLoaderView != null || mDialogLoaderView.isVisible()) && inBackground == false) {
                                    mDialogLoaderView.dismissAllowingStateLoss();
                                }
                           }
                            if (stringResponse != null) {

                                JSONObject jsonObject = new JSONObject(stringResponse);
                                int apiResultStatus = jsonObject.getInt(AppConstants.KEY_STATUS);
                                String strMessage = jsonObject.getString(AppConstants.KEY_MESSAGE);

                                if (apiResultStatus == AppConstants.STATUS_SUCCESS_VALUE) {
                                    requestResponse.onResponse(stringResponse, eventType);
                                } else {

                                    mErrorLayout.showAlert(strMessage, ErrorLayout.MsgType.Error, true);

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    });
                }
            });
        } else {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 if ((mDialogLoaderView != null || mDialogLoaderView.isVisible()) && inBackground == false) {
                        mDialogLoaderView.dismissAllowingStateLoss();
                    }
                    requestResponse.onNoNetworkConnectivity();
                    mErrorLayout.showAlert(context.getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                  //  AppUtil.getInstance().showToast(context, context.getResources().getString(R.string.error_msg_no_internet));
                }

            });
        }
    }



SessionPref mSessionPref;

    public Request createFormDataRequest(final Context context, String url, RequestBody requestBody) {
        mSessionPref = new SessionPref(context);
       String version= GlobalUtil.getAppVersion(context);
        //   RequestBody body = RequestBody.create(JSON, json);
        if(Config.DEVICE_TOKEN.equals("")||Config.DEVICE_TOKEN==null){
            Config.DEVICE_TOKEN= mSessionPref.getDataFromPref(context, mSessionPref.KEY_DEVICE_TOKEN);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader(AppConstants.KEY_VERSION, version)
                .addHeader(AppConstants.KEY_USER_ID, mSessionPref.user_userid)
                .addHeader(AppConstants.KEY_TOKEN, mSessionPref.token)
                .addHeader(AppConstants.KEY_LANG, Config.SELECTED_LANG)
                .addHeader(AppConstants.KEY_TIME_ZONE, Config.TIMEZONE)
                .addHeader(AppConstants.KEY_DEVICE_TOKEN, Config.DEVICE_TOKEN)
                .addHeader(AppConstants.KEY_DEVICE_TYPE, Config.DEVICE_TYPE)
                .build();

        return request;
    }







    public interface onRequestResponse {
        public void onResponse(String response, String eventType);

        public void onNoNetworkConnectivity();

        public void onRequestRetry();

        public void onRequestFailed();
    }


    public boolean checkNetworkConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}

