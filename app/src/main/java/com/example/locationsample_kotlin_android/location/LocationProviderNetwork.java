package com.example.locationsample_kotlin_android.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

class LocationProviderNetwork implements LocationProvidersContract {

    private final Context mContext;
    private final LocationStatusListener mLocationStatusListener;
    private final LocationManager mLocationManager;

    LocationProviderNetwork(Context context, LocationStatusListener locationStatusListener) {
        mContext = context;
        mLocationStatusListener = locationStatusListener;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationStatusListener.onLocationRetrieveError(LocationStatus.locationPermissionNotGranted());
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
    }

    @Override
    public void stopLocationUpdates() {
        if (mLocationManager != null && mLocationListener != null)
            mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public void fetchLatestKnownLocation() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            onLocationRetrieved(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status != LocationProvider.AVAILABLE) {
                fetchLatestKnownLocation();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
}
