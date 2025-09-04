package com.alarmapp.bluetoothalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alarmapp.bluetoothalarm.domain.usecase.SetAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var setAlarmUseCase: SetAlarmUseCase
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                rescheduleAlarms()
            }
        }
    }
    
    private fun rescheduleAlarms() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                setAlarmUseCase.rescheduleAllAlarms()
            } catch (e: Exception) {
                // Log error or handle gracefully
            }
        }
    }
}