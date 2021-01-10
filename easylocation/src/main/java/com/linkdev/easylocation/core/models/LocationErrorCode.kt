package com.linkdev.easylocation.core.models

enum class LocationErrorCode {
    /**
     * Returned when the location request times out.
     */
    TIME_OUT,

    /**
     * Returned When there is an unknown error from the provider.
     */
    UNKNOWN_ERROR,

    /**
     * Returned When the permission is not granted, You should validate the location beforehand or use [EasyLocationBaseFragment].
     */
    LOCATION_PERMISSION_DENIED,

    /**
     * Returned When the permission settings is off, You should validate the location beforehand or use [EasyLocationBaseFragment].
     */
    LOCATION_SETTING_DENIED,

    /**
     * Returned When there is a known error from the provider returned with the exception.
     */
    PROVIDER_EXCEPTION,
}
