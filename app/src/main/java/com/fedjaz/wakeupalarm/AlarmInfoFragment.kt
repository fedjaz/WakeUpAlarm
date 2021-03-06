package com.fedjaz.wakeupalarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A fragment representing a list of Items.
 */
class AlarmInfoFragment(var alarms: ArrayList<Alarm>) : Fragment() {

    private var columnCount = 1

    var onItemEnabled: ((Int, Boolean) -> Unit)? = null
    var onItemClick: ((Int, Alarm) -> Unit)? = null
    var onItemSelected: ((Int, Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = AlarmRecyclerViewAdapter(alarms)
                (adapter as AlarmRecyclerViewAdapter).onItemClick = {position, alarm ->
                    onItemClick?.invoke(position, alarm)
                }
                (adapter as AlarmRecyclerViewAdapter).onItemEnabled = {position, enabled ->
                    onItemEnabled?.invoke(position, enabled)
                }
                (adapter as AlarmRecyclerViewAdapter).onItemSelected = {position, selected ->
                    onItemSelected?.invoke(position, selected)
                }
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int, alarms: ArrayList<Alarm>) =
            AlarmInfoFragment(alarms).apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}