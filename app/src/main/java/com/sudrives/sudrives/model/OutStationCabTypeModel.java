package com.sudrives.sudrives.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OutStationCabTypeModel {
    public  boolean isSelected = false;
  private   String vehicle_id, vehicle_city_id,vehicle_name, cancel_charges,vehicle_sel_img,  vehicle_img, vehicle_discription, amount, charge_per_km, night_charge, sgst, cgst, km_distance, time;
    public OutStationCabTypeModel( String vehicle_id,String vehicle_name,String vehicle_sel_img, String vehicle_img,String vehicle_discription,String amount,String charge_per_km,
                                   String night_charge,String sgst,String cgst,String km_distance,String time,String cancel_charges,String vehicle_city_id) {
        this.vehicle_id = vehicle_id;
        this.vehicle_name = vehicle_name;
        this.vehicle_sel_img = vehicle_sel_img;
        this.vehicle_img = vehicle_img;
        this.time = time;
        this.vehicle_discription = vehicle_discription;
        this.amount = amount;
        this.charge_per_km = charge_per_km;
        this.night_charge = night_charge;
        this.sgst = sgst;
        this.cgst = cgst;
        this.km_distance = km_distance;
        this.cancel_charges = cancel_charges;
        this.vehicle_city_id = vehicle_city_id;
    }

    public OutStationCabTypeModel() {

    }



    public static ArrayList<OutStationCabTypeModel> getList(Context mcontext, JSONArray jsonArray) {
        ArrayList<OutStationCabTypeModel> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String vehicle_id = jsonObject.getString("vehicle_id").trim();
                    String vehicle_name = jsonObject.getString("vehicle_name").trim();
                    String vehicle_sel_img = jsonObject.getString("vehicle_sel_img").trim();
                    String vehicle_img = jsonObject.getString("vehicle_img").trim();
                    String vehicle_discription = jsonObject.getString("vehicle_discription").trim();
                    String amount = jsonObject.getString("amount").trim();
                    String charge_per_km = jsonObject.getString("charge_per_km").trim();
                    String night_charge = jsonObject.getString("night_charge").trim();
                    String sgst = jsonObject.getString("sgst").trim();
                    String cgst = jsonObject.getString("cgst").trim();
                    String km_distance = jsonObject.getString("km_distance").trim();
                    String time = jsonObject.getString("time_minutes").trim();
                    String cancel_charges = jsonObject.getString("cancel_charges").trim();
                    String vehicle_city_id = jsonObject.getString("vehicle_city_id").trim();

                    list.add(new OutStationCabTypeModel(vehicle_id, vehicle_name, vehicle_sel_img, vehicle_img,vehicle_discription,amount,charge_per_km,
                            night_charge,sgst,cgst,km_distance,time,cancel_charges,vehicle_city_id));

                } catch (Exception e) {


                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public String getCancel_charges() {
        return cancel_charges;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public String getVehicle_sel_img() {
        return vehicle_sel_img;
    }

    public String getVehicle_img() {
        return vehicle_img;
    }

    public String getVehicle_discription() {
        return vehicle_discription;
    }

    public String getAmount() {
        return amount;
    }

    public String getCharge_per_km() {
        return charge_per_km;
    }

    public String getNight_charge() {
        return night_charge;
    }

    public String getSgst() {
        return sgst;
    }

    public String getCgst() {
        return cgst;
    }

    public String getKm_distance() {
        return km_distance;
    }

    public String getTime() {
        return time;
    }

    public String getVehicle_city_id() {
        return vehicle_city_id;
    }

    public void setVehicle_city_id(String vehicle_city_id) {
        this.vehicle_city_id = vehicle_city_id;
    }
}
