package com.linkdev.easylocation.location_providers

import android.location.Location

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
sealed class LocationResult(val status: Status, val location: Location? = null) {

    class Success(location: Location) : LocationResult(Status.SUCCESS, location)

    class LocationPermissionNotGranted() : LocationResult(Status.PERMISSION_NOT_GRANTED)

    class Error() : LocationResult(Status.ERROR)
}
