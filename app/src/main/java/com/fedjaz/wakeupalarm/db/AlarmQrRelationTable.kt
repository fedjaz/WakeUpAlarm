package com.fedjaz.wakeupalarm.db

import org.jetbrains.exposed.dao.id.IntIdTable

class AlarmQrRelationTable : IntIdTable("AlarmQRRelations") {
    val alarmId = integer("alarmId")
    val qrId = integer("qrId")
}