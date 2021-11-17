package com.fedjaz.wakeupalarm.db
import org.jetbrains.exposed.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction



class DataAccessLayer(private val databaseLocation: String) {

    object AlarmTable : IntIdTable("Alarm") {
        val hour = integer("hour")
        val minute = integer("minute")
        val days = blob("days")
    }

    object QRTable : IntIdTable("QR"){
        val name = text("name")
        val number = integer("number")
        val location = text("location")
        val image = blob("image")
    }

    object AlarmQrRelationTable : IntIdTable("AlarmQRRelations") {
        val alarmId = integer("alarmId")
        val qrId = integer("qrId")
    }

    init{
        Database.connect("jdbc:sqlite:$databaseLocation", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(AlarmTable, QRTable, AlarmQrRelationTable)
        }
    }
}