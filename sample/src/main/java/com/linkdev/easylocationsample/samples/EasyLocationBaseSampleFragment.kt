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
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.linkdev.easylocationsample.R
import com.linkdev.easylocationsample.model.SampleLocationAttributes
import com.linkdev.easylocationsample.utils.Constants
import com.linkdev.easylocationsample.utils.Utils
import com.linkdev.easylocation.EasyLocationBaseFragment
import com.linkdev.easylocation.core.models.LocationErrorCode
import com.linkdev.easylocation.core.models.LocationResultError
import com.linkdev.easylocationsample.OptionsFragment
import kotlinx.android.synthetic.main.location_sample_fragment.*

/**
 * This sample Fragment is sampling the use of [EasyLocationBaseFragment].
 */
class EasyLocationBaseSampleFragment : EasyLocationBaseFragment(), OptionsFragment.OnOptionsFragmentInteraction {

    private lateinit var mContext: Context

    companion object {
        const val TAG = "EasyLocationBaseSampleFragment"

        fun newInstance(): EasyLocationBaseSampleFragment {
            return EasyLocationBaseSampleFragment().apply {
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
    }

    override fun onLocateClicked(locationAttributes: SampleLocationAttributes) {
        requestLocation(locationAttributes)
    }

    override fun onStopLocation() {
        stopLocation()

        txtLocation.text = getString(R.string.location_placeholder)
    }

    private fun requestLocation(sampleLocationAttributes: SampleLocationAttributes) {
        getLocation(
            sampleLocationAttributes.locationOptions,
            sampleLocationAttributes.requestType,
            sampleLocationAttributes.maxRequestTime
        )
    }

    override fun onLocationRetrieved(location: Location) {
        Utils.setLocationText(location, txtLocation)
        scrlLocation.fullScroll(View.FOCUS_DOWN)
    }

    override fun onLocationRetrievalError(locationResultError: LocationResultError) {
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

    override fun showLocationPermissionRationalDialog() {
        val alertDialog = Utils.showBasicDialog(
            mContext, null, getString(R.string.nearby_location_permission_message),
            getString(R.string.grant_permission), getString(R.string.cancel),
            this::onLocationPermissionDialogInteraction
        )
        alertDialog.setOnCancelListener { dialogInterface ->
            onLocationPermissionDialogInteraction(
                dialogInterface,
                DialogInterface.BUTTON_NEGATIVE
            )
        }
    }

    private fun onLocationPermissionDialogInteraction(
        dialogInterface: DialogInterface,
        which: Int
    ) {
        dialogInterface.dismiss()
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri =
                    Uri.fromParts("package", mContext.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                Toast.makeText(
                    mContext,
                    "You will not be able to use this feature. ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}
