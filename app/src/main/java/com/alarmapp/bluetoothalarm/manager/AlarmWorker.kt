package com.alarmapp.bluetoothalarm.manager

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alarmapp.bluetoothalarm.service.AlarmPlaybackService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val alarmId = inputData.getLong("alarm_id", -1L)
            val hour = inputData.getInt("alarm_hour", 0)
            val minute = inputData.getInt("alarm_minute", 0)
            val audioFilePath = inputData.getString("audio_file_path")
            val audioFileName = inputData.getString("audio_file_name")
            val bluetoothDeviceAddress = inputData.getString("bluetooth_device_address")
            val bluetoothDeviceName = inputData.getString("bluetooth_device_name")
            
            if (alarmId == -1L) {
                return Result.failure()
            }
            
            // Start the alarm playback service
            val intent = Intent(applicationContext, AlarmPlaybackService::class.java).apply {
                putExtra("alarm_id", alarmId)
                putExtra("alarm_hour", hour)
                putExtra("alarm_minute", minute)
                putExtra("audio_file_path", audioFilePath)
                putExtra("audio_file_name", audioFileName)
                putExtra("bluetooth_device_address", bluetoothDeviceAddress)
                putExtra("bluetooth_device_name", bluetoothDeviceName)
            }
            
            applicationContext.startForegroundService(intent)
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}