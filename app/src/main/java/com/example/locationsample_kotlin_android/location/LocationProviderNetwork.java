package com.example.locationsample_kotlin_android.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

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
        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationStatusListener.onLocationError(LocationStatus.locationPermissionNotGranted());
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
        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null)
                onLocationRetrieved(location);
            else
                mLocationStatusListener.onLocationError(LocationStatus.error());
        } else {
            mLocationStatusListener.onLocationError(LocationStatus.locationPermissionNotGranted());
        }
    }

    private void onLocationRetrieved(Location location) {
        mLocationStatusListener.onLocationSuccess(location);
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
