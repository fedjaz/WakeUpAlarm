package com.fedjaz.wakeupalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.*

class AlarmScheduler(val context: Context) {

    fun schedule(alarm: Alarm): Long{
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        intent.putExtra("alarmId", alarm.id)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour)
        calendar.set(Calendar.MINUTE, alarm.minute)
        calendar.set(Calendar.SECOND, 0)

        var extraDay = 0
        if(calendar.timeInMillis <= System.currentTimeMillis()){
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
            extraDay = 1
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmPendingIntent)

        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = System.currentTimeMillis()
        var timeDiff = calendar.timeInMillis - currentCalendar.timeInMillis
        if(alarm.isOneTime || alarm.isDaily){
            return timeDiff
        }
        else{
            var dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK)
            dayOfWeek -= 2
            if(dayOfWeek == -1){
                dayOfWeek = 6
            }

            var nextDay = -1
            for(i in dayOfWeek + extraDay until alarm.days.size){
                if(alarm.days[i]){
                    nextDay = i
                    break
                }
            }

            if(nextDay == -1){
                for(i in 0 .. dayOfWeek){
                    if(alarm.days[i]){
                        nextDay = i
                        break
                    }
                }
            }

            if(nextDay == dayOfWeek && extraDay == 1){
                nextDay = 6
            }
            else if(nextDay == dayOfWeek){
                nextDay = 0
            }
            else{
                nextDay -= dayOfWeek
                nextDay--
                if(nextDay < 0){
                    nextDay += 7
                }
            }

            timeDiff += nextDay * 24 * 60 * 60 * 1000
            return timeDiff
        }
    }

    fun cancel(alarm: Alarm){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)

        intent.putExtra("alarm", alarm)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.cancel(alarmPendingIntent)
    }
}