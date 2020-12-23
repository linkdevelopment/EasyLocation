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

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.linkdev.easylocation.EasyLocation
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.*
import com.linkdev.easylocation.core.models.LocationResult
import com.linkdev.easylocationsample.R
import com.linkdev.easylocationsample.options.OnOptionsFragmentInteraction
import com.linkdev.easylocationsample.options.OptionsFragment
import com.linkdev.easylocationsample.samples.adapter.SamplesAdapter
import com.linkdev.easylocationsample.utils.Utils
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.location_sample_fragment.*

/**
 * This sample Fragment is sampling the use of [EasyLocation].
 */
class SampleEasyLocationFragment : Fragment(), OnOptionsFragmentInteraction {

    private lateinit var mContext: Context

    private lateinit var mEasyLocation: EasyLocation
    private lateinit var mOptionsFragment: OptionsFragment

    private lateinit var mAdapter: SamplesAdapter

    private lateinit var requestType: LocationRequestType
    private lateinit var locationOptions: LocationOptions
    private var maxRequestTime: Long = 0

    companion object {
        const val TAG = "EasyLocationSampleFragment"
        private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1
        private const val REQUEST_CODE_LOCATION_SETTINGS = 2000

        fun newInstance(): SampleEasyLocationFragment {
            return SampleEasyLocationFragment().apply {
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

        checkLocationPermissions(mContext, getString(R.string.locationPermissionIsRequired))
    }

    override fun onStopLocation() {
        stopLocation()
    }

    private fun requestLocation(
        requestType: LocationRequestType,
        locationOptions: LocationOptions,
        maxRequestTime: Long
    ) {
        mEasyLocation = EasyLocation.Builder(mContext, locationOptions)
            .setLocationRequestTimeout(maxRequestTime)
            .setLocationRequestType(requestType)
            .build()

        mEasyLocation.requestLocationUpdates(lifecycle)
            .observe(this@SampleEasyLocationFragment, this::onLocationStatusRetrieved)
    }

    private fun stopLocation() {
        if (::mEasyLocation.isInitialized)
            mEasyLocation.stopLocationUpdates()
    }

    private fun onLocationStatusRetrieved(locationResult: LocationResult) {
        when (locationResult.status) {
            Status.SUCCESS -> {
                onLocationRetrieved(locationResult.location!!)
            }
            Status.ERROR ->
                onLocationRetrievalError(
                    locationResult.locationResultError ?: LocationResultError.UnknownError()
                )
        }
    }

    private fun onLocationRetrieved(location: Location) {
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

    private fun onLocationRetrievalError(locationResultError: LocationResultError) {
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


    /**
     * TODO: The Next part is to handle the permissions and the settings.
     */

    /**
     * Called when both LocationPermission and locationSetting are granted.
     */
    private fun onLocationPermissionsReady() {
        requestLocation(requestType, locationOptions, maxRequestTime)
    }

    private fun onLocationPermissionError(locationResultError: LocationResultError) {
        onLocationRetrievalError(locationResultError)
    }

    private fun onLocationPermissionGranted() {
        checkLocationSettings(activity)
    }

    private fun onLocationPermissionDenied() {
        onLocationPermissionError(LocationResultError.PermissionDenied())
    }

    private fun onLocationSettingGranted() {
        onLocationPermissionsReady()
    }

    private fun onLocationSettingDenied() {
        onLocationPermissionError(LocationResultError.SettingDisabled())
    }

    //* Location Permission *//
    private fun checkLocationPermissions(context: Context?, rationaleDialogMessage: String) {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showLocationPermissionRationalDialog(rationaleDialogMessage)
            }
            checkLocationSelfPermission(context) -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }
            else -> {
                onLocationPermissionGranted()
            }
        }
    }

    private fun checkLocationSelfPermission(context: Context?): Boolean {
        return ActivityCompat.checkSelfPermission(
            mContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted()
            } else {
                onLocationPermissionDenied()
            }
        }
    }

    //* Location Setting *//
    private fun checkLocationSettings(context: Context?) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task =
            LocationServices.getSettingsClient(mContext).checkLocationSettings(builder.build())
        task.addOnCompleteListener { task1: Task<LocationSettingsResponse?> ->
            try { // All location settings are satisfied. The client can initialize location requests here.
                task1.getResult(ApiException::class.java)
                onLocationSettingGranted()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->  // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            val resolvable = exception as ResolvableApiException
                            requestLocationSetting(resolvable)
                        } catch (e: ClassCastException) {
                            e.printStackTrace()
                            onLocationSettingDenied()
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> onLocationSettingDenied()
                }
            }
        }
    }

    private fun requestLocationSetting(resolvable: ResolvableApiException) {
        try {
            startIntentSenderForResult(
                resolvable.resolution.intentSender,
                REQUEST_CODE_LOCATION_SETTINGS, null, 0, 0, 0, null
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
            onLocationSettingDenied()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                if (!isLocationEnabled(mContext)) {
                    onLocationSettingDenied()
                } else {
                    onLocationSettingGranted()
                }
            } else {
                onLocationSettingDenied()
            }
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun showLocationPermissionRationalDialog(rationaleDialogMessage: String) {
        Utils.showBasicDialog(
            context,
            null,
            rationaleDialogMessage,
            getString(com.linkdev.easylocation.R.string.easy_location_continue),
            getString(com.linkdev.easylocation.R.string.easy_location_cancel),
            this::onLocationPermissionDialogInteraction
        ).setOnCancelListener { dialogInterface ->
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
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                onLocationPermissionDenied()
            }
        }
    }
}
