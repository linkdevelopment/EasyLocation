package com.linkdev.easylocationsample.options

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.linkdev.easylocation.core.location_providers.fused.options.DisplacementLocationOptions
import com.linkdev.easylocation.core.location_providers.fused.options.TimeLocationOptions
import com.linkdev.easylocation.core.models.LocationRequestType
import com.linkdev.easylocation.core.models.Priority
import com.linkdev.easylocationsample.R
import kotlinx.android.synthetic.main.fragment_location_options.*

class OptionsFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var mListener: OnOptionsFragmentInteraction

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_location_options,
            container,
            false
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnOptionsFragmentInteraction) {
            mListener = parentFragment as OnOptionsFragmentInteraction
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mContext = requireActivity()

        setListeners()
        initViews()
    }

    private fun setListeners() {
        btnLocate.setOnClickListener { onLocateClicked() }
        btnStopLocation.setOnClickListener { onStopLocation() }
        checkBoxFetchLastKnownLocation.setOnCheckedChangeListener { _, checked ->
            onFetchLastKnownLocationChecked(checked)
        }
    }

    private fun onStopLocation() {
        mListener.onStopLocation()

        btnLocate.visibility = View.VISIBLE
        btnStopLocation.visibility = View.GONE
    }

    private fun onLocateClicked() {
        getLocation()

        btnLocate.visibility = View.GONE
        btnStopLocation.visibility = View.VISIBLE
    }

    private fun getLocation() {
        val priority = getPriority()
        val fastestInterval = getFastestInterval()
        val locationRequestTimeout = getLocationRequestTimeout()

        val locationOptions =
            if (spOptions.selectedItemPosition == 0) {
                TimeLocationOptions(getInterval(), fastestInterval, priority)
            } else {
                DisplacementLocationOptions(getSmallestDisplacement(), fastestInterval, priority)
            }

        mListener.onLocateClicked(
            getRequestType(), locationOptions, locationRequestTimeout
        )
    }

    private fun initViews() {
        initLocationOptionsSpinner()
        initPrioritySpinner()
    }

    private fun initLocationOptionsSpinner() {
        val optionsList =
            arrayListOf("Interval", "Displacement")

        val arrayAdapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, optionsList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spOptions.adapter = arrayAdapter
        spOptions.onItemSelectedListener = onLocationOptionChecked()
    }

    private fun initPrioritySpinner() {
        val optionsList =
            arrayListOf("High accuracy", "Balanced power accuracy", "Low power", "No Power")

        val arrayAdapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, optionsList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spPriority.adapter = arrayAdapter
    }

    private fun getLocationRequestTimeout(): Long {
        return edtLocationRequestTimeout.text.toString().toLong()
    }

    private fun getSmallestDisplacement(): Float {
        return edtSmallestDisplacement.text.toString().toFloat()
    }

    private fun getInterval(): Long {
        return edtInterval.text.toString().toLong()
    }

    private fun getPriority(): Priority {
        return when (spPriority.selectedItemPosition) {
            0 -> Priority.PRIORITY_HIGH_ACCURACY
            1 -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            2 -> Priority.PRIORITY_LOW_POWER
            else -> Priority.PRIORITY_NO_POWER
        }
    }

    private fun getFastestInterval(): Long {
        return edtFastestInterval.text.toString().toLong()
    }

    private fun getRequestType(): LocationRequestType {
        return when {
            getIsOneTimeRequest() -> LocationRequestType.ONE_TIME_REQUEST
            checkBoxFetchLastKnownLocation.isChecked -> LocationRequestType.FETCH_LAST_KNOWN_LOCATION
            else -> LocationRequestType.UPDATES
        }
    }

    private fun onLocationOptionChecked(): AdapterView.OnItemSelectedListener? {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (position) {
                    0 -> {
                        tlInterval.visibility = View.VISIBLE
                        tlSmallestDisplacement.visibility = View.INVISIBLE
                    }
                    1 -> {
                        tlInterval.visibility = View.INVISIBLE
                        tlSmallestDisplacement.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun showLocateButton(show: Boolean) {
        btnLocate.post {
            btnLocate.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    fun showStopLocationButton(show: Boolean) {
        btnStopLocation.post {
            btnStopLocation.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun getIsOneTimeRequest(): Boolean {
        return checkBoxSingleRequest.isChecked
    }

    /**
     * Hide options on fetch last known location checked
     */
    private fun onFetchLastKnownLocationChecked(isChecked: Boolean) {
        groupFetchLastKnownLocation.visibility = if (isChecked) {
            tlInterval.visibility = View.GONE
            tlSmallestDisplacement.visibility = View.GONE

            View.GONE
        } else {
            if (spOptions.selectedItemPosition == 0) {
                tlInterval.visibility = View.VISIBLE
                tlSmallestDisplacement.visibility = View.INVISIBLE
            } else {
                tlInterval.visibility = View.INVISIBLE
                tlSmallestDisplacement.visibility = View.VISIBLE
            }

            View.VISIBLE
        }
    }
}
