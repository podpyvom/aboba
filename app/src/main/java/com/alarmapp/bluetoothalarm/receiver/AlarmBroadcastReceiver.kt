package com.alarmapp.bluetoothalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alarmapp.bluetoothalarm.service.AlarmPlaybackService

class AlarmBroadcastReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        when (intent.action) {
            ACTION_ALARM_TRIGGER -> {
                handleAlarmTrigger(context, intent)
            }
            ACTION_STOP_ALARM -> {
                handleStopAlarm(context)
            }
        }
    }
    
    private fun handleAlarmTrigger(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, AlarmPlaybackService::class.java).apply {
            putExtras(intent.extras ?: return)
        }
        context.startForegroundService(serviceIntent)
    }
    
    private fun handleStopAlarm(context: Context) {
        val serviceIntent = Intent(context, AlarmPlaybackService::class.java)
        context.stopService(serviceIntent)
    }
    
    companion object {
        const val ACTION_ALARM_TRIGGER = "com.alarmapp.bluetoothalarm.ALARM_TRIGGER"
        const val ACTION_STOP_ALARM = "com.alarmapp.bluetoothalarm.STOP_ALARM"
    }
}