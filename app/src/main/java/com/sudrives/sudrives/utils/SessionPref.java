package com.sudrives.sudrives.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

/**
 * Created by admin on 21/03/18.
 */

public class SessionPref {

    public String user_device_token, user_mobile, user_license, user_pic, user_carmodel, user_carmaker, user_created_at;
    public String user_userid,unique_no;
    public final static String KEY_DEVICE_TOKEN = "device_token";
    public final static String KEY_HOME_ADDRESS = "home_address";
    public final static String KEY_HOME_LAT = "home_lat";
    public final static String KEY_HOME_LONG = "home_long";



    public final static String KEY_WORK_ADDRESS = "work_address";

    public final static String KEY_WORK_LAT = "work_lat";
    public final static String KEY_WORK_LONG = "work_long";


    public String user_email;
    public String referral_code;
    public String user_fullname,user_fname,language,user_lname,user_company_name;
    public static final String KEY_HEIGHT = "height";
    public String user_social_tag,token;
    private Context mContext;

    public static final String PREFS_NAME = "Haulage";
    public String SESSION_PREF_KEY = "login-pref-key";
    public String SESSION_PREF_VALUE = "login-pref-value";
    private final static String PRFERENCE_NAME = "Haulagelanguage";
    public final static String LANGUAGE = "language";
    public final static String IS_LANGUAGE_SELECTED = "isLanguageSelected";
    public final static String SELECTED_VEHICLE = "selVehicle";
    public final static String SELECTED_BOOKING = "selBooking";
    public final static String PAYMENT_MODE_SELECTED = "payment_mode_selected";


    public SessionPref(Context context) {
        mContext = context;
        SharedPreferences pref = getSharedPRef(mContext);

        unique_no = pref.getString("unique_no","");
        user_userid = pref.getString("user_userid", "");
        user_device_token = pref.getString("user_device_token", "");
        user_email = pref.getString("user_email", "");
        language = pref.getString("language", "");
        referral_code = pref.getString("referral_code", "");
        token = pref.getString("token", "");
        user_fullname = pref.getString("user_fullname", "");
        user_fname = pref.getString("user_fname", "");
        user_lname = pref.getString("user_lname", "");
        user_company_name = pref.getString("user_company_name", "");
        user_mobile = pref.getString("user_mobile", "");
        user_license = pref.getString("user_license", "");
        user_pic = pref.getString("user_pic", "");
        user_carmaker = pref.getString("user_carmaker", "");
        user_carmodel = pref.getString("user_carmodel", "");
      //  token = pref.getString("user_token", "");
        user_created_at = pref.getString("user_created_at", "");
        user_social_tag = pref.getString("user_social_tag", "");



    }

    private SharedPreferences getSharedPRef(Context context) {

        return context.getSharedPreferences(SESSION_PREF_KEY, Context.MODE_PRIVATE);
    }


    public void persistUser(JSONObject jsonObj) {
        SharedPreferences.Editor editor = getSharedPRef(mContext).edit();

        try {
            editor.putString("user_userid", jsonObj.optString("user_id", ""));
            editor.putString("unique_no",jsonObj.optString("unique_no",""));


            editor.putString("user_email", jsonObj.optString("email", ""));
            editor.putString("language", jsonObj.optString("language", ""));
            editor.putString("user_fname", jsonObj.optString("first_name", ""));
            editor.putString("user_lname", jsonObj.optString("last_name", ""));
            editor.putString("user_fullname", jsonObj.optString("fname", "") + " " + jsonObj.optString("lname", ""));
            editor.putString("user_mobile", jsonObj.optString("mobile", ""));
            editor.putString("token", jsonObj.optString("token", ""));
            editor.putString("user_pic", jsonObj.optString("profile_img", ""));
            editor.putString("referral_code", jsonObj.optString("referral_code", ""));


            if (jsonObj.has("device_token")) {
                editor.putString("user_device_token", jsonObj.getString("device_token"));
            }
            editor.commit();


            // user_userid = "119";
            user_fname = jsonObj.optString("first_name", "");
            user_lname = jsonObj.optString("last_name", "");
        //    user_company_name = jsonObj.optString("company_name", "");

            user_userid = jsonObj.optString("user_id", "");
            unique_no = jsonObj.optString("unique_no");
            user_pic = jsonObj.optString("profile_img", "");
            user_device_token = jsonObj.optString("device_token", "");
            user_email = jsonObj.optString("email", "");
            language = jsonObj.optString("language", "");
            token = jsonObj.optString("token", "");
            referral_code = jsonObj.optString("referral_code", "");
            user_fullname = jsonObj.optString("firstname", "") + " " + jsonObj.optString("lastname", "");
            user_mobile = jsonObj.optString("mobile", "");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveDataIntoSharedPrefLang(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getDataFromPrefLang(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static void saveDataIntoSharedPrefBooking(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getDataFromPrefBooking(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public void clearsaveDataBooking(Context context) {
        mContext = context;
        SharedPreferences.Editor editor = getSharedPRef(mContext).edit();
        editor.putString("dropLocCheck", "");

        editor.commit();
    }

    public void clearsaveDataLanguage(Context context) {
        mContext = context;
        SharedPreferences.Editor editor = getSharedPRef(mContext).edit();
        editor.putString("language", "");

        editor.commit();
    }
    public void savesocialLoginTag(String tag) {
        SharedPreferences.Editor editor = getSharedPRef(mContext).edit();

        editor.putString("user_social_tag", tag);
          editor.commit();
    }

    public static void clearSharedPref(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();

    }

    public void clearSocialTagSessionPref(Context context) {
        mContext = context;
        SharedPreferences.Editor editor = getSharedPRef(mContext).edit();


        editor.putString("user_social_tag", "");

        editor.commit();
    }

    public void clearDriverIdSessionPref(Context context) {
        mContext = context;
        SharedPreferences.Editor editor = getSharedPRef(mContext).edit();


        editor.putString("driver_id", "");

        editor.commit();
    }





    public void clearSessionPrefUser(Context context) {
        mContext = context;
        SharedPreferences.Editor editor = getSharedPRef(mContext).edit();
        editor.putString("user_userid", "");
        editor.putString("unique_no","");
        editor.putString("is_operator", "");
        editor.putString("user_device_token", "");
        editor.putString("user_email", "");

        editor.putString("token", "");
        editor.putString("referral_code", "");
        editor.putString("user_fullname", "");
        editor.putString("user_mobile", "");
        editor.putString("user_license", "");
        editor.putString("user_pic", "");
        editor.putString("user_carmodel", "");
        editor.putString("user_carmaker", "");
        editor.putString("user_token", "");
        editor.putString("user_created_at", "");
        editor.putString("user_social_tag", "");

        editor.commit();

    }

    public static String loadStringPref(Context context, String key) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String status = settings.getString(key, "");
        return status;

    }

    public static void saveDataIntoSharedPref(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);

        editor.commit();
    }

    public static String getDataFromPref(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
    public static void saveDataIntoSharedPrefConfirm(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getDataFromPrefConfirm(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }


    public static void saveDataIntoSharedPrefVehicle(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getDataFromPrefVehicle(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PRFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
}
