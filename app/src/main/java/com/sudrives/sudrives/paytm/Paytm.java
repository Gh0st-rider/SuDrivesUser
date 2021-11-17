package com.sudrives.sudrives.paytm;

import com.google.gson.annotations.SerializedName;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Belal on 1/10/2018.
 */


public class Paytm {

    @SerializedName("MID")
    String mId;

    @SerializedName("ORDER_ID")
    String orderId;

    @SerializedName("CUST_ID")
    String custId;

    @SerializedName("CHANNEL_ID")
    String channelId;

    @SerializedName("TXN_AMOUNT")
    String txnAmount;

    @SerializedName("WEBSITE")
    String website;

    @SerializedName("CALLBACK_URL")
    String callBackUrl;

    @SerializedName("INDUSTRY_TYPE_ID")
    String industryTypeId;

    @SerializedName("MOBILE_NO")
    String mobileNo;

    @SerializedName("EMAIL")
    String email;


    @SerializedName("Key")
    String merchantKey;


    public Paytm(String mId, String channelId, String txnAmount, String website, String callBackUrl, String industryTypeId, String mobileNo, String email, String merchantKey) {
        this.mId = mId;
        this.orderId = "SuDrives-ANDROID-";
        this.custId = "SuDrives-CUST-";
        this.channelId = channelId;
        this.txnAmount = txnAmount;
        this.website = website;
        this.callBackUrl = callBackUrl;
        this.industryTypeId = industryTypeId;
        this.mobileNo = mobileNo;
        this.email = email;
        this.merchantKey = merchantKey;
    }

    public String getmId() {
        return mId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustId() {
        return custId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public String getWebsite() {
        return website;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }

    /*
     * The following method we are using to generate a random string everytime
     * As we need a unique customer id and order id everytime
     * For real scenario you can implement it with your own application logic
     * */
    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public static String getRandomNumber(){
        Random rand = new Random();
        int num = rand.nextInt(9000000) + 1000000;

        return ""+num;

    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public void setMerchantKey(String merchantKey) {
        this.merchantKey = merchantKey;
    }
}

