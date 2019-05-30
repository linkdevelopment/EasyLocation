package com.example.locationsample_kotlin_android.location;

import android.location.Location;

public interface LocationStatusListener {
    void onLocationSuccess(Location location);

    void onLocationError(LocationStatus locationStatus);
}