package com.sudrives.sudrives.model;

import java.io.Serializable;

public class RegisterModel implements Serializable {
    private String firstname;
    private String lastname;
    private String gender;
    private String mobileNumber;
    private String email;
    private String otp;
    private String city;
    private String state;
    private String userid;


    public RegisterModel() {

    }

    public RegisterModel(String firstname, String lastname, String gender, String mobileNumber, String email, String otp, String city, String state, String userid) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.otp = otp;
        this.city = city;
        this.state = state;
        this.userid = userid;
       // this.unique_no = unique_no;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
