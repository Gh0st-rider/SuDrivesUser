package com.sudrives.sudrives.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;


import com.sudrives.sudrives.R;

import com.sudrives.sudrives.activity.HomeMenuActivity;
import com.sudrives.sudrives.activity.Splash;
import com.sudrives.sudrives.fragment.HomeFragment;
import com.sudrives.sudrives.fragment.LiveFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by ADMIN14 on 5/3/2017.
 */

public class GetLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String TAG = "Location";
    private Context mContext;
    public static final int PERMISSION_REQUEST_CODE = 1;

    /**
     * Constant used in the location settings dialog.
     */
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;


    // Fused Api Client;
    protected FusedLocationProviderClient fusedLocationProviderClient;

    // Location Callback
    protected LocationCallback mLocationCallback;


    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";


    public GetLocation(Context context) {
        mContext = context;
        buildGoogleApiClient(context);
        createLocationRequest(context);
        buildLocationSettingsRequest(context);

    }

    public GetLocation() {

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient(Context context) {
        // Log.i(TAG, "Building GoogleApiClient");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest(Context context) {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest(Context context) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                //   Log.i("MainActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

            }

            onLocationChanged(locationResult.getLastLocation());
        }
    };


    /**
     * Requests location updates from the FusedLocationApi.
     */
    public void startLocationUpdates(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());


    }

    // Check Location Permission
    public void checkLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ((Activity) mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }


            checkLocationSettings();
        } else {
            checkLocationSettings();
        }


    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    public void checkLocationSettings() {
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(mContext).checkLocationSettings(mLocationSettingsRequest);
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    startLocationUpdates(mContext);

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {

                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;

                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult((Activity) mContext, REQUEST_CHECK_SETTINGS);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                           /* Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                                    "not created.");*/
                            break;
                    }
                }
            }
        });
    }

    public void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void disConnectGoogleClient() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Log.e("I am Connected", "I am Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void onLocationChanged(Location location) {
        //  Log.e("Location found", "Location Found");

        if (location.getLatitude() != 0 && location.getLongitude() != 0) {

            if (mContext instanceof Splash) {
            ((Splash) mContext).onLocationUpdate(location);
            Log.e("Location found", "Location Found");
        }
          else  if (mContext instanceof HomeMenuActivity)  {
              try{
                if (((HomeMenuActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.frame_content) instanceof HomeFragment) {

                    ((HomeFragment) ((HomeMenuActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.frame_content)).onLocationUpdate(location);


                }
                if (((HomeMenuActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.frame_content) instanceof LiveFragment) {

                    ((LiveFragment) ((HomeMenuActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.frame_content)).onLocationUpdate(location);


                }
              }catch(Exception e){
                  e.printStackTrace();
              }
            }
        }

    }


}
