package com.sudrives.sudrives.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public  class CityModel {


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
        @SerializedName("state_id")
        public String state_id;
        @Expose
        @SerializedName("city_code")
        public String city_code;
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

        public Result(String state_id, String city_code, String name, String id) {
            this.state_id = state_id;
            this.city_code = city_code;
            this.name = name;
            this.id = id;
        }
    }
}
