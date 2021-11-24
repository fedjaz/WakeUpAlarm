package com.fedjaz.wakeupalarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class QRFragment(var QRs: ArrayList<QR>, val isViewOnly: Boolean, val padding: Boolean = true) : Fragment() {

    private var columnCount = 1

    var onItemClick: ((Int, QR) -> Unit)? = null

    var onItemSelected: ((Int, Boolean) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list2, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = QRRecyclerViewAdapter(QRs, isViewOnly)
                (adapter as QRRecyclerViewAdapter).onItemClick = { position, QR ->
                    onItemClick?.invoke(position, QR)
                }
                (adapter as QRRecyclerViewAdapter).onItemSelected = { position, isChecked ->
                    onItemSelected?.invoke(position, isChecked)
                }

                if(!padding){
                    view.setPadding(0, 0, 0, 0)
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
        fun newInstance(QRs: ArrayList<QR>, isViewOnly: Boolean, padding: Boolean = true) =
                QRFragment(QRs, isViewOnly, padding).apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}