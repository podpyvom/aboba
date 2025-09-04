package com.alarmapp.bluetoothalarm.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val audioFilePath: String?,
    val audioFileName: String?,
    val bluetoothDeviceAddress: String?,
    val bluetoothDeviceName: String?,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        return String.format("%02d:%02d", hour, minute)
    }
    
    fun getNextAlarmTimeMillis(): Long {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
        calendar.set(java.util.Calendar.MINUTE, minute)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        
        // If alarm time has passed today, schedule for tomorrow
        if (calendar.timeInMillis <= now) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        
        return calendar.timeInMillis
    }
}