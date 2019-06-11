package com.example.locationsample_kotlin_android.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
public abstract class BaseLocationFragment extends Fragment {
    public static final String TAG = "BaseLocationFragment";

    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2000;

    /**
     * Called when both LocationPermission and locationSetting are granted.
     */
    public abstract void onLocationReady();

    public abstract void onLocationReadyError(LocationHelper.LocationError locationError);

    /**
     * Checks both location permission and settings are granted.
     */
    protected void checkLocationReady() {
        checkLocationPermissions(getActivity());
    }

    protected void onLocationPermissionGranted() {
        Log.d(TAG, "onLocationPermissionGranted: ");
        checkLocationSettings(getActivity());
    }

    protected void onLocationPermissionDenied() {
        onLocationReadyError(LocationHelper.LocationError.LOCATION_PERMISSION_DENIED);
    }

    private void onLocationSettingGranted() {
        onLocationReady();
    }

    private void onLocationSettingDenied() {
        onLocationReadyError(LocationHelper.LocationError.LOCATION_SETTING_DENIED);
    }

    //* Location Permission *//
    private void checkLocationPermissions(Context context) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // show dialog in case the user had already clicked deny before to redirect to settings
            onLocationReadyError(LocationHelper.LocationError.SHOULD_SHOW_RATIONAL);
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        } else {
            onLocationPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted();
            } else {
                onLocationPermissionDenied();
            }
        }
    }

    //* Location Setting *//
    private void checkLocationSettings(Context context) {
        LocationRequest locationRequest = LocationHelper.createLocationRequest(LocationHelper.Constants.INTERVAL,
                LocationHelper.Constants.FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
        task.addOnCompleteListener(task1 -> {
            try {
                // All location settings are satisfied. The client can initialize location requests here.
                task1.getResult(ApiException.class);
                onLocationSettingGranted();
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            requestLocationSetting(context, resolvable);
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                            onLocationSettingDenied();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        onLocationSettingDenied();
                        break;
                }
            }
        });
    }

    private void requestLocationSetting(Context context, ResolvableApiException resolvable) {
        try {
            startIntentSenderForResult(resolvable.getResolution().getIntentSender(), REQUEST_CODE_LOCATION_SETTINGS, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            onLocationSettingDenied();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                if (!LocationHelper.isLocationEnabled(getActivity())) {
                    onLocationSettingDenied();
                } else {
                    onLocationSettingGranted();
                }
            } else {
                onLocationSettingDenied();
            }
        }
    }
}
