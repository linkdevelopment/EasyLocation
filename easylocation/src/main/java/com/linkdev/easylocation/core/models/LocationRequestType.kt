package com.linkdev.easylocation.core.models

enum class LocationRequestType {
    /**
     * Return the location only once and then auto cancel the location updates
     *
     * Use if you need to know the current location.
     */
    ONE_TIME_REQUEST,

    /**
     * Return the last known location of the device
     */
    FETCH_LAST_KNOWN_LOCATION,

    /**
     * If should continue returning every update of the location updates.
     */
    UPDATES;
}
