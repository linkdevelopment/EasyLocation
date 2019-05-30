package com.example.locationsample_kotlin_android.location;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.locationsample_kotlin_android.R;
import com.example.locationsample_kotlin_android.utils.UIUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.Task;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
public abstract class LocationBaseFragment extends Fragment {
    private Context mContext;

    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2000;

    public abstract void onLocationPermissionGranted();

    public abstract void onLocationPermissionDenied();

    public abstract void onLocationSettingGranted();

    public abstract void onLocationSettingDenied();

    //* Location Permission *//
    protected void checkLocationPermissions(Context context) {
        mContext = context;
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

            UIUtils.showBasicDialog(mContext, null, getString(R.string.nearby_location_permission_message),
                    getString(R.string.grant_permission), getString(R.string.cancel),
                    this::onLocationPermissionDialogInteraction,
                    this::onLocationPermissionDialogInteraction)
                    .setOnCancelListener(dialog -> onLocationPermissionDialogInteraction(dialog, DialogInterface.BUTTON_NEGATIVE));
        } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        } else {
            onLocationPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLocationPermissionGranted();
                } else {
                    onLocationPermissionDenied();
                }
                break;
            }
        }
    }

    private void onLocationPermissionDialogInteraction(DialogInterface dialogInterface, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                break;
            }
            default:
            case DialogInterface.BUTTON_NEGATIVE: {
                onLocationPermissionDenied();
                break;
            }
        }
    }

    //* Location Setting *//
    protected void checkLocationSettings(Context context) {
        mContext = context;
        LocationRequest locationRequest = LocationHelper.createLocationRequest(Constants.LocationConstants.INTERVAL,
                Constants.LocationConstants.FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(mContext).checkLocationSettings(builder.build());
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
                            requestLocationSetting(mContext, resolvable);
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
        mContext = context;
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
                if (!LocationHelper.isLocationEnabled(mContext)) {
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
