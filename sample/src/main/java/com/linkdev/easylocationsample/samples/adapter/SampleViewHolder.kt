package com.linkdev.easylocationsample.samples.adapter

import android.location.Location
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linkdev.easylocationsample.utils.Utils
import kotlinx.android.synthetic.main.item_sample_location.view.*
import java.util.*

class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val txtLocation: TextView = itemView.txtLocation

    fun bind(location: Location) {
        val latLng = String.format(
            Locale.ENGLISH, "%f - %f",
            location.latitude, location.longitude
        )

        txtLocation.text = String.format(
            Locale.ENGLISH, "%s / %s",
            latLng, Utils.getCurrentTime()
        )
    }
}
