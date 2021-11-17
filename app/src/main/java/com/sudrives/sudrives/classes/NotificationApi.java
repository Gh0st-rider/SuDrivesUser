package com.sudrives.sudrives.classes;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user-5 on 12/3/18.
 */

public class NotificationApi {
    private BaseRequest mBaseRequest;
    private String user_id;
    private Context mContext;
    TextView tv_notification_badge;

    public NotificationApi(Context mContext, String user_id, TextView tv_notification_badge) {
        this.mContext = mContext;
        this.user_id = user_id;
        this.tv_notification_badge = tv_notification_badge;

        notificationListApi(mContext, user_id);

    }

    private void notificationListApi(Context mContext, String user_id) {
        SessionPref mSessionPref = new SessionPref(mContext);

        mBaseRequest = new BaseRequest(mContext, false);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", user_id, "token", mSessionPref.token, "language", Config.SELECTED_LANG, "page", "0");


        //isLoadMore=false;
        mBaseRequest.setBaseRequest(jsonObj, "api/get_notifications", user_id, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                try {
                    if (response != null) {
                     //   Log.e("I am connected", "I am connected" + "   " +response);

                        JSONObject JsonRes = new JSONObject(response.toString());

                        if (JsonRes.getString("status").equals("1")) {
                            JSONArray arr = JsonRes.getJSONArray("result");


                            int count = Integer.parseInt(JsonRes.optString("notification_unread", ""));

                            int mbadgeCounts = count;


                            if (mbadgeCounts > 0) {
                                tv_notification_badge.setVisibility(View.VISIBLE);
                                tv_notification_badge.setText(String.valueOf(mbadgeCounts));
                            } else {
                                tv_notification_badge.setVisibility(View.GONE);
                            }

                        }else {
                            tv_notification_badge.setText("");
                            tv_notification_badge.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }

            @Override
            public void getError(Object response, String error) {
                if (!ConnectivityReceiver.isConnected()) {
                    //    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                } else {
                    //   mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


    }
}
