package com.fedjaz.wakeupalarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.FieldPosition

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "position"
private const val ARG_PARAM2 = "qrId"
private const val ARG_PARAM3 = "name"
private const val ARG_PARAM4 = "location"
private const val ARG_PARAM5 = "number"

class QrSheetFragment : BottomSheetDialogFragment() {
    private var position: Int? = null
    private var qrId: Int? = null
    private var name: String? = null
    private var location: String? = null
    private var number: Int? = null

    var created: ((QR) -> Unit)? = null
    var edited: ((Int, QR) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_PARAM1)
            qrId = it.getInt(ARG_PARAM2)
            name = it.getString(ARG_PARAM3)
            location = it.getString(ARG_PARAM4)
            number = it.getInt(ARG_PARAM5)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.qr_sheet, container, false)
        val nameEditText = view.findViewById<EditText>(R.id.qrNameEditText)
        val locationEditText = view.findViewById<EditText>(R.id.qrLocationEditText)
        val numberEditText = view.findViewById<EditText>(R.id.qrNumberEditText)

        if(qrId != 0){
            nameEditText.setText(name)
            locationEditText.setText(location)
            numberEditText.setText(number.toString())
            view.findViewById<Button>(R.id.saveQrButton).setOnClickListener {
                val newName = nameEditText.text.toString()
                val newLocation = locationEditText.text.toString()
                val newNumber = numberEditText.text.toString().toInt()
                val newQr = QR(this.qrId!!, newName, newNumber, newLocation)

                edited?.invoke(position!!, newQr)
                dismiss()
            }
        }
        else{
            view.findViewById<Button>(R.id.saveQrButton).setOnClickListener {
                val newName = nameEditText.text.toString()
                val newLocation = locationEditText.text.toString()
                val newNumber = numberEditText.text.toString().toInt()
                val newQr = QR(0, newName, newNumber, newLocation)

                created?.invoke(newQr)
                dismiss()
            }
        }


        return view
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int? = null, qrId: Int? = null, name: String? = null, location: String? = null, number: Int? = null) =
            QrSheetFragment().apply {
                arguments = Bundle().apply {
                    if(position != null){
                        putInt(ARG_PARAM1, position)
                    }
                    if(qrId != null){
                        putInt(ARG_PARAM2, qrId)
                    }
                    if(name != null){
                        putString(ARG_PARAM3, name)
                    }
                    if(location != null){
                        putString(ARG_PARAM4, location)
                    }
                    if(number != null){
                        putInt(ARG_PARAM5, number)
                    }
                }
            }
    }
}