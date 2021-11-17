package com.sudrives.sudrives.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StatesModel {


    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("result")
    public ArrayList<Result> result;
    @Expose
    @SerializedName("error_line")
    public int error_line;
    @Expose
    @SerializedName("error_code")
    public int error_code;
    @Expose
    @SerializedName("status")
    public int status;

    public static class Result {
        @Expose
        @SerializedName("country_id")
        public String country_id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;

        @Override
        public String toString() {
            return name;
        }


        public Result(String country_id, String name, String id) {
            this.country_id = country_id;
            this.name = name;
            this.id = id;
        }
    }




}
