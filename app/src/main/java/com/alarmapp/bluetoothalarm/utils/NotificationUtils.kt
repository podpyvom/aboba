package com.alarmapp.bluetoothalarm.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.alarmapp.bluetoothalarm.MainActivity
import com.alarmapp.bluetoothalarm.R

object NotificationUtils {
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Alarm notification channel
            val alarmChannel = NotificationChannel(
                Constants.ALARM_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.alarm_notification_channel),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.alarm_notification_channel_desc)
                enableVibration(true)
                setSound(null, null) // We handle sound through MediaPlayer
            }
            
            // Bluetooth notification channel
            val bluetoothChannel = NotificationChannel(
                Constants.BLUETOOTH_NOTIFICATION_CHANNEL_ID,
                "Bluetooth Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Bluetooth connection monitoring service"
                enableVibration(false)
                setSound(null, null)
            }
            
            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(bluetoothChannel)
        }
    }
    
    fun createAlarmNotification(context: Context): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, Constants.ALARM_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(context.getString(R.string.alarm_playing))
            .setContentText(context.getString(R.string.stop_alarm))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
    }
    
    fun createBluetoothServiceNotification(context: Context): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, Constants.BLUETOOTH_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bluetooth)
            .setContentTitle("Bluetooth Monitor")
            .setContentText("Monitoring Bluetooth connection")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
    }
}