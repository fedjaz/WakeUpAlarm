package com.fedjaz.wakeupalarm

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import kotlin.system.exitProcess


class AlarmTrigger : AppCompatActivity() {
    private var qrs = arrayListOf<QR>()
    private val leftQrIds = arrayListOf<Int>()
    private val scannedQrIds = arrayListOf<Int>()
    private var qrsFragment: QRFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_trigger)

        animateClock()

        val dataAccessLayer = DataAccessLayer(this)

        val alarmId = intent.extras?.getInt("alarmId")!!
        val alarm = dataAccessLayer.getAlarmById(alarmId)

        qrs = dataAccessLayer.getQrsForAlarm(alarm)

        for(qr in qrs){
            leftQrIds.add(qr.id)
        }

        val dismissButton = findViewById<Button>(R.id.dismissButton)
        val scanButton = findViewById<Button>(R.id.scanButton)

        dismissButton.setOnClickListener {
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
            val local = Intent()
            if(alarm.isOneTime){
                local.putExtra("alarmId", alarmId)
            }
            local.action = "com.fedjaz.wakeupalarm.MainActivity.action"
            sendBroadcast(local)

            finishAndRemoveTask()
        }

        scanButton.setOnClickListener {
            scanCode()
        }

        if(qrs.size > 0){
            qrsFragment = QRFragment.newInstance(qrs, true)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.qrsLayout, qrsFragment!!, "tag")
            transaction.commit()

            dismissButton.visibility = GONE
        }
        else{
            dismissButton.visibility = VISIBLE
            val infoText = findViewById<TextView>(R.id.alarmTriggerInfo)
            val qrsLayout = findViewById<FrameLayout>(R.id.qrsLayout)
            scanButton.visibility = GONE
            qrsLayout.visibility = GONE
            infoText.visibility = GONE
        }
    }

    private fun scanCode(){
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setPrompt("Scan QR code")
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(intentResult != null && intentResult.contents != null){
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            try{
                val map = gson.fromJson(intentResult.contents, Map::class.java)
                val id = map["id"].toString().toDouble().toInt()
                when (id) {
                    in leftQrIds -> {
                        leftQrIds.remove(id)
                        scannedQrIds.add(id)
                        changeCheckBoxes(id)
                    }
                    in scannedQrIds -> {
                        Toast.makeText(this, "You already scanned this QR", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(this, "This QR is not in this alarm!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            catch (e: Exception){
                Toast.makeText(this, "This QR is not valid for this app", Toast.LENGTH_LONG).show()
            }

            if(leftQrIds.size == 0){
                scanComplete()
            }
        }
    }

    private fun changeCheckBoxes(id: Int){
        for(qr in qrs){
            if(qr.id == id){
                qr.checked = true
                break
            }
        }

        (qrsFragment!!.view as RecyclerView).adapter?.notifyDataSetChanged()
    }

    private fun scanComplete(){
        val dismissButton = findViewById<Button>(R.id.dismissButton)
        val scanButton = findViewById<Button>(R.id.scanButton)
        val infoText = findViewById<TextView>(R.id.alarmTriggerInfo)
        val qrsLayout = findViewById<FrameLayout>(R.id.qrsLayout)

        dismissButton.visibility = VISIBLE
        scanButton.visibility = GONE
        infoText.visibility = GONE
        qrsLayout.visibility = GONE
    }

    private fun animateClock() {
        val clock = findViewById<ImageView>(R.id.alarmAnimationView)
        val rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }
}