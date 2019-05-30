package com.example.locationsample_kotlin_android.location;

public interface LocationProvidersContract {

    void requestLocationUpdates();

    void stopLocationUpdates();

    void fetchLatestKnownLocation();
}
