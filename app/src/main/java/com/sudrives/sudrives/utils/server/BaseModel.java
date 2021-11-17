package com.sudrives.sudrives.utils.server;

import android.content.Context;
import android.text.TextUtils;

import com.sudrives.sudrives.utils.Config;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



@SuppressWarnings("unchecked")
public class BaseModel<T> {

    /*Response code for success*/
    public static final String RESPONSE_CODE = "1";
    public String baseResponseStr = "";
    public Context mContext;
    private JSONObject jsonObj;
    public String ResponseCode = "", Message = "",Flag="";
    public int Status = 0;

    public BaseModel(Context context) {
        this.mContext = context;
    }

    public BaseModel(String str) {

        this.baseResponseStr = str;
    }

    public boolean isParse(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                jsonObj = new JSONObject(str);
               // ResponseCode = jsonObj.optString("ResponseCode", "");
                Message = jsonObj.optString("message", "");
              /*if( jsonObj.has("flag")) {
                  Flag= jsonObj.optString("flag", "");

              }*/
              //  jsonObj.optString("message", "");

                if (Config.isLog) {
                    //Log.v("Message", "Message: " + Message + " msg: " + jsonObj.optString("message", ""));
                }
                Status = jsonObj.optInt("status", 0);
                if (ResponseCode.equalsIgnoreCase(RESPONSE_CODE) || Status == 1) {
                    return true;
                } else if (Status == 101) {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return false;
    }

    /*
    * get Data object
    * */
    public JSONObject getDataObject() {
        try {
            return jsonObj.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    } /*
    * get Data object
    * */
    public JSONObject getResultObject() {
        try {
            return jsonObj.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
 /*
    * get Data object
    * */
    public JSONObject getResponseObject() {
        try {
            return jsonObj.getJSONObject("response");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
       * get Data array
       * */
    public JSONArray getDataArray() {
        try {
            return jsonObj.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
     * get response array
     * */
    public JSONArray getResultArray() {
        try {
            return jsonObj.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
     * get response array
     * */
    public JSONArray getHistoryArray() {
        try {
            return jsonObj.getJSONArray("history");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    * get jsonobject full response
    * */
    public JSONObject getCompleteResponse() {
        return jsonObj;
    }


    /*
    * get array list without data object
    * */
    public ArrayList<Object> getWithoutDataList(Class<T> clazz, String arrayName) {
        JSONArray dataArray = null;
        try {
            dataArray = getCompleteResponse().getJSONArray(arrayName);
            return getClassList(dataArray, clazz);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    * get json array from data object
    * */
    public ArrayList<Object> getClassList(JSONArray jsonArray, Class<T> clazz) {
        Gson gsn = null;
        ArrayList<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            gsn = new Gson();
            Object object = gsn.fromJson(jsonArray.optJSONObject(i).toString(), clazz);
            list.add(object);
        }

        return list;
    }


    /*
    * return a class type
    * */
    public Class<Object> getClassObj(JSONObject jsonObj, Class<T> clazz) {
        Gson gsn = new Gson();
        Object object = gsn.fromJson(jsonObj.toString(), clazz);
        return (Class<Object>) object;
    }


}
