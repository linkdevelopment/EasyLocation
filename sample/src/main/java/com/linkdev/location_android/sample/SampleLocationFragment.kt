package com.linkdev.location_android.sample

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.linkdev.easylocation.EasyLocationBaseFragment
import com.linkdev.easylocation.location_providers.LocationError
import com.linkdev.easylocation.location_providers.LocationProvidersTypes
import com.linkdev.easylocation.location_providers.location_manager.DisplacementLocationManagerOptions
import com.linkdev.location_android.R
import com.linkdev.location_android.sample.utils.UIUtils
import kotlinx.android.synthetic.main.location_sample_fragment.*
import java.util.*

class SampleLocationFragment : EasyLocationBaseFragment() {

    private lateinit var mContext: Context

    companion object {
        const val TAG = "SampleLocationFragment"

        fun newInstance(): SampleLocationFragment {
            return SampleLocationFragment()
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
            mContext = activity!!

        checkLocationReady(
            LocationProvidersTypes.LOCATION_MANAGER_LOCATION_PROVIDER,
            DisplacementLocationManagerOptions(),
            false
        )
        tvLocation.text = "Retrieving ..."
    }

    override fun onLocationRetrieved(location: Location) {
        Toast.makeText(context!!, "Location updated.", Toast.LENGTH_LONG).show()
        val latLng = String.format(
            Locale.ENGLISH, "%f - %f",
            location.latitude, location.longitude
        )
        Log.d("LocationUpdated", "Location updated $latLng")
        tvLocation.text = latLng
    }

    override fun onLocationRetrievalError(locationError: LocationError) {
        when (locationError) {
            LocationError.SHOULD_SHOW_RATIONAL -> {
                val alertDialog = UIUtils.showBasicDialog(
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
            LocationError.LOCATION_SETTING_DENIED ->
                Toast.makeText(mContext, "Location setting denied", Toast.LENGTH_SHORT).show()
            LocationError.LOCATION_PERMISSION_DENIED ->
                Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show()
            LocationError.LOCATION_ERROR ->
                Toast.makeText(
                    mContext,
                    "Something went wrong and the location returned as null",
                    Toast.LENGTH_SHORT
                ).show()
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
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
