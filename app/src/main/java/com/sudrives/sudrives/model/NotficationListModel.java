package com.sudrives.sudrives.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user-5 on 3/29/18.
 */

public class NotficationListModel {


    public String noti_id;
    public String user_id;

    public String noti_title;
    public String noti_text;
    public String noti_image;
    public String noti_status;
    public String noti_create;
    public String user_avail_limit;

    public String coupon_no_of_use;
    public String coupon_code,end_date;
    public String settag,vehicle_img;
    public String vehicle_id,vehicle_name,promotions_title,promotions_used_times,description,code_type,code_precantage,max_discount_amount,promotions_user_used_times,code,coupon_expire;

    public NotficationListModel(String noti_id, String user_id, String noti_title, String noti_text,
                                String noti_status, String noti_create) {
        this.noti_id = noti_id;
        this.user_id = user_id;
        this.noti_title = noti_title;
        this.noti_text = noti_text;
        this.noti_status = noti_status;
        this.noti_create = noti_create;


    }

    public String getSettag() {
        return settag;
    }

    public void setSettag(String settag) {
        this.settag = settag;
    }

    public String getNoti_status() {
        return noti_status;
    }

    public void setNoti_status(String noti_status) {
        this.noti_status = noti_status;
    }

   public NotficationListModel(String noti_id, String vehicle_id, String vehicle_name, String vehicle_img, String promotions_title,
                               String promotions_used_times, String description, String code_type, String code_precantage, String max_discount_amount,
                               String promotions_user_used_times, String code, String coupon_expire, String end_date, String user_avail_limit) {
        this.noti_id = noti_id;
        this.vehicle_id = vehicle_id;
        this.vehicle_name = vehicle_name;
        this.vehicle_img = vehicle_img;
        this.promotions_title = promotions_title;
        this.promotions_used_times = promotions_used_times;
        this.description = description;
        this.code_type = code_type;
        this.code_precantage = code_precantage;
        this.max_discount_amount = max_discount_amount;
        this.promotions_user_used_times = promotions_user_used_times;
        this.code = code;
        this.coupon_expire = coupon_expire;
        this.end_date = end_date;
        this.user_avail_limit = user_avail_limit;

    }

    public NotficationListModel(String noti_id) {
        this.noti_id = noti_id;

    }


    public static ArrayList<NotficationListModel> getNotificationList(JSONArray jsonArr) {
        ArrayList<NotficationListModel> list = new ArrayList<>();
        try {
            if (jsonArr != null) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    list.add(new NotficationListModel(
                            jsonObj.optString("id"),
                            jsonObj.optString("user_id"),
                            jsonObj.optString("title"),
                            jsonObj.optString("message"),
                            jsonObj.optString("read_status"),
                            jsonObj.optString("create_dt")));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static ArrayList<NotficationListModel> getPromoNotificationList(JSONArray jsonArr) {
        ArrayList<NotficationListModel> list = new ArrayList<>();
        try {
            if (jsonArr != null) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    list.add(new NotficationListModel(jsonObj.optString("id"),
                            jsonObj.optString("vehicle_id"),
                            jsonObj.optString("vehicle_name"),
                            jsonObj.optString("vehicle_img"),
                            jsonObj.optString("promotions_title"),
                            jsonObj.optString("promotions_used_times"),
                            jsonObj.optString("description"),
                            jsonObj.optString("code_type"),
                            jsonObj.optString("code_precantage"),
                            jsonObj.optString("max_discount_amount"),
                            jsonObj.optString("promotions_user_used_times"),
                            jsonObj.optString("code"),
                            jsonObj.optString("coupon_expire"), jsonObj.optString("end_date"), jsonObj.optString("user_avail_limit")));

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static ArrayList<NotficationListModel> getPromoNotificationdemoList() {
        ArrayList<NotficationListModel> list = new ArrayList<>();
        try {

            list.add(new NotficationListModel(""));


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
