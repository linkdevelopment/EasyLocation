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
package com.linkdev.easy_location_sample.samples

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.linkdev.easy_location_sample.R
import com.linkdev.easy_location_sample.model.SampleLocationAttributes
import com.linkdev.easy_location_sample.utils.Constants
import com.linkdev.easy_location_sample.utils.Utils
import com.linkdev.easylocation.EasyLocation
import com.linkdev.easylocation.core.models.LocationError
import com.linkdev.easylocation.core.models.LocationResult
import com.linkdev.easylocation.core.models.Status
import kotlinx.android.synthetic.main.location_sample_fragment.*

/**
 * This sample Fragment is sampling the use of [EasyLocation].
 */
class EasyLocationSampleFragment : Fragment() {

    private lateinit var mContext: Context

    private lateinit var mEasyLocation: EasyLocation

    companion object {
        const val TAG = "EasyLocationSampleFragment"

        fun newInstance(sampleLocationAttributes: SampleLocationAttributes): EasyLocationSampleFragment {
            return EasyLocationSampleFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SAMPLE_LOCATION_ATTRIBUTES, sampleLocationAttributes)
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

        requestLocation(arguments?.get(Constants.SAMPLE_LOCATION_ATTRIBUTES) as SampleLocationAttributes)
    }

    private fun requestLocation(sampleLocationAttributes: SampleLocationAttributes) {
        mEasyLocation = EasyLocation.Builder(mContext, sampleLocationAttributes.locationOptions)
            .setMaxLocationRequestTime(sampleLocationAttributes.maxRequestTime)
            .setLocationRequestType(sampleLocationAttributes.requestType)
            .build()

        mEasyLocation.requestLocationUpdates(lifecycle)
            .observe(this@EasyLocationSampleFragment, this::onLocationStatusRetrieved)
    }

    private fun onLocationStatusRetrieved(locationResult: LocationResult) {
        when (locationResult.status) {
            Status.SUCCESS -> {
                if (locationResult.location == null) {
                    onLocationRetrievalError(LocationError.LOCATION_ERROR)
                    return
                }
                onLocationRetrieved(locationResult.location!!)
            }
            Status.UNKNOWN_ERROR ->
                onLocationRetrievalError(LocationError.LOCATION_ERROR)
            Status.PERMISSION_NOT_GRANTED -> {
                onLocationRetrievalError(LocationError.LOCATION_PERMISSION_DENIED)
            }
        }
    }

    private fun onLocationRetrieved(location: Location) {
        val string = Utils.getLocationString(location)
        txtLocation.append(string)
        scrlLocation.fullScroll(View.FOCUS_DOWN)
    }

    private fun onLocationRetrievalError(locationError: LocationError) {
        when (locationError) {
            LocationError.LOCATION_SETTING_DENIED ->
                Toast.makeText(mContext, "Needs Location setting", Toast.LENGTH_SHORT).show()
            LocationError.LOCATION_PERMISSION_DENIED ->
                Toast.makeText(mContext, "Needs Location permission", Toast.LENGTH_SHORT)
                    .show()
            LocationError.LOCATION_ERROR ->
                Toast.makeText(
                    mContext,
                    "Something went wrong and the location returned as null",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }
}
