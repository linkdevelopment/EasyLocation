package com.example.locationsample_kotlin_android.location;

import android.content.Context;
import android.location.LocationManager;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Sherif.ElNady on 7/10/2018.
 */

public class LocationHelper {

    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gps_enabled;
    }

    public static LocationRequest createLocationRequest(long interval, long fastestInterval) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
}
