package com.fedjaz.wakeupalarm

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView


class QRRecyclerViewAdapter(
        private val values: List<QR>)
    : RecyclerView.Adapter<QRRecyclerViewAdapter.ViewHolder>() {

    private var disableEvents = false
    var onItemClick: ((Int, QR) -> Unit)? = null
    var onItemSelected: ((Int, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_qr_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.nameView.text = item.getQrName()
        holder.locationView.text = item.location

        disableEvents = true
        holder.qrCheckBox.isChecked = values[position].checked
        disableEvents = false

        if(!item.isImageCreated){
            item.createImage()
        }
        holder.qrImage.setImageBitmap(item.bitmap)
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.qrName)
        val locationView: TextView = view.findViewById(R.id.qrLocation)
        val qrCheckBox: CheckBox = view.findViewById(R.id.qrCheckBox)
        val qrImage: ImageView = view.findViewById(R.id.qrImageView)

        init {
            view.setOnClickListener {
                onItemClick?.invoke(adapterPosition, values[adapterPosition])
            }
            val qrCheckBox = view.findViewById<CheckBox>(R.id.qrCheckBox)
            qrCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if(!disableEvents){
                    values[adapterPosition].checked = isChecked
                    onItemSelected?.invoke(adapterPosition, isChecked)
                }

            }
        }

        override fun toString(): String {
            return "qq"
        }
    }
}