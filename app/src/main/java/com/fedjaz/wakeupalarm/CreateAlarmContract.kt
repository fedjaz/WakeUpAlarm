package com.fedjaz.wakeupalarm

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class CreateAlarmContract : ActivityResultContract<Pair<ArrayList<QR>, Alarm?>, Alarm?>() {
    override fun createIntent(context: Context, input: Pair<ArrayList<QR>, Alarm?>): Intent {
        return Intent(context, AlarmCreate::class.java).apply {
            putExtra("qrs", input.first)
            putExtra("alarm", input.second)

        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Alarm? {
        return if(resultCode == Activity.RESULT_OK){
            intent?.extras?.get("alarm") as Alarm
        }
        else{
            null
        }
    }
}