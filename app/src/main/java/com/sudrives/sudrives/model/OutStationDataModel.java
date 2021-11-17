package com.sudrives.sudrives.model;

import java.io.Serializable;

/**
 * Created by user-5 on 1/8/19.
 */

public class OutStationDataModel  implements Serializable {
    private  String mToAddress , mFromAddress ,mLeaveDate, mLeaveDateSend, mFromLat,mFromLong,mToLat,mToLong, mVehicleName,mVehicleDescription,mTripType,distance
            ,mAmount,mBaseFare,mTime,mCgst,mSgst,mNightCharges,mVehicleId,mCancelCharges,mVehicleCityId,mReturnDate, mReturnDateSend ;

    public String getmReturnDate() {
        return mReturnDate;
    }

    public void setmReturnDate(String mReturnDate) {
        this.mReturnDate = mReturnDate;
    }

    public String getmReturnDateSend() {
        return mReturnDateSend;
    }

    public void setmReturnDateSend(String mReturnDateSend) {
        this.mReturnDateSend = mReturnDateSend;
    }

    public String getmCancelCharges() {
        return mCancelCharges;
    }

    public void setmCancelCharges(String mCancelCharges) {
        this.mCancelCharges = mCancelCharges;
    }

    public String getmLeaveDateSend() {
        return mLeaveDateSend;
    }

    public void setmLeaveDateSend(String mLeaveDateSend) {
        this.mLeaveDateSend = mLeaveDateSend;
    }

    public String getmVehicleId() {
        return mVehicleId;
    }

    public void setmVehicleId(String mVehicleId) {
        this.mVehicleId = mVehicleId;
    }

    public String getmNightCharges() {
        return mNightCharges;
    }

    public void setmNightCharges(String mNightCharges) {
        this.mNightCharges = mNightCharges;
    }

    public String getmCgst() {
        return mCgst;
    }

    public void setmCgst(String mCgst) {
        this.mCgst = mCgst;
    }

    public String getmSgst() {
        return mSgst;
    }

    public void setmSgst(String mSgst) {
        this.mSgst = mSgst;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmBaseFare() {
        return mBaseFare;
    }

    public void setmBaseFare(String mBaseFare) {
        this.mBaseFare = mBaseFare;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getmTripType() {
        return mTripType;
    }

    public void setmTripType(String mTripType) {
        this.mTripType = mTripType;
    }

    public String getmVehicleName() {
        return mVehicleName;
    }

    public void setmVehicleName(String mVehicleName) {
        this.mVehicleName = mVehicleName;
    }

    public String getmVehicleDescription() {
        return mVehicleDescription;
    }

    public void setmVehicleDescription(String mVehicleDescription) {
        this.mVehicleDescription = mVehicleDescription;
    }

    public String getmToAddress() {
        return mToAddress;
    }

    public void setmToAddress(String mToAddress) {
        this.mToAddress = mToAddress;
    }

    public String getmFromAddress() {
        return mFromAddress;
    }

    public void setmFromAddress(String mFromAddress) {
        this.mFromAddress = mFromAddress;
    }

    public String getmLeaveDate() {
        return mLeaveDate;
    }

    public void setmLeaveDate(String mLeaveDate) {
        this.mLeaveDate = mLeaveDate;
    }

    public String getmFromLat() {
        return mFromLat;
    }

    public void setmFromLat(String mFromLat) {
        this.mFromLat = mFromLat;
    }

    public String getmFromLong() {
        return mFromLong;
    }

    public void setmFromLong(String mFromLong) {
        this.mFromLong = mFromLong;
    }

    public String getmToLat() {
        return mToLat;
    }

    public void setmToLat(String mToLat) {
        this.mToLat = mToLat;
    }

    public String getmToLong() {
        return mToLong;
    }

    public void setmToLong(String mToLong) {
        this.mToLong = mToLong;
    }

    public String getmVehicleCityId() {
        return mVehicleCityId;
    }

    public void setmVehicleCityId(String mVehicleCityId) {
        this.mVehicleCityId = mVehicleCityId;
    }
}
