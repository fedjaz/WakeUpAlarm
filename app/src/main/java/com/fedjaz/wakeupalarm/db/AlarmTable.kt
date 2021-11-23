package com.fedjaz.wakeupalarm.db

import com.orm.SugarRecord

class AlarmTable : SugarRecord() {
    var hour = 0
    var minute = 0
    var days = byteArrayOf()
    var enabled = false
    var isStrict = false
}