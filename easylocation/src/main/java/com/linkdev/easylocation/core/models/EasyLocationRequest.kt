package com.linkdev.easylocation.core.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Used internally to pass the location arguments between core layers.
 */
@Parcelize
internal data class EasyLocationRequest(
    var locationRequestTimeout: Long = EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT,
    var locationRequestType: LocationRequestType = LocationRequestType.UPDATES
) : Parcelable
