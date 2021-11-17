package com.sudrives.sudrives.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CancelOrderListModel {
    public String id, answer_report_type;
    public  boolean isSelected = false;

    public CancelOrderListModel(String id, String answer_report_type) {
        this.id = id;
        this.answer_report_type = answer_report_type;
    }

    public CancelOrderListModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer_report_type() {
        return answer_report_type;
    }

    public void setAnswer_report_type(String answer_report_type) {
        this.answer_report_type = answer_report_type;
    }

    public static ArrayList<CancelOrderListModel> getList(Context mcontext, JSONArray jsonArray) {
        ArrayList<CancelOrderListModel> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String answer_report_type = jsonObject.getString("name");

                    list.add(new CancelOrderListModel(id, answer_report_type));

                } catch (Exception e) {



                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
