package com.sudrives.sudrives.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectPackageModel {
    public String id, package_name,status,create_dt,city_assign_id;
    public  boolean isSelected = false;

    public SelectPackageModel(String id, String package_name, String status, String create_dt, String city_assign_id) {
        this.id = id;
        this.package_name = package_name;
        this.status = status;
        this.create_dt = create_dt;
        this.city_assign_id = city_assign_id;
    }

    public SelectPackageModel() {

    }




    public static ArrayList<SelectPackageModel> getList(Context mcontext, JSONArray jsonArray) {
        ArrayList<SelectPackageModel> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id").trim();
                    String package_name = jsonObject.getString("package_name").trim();
                    String status = jsonObject.getString("status").trim();
                    String create_dt = jsonObject.getString("create_dt").trim();
                    String city_assign_id = jsonObject.getString("city_assign_id").trim();

                    list.add(new SelectPackageModel(id, package_name, status, create_dt,city_assign_id));

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

    public String getPackage_name() {
        return package_name;
    }

    public String getStatus() {
        return status;
    }

    public String getCreate_dt() {
        return create_dt;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getCity_assign_id() {
        return city_assign_id;
    }

    public void setCity_assign_id(String city_assign_id) {
        this.city_assign_id = city_assign_id;
    }
}
