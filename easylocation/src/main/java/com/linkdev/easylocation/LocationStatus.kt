package com.linkdev.easylocation

import android.location.Location

/**
 * Created by Mohammed Fareed on 30/5/2019.
 */
sealed class LocationStatus(val status: Status, val location: Location? = null) {

    class LocationPermissionNotGranted() : LocationStatus(Status.PERMISSION_NOT_GRANTED)

    class Error() : LocationStatus(Status.ERROR)

    class Success(location: Location) : LocationStatus(Status.SUCCESS, location)
}
