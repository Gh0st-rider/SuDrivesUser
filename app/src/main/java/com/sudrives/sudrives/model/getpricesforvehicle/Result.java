
package com.sudrives.sudrives.model.getpricesforvehicle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;
    @SerializedName("vehicle_image")
    @Expose
    private String vehicleImage;
    @SerializedName("kilometers")
    @Expose
    private String kilometers;
    @SerializedName("fare_of_distance")
    @Expose
    private String fareOfDistance;
    @SerializedName("eta")
    @Expose
    private String eta;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public String getKilometers() {
        return kilometers;
    }

    public void setKilometers(String kilometers) {
        this.kilometers = kilometers;
    }

    public String getFareOfDistance() {
        return fareOfDistance;
    }

    public void setFareOfDistance(String fareOfDistance) {
        this.fareOfDistance = fareOfDistance;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

}
