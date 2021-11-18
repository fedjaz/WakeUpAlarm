package com.fedjaz.wakeupalarm.db

import com.orm.SugarRecord

class QRTable : SugarRecord(){
    var name = ""
    var number = 0
    var location = ""
    var image = byteArrayOf()
}