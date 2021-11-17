package com.sudrives.sudrives.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LiveTripModel {
    public String order_num;
    public String ride_user;
    public String ride_assigned_by;
    public String ride_from_lat;
    public String origin;
    public String destination;
    public String ride_from_long;
    public String ride_to_lat;
    public String ride_to_long;
    public String ride_time;
    public String ride_est_date;
    public String ride_est_time;
    public String ride_seat;
    public String ride_amount;
    public String ride_about;
    public String ride_status;
    public String ride_create;
    public String driver_id;
    public String driver_name;
    public String driver_dob;
    public String driver_vehicle;
    public String driver_cdl;
    public String driver_license;
    public String driver_mobile;
    public String driver_img;
    public String book_seat;
    public String book_from_address;
    public String book_from_lat;
    public String book_from_long;
    public String book_to_address;
    public String book_to_lat;
    public String book_to_long;
    public String book_comment;
    public String book_reciever_name;
    public String book_reciever_mobile;
    public String book_img;
    public String book_reason;
    public String book_reason_text;
    public String book_create;
    public String book_status;
    public String distance;
    public String departure;
    public String arrival;
    public String ride_date_arrive;
    public String rating;
    public String noofcars;
    public String date;
    public String truck_type, delivery_date, delivery_time, book_card_id_status;
    public String book_id, book_time;

    public LiveTripModel(String book_id, String book_time) {
        this.book_time = book_time;
        this.book_id = book_id;

    }


    public LiveTripModel(String order_num, String ride_user, String ride_assigned_by,
                         String ride_from_lat, String origin, String destination, String ride_from_long,
                         String ride_to_lat, String ride_to_long, String ride_time, String ride_est_date,
                         String ride_est_time, String ride_seat, String ride_amount, String ride_about,
                         String ride_status, String ride_create, String driver_id, String driver_name,
                         String driver_dob, String driver_vehicle, String driver_cdl, String driver_license,
                         String driver_mobile, String driver_img, String book_id, String book_seat,
                         String book_from_address, String book_from_lat, String book_from_long, String book_to_address,
                         String book_to_lat, String book_to_long, String book_comment, String book_reciever_name,
                         String book_reciever_mobile, String book_img, String book_reason, String book_reason_text,
                         String book_create, String book_status, String distance, String departure, String arrival,
                         String ride_date_arrive, String rating, String noofcars, String date, String truck_type,
                         String delivery_date, String delivery_time, String book_card_id_status) {
        this.order_num = order_num;
        this.ride_user = ride_user;
        this.ride_assigned_by = ride_assigned_by;
        this.ride_from_lat = ride_from_lat;
        this.origin = origin;
        this.destination = destination;
        this.ride_from_long = ride_from_long;
        this.ride_to_lat = ride_to_lat;
        this.ride_to_long = ride_to_long;
        this.ride_time = ride_time;
        this.ride_est_date = ride_est_date;
        this.ride_est_time = ride_est_time;
        this.ride_seat = ride_seat;
        this.ride_amount = ride_amount;
        this.ride_about = ride_about;
        this.ride_status = ride_status;
        this.ride_create = ride_create;
        this.driver_id = driver_id;
        this.driver_name = driver_name;
        this.driver_dob = driver_dob;
        this.driver_vehicle = driver_vehicle;
        this.driver_cdl = driver_cdl;
        this.driver_license = driver_license;
        this.driver_mobile = driver_mobile;
        this.driver_img = driver_img;
        this.book_id = book_id;
        this.book_seat = book_seat;
        this.book_from_address = book_from_address;
        this.book_from_lat = book_from_lat;
        this.book_from_long = book_from_long;
        this.book_to_address = book_to_address;
        this.book_to_lat = book_to_lat;
        this.book_to_long = book_to_long;
        this.book_comment = book_comment;
        this.book_reciever_name = book_reciever_name;
        this.book_reciever_mobile = book_reciever_mobile;
        this.book_img = book_img;
        this.book_reason = book_reason;
        this.book_reason_text = book_reason_text;
        this.book_create = book_create;
        this.book_status = book_status;
        this.distance = distance;
        this.departure = departure;
        this.arrival = arrival;
        this.ride_date_arrive = ride_date_arrive;
        this.rating = rating;
        this.noofcars = noofcars;
        this.date = date;
        this.truck_type = truck_type;
        this.delivery_date = delivery_date;
        this.delivery_time = delivery_time;
        this.book_card_id_status = book_card_id_status;

    }

    public LiveTripModel(String order_num) {
        this.order_num = order_num;
    }


    public static ArrayList<LiveTripModel> getMyBookingsList(JSONArray jsonArr) {


        ArrayList<LiveTripModel> list = new ArrayList<>();
        try {
            if (jsonArr != null) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    if (jsonObj.optString("ride_status") != null) {
                        if (!jsonObj.optString("ride_status").equals("0")) {
                            list.add(new LiveTripModel(jsonObj.optString("ride_id"),
                                    jsonObj.optString("ride_user"),
                                    jsonObj.optString("ride_assigned_by"),
                                    jsonObj.optString("ride_from_lat"),
                                    jsonObj.optString("ride_from_address"),
                                    jsonObj.optString("ride_to_address"),
                                    jsonObj.optString("ride_from_long"),
                                    jsonObj.optString("ride_to_lat"),
                                    jsonObj.optString("ride_to_long"),
                                    jsonObj.optString("ride_time"),
                                    jsonObj.optString("ride_est_date"),
                                    jsonObj.optString("ride_est_time"),
                                    jsonObj.optString("ride_seat"),
                                    jsonObj.optString("ride_amount"),
                                    jsonObj.optString("ride_about"),
                                    jsonObj.optString("ride_status"),
                                    jsonObj.optString("ride_create"),
                                    jsonObj.optString("driver_id"),
                                    jsonObj.optString("driver_name"),
                                    jsonObj.optString("driver_dob"),
                                    jsonObj.optString("driver_vehicle"),
                                    jsonObj.optString("driver_cdl"),
                                    jsonObj.optString("driver_license"),
                                    jsonObj.optString("driver_mobile"),
                                    jsonObj.optString("driver_img"),
                                    jsonObj.optString("book_id"),
                                    jsonObj.optString("book_seat"),
                                    jsonObj.optString("book_from_address"),
                                    jsonObj.optString("book_from_lat"),
                                    jsonObj.optString("book_from_long"),
                                    jsonObj.optString("book_to_address"),
                                    jsonObj.optString("book_to_lat"),
                                    jsonObj.optString("book_to_long"),
                                    jsonObj.optString("book_comment"),
                                    jsonObj.optString("book_reciever_name"),
                                    jsonObj.optString("book_reciever_mobile"),
                                    jsonObj.optString("book_img"),
                                    jsonObj.optString("book_reason"),
                                    jsonObj.optString("book_reason_text"),
                                    jsonObj.optString("book_create"),
                                    jsonObj.optString("book_status"),
                                    jsonObj.optString("distance"),
                                    jsonObj.optString("departure"),
                                    jsonObj.optString("arrival"),
                                    jsonObj.optString("ride_date_arrive"),
                                    jsonObj.optString("rating"),
                                    jsonObj.optString("noofcars"),
                                    jsonObj.optString("ride_date"),
                                    jsonObj.optString("ride_vehicle"),
                                    jsonObj.optString("ride_delivery_date")
                                    , jsonObj.optString("ride_delivery_time"), jsonObj.optString("book_card_id_status")
                            ));
                        }
                    }

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static ArrayList<LiveTripModel> getMyTripList(JSONArray jsonArr) {
        ArrayList<LiveTripModel> list = new ArrayList<>();

        if (jsonArr != null) {
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = jsonArr.getJSONObject(i);
                    list.add(new LiveTripModel(jsonObj.optString("booking_id"),
                            jsonObj.optString("booking_start_dt")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
            return list;
    }

    public static ArrayList<LiveTripModel> getMyBookingsDemoList() {

        ArrayList<LiveTripModel> list = new ArrayList<>();

        list.add(new LiveTripModel("51"));


        return list;
    }
}
