package com.linkdev.easylocation.core.utils

import android.os.Handler
import android.os.Looper

/**
 * Handles the timeout handler and exposes the main functionalities.
 */
internal class EasyLocationTimeoutHelper(
    private val mLocationRequestTimeout: Long,
    private val mOnTimedOutRunnable: Runnable
) {

    /**
     * Handler for request timeout
     */
    private var mTimeoutHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * Starts a timer to stop the request of the location updates after [mLocationRequestTimeout] seconds.
     */
    fun startLocationRequestTimer() {
        mTimeoutHandler.postDelayed(mOnTimedOutRunnable, mLocationRequestTimeout)
    }

    /**
     * Restarts the location timer [mOnTimedOutRunnable]
     */
    fun restartTimer() {
        mTimeoutHandler.removeCallbacks(mOnTimedOutRunnable)
        mTimeoutHandler.postDelayed(mOnTimedOutRunnable, mLocationRequestTimeout)
    }

    fun stop() {
        mTimeoutHandler.removeCallbacks(mOnTimedOutRunnable)
    }
}
