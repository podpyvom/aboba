package com.alarmapp.bluetoothalarm.manager

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alarmapp.bluetoothalarm.data.models.Alarm
import com.alarmapp.bluetoothalarm.utils.Constants
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    
    fun scheduleAlarm(alarm: Alarm): Boolean {
        try {
            val currentTime = System.currentTimeMillis()
            val alarmTime = alarm.getNextAlarmTimeMillis()
            val delay = alarmTime - currentTime
            
            if (delay <= 0) {
                return false
            }
            
            // Schedule Bluetooth connection 1 minute before alarm
            val bluetoothConnectionDelay = delay - (60 * 1000) // 1 minute before
            if (bluetoothConnectionDelay > 0 && alarm.bluetoothDeviceAddress != null) {
                scheduleBluetoothConnection(alarm, bluetoothConnectionDelay)
            }
            
            val inputData = Data.Builder()
                .putLong("alarm_id", alarm.id)
                .putInt("alarm_hour", alarm.hour)
                .putInt("alarm_minute", alarm.minute)
                .putString("audio_file_path", alarm.audioFilePath)
                .putString("audio_file_name", alarm.audioFileName)
                .putString("bluetooth_device_address", alarm.bluetoothDeviceAddress)
                .putString("bluetooth_device_name", alarm.bluetoothDeviceName)
                .build()
            
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setConstraints(constraints)
                .addTag(Constants.ALARM_WORK_TAG)
                .addTag("alarm_${alarm.id}")
                .build()
            
            workManager.enqueueUniqueWork(
                "alarm_${alarm.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    fun cancelAlarm(alarmId: Long) {
        workManager.cancelUniqueWork("alarm_$alarmId")
        workManager.cancelUniqueWork("bluetooth_$alarmId")
    }
    
    fun cancelAllAlarms() {
        workManager.cancelAllWorkByTag(Constants.ALARM_WORK_TAG)
    }
    
    fun rescheduleAllAlarms(alarms: List<Alarm>) {
        // Cancel all existing alarms
        cancelAllAlarms()
        
        // Schedule all enabled alarms
        alarms.filter { it.isEnabled }.forEach { alarm ->
            scheduleAlarm(alarm)
        }
    }
    
    private fun scheduleBluetoothConnection(alarm: Alarm, delay: Long) {
        val inputData = Data.Builder()
            .putLong("alarm_id", alarm.id)
            .putString("bluetooth_device_address", alarm.bluetoothDeviceAddress)
            .putString("bluetooth_device_name", alarm.bluetoothDeviceName)
            .putString("action", "connect_bluetooth")
            .build()
        
        val bluetoothWorkRequest = OneTimeWorkRequestBuilder<BluetoothConnectionWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("bluetooth_${alarm.id}")
            .build()
        
        workManager.enqueueUniqueWork(
            "bluetooth_${alarm.id}",
            ExistingWorkPolicy.REPLACE,
            bluetoothWorkRequest
        )
    }
}