package com.example.locationsample_kotlin_android.location.location_providers.network

import com.example.locationsample_kotlin_android.location.Constants
import com.example.locationsample_kotlin_android.location.location_providers.LocationOptions

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 1/15/2020.
 * * // Copyright (c) 2020 Link. All rights reserved.**/
class NetworkLocationOptions(val minTime: Long = Constants.MIN_TIME, val minDistance: Float = Constants.MIN_DISTANCE) : LocationOptions