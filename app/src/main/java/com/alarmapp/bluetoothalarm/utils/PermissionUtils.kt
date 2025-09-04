package com.alarmapp.bluetoothalarm.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtils {
    
    fun getRequiredBluetoothPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }
    
    fun hasBluetoothPermissions(context: Context): Boolean {
        return getRequiredBluetoothPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun hasExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
    
    fun hasAudioPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun hasAllRequiredPermissions(context: Context): Boolean {
        return hasBluetoothPermissions(context) && 
               hasExactAlarmPermission(context) && 
               hasAudioPermissions(context)
    }
}