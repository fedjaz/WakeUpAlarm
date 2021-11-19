package com.fedjaz.wakeupalarm

import java.io.Serializable
import java.sql.Time

class Alarm(var id: Int = -1, var hour: Int = 0, var minute: Int = 0, var enabled: Boolean = false, var days: ArrayList<Boolean>) : Serializable {
    private val daysPattern = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    var checked = false
    var qrIds = arrayListOf<Int>()

    val isOneTime: Boolean
        get() {
            return !days.contains(true)
        }

    val isDaily: Boolean
        get() {
            return !days.contains(false)
        }

    val isWorkDays: Boolean
        get() {
            val workdays = days.subList(0, 5)
            val weekend = days.subList(5, 7)
            return !workdays.contains(false) && !weekend.contains(true)
        }

    fun getTimeString(): String{
        return "%02d".format(hour) + ":" + "%02d".format(minute)
    }

    fun getDaysString(): String{
        if(isDaily){
            return "Daily"
        }

        if(isOneTime){
            return "One-time"
        }

        if(isWorkDays){
            return "Work days"
        }

        var output = ""
        for(i in 0..6){
            if(days[i])
                output += daysPattern[i] + " "
        }
        return output
    }
}

