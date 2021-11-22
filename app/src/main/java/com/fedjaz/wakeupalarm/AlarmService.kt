package com.fedjaz.wakeupalarm

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.io.Serializable


class AlarmService: Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alarmsound)
        mediaPlayer?.isLooping = true

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, AlarmTrigger::class.java)

        val alarmId = intent?.extras?.getInt("alarmId") as Int
        notificationIntent.putExtra("alarmId", alarmId)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, "ALARM_SERVICE_CHANNEL")
            .setContentText("It's time to wake up and scan some codes!")
            .setSmallIcon(R.mipmap.alarm)
            .setContentIntent(pendingIntent)
            .build()

        mediaPlayer?.start()
        val pattern = longArrayOf(0, 100, 1000)
        vibrator?.vibrate(pattern, 0)

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        vibrator?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}