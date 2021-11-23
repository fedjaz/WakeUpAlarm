package com.fedjaz.wakeupalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.*

class AlarmScheduler(val context: Context) {

    fun schedule(alarm: Alarm){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        intent.putExtra("alarmId", alarm.id)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour)
        calendar.set(Calendar.MINUTE, alarm.minute)
        calendar.set(Calendar.SECOND, 0)

        if(calendar.timeInMillis <= System.currentTimeMillis()){
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmPendingIntent)
    }

    fun cancel(alarm: Alarm){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        intent.putExtra("alarm", alarm)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.cancel(alarmPendingIntent)
    }
}