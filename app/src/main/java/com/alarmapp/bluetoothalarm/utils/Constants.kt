package com.alarmapp.bluetoothalarm.utils

object Constants {
    // Alarm Configuration
    const val ALARM_REQUEST_CODE = 1000
    const val ALARM_NOTIFICATION_ID = 2000
    
    // Volume Progression
    const val INITIAL_VOLUME_PERCENT = 10
    const val MAX_VOLUME_PERCENT = 100
    const val VOLUME_INCREMENT_PERCENT = 1
    const val VOLUME_INCREMENT_INTERVAL_MS = 1000L
    
    // Bluetooth Configuration
    const val BLUETOOTH_SCAN_DURATION_MS = 30000L
    const val BLUETOOTH_CONNECTION_TIMEOUT_MS = 15000L
    const val BLUETOOTH_RECONNECTION_DELAY_MS = 5000L
    const val BLUETOOTH_MAX_RETRY_ATTEMPTS = 3
    
    // Service Configuration
    const val ALARM_PLAYBACK_SERVICE_ID = 3000
    const val BLUETOOTH_MONITOR_SERVICE_ID = 3001
    
    // SharedPreferences Keys
    const val PREFS_NAME = "alarm_preferences"
    const val PREF_SELECTED_BLUETOOTH_ADDRESS = "selected_bluetooth_address"
    const val PREF_SELECTED_BLUETOOTH_NAME = "selected_bluetooth_name"
    const val PREF_SELECTED_AUDIO_URI = "selected_audio_uri"
    const val PREF_SELECTED_AUDIO_NAME = "selected_audio_name"
    const val PREF_LAST_BLUETOOTH_CONNECTION = "last_bluetooth_connection"
    
    // Notification Channels
    const val ALARM_NOTIFICATION_CHANNEL_ID = "alarm_notifications"
    const val BLUETOOTH_NOTIFICATION_CHANNEL_ID = "bluetooth_notifications"
    
    // Permissions
    const val BLUETOOTH_PERMISSIONS_REQUEST_CODE = 100
    const val EXACT_ALARM_PERMISSION_REQUEST_CODE = 101
    const val AUDIO_FILE_PERMISSION_REQUEST_CODE = 102
    
    // WorkManager Tags
    const val ALARM_WORK_TAG = "alarm_work"
    const val BLUETOOTH_MONITOR_WORK_TAG = "bluetooth_monitor_work"
    
    // Audio Configuration
    const val AUDIO_FADE_IN_DURATION_MS = 2000L
    const val DEFAULT_ALARM_DURATION_MS = 300000L // 5 minutes
    
    // UI Configuration
    const val TIME_PICKER_HOUR_MIN = 0
    const val TIME_PICKER_HOUR_MAX = 23
    const val TIME_PICKER_MINUTE_MIN = 0
    const val TIME_PICKER_MINUTE_MAX = 59
}