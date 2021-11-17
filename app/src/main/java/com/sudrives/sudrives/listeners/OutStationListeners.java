package com.sudrives.sudrives.listeners;

import android.app.ProgressDialog;

import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user-5 on 1/8/19.
 */

public class OutStationListeners {

    public static void requestOutStationCabType(ProgressDialog progressDialogPackage, String tripType, String userId, String token,
                                                String mFromAddress, String mFromLat, String mFromLong, String mToAddress, String mToLat
            , String mToLong, String mLeaveDate, String mReturnDate, String vehicle_city_id) {
        progressDialogPackage.show();

        JSONObject jsonObject = new JSONObject();
        try {


            jsonObject.put("userid", userId);
            jsonObject.put("token", token);
            jsonObject.put("round_trip", tripType);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("book_from_address", mFromAddress);
            jsonObject.put("book_from_lat", mFromLat);
            jsonObject.put("book_from_long", mFromLong);
            jsonObject.put("book_to_address", mToAddress);
            jsonObject.put("book_to_lat", mToLat);
            jsonObject.put("book_to_long", mToLong);
            jsonObject.put("book_later_date_time", mLeaveDate);
            jsonObject.put("book_later_return_date_time", mReturnDate);
            jsonObject.put("vehicle_city_id", vehicle_city_id);

        //  Log.e("vehicle request   ", jsonObject.toString());
        } catch (JSONException e) {
            progressDialogPackage.dismiss();
            e.printStackTrace();
        }
        SocketConnection.emitToServer(Config.EMIT_GET_OUTSTATION_CABS, jsonObject);

    }
}
