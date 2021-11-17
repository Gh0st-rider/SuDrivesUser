package com.sudrives.sudrives.model;

import java.io.Serializable;

public class ContactBean implements Serializable {

    String name;
    String phone_number;
    String isChecked;





    public String getName() {
        return name;
    }

    public String getPhone_number() {
        return phone_number;
    }


    public ContactBean(String name, String phone_number,String isChecked) {
        this.name = name;
        this.phone_number = phone_number;
        this.isChecked = isChecked;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }
}
