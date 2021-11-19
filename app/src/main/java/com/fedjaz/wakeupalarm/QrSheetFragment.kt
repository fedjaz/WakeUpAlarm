package com.fedjaz.wakeupalarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "qr"

class QrSheetFragment : BottomSheetDialogFragment() {
    private var editQr: QR? = null

    var created: ((QR) -> Unit)? = null
    var edited: ((QR) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            editQr = it.getSerializable(ARG_PARAM1) as QR?
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

        if(editQr != null){
            nameEditText.setText(editQr!!.name)
            locationEditText.setText(editQr!!.location)
            numberEditText.setText(editQr!!.number.toString())
            view.findViewById<Button>(R.id.saveQrButton).setOnClickListener {
                editQr!!.name = nameEditText.text.toString()
                editQr!!.location = locationEditText.text.toString()
                editQr!!.number = numberEditText.text.toString().toInt()

                edited?.invoke(editQr!!)
                dismiss()
            }
        }
        else{
            view.findViewById<Button>(R.id.saveQrButton).setOnClickListener {
                val newName = nameEditText.text.toString()
                val newLocation = locationEditText.text.toString()
                val newNumber = numberEditText.text.toString().toInt()
                val newQr = QR(-1, newName, newNumber, newLocation)

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
        fun newInstance(editQr: QR? = null) =
            QrSheetFragment().apply {
                arguments = Bundle().apply {
                    if(editQr != null){
                        putSerializable(ARG_PARAM1, editQr)
                    }
                }
            }
    }
}