package com.fedjaz.wakeupalarm.db

import org.jetbrains.exposed.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

class AlarmTable : IntIdTable("Alarm") {
    val hour = integer("hour")
    val minute = integer("minute")
    val days = blob("days")
}