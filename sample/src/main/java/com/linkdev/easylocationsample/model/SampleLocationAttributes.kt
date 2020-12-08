package com.linkdev.easylocationsample.model

import android.os.Parcelable
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.LocationRequestType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SampleLocationAttributes(
    val requestType: LocationRequestType,
    val locationOptions: LocationOptions,
    val maxRequestTime: Long
) : Parcelable
