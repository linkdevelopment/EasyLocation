package com.example.locationsample_kotlin_android.location;

import android.location.Location;

public interface LocationStatusListener {
    void onLocationRetrieved(Location location);

    void onLocationRetrieveError(LocationStatus locationStatus);
}
