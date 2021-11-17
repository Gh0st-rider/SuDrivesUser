package com.sudrives.sudrives.model;

import android.content.Context;


import com.sudrives.sudrives.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pankaj on 10/31/18.
 */

public class ReportIssueModel {

    String id;
    String name;
    String types;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }


    public ReportIssueModel(String id, String name, String types) {
        this.id = id;
        this.name = name;
        this.types = types;
    }

    public static ArrayList<ReportIssueModel> getList(Context mcontext, JSONArray jsonArray) {
        ArrayList<ReportIssueModel> list = new ArrayList<>();
        if (jsonArray != null) {
            list.add(new ReportIssueModel("0",mcontext.getString(R.string.select),""));
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String types = jsonObject.getString("types");

                    list.add(new ReportIssueModel(id, name,types));

                } catch (Exception e) {



                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
