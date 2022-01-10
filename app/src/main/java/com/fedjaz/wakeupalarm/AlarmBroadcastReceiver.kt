package com.fedjaz.wakeupalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class AlarmBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val dataAccessLayer = DataAccessLayer(context!!)
        val scheduler = AlarmScheduler(context, dataAccessLayer)
        val hour = intent?.extras?.getInt("hour")!!
        val minute = intent.extras?.getInt("minute")!!
        if(Intent.ACTION_BOOT_COMPLETED == intent.action){
            val alarms = dataAccessLayer.getAllAlarms()
            dataAccessLayer.clearEvents()
            for(alarm in alarms){
                if(alarm.enabled){
                    scheduler.schedule(alarm)
                }
            }
        }
        else{
            val alarms = dataAccessLayer.getMatchingAlarms(hour, minute).filter { alarm ->  alarmIsToday(alarm) }
            if(alarms.isEmpty()){
                return
            }

            val oneTimeAlarms = alarms.filter { alarm ->  alarm.isOneTime}
            val alarm =
            if(oneTimeAlarms.isNotEmpty()){
                oneTimeAlarms[0]
            }
            else{
                alarms[0]
            }

            if(!alarm.isOneTime){
                scheduler.reschedule(alarm)
            }
            else{
                alarm.enabled = false
                dataAccessLayer.enableAlarm(alarm)
            }

            startService(context, alarm)
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