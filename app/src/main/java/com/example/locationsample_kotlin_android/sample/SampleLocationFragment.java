package com.example.locationsample_kotlin_android.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import com.example.locationsample_kotlin_android.R;
import com.example.locationsample_kotlin_android.location.BaseLocationFragment;
import com.example.locationsample_kotlin_android.location.LocationHelper;
import com.example.locationsample_kotlin_android.location.LocationLifecycleObserver;
import com.example.locationsample_kotlin_android.location.LocationStatus;
import com.example.locationsample_kotlin_android.sample.utils.UIUtils;

import java.util.Locale;

public class SampleLocationFragment extends BaseLocationFragment {
    public static final String TAG = "SampleLocationFragment";
    private Context mContext;

    private LocationSampleViewModel mViewModel;

    private TextView tvLocation;
    private LocationLifecycleObserver mLocationLifecycleObserver;

    static SampleLocationFragment newInstance() {
        return new SampleLocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_sample_fragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        tvLocation = view.findViewById(R.id.tvLocation);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mViewModel = ViewModelProviders.of(this).get(LocationSampleViewModel.class);

        checkLocationReady();
        tvLocation.setText("Retrieving ...");
    }

    @Override
    public void onLocationReady() {
        mLocationLifecycleObserver =
                new LocationLifecycleObserver(mContext, LocationHelper.Constants.MAX_LOCATION_REQUEST_TIME);
        getLifecycle().addObserver(mLocationLifecycleObserver);

//        mLocationLifecycleObserver.startNetworkLocationUpdates()
        mLocationLifecycleObserver.startFusedLocationUpdates(LocationHelper.Constants.INTERVAL, LocationHelper.Constants.FASTEST_INTERVAL)
                .observe(this, this::onLocationRetrieved);
    }

    @Override
    public void onLocationError(LocationHelper.LocationError locationError) {
        switch (locationError) {
            case SHOULD_SHOW_RATIONAL:
                UIUtils.showBasicDialog(mContext, null, getString(R.string.nearby_location_permission_message),
                        getString(R.string.grant_permission), getString(R.string.cancel),
                        this::onLocationPermissionDialogInteraction,
                        this::onLocationPermissionDialogInteraction)
                        .setOnCancelListener(dialog -> onLocationPermissionDialogInteraction(dialog, DialogInterface.BUTTON_NEGATIVE));
                break;
            case LOCATION_SETTING_DENIED:
                Toast.makeText(mContext, "Location setting denied", Toast.LENGTH_SHORT).show();
                break;
            case LOCATION_PERMISSION_DENIED:
                Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void onLocationPermissionDialogInteraction(DialogInterface dialogInterface, int which) {
        dialogInterface.dismiss();
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
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

    private void onLocationRetrieved(LocationStatus locationStatus) {
        switch (locationStatus.getStatus()) {
            case SUCCESS:
                Toast.makeText(mContext, "Location updated.", Toast.LENGTH_LONG).show();
                String latLng = String.format(Locale.ENGLISH,
                        "%f - %f",
                        locationStatus.getLocation().getLatitude(), locationStatus.getLocation().getLongitude());
                tvLocation.setText(latLng);
                break;
            case ERROR:
                Toast.makeText(mContext, "Location retrieval error.", Toast.LENGTH_SHORT).show();
                break;
            case PERMISSION_NOT_GRANTED:
                Toast.makeText(mContext, "Location permission not granted.", Toast.LENGTH_SHORT).show();
                checkLocationReady();
                break;
        }
    }

    @Override
    public void onDestroy() {
        mLocationLifecycleObserver.stopLocationUpdates();
        super.onDestroy();
    }
}
