package com.example.locationsample_kotlin_android.location;

import android.content.Context;
import android.location.LocationManager;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
public class LocationHelper {

    public enum LocationError {
        LOCATION_PERMISSION_DENIED,
        LOCATION_SETTING_DENIED,
        SHOULD_SHOW_RATIONAL,
    }

    public class Constants {
        public static final long INTERVAL = 10000;
        public static final long FASTEST_INTERVAL = 5000;
        public static final long MAX_LOCATION_REQUEST_TIME = 15000;
    }

    static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gps_enabled;
    }

    static LocationRequest createLocationRequest(long interval, long fastestInterval) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
}
