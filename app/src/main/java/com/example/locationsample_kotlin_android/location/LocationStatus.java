package com.example.locationsample_kotlin_android.location;

import android.location.Location;
import androidx.annotation.NonNull;

/**
 * Created by Sherif.ElNady on 10/2/2018.
 */

public class LocationStatus {

    private Location location;
    private Status status;

    private LocationStatus(Status status) {
        this.status = status;
    }

    private LocationStatus(Status status, Location location) {
        this.location = location;
        this.status = status;
    }

    ////// exposed Constructors
    @NonNull
    public static LocationStatus locationPermissionNotGranted() {
        return new LocationStatus(Status.PERMISSION_NOT_GRANTED);
    }

    @NonNull
    public static LocationStatus error() {
        return new LocationStatus(Status.ERROR);
    }

    @NonNull
    public static LocationStatus success(Location location) {
        return new LocationStatus(Status.SUCCESS, location);
    }

    ///// Getters
    public Location getLocation() {
        return location;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        SUCCESS,
        ERROR,
        PERMISSION_NOT_GRANTED
    }
}