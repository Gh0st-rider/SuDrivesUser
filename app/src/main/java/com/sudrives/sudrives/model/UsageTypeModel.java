package com.sudrives.sudrives.model;

import android.content.Context;


import com.sudrives.sudrives.R;

import java.util.ArrayList;

public class UsageTypeModel {

    public String id;
    public String type,tag_send;


    public UsageTypeModel(String id, String vehicle_name, String tag_send) {
        this.id = id;
        this.type = vehicle_name;
        this.tag_send = tag_send;
    }

    public static ArrayList<UsageTypeModel> getHaulageUsageTypeList(Context context) {
        ArrayList<UsageTypeModel> list = new ArrayList<>();
        list.add(new UsageTypeModel("0","I will be using service",""));
        list.add(new UsageTypeModel("1",context.getString(R.string.business_usage),context.getString(R.string.Business_Usage) ));
        list.add(new UsageTypeModel("1", context.getString(R.string.personal_usage), context.getString(R.string.Personal_Usage)));

        return list;
    }

}