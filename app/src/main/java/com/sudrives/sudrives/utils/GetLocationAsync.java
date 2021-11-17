package com.sudrives.sudrives.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocationAsync extends AsyncTask<String, Void, String> {
    private String fulladdress = "";
    private String smallAddress = "";
    private String city = "";
    private String state = "";
    private String country = "";
    private String zipcode = "";
    private String placeName = "";

    private AsyncResponse asyncResponseDelegate = null;
    private   double x, y;
    private Context context;

    public interface AsyncResponse {
        void onProcessFinished(String fulladdress, String smallAddress, String state, String city, String country, String zipCode, String placeName);
    }

    public GetLocationAsync(double latitude, double longitude, final Context context, AsyncResponse asyncResponse) {
        x = latitude;
        y = longitude;
        this.context = context;
        this.asyncResponseDelegate = asyncResponse;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addressList = geocoder.getFromLocation(x, y, 1);
            StringBuilder sb = new StringBuilder();
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                fulladdress = "";
                smallAddress = "";
                placeName = "";
                if (sb.append(address.getAddressLine(0)) != null) {
                    smallAddress = address.getAddressLine(0);
                    fulladdress = smallAddress;
                }
                if (address.getLocality() != null) {
                    city = address.getLocality();
                    if (!fulladdress.contains(city))
                        fulladdress = fulladdress + " " + city;
                }
                if (address.getFeatureName() != null) {
                    placeName = address.getFeatureName();
                }
                if (address.getAdminArea() != null) {
                    state = address.getAdminArea();
                    if (!fulladdress.contains(state))
                        fulladdress = fulladdress + " " + state;
                }
                if (address.getPostalCode() != null) {
                    zipcode = address.getPostalCode();
                    if (!fulladdress.contains(zipcode))
                        fulladdress = fulladdress + " " + zipcode;
                }
                if (address.getCountryName() != null) {
                    country = address.getCountryName();
                    if (!fulladdress.contains(country))
                        fulladdress = fulladdress + " " + country;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (fulladdress != null && !fulladdress.isEmpty()) {
                asyncResponseDelegate.onProcessFinished(fulladdress, smallAddress, state, city, country, zipcode, placeName);
            } else {
                asyncResponseDelegate.onProcessFinished("", "", "", "", "", "", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}