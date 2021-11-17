package com.sudrives.sudrives.SocketListnerMethods;

import android.app.ProgressDialog;

import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user-5 on 1/5/19.
 */

public class RentalListeners {

    public static void requestPackage(ProgressDialog progressDialogPackage, String userId, String token, String lat, String longi) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userId);
            jsonObject.put("token", token);
            jsonObject.put("user_lat", lat);
            jsonObject.put("user_long", longi);

            jsonObject.put("language", Config.SELECTED_LANG);
        } catch (JSONException e) {
            progressDialogPackage.dismiss();
            e.printStackTrace();
        }

        SocketConnection.emitToServer(Config.EMIT_GET_RENTAL_PACKAGES, jsonObject);

    }

    public static void requestCabType(ProgressDialog progressDialogPackage, String packageId, String userId, String token, String vehicle_city_id, String user_lat, String user_long) {
        progressDialogPackage.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userId);
            jsonObject.put("token", token);
            jsonObject.put("package_id", packageId);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("city_assign_id",vehicle_city_id);
            jsonObject.put("user_lat",user_lat);
            jsonObject.put("user_long",user_long);
           // Log.e("getCabs request", jsonObject.toString());
        } catch (JSONException e) {
            progressDialogPackage.dismiss();
            e.printStackTrace();
        }
        SocketConnection.emitToServer(Config.EMIT_GET_RENTAL_CABS, jsonObject);

    }


}
