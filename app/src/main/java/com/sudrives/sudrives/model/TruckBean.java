package com.sudrives.sudrives.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

//model

public class TruckBean implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("error_line")
    @Expose
    private Integer errorLine;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }


    public class VehicleFare {

        @SerializedName("key_pair")
        @Expose
        private String keyPair;
        @SerializedName("value_pair")
        @Expose
        private String valuePair;
        @SerializedName("value_pair_key")
        @Expose
        private String valuePairKey;
        @SerializedName("additional_param")
        @Expose
        private String additionalParam;

        public String getKeyPair() {
            return keyPair;
        }

        public void setKeyPair(String keyPair) {
            this.keyPair = keyPair;
        }

        public String getValuePair() {
            return valuePair;
        }

        public void setValuePair(String valuePair) {
            this.valuePair = valuePair;
        }

        public String getValuePairKey() {
            return valuePairKey;
        }

        public void setValuePairKey(String valuePairKey) {
            this.valuePairKey = valuePairKey;
        }

        public String getAdditionalParam() {
            return additionalParam;
        }

        public void setAdditionalParam(String additionalParam) {
            this.additionalParam = additionalParam;
        }

    }

    public class Result {

        public boolean isClicked = false;

        public Result(boolean isClicked) {
            this.isClicked = isClicked;
        }

        public boolean isClicked() {
            return isClicked;
        }

        public void setClicked(boolean clicked) {
            isClicked = clicked;
        }

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("fare_charges")
        @Expose
        private String fareCharges;
        @SerializedName("vehicle_name")
        @Expose
        private String vehicleName;
        @SerializedName("vehicle_marker_img")
        @Expose
        private String vehicleMarkerImg;
        @SerializedName("vehicle_img")
        @Expose
        private String vehicleImg;
        @SerializedName("vehicle_sel_img")
        @Expose
        private String vehicleSelImg;
        @SerializedName("vehicle_size")
        @Expose
        private String vehicleSize;
        @SerializedName("vehicle_payload")
        @Expose
        private String vehiclePayload;
        @SerializedName("vehicle_discription")
        @Expose
        private String vehicleDiscription;
        @SerializedName("vehicle_condition")
        @Expose
        private String vehicleCondition;

        @SerializedName("vehicle_city_id")
        @Expose
        private String vehicle_city_id;
        @SerializedName("vehicle_type_id")
        @Expose
        private String vehicle_type_id;

        @SerializedName("vehicle_fare")
        @Expose
        private List<VehicleFare> vehicleFare = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFareCharges() {
            return fareCharges;
        }

        public void setFareCharges(String fareCharges) {
            this.fareCharges = fareCharges;
        }

        public String getVehicleName() {
            return vehicleName;
        }

        public void setVehicleName(String vehicleName) {
            this.vehicleName = vehicleName;
        }

        public String getVehicleMarkerImg() {
            return vehicleMarkerImg;
        }

        public void setVehicleMarkerImg(String vehicleMarkerImg) {
            this.vehicleMarkerImg = vehicleMarkerImg;
        }

        public String getVehicleImg() {
            return vehicleImg;
        }

        public void setVehicleImg(String vehicleImg) {
            this.vehicleImg = vehicleImg;
        }

        public String getVehicleSelImg() {
            return vehicleSelImg;
        }

        public void setVehicleSelImg(String vehicleSelImg) {
            this.vehicleSelImg = vehicleSelImg;
        }

        public String getVehicleSize() {
            return vehicleSize;
        }

        public void setVehicleSize(String vehicleSize) {
            this.vehicleSize = vehicleSize;
        }

        public String getVehiclePayload() {
            return vehiclePayload;
        }

        public void setVehiclePayload(String vehiclePayload) {
            this.vehiclePayload = vehiclePayload;
        }

        public String getVehicleDiscription() {
            return vehicleDiscription;
        }

        public void setVehicleDiscription(String vehicleDiscription) {
            this.vehicleDiscription = vehicleDiscription;
        }

        public String getVehicleCondition() {
            return vehicleCondition;
        }

        public void setVehicleCondition(String vehicleCondition) {
            this.vehicleCondition = vehicleCondition;
        }

        public List<VehicleFare> getVehicleFare() {
            return vehicleFare;
        }

        public void setVehicleFare(List<VehicleFare> vehicleFare) {
            this.vehicleFare = vehicleFare;
        }

        public String getVehicle_city_id() {
            return vehicle_city_id;
        }

        public void setVehicle_city_id(String vehicle_city_id) {
            this.vehicle_city_id = vehicle_city_id;
        }

        public String getVehicle_type_id() {
            return vehicle_type_id;
        }

        public void setVehicle_type_id(String vehicle_type_id) {
            this.vehicle_type_id = vehicle_type_id;
        }
    }
}
