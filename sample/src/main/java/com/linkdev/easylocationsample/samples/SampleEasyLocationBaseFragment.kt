/**
 * Copyright (c) 2020-present Link Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkdev.easylocationsample.samples

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.linkdev.easylocation.EasyLocationBaseFragment
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.LocationErrorCode
import com.linkdev.easylocation.core.models.LocationRequestType
import com.linkdev.easylocation.core.models.LocationResultError
import com.linkdev.easylocationsample.R
import com.linkdev.easylocationsample.options.OnOptionsFragmentInteraction
import com.linkdev.easylocationsample.options.OptionsFragment
import com.linkdev.easylocationsample.samples.adapter.SamplesAdapter
import com.linkdev.easylocationsample.utils.Utils
import kotlinx.android.synthetic.main.location_sample_fragment.*

/**
 * This sample Fragment is sampling the use of [EasyLocationBaseFragment].
 */
class SampleEasyLocationBaseFragment : EasyLocationBaseFragment(), OnOptionsFragmentInteraction {

    private lateinit var mContext: Context
    private lateinit var mOptionsFragment: OptionsFragment

    private lateinit var mAdapter: SamplesAdapter

    private lateinit var requestType: LocationRequestType
    private lateinit var locationOptions: LocationOptions
    private var maxRequestTime: Long = 0

    companion object {
        const val TAG = "EasyLocationBaseSampleFragment"

        fun newInstance(): SampleEasyLocationBaseFragment {
            return SampleEasyLocationBaseFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.location_sample_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null)
            mContext = requireActivity()

        mOptionsFragment =
            childFragmentManager.findFragmentById(R.id.optionsFragment) as OptionsFragment

        initAdapter()
    }

    private fun initAdapter() {
        mAdapter = SamplesAdapter(mContext, arrayListOf())

        rvLocation.layoutManager = LinearLayoutManager(mContext)
        rvLocation.adapter = mAdapter
    }

    override fun onLocateClicked(
        requestType: LocationRequestType,
        locationOptions: LocationOptions,
        maxRequestTime: Long
    ) {
        this.requestType = requestType
        this.locationOptions = locationOptions
        this.maxRequestTime = maxRequestTime

        mAdapter.clear()

        requestLocation(requestType, locationOptions, maxRequestTime)
    }

    override fun onStopLocation() {
        stopLocation()
    }

    private fun requestLocation(
        requestType: LocationRequestType,
        locationOptions: LocationOptions,
        maxRequestTime: Long
    ) {
        getLocation(
            locationOptions,
            requestType,
            maxRequestTime
        )
    }

    override fun onLocationRetrieved(location: Location) {
        if (requestType == LocationRequestType.ONE_TIME_REQUEST || requestType == LocationRequestType.FETCH_LAST_KNOWN_LOCATION) {
            mOptionsFragment.showLocateButton(true)
            mOptionsFragment.showStopLocationButton(false)
        }

        mAdapter.addItem(location)

        // Scroll to the first item only
        if (mAdapter.mData.size == 1)
            scrlLocation.postDelayed({
                scrlLocation.fullScroll(View.FOCUS_DOWN)
            }, 200)
    }

    override fun onLocationRetrievalError(locationResultError: LocationResultError) {
        mOptionsFragment.showLocateButton(true)
        mOptionsFragment.showStopLocationButton(false)

        when (locationResultError.errorCode) {
            LocationErrorCode.LOCATION_SETTING_DENIED,
            LocationErrorCode.LOCATION_PERMISSION_DENIED,
            LocationErrorCode.UNKNOWN_ERROR,
            LocationErrorCode.TIME_OUT ->
                Toast.makeText(mContext, locationResultError.errorMessage, Toast.LENGTH_LONG)
                    .show()
            LocationErrorCode.PROVIDER_EXCEPTION ->
                Toast.makeText(mContext, locationResultError.exception?.message, Toast.LENGTH_LONG)
                    .show()
        }
    }
}
