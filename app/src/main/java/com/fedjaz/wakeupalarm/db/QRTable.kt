package com.fedjaz.wakeupalarm.db

import org.jetbrains.exposed.dao.id.IntIdTable

class QRTable : IntIdTable("QR"){
    val name = text("name")
    val number = integer("number")
    val location = text("location")
    val image = blob("image")
}