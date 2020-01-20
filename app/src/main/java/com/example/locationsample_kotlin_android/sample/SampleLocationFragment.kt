package com.example.locationsample_kotlin_android.sample

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.locationsample_kotlin_android.R
import com.example.locationsample_kotlin_android.location.*
import com.example.locationsample_kotlin_android.location.location_providers.LocationProvidersTypes
import com.example.locationsample_kotlin_android.location.location_providers.fused.FusedLocationOptions
import com.example.locationsample_kotlin_android.sample.utils.UIUtils
import kotlinx.android.synthetic.main.location_sample_fragment.*
import java.util.*

class SampleLocationFragment : BaseLocationFragment() {

    private lateinit var mContext: Context
//    private lateinit var mLocationObserver: LocationObserver

    companion object {
        const val TAG = "SampleLocationFragment"
        @JvmStatic // todo remove
        fun newInstance(): SampleLocationFragment {
            return SampleLocationFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.location_sample_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null)
            mContext = activity!!

        checkLocationReady()
        tvLocation.text = "Retrieving ..."
    }

    override fun onLocationReady() {
        LocationObserver(lifecycle, mContext, Constants.MAX_LOCATION_REQUEST_TIME, true)
                .requestLocationUpdates(LocationProvidersTypes.FUSED_LOCATION_PROVIDER, FusedLocationOptions())
                .observe(this, Observer { onLocationRetrieved(it) })

//        mLocationObserver.startNetworkLocationUpdates()
//                .observe(this, Observer { onLocationRetrieved(it) })
//        mLocationObserver.startFusedLocationUpdates(Constants.INTERVAL, Constants.FASTEST_INTERVAL)
//                .observe(this, Observer { onLocationRetrieved(it) })
    }

    override fun onLocationReadyError(locationError: LocationError) {
        when (locationError) {
            LocationError.SHOULD_SHOW_RATIONAL ->
                UIUtils.showBasicDialog(mContext, null, getString(R.string.nearby_location_permission_message),
                        getString(R.string.grant_permission), getString(R.string.cancel),
                        { dialogInterface: DialogInterface, which: Int ->
                            onLocationPermissionDialogInteraction(dialogInterface, which)
                        }, { dialogInterface: DialogInterface, which: Int ->
                    onLocationPermissionDialogInteraction(dialogInterface, which)
                })
                        .setOnCancelListener { dialog: DialogInterface ->
                            onLocationPermissionDialogInteraction(dialog, DialogInterface.BUTTON_NEGATIVE)
                        }
            LocationError.LOCATION_SETTING_DENIED ->
                Toast.makeText(mContext, "Location setting denied", Toast.LENGTH_SHORT).show()
            LocationError.LOCATION_PERMISSION_DENIED ->
                Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onLocationPermissionDialogInteraction(dialogInterface: DialogInterface, which: Int) {
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
                onLocationPermissionDenied()
            }
            else -> {
                onLocationPermissionDenied()
            }
        }
    }

    private fun onLocationRetrieved(locationStatus: LocationStatus) {
        when (locationStatus.status) {
            Status.SUCCESS -> {
                Toast.makeText(mContext, "Location updated.", Toast.LENGTH_LONG).show()
                val latLng = String.format(
                        Locale.ENGLISH,
                        "%f - %f",
                        locationStatus.location?.latitude,
                        locationStatus.location?.longitude
                )
                tvLocation!!.text = latLng
            }
            Status.ERROR ->
                Toast.makeText(mContext, "Location retrieval error.", Toast.LENGTH_SHORT).show()
            Status.PERMISSION_NOT_GRANTED -> {
                Toast.makeText(mContext, "Location permission not granted.", Toast.LENGTH_SHORT)
                        .show()
                checkLocationReady()
            }
        }
    }
}
