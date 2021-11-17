package com.sudrives.sudrives.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CabTypeModel {
    public String id, package_id,vehicle_id,amount,time,km_driven,create_dt,vehicle_name,vehicle_discription,vehicle_img,vehicle_sel_img;
    public  boolean isSelected = false;

    public CabTypeModel(String id, String package_id, String vehicle_id, String amount,String time,String km_driven,String create_dt
    ,String vehicle_name,String vehicle_discription,String vehicle_img,String vehicle_sel_img) {
        this.id = id;
        this.package_id = package_id;
        this.vehicle_id = vehicle_id;
        this.amount = amount;
        this.time = time;
        this.km_driven = km_driven;
        this.create_dt = create_dt;
        this.vehicle_name = vehicle_name;
        this.vehicle_discription = vehicle_discription;
        this.vehicle_img = vehicle_img;
        this.vehicle_sel_img = vehicle_sel_img;
    }

    public CabTypeModel() {

    }



    public static ArrayList<CabTypeModel> getList(Context mcontext, JSONArray jsonArray) {
        ArrayList<CabTypeModel> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id").trim();
                    String package_id = jsonObject.getString("package_id").trim();
                    String vehicle_id = jsonObject.getString("vehicle_id").trim();
                    String amount = jsonObject.getString("amount").trim();
                    String time = jsonObject.getString("time").trim();
                    String km_driven = jsonObject.getString("km_driven").trim();
                    String create_dt = jsonObject.getString("create_dt").trim();
                    String vehicle_name = jsonObject.getString("vehicle_name").trim();
                    String vehicle_discription = jsonObject.getString("vehicle_discription").trim();
                    String vehicle_img = jsonObject.getString("vehicle_img").trim();
                    String vehicle_sel_img = jsonObject.getString("vehicle_sel_img").trim();

                    list.add(new CabTypeModel(id, package_id, vehicle_id, amount,time,km_driven,create_dt,
                            vehicle_name,vehicle_discription,vehicle_img,vehicle_sel_img));

                } catch (Exception e) {


                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getTime() {
        return time;
    }

    public String getKm_driven() {
        return km_driven;
    }

    public String getCreate_dt() {
        return create_dt;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public String getVehicle_discription() {
        return vehicle_discription;
    }

    public String getVehicle_img() {
        return vehicle_img;
    }

    public String getVehicle_sel_img() {
        return vehicle_sel_img;
    }
}
