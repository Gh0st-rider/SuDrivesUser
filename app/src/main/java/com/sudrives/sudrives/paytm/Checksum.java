package com.sudrives.sudrives.paytm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Belal on 1/10/2018.
 */

public class Checksum {

    @SerializedName("error_line")
    @Expose
    private Integer errorLine;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private Result result;

    public Integer getErrorLine() {
        return errorLine;
    }

    public void setErrorLine(Integer errorLine) {
        this.errorLine = errorLine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }



   public  class Result {

        @SerializedName("paytmChecksum")
        @Expose
        private String paytmChecksum;
        @SerializedName("paytmParams")
        @Expose
        private PaytmParams paytmParams;

        public String getPaytmChecksum() {
            return paytmChecksum;
        }

        public void setPaytmChecksum(String paytmChecksum) {
            this.paytmChecksum = paytmChecksum;
        }

        public PaytmParams getPaytmParams() {
            return paytmParams;
        }

        public void setPaytmParams(PaytmParams paytmParams) {
            this.paytmParams = paytmParams;
        }



       public  class PaytmParams {

           @SerializedName("CHANNEL_ID")
           @Expose
           private String cHANNELID;
           @SerializedName("MID")
           @Expose
           private String mID;
           @SerializedName("INDUSTRY_TYPE_ID")
           @Expose
           private String iNDUSTRYTYPEID;
           @SerializedName("TXN_AMOUNT")
           @Expose
           private String tXNAMOUNT;
           @SerializedName("CUST_ID")
           @Expose
           private String cUSTID;
           @SerializedName("CALLBACK_URL")
           @Expose
           private String cALLBACKURL;
           @SerializedName("ORDER_ID")
           @Expose
           private String oRDERID;
           @SerializedName("MOBILE_NO")
           @Expose
           private String mOBILENO;
           @SerializedName("EMAIL")
           @Expose
           private String eMAIL;
           @SerializedName("WEBSITE")
           @Expose
           private String wEBSITE;

           public String getCHANNELID() {
               return cHANNELID;
           }

           public void setCHANNELID(String cHANNELID) {
               this.cHANNELID = cHANNELID;
           }

           public String getMID() {
               return mID;
           }

           public void setMID(String mID) {
               this.mID = mID;
           }

           public String getINDUSTRYTYPEID() {
               return iNDUSTRYTYPEID;
           }

           public void setINDUSTRYTYPEID(String iNDUSTRYTYPEID) {
               this.iNDUSTRYTYPEID = iNDUSTRYTYPEID;
           }

           public String getTXNAMOUNT() {
               return tXNAMOUNT;
           }

           public void setTXNAMOUNT(String tXNAMOUNT) {
               this.tXNAMOUNT = tXNAMOUNT;
           }

           public String getCUSTID() {
               return cUSTID;
           }

           public void setCUSTID(String cUSTID) {
               this.cUSTID = cUSTID;
           }

           public String getCALLBACKURL() {
               return cALLBACKURL;
           }

           public void setCALLBACKURL(String cALLBACKURL) {
               this.cALLBACKURL = cALLBACKURL;
           }

           public String getORDERID() {
               return oRDERID;
           }

           public void setORDERID(String oRDERID) {
               this.oRDERID = oRDERID;
           }

           public String getMOBILENO() {
               return mOBILENO;
           }

           public void setMOBILENO(String mOBILENO) {
               this.mOBILENO = mOBILENO;
           }

           public String getEMAIL() {
               return eMAIL;
           }

           public void setEMAIL(String eMAIL) {
               this.eMAIL = eMAIL;
           }

           public String getWEBSITE() {
               return wEBSITE;
           }

           public void setWEBSITE(String wEBSITE) {
               this.wEBSITE = wEBSITE;
           }

       }

    }
}


