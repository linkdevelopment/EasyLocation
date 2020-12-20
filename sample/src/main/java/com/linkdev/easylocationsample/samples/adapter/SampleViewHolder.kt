package com.linkdev.easylocationsample.samples.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_sample_location.view.*

class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val txtLocation: TextView = itemView.txtLocation
}
