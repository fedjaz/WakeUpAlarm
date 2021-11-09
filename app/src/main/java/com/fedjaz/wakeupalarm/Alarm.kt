package com.fedjaz.wakeupalarm

import java.io.Serializable
import java.sql.Time

class Alarm(var id: Int = 0, var hour: Int = 0, var minute: Int = 0, var enabled: Boolean = false, var days: ArrayList<Boolean>) : Serializable {
    private val daysPattern = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    var qrIds = arrayListOf<Int>()

    val isOneTime: Boolean
        get() {
            return !days.contains(true)
        }

    fun getTimeString(): String{
        return "%02d".format(hour) + ":" + "%02d".format(minute)
    }

    fun getDaysString(): String{
        if(!days.contains(false)){
            return "Daily"
        }

        if(!days.contains(true)){
            return "One-time"
        }

        val workdays = days.subList(0, 5)
        val weekend = days.subList(5, 7)
        if(!workdays.contains(false) && !weekend.contains(true)){
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

