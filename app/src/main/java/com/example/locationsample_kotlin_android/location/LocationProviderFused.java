package com.example.locationsample_kotlin_android.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

class LocationProviderFused implements LocationProvidersContract {
    private Context mContext;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationStatusListener mLocationStatusListener;
    private long mInterval, mFastestInterval;

    LocationProviderFused(Context context, LocationStatusListener locationStatusListener, long interval, long fastestInterval) {
        mContext = context;
        mInterval = interval;
        mFastestInterval = fastestInterval;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationStatusListener = locationStatusListener;
    }

    @Override
    public void requestLocationUpdates() {
        LocationRequest locationRequest = LocationHelper.createLocationRequest(mInterval, mFastestInterval);
        requestLocationUpdates(locationRequest);
    }

    @Override
    public void stopLocationUpdates() {
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void fetchLatestKnownLocation() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
            if (location != null)
                onLocationRetrieved(location);
            else
                mLocationStatusListener.onLocationRetrieveError(LocationStatus.error());
        } else {
            mLocationStatusListener.onLocationRetrieveError(LocationStatus.locationPermissionNotGranted());
        }
    }

    private void onLocationRetrieved(Location location) {
        mLocationStatusListener.onLocationRetrieved(location);
    }

    private void requestLocationUpdates(LocationRequest locationRequest) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                mLocationStatusListener.onLocationRetrieveError(LocationStatus.error());
                return;
            }
            onLocationRetrieved(locationResult.getLastLocation());
        }
    };
}
