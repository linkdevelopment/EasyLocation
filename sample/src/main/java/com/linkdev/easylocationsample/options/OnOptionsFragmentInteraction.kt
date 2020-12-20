package com.linkdev.easylocationsample.options

import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.LocationRequestType

interface OnOptionsFragmentInteraction {

    fun onLocateClicked(
        requestType: LocationRequestType,
        locationOptions: LocationOptions,
        maxRequestTime: Long,
        fetchLastKnownLocation: Boolean
    )

    fun onStopLocation()
}
