package com.linkdev.easylocation.core.models

enum class LocationRequestType {
    /**
     * If should return the location only once and then auto cancel the location updates
     */
    ONE_TIME_REQUEST,

    /**
     * If should continue returning every update of the location updates.
     */
    UPDATES;
}
