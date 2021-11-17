package com.sudrives.sudrives.SocketListnerMethods;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.TripCompletedActivity;
import com.sudrives.sudrives.fragment.HomeFragment;
import com.sudrives.sudrives.fragment.LiveFragment;
import com.sudrives.sudrives.fragment.MyBookingFragment;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.SocketConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user-5 on 11/30/18.
 */

public class ListenersSocket {

    public static void parseVehicleData(Fragment fragment, JSONArray mTruckList, String mVehicleResponse) {
        Log.e("socketVahicle",mTruckList+""+mVehicleResponse+"111111");
        String vehicle_id = "", vehicle_name = "", vehicle_sel_img = "", vehicle_payload = "",
                vehicle_size = "", fare_charges = "", vehicle_type_id = "", vehicle_city_id = "";
        try {
            JSONObject jsonObj = mTruckList.getJSONObject(0);
            vehicle_name = jsonObj.getString("vehicle_name");
            vehicle_id = jsonObj.getString("id");
            vehicle_sel_img = jsonObj.getString("vehicle_sel_img");
            vehicle_payload = jsonObj.getString("vehicle_payload");
            vehicle_size = jsonObj.getString("vehicle_size");
            fare_charges = jsonObj.getString("fare_charges");

          if (jsonObj.has("vehicle_city_id")) {
              vehicle_city_id = jsonObj.getString("vehicle_city_id");
         } if (jsonObj.has("vehicle_type_id")) {
                vehicle_type_id = jsonObj.getString("vehicle_type_id");
         }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            if (fragment instanceof HomeFragment) {
                ((HomeFragment) fragment).setVehicleData(mVehicleResponse, vehicle_id,
                        vehicle_name, vehicle_sel_img, vehicle_payload, vehicle_size, fare_charges, "1x",vehicle_city_id,vehicle_type_id,"1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void parseArrivedTrip(Fragment fragment, TextView iv_help, ImageView iv_notification) {
        try {
            if (fragment instanceof LiveFragment) {
               // iv_help.setImageResource(R.mipmap.help);
                iv_notification.setVisibility(View.GONE);
                iv_help.setVisibility(View.GONE);
                //  ((LiveFragment) fragment).reloadLivetripData();
            }
            if (fragment instanceof MyBookingFragment) {
                iv_notification.setImageResource(R.drawable.notification_icon);
                ((MyBookingFragment) fragment).reloadData(true);
            }
            if (fragment instanceof HomeFragment) {
                iv_notification.setImageResource(R.drawable.notification_icon);
               // ((HomeFragment) fragment).tripStatus(false, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseCompleteTrip(Context mContext, Fragment fragment, String tripId) {

        try {
           /* if (fragment instanceof LiveFragment) {
                ((LiveFragment) fragment).reloadLivetripData();
            }*/
            if (fragment instanceof MyBookingFragment) {
                ((MyBookingFragment) fragment).reloadData(true);
            }
            mContext.startActivity(new Intent(mContext, TripCompletedActivity.class).putExtra("tripid", tripId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseEndTrip(Context mContext, Fragment fragment, String tripId, int mbadgeCounts, TextView tv_notification_badge) {
        try {
          /*  if (fragment instanceof LiveFragment) {
                ((LiveFragment) fragment).reloadLivetripData();
            }*/
            if (fragment instanceof MyBookingFragment) {
                if (mbadgeCounts > 0) {
                    tv_notification_badge.setVisibility(View.VISIBLE);
                }

                ((MyBookingFragment) fragment).reloadData(true);
            }
            mContext.startActivity(new Intent(mContext, TripCompletedActivity.class).putExtra("tripid", tripId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseAcceptedTrip(Context mContext, Fragment fragment) {

        try {
            if (fragment instanceof LiveFragment) {
                ((LiveFragment) fragment).reloadLivetripData();
            }

            if (fragment instanceof HomeFragment) {
              //  ((HomeFragment) fragment).tripStatus(false, "");
            }

            if (fragment instanceof MyBookingFragment) {
                ((MyBookingFragment) fragment).reloadData(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseStartTrip(Context mContext, Fragment fragment, String tripId, int mbadgeCounts, TextView tv_notification_badge, ImageView iv_notification) {
        try {
            if (fragment instanceof LiveFragment) {
                // iv_notification.setImageResource(R.mipmap.help);
                 iv_notification.setVisibility(View.GONE);
                ((LiveFragment) fragment).drawOriginToDestination();
            }
            if (fragment instanceof MyBookingFragment) {

                if (mbadgeCounts > 0) {
                    tv_notification_badge.setVisibility(View.VISIBLE);

                }
                ((MyBookingFragment) fragment).reloadData(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getDistanceMatrix(Context mContext,String vehicleId,String mFromLat,String mFromLong,String mToLat,String mToLong,String vehicle_city_id){
        JSONObject jsonObject = new JSONObject();
        SessionPref mSessionPref = new SessionPref(mContext);

        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("vehicle_type_id", vehicleId);
            jsonObject.put("book_from_lat", mFromLat);
            jsonObject.put("book_from_long", mFromLong);
            jsonObject.put("book_to_lat", mToLat);
            jsonObject.put("book_to_long", mToLong);
            jsonObject.put("vehicle_city_id", vehicle_city_id);

          Log.e("estimatefarerequest--", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketConnection.emitToServer(Config.EMIT_GET_DISTANCE_MATRIX_SERVICE, jsonObject);
        return jsonObject;
    }

    public static JSONObject getDrivers(Context mContext,String vehicleId,String Lat,String Long,String daily,String rental,String outstation){
        JSONObject jsonObject = new JSONObject();
        SessionPref mSessionPref = new SessionPref(mContext);

        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("lat", Lat);
            jsonObject.put("lang", Long);
            jsonObject.put("vehicle_types_id", vehicleId);
            jsonObject.put("driver_daily", daily);
            jsonObject.put("driver_rental", rental);
            jsonObject.put("driver_outstation", outstation);
       // Log.e("Driver request", ": " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketConnection.emitToServer(Config.EMIT_GET_DRIVERS, jsonObject);
        return jsonObject;
    }


   /*
  ////////
  */



    public static JSONObject getChangeDestination(Context mContext,String trip_id,String ChangeFromAddress,String ChangeFromLat,String ChangeFromLong,String ChangeToAddress,String ChangeToLat,String ChangeToLong){
        JSONObject jsonObject = new JSONObject();
        SessionPref mSessionPref = new SessionPref(mContext);

        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("trip_id", trip_id);
            jsonObject.put("changed_from_address", ChangeFromAddress);
            jsonObject.put("changed_from_lat", ChangeFromLat);
            jsonObject.put("changed_from_long", ChangeFromLong);
            jsonObject.put("changed_to_address", ChangeToAddress);
            jsonObject.put("changed_to_lat", ChangeToLat);
            jsonObject.put("changed_to_long", ChangeToLong);

          //  Log.e("CHANGE DESTINATION", ": " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketConnection.emitToServer(Config.EMIT_GET_CHANGE_DESTINATION_ADDRESS, jsonObject);
        return jsonObject;
    }



   /* userid :  '8',
    token : '798842351',
    language : 'english',
    trip_id : '379' ,
    changed_from_address : 'Vijay Nagar, Indore, Madhya Pradesh, India' ,
    changed_from_lat : '22.7533' ,
    changed_from_long : '75.8937' ,
    changed_to_address : 'Devi Ahilya Bai Holkar Airport, Indore, Madhya Pradesh, India' ,
    changed_to_lat : '22.7277' ,
    changed_to_long : '75.8044' ,*/

    public static void requestTripCompleteList(Context mContext) {
        JSONObject jsonObject = new JSONObject();
        try {
            SessionPref mSessionPref = new SessionPref(mContext);
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);
            jsonObject.put("typefor", "user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
         //Log.e("Complete trips", ": " + jsonObject);
        SocketConnection.emitToServer(Config.EMIT_GET_TRIPS, jsonObject);
    }

    public static void requestBookingStatus(Context mContext) {
        if (ConnectivityReceiver.isConnected()) {
            SocketConnection.emitToServer(Config.EMIT_GET_BOOKING_STATUS, ListenersSocket.geJasonForSocket(mContext));
        } else {

        }
    }
    public static void requestprofile(Context mContext) {
        SocketConnection.emitToServer(Config.EMIT_EMERGENCY_GET_PROFILE, ListenersSocket.geJasonForSocket(mContext));
    }
    public static JSONObject geJasonForSocket(Context mContext){
        JSONObject jsonObject = new JSONObject();
        SessionPref mSessionPref = new SessionPref(mContext);
        try {
            jsonObject.put("userid", mSessionPref.user_userid);
            jsonObject.put("token", mSessionPref.token);
            jsonObject.put("language", Config.SELECTED_LANG);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}
