package com.fedjaz.wakeupalarm
import android.content.Context
import com.fedjaz.wakeupalarm.db.AlarmQrRelationTable
import com.fedjaz.wakeupalarm.db.AlarmTable
import com.fedjaz.wakeupalarm.db.QRTable
import com.orm.SugarContext
import com.orm.SugarRecord


class DataAccessLayer(context: Context) {

    init {
        SugarContext.init(context)
    }

    fun getAllAlarms(): ArrayList<Alarm>{
        val alarmRecords = SugarRecord.listAll(AlarmTable::class.java)
        val alarms = arrayListOf<Alarm>()
        for(alarmRecord in alarmRecords){
            val alarm = Alarm(alarmRecord.id.toInt(), alarmRecord.hour, alarmRecord.minute, alarmRecord.enabled, arrayListOf<Boolean>())
            for(day in alarmRecord.days){
                alarm.days.add(day.compareTo(0) != 0)
            }
            val alarmQrRelations = SugarRecord.find(AlarmQrRelationTable::class.java, "ALARM_ID = ?", alarm.id.toString())
            for(alarmQrRelation in alarmQrRelations){
                alarm.qrIds.add(alarmQrRelation.qrId)
            }
            alarms.add(alarm)
        }

        return alarms
    }

    fun getAlarmById(id: Int): Alarm{
        val alarmRecord = SugarRecord.findById(AlarmTable::class.java, id)
        val alarm = Alarm(alarmRecord.id.toInt(), alarmRecord.hour, alarmRecord.minute, alarmRecord.enabled, arrayListOf<Boolean>())
        for(day in alarmRecord.days){
            alarm.days.add(day.compareTo(0) != 0)
        }
        val alarmQrRelations = SugarRecord.find(AlarmQrRelationTable::class.java, "ALARM_ID = ?", alarm.id.toString())
        for(alarmQrRelation in alarmQrRelations){
            alarm.qrIds.add(alarmQrRelation.qrId)
        }
        return alarm
    }

    fun createAlarm(alarm: Alarm): Int{
        val alarmRecord = AlarmTable()
        alarmRecord.hour = alarm.hour
        alarmRecord.minute = alarm.minute
        for(day in alarm.days){
            val byte: Byte = if(day){
                0x1
            }
            else{
                0x0
            }
            alarmRecord.days += byte
        }

        alarmRecord.enabled = alarm.enabled
        val id = alarmRecord.save().toInt()

        for(qrId in alarm.qrIds){
            val alarmQrRelation = AlarmQrRelationTable()
            alarmQrRelation.alarmId = id
            alarmQrRelation.qrId = qrId

            alarmQrRelation.save()
        }
        return id
    }

    fun editAlarm(alarm: Alarm){
        val alarmRecord = SugarRecord.findById(AlarmTable::class.java, alarm.id)
        alarmRecord.hour = alarm.hour
        alarmRecord.minute = alarm.minute
        alarm.days = arrayListOf()
        for(day in alarm.days){
            val byte: Byte = if(day){
                0x1
            }
            else{
                0x0
            }
            alarmRecord.days += byte
        }
        alarmRecord.enabled = alarm.enabled

        val alarmQrRelations = SugarRecord.find(AlarmQrRelationTable::class.java, "ALARM_ID = ?", alarm.id.toString())
        for(alarmQrRelation in alarmQrRelations){
            alarmQrRelation.delete()
        }

        for(qrId in alarm.qrIds){
            val alarmQrRelation = AlarmQrRelationTable()
            alarmQrRelation.alarmId = alarm.id
            alarmQrRelation.qrId = qrId

            alarmQrRelation.save()
        }
        alarmRecord.save()
    }

    fun enableAlarm(alarm: Alarm){
        val alarmRecord = SugarRecord.findById(AlarmTable::class.java, alarm.id)
        alarmRecord.enabled = alarm.enabled
        alarmRecord.save()
    }

    fun deleteAlarm(alarm: Alarm){
        SugarRecord.findById(AlarmTable::class.java, alarm.id).delete()
        val alarmQrRelations = SugarRecord.find(AlarmQrRelationTable::class.java, "ALARM_ID = ?", alarm.id.toString())
        for(alarmQrRelation in alarmQrRelations){
            alarmQrRelation.delete()
        }
    }

    fun getAllQrs(): ArrayList<QR>{
        val qrRecords = SugarRecord.listAll(QRTable::class.java)
        val qrs = arrayListOf<QR>()

        for(qrRecord in qrRecords){
            val qr = QR(qrRecord.id.toInt(), qrRecord.name, qrRecord.number, qrRecord.location)
            qr.imageByteArray = qrRecord.image
            qr.initializeImage()
            qr.isImageCreated = true
            qrs.add(qr)
        }
        return qrs
    }

    fun getQrsForAlarm(alarm: Alarm): ArrayList<QR>{
        val qrs = arrayListOf<QR>()
        val relations = SugarRecord.find(AlarmQrRelationTable::class.java, "ALARM_ID = ?", alarm.id.toString())
        for(relation in relations){
            val qrRecord = SugarRecord.findById(QRTable::class.java, relation.qrId)
            val qr = QR(qrRecord.id.toInt(), qrRecord.name, qrRecord.number, qrRecord.location)
            qr.imageByteArray = qrRecord.image
            qr.initializeImage()
            qr.isImageCreated = true
            qrs.add(qr)
        }
        return qrs
    }

    fun createQr(qr: QR): Int{
        val qrRecord = QRTable()
        qrRecord.name = qr.name
        qrRecord.number = qr.number
        qrRecord.location = qr.location
        qrRecord.image = qr.imageByteArray
        return qrRecord.save().toInt()
    }

    fun editQr(qr: QR){
        val qrRecord = SugarRecord.findById(QRTable::class.java, qr.id)
        qrRecord.name = qr.name
        qrRecord.number = qr.number
        qrRecord.location = qr.location
        qrRecord.image = qr.imageByteArray
        qrRecord.save()
    }

    fun deleteQR(qr: QR){
        SugarRecord.findById(QRTable::class.java, qr.id).delete()
        val alarmQrRelations = SugarRecord.find(AlarmQrRelationTable::class.java, "QR_ID = ?", qr.id.toString())
        for(alarmQrRelation in alarmQrRelations){
            alarmQrRelation.delete()
        }
    }

}