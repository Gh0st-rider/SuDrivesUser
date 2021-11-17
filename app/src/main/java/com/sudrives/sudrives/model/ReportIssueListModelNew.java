package com.sudrives.sudrives.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportIssueListModelNew {
    private    String mobile_no, email, subject, comments, status, types, create_dt, book_from_address, book_to_address, booking_id, vehicle_name, trip_price;
    private   String id;
    private String paytm_orderid;
    private String paytm_trxid;
    private String refund_proccesed;
    private String sort_created_at;
    private String payment_date;

    public String getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }

    private String total_fare;
    private String type_of_booking;

    public ReportIssueListModelNew(String mobile_no, String email, String subject, String comments, String status, String types, String create_dt, String book_from_address,
                                String book_to_address, String booking_id, String vehicle_name, String trip_price,String total_fare,String type_of_booking) {

        this.mobile_no = mobile_no;
        this.email = email;
        this.subject = subject;
        this.comments = comments;
        this.status = status;
        this.types = types;
        this.create_dt = create_dt;
        this.book_from_address = book_from_address;
        this.book_to_address = book_to_address;
        this.booking_id = booking_id;
        this.trip_price = trip_price;
        this.vehicle_name = vehicle_name;
        this.total_fare = total_fare;
        this.type_of_booking = type_of_booking;
    }


    public ReportIssueListModelNew(String id, String paytm_orderid, String paytm_trxid, String trip_price, String comments,String status,String types,
                                String payment_date, String create_dt,
                                String refund_proccesed, String sort_created_at,String total_fare,String type_of_booking) {

        this.id = id;
        this.paytm_orderid = paytm_orderid;
        this.paytm_trxid = paytm_trxid;
        this.trip_price = trip_price;
        this.comments = comments;
        this.types = types;
        this.create_dt = create_dt;
        this.status = status;

        this.payment_date = payment_date;
        this.refund_proccesed = refund_proccesed;
        this.sort_created_at = sort_created_at;
        this.total_fare = total_fare;
        this.type_of_booking = type_of_booking;
    }

    public static ArrayList<ReportIssueListModelNew> getMyIssueList(JSONArray jsonArr) {


        ArrayList<ReportIssueListModelNew> list = new ArrayList<>();
        try {
            if (jsonArr != null) {

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    if (jsonObj.getString("types").equalsIgnoreCase("booking")) {
                        list.add(new ReportIssueListModelNew(jsonObj.optString("mobile_no"),
                                jsonObj.optString("email"),
                                jsonObj.optString("subject"),
                                jsonObj.optString("comments"),
                                jsonObj.optString("status"),
                                jsonObj.optString("types"),
                                jsonObj.optString("create_dt"),
                                jsonObj.optString("book_from_address"),
                                jsonObj.optString("book_to_address"),
                                jsonObj.optString("booking_id"),
                                jsonObj.optString("vehicle_name"),
                                jsonObj.optString("trip_price"),
                                jsonObj.optString("total_fare"),
                                jsonObj.optString("type_of_booking")


                        ));
                    } else {


                        list.add(new ReportIssueListModelNew(jsonObj.optString("id"),
                                jsonObj.optString("paytm_orderid"),
                                jsonObj.optString("paytm_trxid"),
                                jsonObj.optString("amount"),
                                jsonObj.optString("description"),
                                jsonObj.optString("status"),
                                jsonObj.optString("types"),
                                jsonObj.optString("payment_date"),
                                jsonObj.optString("created_at"),
                                jsonObj.optString("refund_proccesed"),
                                jsonObj.optString("sort_created_at"),
                                jsonObj.optString("total_fare"),
                                jsonObj.optString("type_of_booking")


                        ));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public String getPaytm_orderid() {
        return paytm_orderid;
    }

    public String getPaytm_trxid() {
        return paytm_trxid;
    }

    public String getRefund_proccesed() {
        return refund_proccesed;
    }

    public String getSort_created_at() {
        return sort_created_at;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getComments() {
        return comments;
    }

    public String getStatus() {
        return status;
    }

    public String getTypes() {
        return types;
    }

    public String getCreate_dt() {
        return create_dt;
    }

    public String getBook_from_address() {
        return book_from_address;
    }

    public String getBook_to_address() {
        return book_to_address;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public String getTrip_price() {
        return trip_price;
    }

    public String getType_of_booking() {
        return type_of_booking;
    }
}
