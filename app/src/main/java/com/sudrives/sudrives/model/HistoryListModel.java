package com.sudrives.sudrives.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user-5 on 3/29/18.
 */

public class HistoryListModel {

    String id;
    String booking_fee;
    String booking_id;
    String book_reciever_name;
    String book_reciever_mobile;
    String book_from_address;
    String book_from_lat;
    String book_from_long;
    String book_to_address;
    String book_to_lat;
    String book_to_long;
    String booking_status;
    String booking_date;
    String booking_start_dt;
    String booking_end_dt;
    String driver_arrived_pickup;
    String driver_arrived;
    String is_online_payment_accept;
    String payment_mode;
    String create_dt;
    String eta;
    String vehicle_name;
    String firstname;
    String lastname;
    String rating;
    String profile_img;
    String driver_id;
    String mobile;
    String vehicle_number;
    String total_time;
    String invoice_link;
    String status_name;
    String total_distance;
    String total_charge;
    String cancel_by;
    String bilty_link;
    String total_fare;

    String base_fare;
    String amount_to_pay;
    String cancel_charge;
    String booking_type;
    String book_later_date_time;
    String discount_amount;
    String final_amount;
    String type_of_booking;
    String sgst;
    String cgst;
    String start_otp;

    public String getStart_otp() {
        return start_otp;
    }

    public void setStart_otp(String start_otp) {
        this.start_otp = start_otp;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }



    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }



    public String getType_of_booking() {
        return type_of_booking;
    }

    public String getDiscount_amount() {
        return discount_amount;
    }

    public String getFinal_amount() {
        return final_amount;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public String getBook_later_date_time() {
        return book_later_date_time;
    }

    public String getCancel_charge() {
        return cancel_charge;
    }

    public HistoryListModel(String amount_to_pay) {
        this.amount_to_pay=amount_to_pay;
    }

    public String getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }

    public String getBase_fare() {
        return base_fare;
    }

    public void setBase_fare(String base_fare) {
        this.base_fare = base_fare;
    }

    public String getAmount_to_pay() {
        return amount_to_pay;
    }

    public void setAmount_to_pay(String amount_to_pay) {
        this.amount_to_pay = amount_to_pay;
    }


    public String getFirstname() {
        return firstname;
    }


    public String getLastname() {
        return lastname;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProfile_img() {
        return profile_img;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }




    public String getBilty_link() {
        return bilty_link;
    }

    public String getCancel_by() {
        return cancel_by;
    }

    public String getTotal_charge() {
        return total_charge;
    }

    public String getBook_reciever_name() {
        return book_reciever_name;
    }

    public String getBooking_start_dt() {
        return booking_start_dt;
    }

    public String getBooking_end_dt() {
        return booking_end_dt;
    }

    public String getDriver_arrived_pickup() {
        return driver_arrived_pickup;
    }

    public String getDriver_arrived() {
        return driver_arrived;
    }

    public String getIs_online_payment_accept() {
        return is_online_payment_accept;
    }

    public String getCreate_dt() {
        return create_dt;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public String getInvoice_link() {
        return invoice_link;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBooking_fee() {
        return booking_fee;
    }


    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }


    public String getBook_reciever_mobile() {
        return book_reciever_mobile;
    }

  public String getBook_from_address() {
        return book_from_address;
    }



    public String getBook_from_lat() {
        return book_from_lat;
    }


    public String getBook_from_long() {
        return book_from_long;
    }


    public String getBook_to_address() {
        return book_to_address;
    }



    public String getBook_to_lat() {
        return book_to_lat;
    }


    public String getBook_to_long() {
        return book_to_long;
    }


    public String getBooking_status() {
        return booking_status;
    }


    public String getBooking_date() {
        return booking_date;
    }




    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getTotal_time() {
        return total_time;
    }

    public HistoryListModel(String id, String booking_fee, String booking_id, String book_reciever_name,
                            String book_reciever_mobile, String book_from_address, String book_from_lat,
                            String book_from_long, String book_to_address, String book_to_lat, String book_to_long,
                            String booking_status, String booking_date, String booking_start_dt, String booking_end_dt,
                            String driver_arrived_pickup, String driver_arrived, String is_online_payment_accept,String payment_mode, String create_dt,
                            String eta, String vehicle_name, String firstname, String lastname, String rating, String profile_img,
                            String driver_id, String mobile, String vehicle_number, String status_name, String total_distance,
                            String total_time, String total_charge, String cancel_by, String invoice_link, String bilty_link, String total_fare,

                            String base_fare,
                            String amount_to_pay,
                            String cancel_charge,
                            String booking_type,
                            String book_later_date_time,
                            String final_amount,
                            String discount_amount,
                            String type_of_booking,String sgst,String cgst,String start_otp) {
        this.id = id;
        this.booking_fee = booking_fee;
        this.booking_id = booking_id;
        this.book_reciever_name = book_reciever_name;
        this.book_reciever_mobile = book_reciever_mobile;
        this.book_from_address = book_from_address;
        this.book_from_lat = book_from_lat;
        this.book_from_long = book_from_long;
        this.book_to_address = book_to_address;
        this.book_to_lat = book_to_lat;
        this.book_to_long = book_to_long;
        this.booking_status = booking_status;
        this.booking_date = booking_date;
        this.booking_start_dt = booking_start_dt;
        this.booking_end_dt = booking_end_dt;
        this.driver_arrived_pickup = driver_arrived_pickup;
        this.driver_arrived = driver_arrived;
        this.is_online_payment_accept = is_online_payment_accept;
        this.payment_mode = payment_mode;
        this.create_dt = create_dt;
        this.eta = eta;
        this.vehicle_name = vehicle_name;

        this.firstname = firstname;
        this.lastname = lastname;
        this.rating = rating;
        this.profile_img = profile_img;
        this.driver_id = driver_id;
        this.mobile = mobile;
        this.vehicle_number = vehicle_number;
        this.status_name = status_name;
        this.total_distance = total_distance;
        this.total_time = total_time;
        this.total_charge = total_charge;
        this.cancel_by = cancel_by;
        this.invoice_link = invoice_link;
        this.bilty_link = bilty_link;
        this.total_fare = total_fare;
        this.base_fare = base_fare;
        this.amount_to_pay = amount_to_pay;
        this.cancel_charge = cancel_charge;
        this.booking_type = booking_type;
        this.book_later_date_time = book_later_date_time;
        this.discount_amount = discount_amount;
        this.final_amount = final_amount;
        this.type_of_booking = type_of_booking;
        this.sgst = sgst;
        this.cgst = cgst;
        this.start_otp = start_otp;



    }


    /*
    }"id": "26",
            "booking_fee": "0",
            "booking_id": "1315566894",
            "book_reciever_name": "FDGFDG",
            "book_reciever_mobile": "3454354354",
            "book_from_address": "Aashay Complex, 56 Dukan St, Behind 56 Dukan, 11 Bungalow Colony, New Palasia, Indore, Madhya Pradesh 452001, India",
            "book_from_lat": "22.724298258434708",
            "book_from_long": "75.8842983841896",
            "book_to_address": "29, Mill Road, Dewas, Madhya Pradesh 455001, India",
            "book_to_lat": "22.962267199999996",
            "book_to_long": "76.0507949",
            "booking_status": "1",
            "booking_date": "1541054188",
            "booking_start_dt": "0",
            "booking_end_dt": "0",
            "driver_arrived_pickup": "1",
            "driver_arrived": "1541057318",
            "is_online_payment_accept": "0",
            "create_dt": "1541054188",
            "eta": "02:30 PM",
            "driver_details": {
            "driver_id": "86",
            "first_name": "Saad",
            "last_name": "",
            "mobile": "9009866517",
            "profile_img": "http:\/\/13.232.205.186\/api\/media\/user\/default.png",
            "vehicle_number": "Mp09QW6458",
            "rating": 0,
            "vehicle_name": "Pickup \/ 8ft"
    */

    public String getStatus_name() {
        return status_name;
    }

    public static ArrayList<HistoryListModel> getMyBookingsList(JSONArray jsonArr) {


        ArrayList<HistoryListModel> list = new ArrayList<>();
        try {
            if (jsonArr != null) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    JSONObject driver_details = jsonObj.getJSONObject("driver_details");
                    String vehicle_name = jsonObj.getString("vehicle_name");
                    String rating = driver_details.getString("rating");
                    String driver_id = driver_details.getString("driver_id");
                    String first_name = driver_details.getString("first_name");
                    String last_name = driver_details.getString("last_name");
                    String mobile = driver_details.getString("mobile");
                    String profile_img = driver_details.getString("profile_img");
                    String vehicle_number = driver_details.getString("vehicle_number");


                    list.add(new HistoryListModel(jsonObj.optString("id"),
                            jsonObj.optString("booking_fee"),
                            jsonObj.optString("booking_id"),
                            jsonObj.optString("book_reciever_name"),
                            jsonObj.optString("book_reciever_mobile"),
                            jsonObj.optString("book_from_address"),
                            jsonObj.optString("book_from_lat"),
                            jsonObj.optString("book_from_long"),
                            jsonObj.optString("book_to_address"),
                            jsonObj.optString("book_to_lat"),
                            jsonObj.optString("book_to_long"),
                            jsonObj.optString("booking_status"),
                            jsonObj.optString("booking_date"),
                            jsonObj.optString("booking_start_dt"),
                            jsonObj.optString("booking_end_dt"),
                            jsonObj.optString("driver_arrived_pickup"),
                            jsonObj.optString("driver_arrived"),
                            jsonObj.optString("is_online_payment_accept"),
                            jsonObj.optString("payment_mode"),
                            jsonObj.optString("create_dt"),
                            jsonObj.optString("eta"),
                            vehicle_name,first_name,last_name, rating,profile_img,driver_id,mobile,vehicle_number,
                            jsonObj.optString("status_name")
                            ,  jsonObj.optString("total_distance")
                            ,  jsonObj.optString("total_time")
                            ,  jsonObj.optString("total_charge"),  jsonObj.optString("cancel_by")
                            ,jsonObj.optString("invoice_link"),
                            jsonObj.optString("bilty_link"),
                            jsonObj.optString("total_fare"),
                            jsonObj.optString("base_fare"),
                            jsonObj.optString("amount_to_pay"),
                            jsonObj.optString("cancel_charge"),
                            jsonObj.optString("booking_type"),
                            jsonObj.optString("book_later_date_time"),
                            jsonObj.optString("final_amount"),
                            jsonObj.optString("discount_amount"),
                            jsonObj.optString("type_of_booking"),
                            jsonObj.optString("sgst"),
                            jsonObj.optString("cgst"),
                            jsonObj.optString("start_otp")



                    ));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    } 
    
    public static ArrayList<HistoryListModel> getMyBookingsLists() {

        ArrayList<HistoryListModel> list = new ArrayList<>();

                    list.add(new HistoryListModel("name"));




        return list;
    }

}
