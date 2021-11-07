package com.fedjaz.wakeupalarm

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

import com.fedjaz.wakeupalarm.dummy.DummyContent.DummyItem
import com.google.android.material.switchmaterial.SwitchMaterial

class MyAlarmInfoRecyclerViewAdapter(
    private val values: List<Alarm>
) : RecyclerView.Adapter<MyAlarmInfoRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_alarm_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.dateView.text = item.getDaysString()
        holder.timeDisplay.text = item.getTimeString()
        holder.enabledSwitch.isChecked = item.enabled
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView = view.findViewById(R.id.dateDisplay)
        val timeDisplay: TextView = view.findViewById(R.id.timeDisplay)
        val enabledSwitch: SwitchCompat = view.findViewById(R.id.enabled)

        override fun toString(): String {
            return "qq"
        }
    }
}