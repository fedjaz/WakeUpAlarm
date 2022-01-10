package com.fedjaz.wakeupalarm.db

import com.orm.SugarRecord

class AlarmEventTable : SugarRecord() {
    var hour = 0
    var minute = 0
    var alarmsCount = 0
}