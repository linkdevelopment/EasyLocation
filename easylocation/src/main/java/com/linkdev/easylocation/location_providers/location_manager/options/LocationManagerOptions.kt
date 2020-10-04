package com.linkdev.easylocation.location_providers.location_manager.options

import android.location.Criteria
import com.linkdev.easylocation.location_providers.LocationOptions

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 3/5/2020.
 * // Copyright (c) 2020 LinkDev. All rights reserved.
 *
 * @param locationManagerProvider One of [LocationManagerProviderTypes], There are multiple sensors in the device
 * to determine device location using the Location manger framework you can specify which specific provider to use.
 * @param criteria Should be applied in case of [LocationManagerProviderTypes.CRITERIA_BASED] only, otherwise the value will be ignored.
 * **/
open class LocationManagerOptions(
        open val locationManagerProvider: LocationManagerProviderTypes,
        open val criteria: Criteria? = null
) : LocationOptions
