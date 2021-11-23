package com.fedjaz.wakeupalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.util.*

class AlarmBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val dataAccessLayer = DataAccessLayer(context!!)
        val scheduler = AlarmScheduler(context)
        if(Intent.ACTION_BOOT_COMPLETED == intent?.action){
            val alarms = dataAccessLayer.getAllAlarms()
            for(alarm in alarms){
                if(alarm.enabled){
                    scheduler.schedule(alarm)
                }
            }
        }
        else{
            val alarmId: Int = intent?.extras?.getInt("alarmId")!!
            val alarm = dataAccessLayer.getAlarmById(alarmId)
            if(!alarm.isOneTime){
                scheduler.schedule(alarm)
            }
            else{
                alarm.enabled = false
                dataAccessLayer.enableAlarm(alarm)
            }

            if(alarmIsToday(alarm)){
                startService(context, alarm)
            }
        }
    }

    private fun alarmIsToday(alarm: Alarm): Boolean{
        if(alarm.isOneTime){
            return true
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        dayOfWeek -= 2
        if(dayOfWeek == -1){
            dayOfWeek = 6
        }
        return alarm.days[dayOfWeek]
    }

    private fun startService(context: Context?, alarm: Alarm){
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra("alarmId", alarm.id)
        context?.startForegroundService(intentService)
    }
}