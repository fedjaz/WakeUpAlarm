package com.fedjaz.wakeupalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View.*

class AlarmCreate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_activity_alarm)

        val extras = intent.extras
        val qrs: ArrayList<QR> = extras?.get("qrs") as ArrayList<QR>
        for(qr in qrs){
            qr.createImage()
        }

        val qrsFragment = QRFragment.newInstance(1, qrs)

        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.qrLayout, qrsFragment, "tag")
        transaction.commit()

        val timePicker = findViewById<TimePicker>(R.id.alarmTimePicker)
        timePicker.setIs24HourView(true)

        val radioGroup = findViewById<RadioGroup>(R.id.repeatRadioGroup)
        val daysLayout = findViewById<ConstraintLayout>(R.id.daysLayout)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.selectedDaysRadioButton){
                daysLayout.visibility = VISIBLE
            }
            else{
                daysLayout.visibility = GONE
            }
        }
    }
}