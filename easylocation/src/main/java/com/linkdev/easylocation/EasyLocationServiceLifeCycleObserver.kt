package com.linkdev.easylocation

import android.content.Context
import androidx.lifecycle.Lifecycle

/**
 * LocationSampleKotlin_android Created by Mohammed.Fareed on 3/10/2020.
 * * // Copyright (c) 2020 LinkDev. All rights reserved.**/
class EasyLocationServiceLifeCycleObserver(lifecycle: Lifecycle, private val mContext: Context,
                                           private var mMaxLocationRequestTime: Long = EasyLocationConstants.DEFAULT_MAX_LOCATION_REQUEST_TIME,
                                           private var mSingleLocationRequest: Boolean = false) : EasyLocationLifeCycleObserver(lifecycle, mContext, mMaxLocationRequestTime, mSingleLocationRequest) {

}
