package com.fedjaz.wakeupalarm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View.*
import android.widget.Button
import android.widget.CheckBox

class AlarmCreate : AppCompatActivity() {
    private val daily = arrayListOf(true, true, true, true, true, true, true)
    private val workDays = arrayListOf(true, true, true, true, true, false, false)
    private val once = arrayListOf(false, false, false, false, false, false, false)
    private var qrs: ArrayList<QR> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_alarm)

        val extras = intent.extras
        qrs = extras?.get("qrs") as ArrayList<QR>
        val editAlarm: Alarm? = extras.get("alarm") as Alarm?
        for(qr in qrs){
            qr.createImage()
        }

        val timePicker = findViewById<TimePicker>(R.id.alarmTimePicker)
        timePicker.setIs24HourView(true)

        if(editAlarm != null){
            timePicker.hour = editAlarm.hour
            timePicker.minute = editAlarm.minute
            for(qr in qrs){
                if(qr.id in editAlarm.qrIds){
                    qr.checked = true
                }
            }
        }

        val qrsFragment = QRFragment.newInstance(1, qrs)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.qrLayout, qrsFragment, "tag")
        transaction.commit()

        val radioGroup = findViewById<RadioGroup>(R.id.repeatRadioGroup)
        val daysLayout = findViewById<ConstraintLayout>(R.id.daysLayout)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId == R.id.selectedDaysRadioButton){
                daysLayout.visibility = VISIBLE
            }
            else{
                daysLayout.visibility = GONE
            }
        }

        val saveButton = findViewById<Button>(R.id.saveAlarmButton)
        saveButton.setOnClickListener {
            saveAndExit(editAlarm)
        }

    }

    private fun saveAndExit(editAlarm: Alarm?){
        val timePicker = findViewById<TimePicker>(R.id.alarmTimePicker)
        val radioGroup = findViewById<RadioGroup>(R.id.repeatRadioGroup)

        val hour = timePicker.hour
        val minute = timePicker.minute

        val checkedQrs = arrayListOf<Int>()
        for(qr in qrs){
            if(qr.checked){
                checkedQrs.add(qr.id)
            }
        }

        val days = when (radioGroup.checkedRadioButtonId) {
            R.id.dailyRadioButton -> {
                daily
            }
            R.id.workDaysRadioButton -> {
                workDays
            }
            R.id.onceRadioButton -> {
                once
            }
            else -> {
                val monday = findViewById<CheckBox>(R.id.mondayCheckBox)
                val tuesday = findViewById<CheckBox>(R.id.tuesdayCheckBox)
                val wednesday = findViewById<CheckBox>(R.id.wednesdayCheckBox)
                val thursday = findViewById<CheckBox>(R.id.thursdayCheckBox)
                val friday = findViewById<CheckBox>(R.id.fridayCheckBox)
                val saturday = findViewById<CheckBox>(R.id.saturdayCheckBox)
                val sunday = findViewById<CheckBox>(R.id.sundayCheckBox)
                val daysBoxes = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
                val days = once
                for(i in 0..6){
                    days[i] = daysBoxes[i].isChecked
                }
                days
            }
        }
        val returnAlarm = if(editAlarm != null){
            editAlarm.hour = hour
            editAlarm.minute = minute
            editAlarm.qrIds = checkedQrs
            editAlarm.days = days
            editAlarm
        }
        else{
            val alarm = Alarm(0, hour, minute, true, days)
            alarm.qrIds = checkedQrs
            alarm
        }

        val data = Intent()
        data.putExtra("alarm", returnAlarm)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun checkForEquality(arr1: ArrayList<Boolean>, arr2: ArrayList<Boolean>){

    }
}