package com.linkdev.easylocation.core.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Copyright (c) 2021 Link Development All rights reserved.
@Parcelize
internal data class EasyLocationRequest(
    var locationRequestTimeout: Long = EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT,
    var locationRequestType: LocationRequestType = LocationRequestType.UPDATES
) : Parcelable
