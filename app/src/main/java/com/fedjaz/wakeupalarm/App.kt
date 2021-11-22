package com.fedjaz.wakeupalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager


class App: Application() {
    private val CHANNEL_ID = "ALARM_SERVICE_CHANNEL"

    override fun onCreate() {
        super.onCreate()
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }
}