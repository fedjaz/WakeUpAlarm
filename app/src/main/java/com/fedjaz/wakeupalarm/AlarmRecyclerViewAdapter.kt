package com.fedjaz.wakeupalarm

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat


class AlarmRecyclerViewAdapter(
    private val values: List<Alarm>
) : RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder>() {

    var onItemEnabled: ((Int, Boolean) -> Unit)? = null
    var onItemClick: ((Int, Alarm) -> Unit)? = null
    var onItemSelected: ((Int, Boolean) -> Unit)? = null

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
        val enabledSwitch: SwitchCompat = view.findViewById(R.id.alarmSwitch)

        init {
//            val checkBox = view.findViewById<>()
            val switch = view.findViewById<SwitchCompat>(R.id.alarmSwitch)
            switch.setOnCheckedChangeListener { _, isChecked ->
                onItemEnabled?.invoke(adapterPosition, isChecked)
            }
            view.setOnClickListener {
                onItemClick?.invoke(adapterPosition, values[adapterPosition])
            }
        }

        override fun toString(): String {
            return "qq"
        }
    }
}