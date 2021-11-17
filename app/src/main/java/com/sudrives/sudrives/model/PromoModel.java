package com.sudrives.sudrives.model;

public class PromoModel {

    int id,min_bill_amount,max_discount_amount;
    String promotions_title,vehicle_name,code,description,end_date;


    public PromoModel(int id, int min_bill_amount, int max_discount_amount, String promotions_title, String vehicle_name, String code, String description, String end_date) {
        this.id = id;
        this.min_bill_amount = min_bill_amount;
        this.max_discount_amount = max_discount_amount;
        this.promotions_title = promotions_title;
        this.vehicle_name = vehicle_name;
        this.code = code;
        this.description = description;
        this.end_date = end_date;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin_bill_amount() {
        return min_bill_amount;
    }

    public void setMin_bill_amount(int min_bill_amount) {
        this.min_bill_amount = min_bill_amount;
    }

    public int getMax_discount_amount() {
        return max_discount_amount;
    }

    public void setMax_discount_amount(int max_discount_amount) {
        this.max_discount_amount = max_discount_amount;
    }

    public String getPromotions_title() {
        return promotions_title;
    }

    public void setPromotions_title(String promotions_title) {
        this.promotions_title = promotions_title;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
