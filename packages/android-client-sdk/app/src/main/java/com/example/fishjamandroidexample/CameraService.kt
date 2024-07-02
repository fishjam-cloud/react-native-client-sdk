package com.example.fishjamandroidexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import timber.log.Timber

class CameraService : Service() {
    private fun startForeground() {
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_DENIED) {
            stopSelf()
            return
        }

        try {
            val channelID = "FISHJAM_NOTIFICATION_CHANNEL"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Fishjam notification channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(channelID, name, importance)
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }
            val notification =
                NotificationCompat.Builder(this, channelID)
                    .setContentTitle("Fishjam is running")
                    .build()
            ServiceCompat.startForeground(
                this,
                100,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                } else {
                    0
                }
            )
        } catch (e: Exception) {
            Timber.w("Failed to start foreground service")
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        startForeground()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
