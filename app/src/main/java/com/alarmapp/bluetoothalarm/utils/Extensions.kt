package com.alarmapp.bluetoothalarm.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.alarmapp.bluetoothalarm.data.models.AudioFile
import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.data.models.ConnectionState

fun Context.formatTime(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
}

fun Context.getSharedPrefs(): SharedPreferences {
    return getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
}

fun SharedPreferences.saveBluetoothDevice(device: BluetoothAudioDevice) {
    edit()
        .putString(Constants.PREF_SELECTED_BLUETOOTH_ADDRESS, device.address)
        .putString(Constants.PREF_SELECTED_BLUETOOTH_NAME, device.name)
        .putLong(Constants.PREF_LAST_BLUETOOTH_CONNECTION, System.currentTimeMillis())
        .apply()
}

fun SharedPreferences.getSelectedBluetoothDevice(): BluetoothAudioDevice? {
    val address = getString(Constants.PREF_SELECTED_BLUETOOTH_ADDRESS, null)
    val name = getString(Constants.PREF_SELECTED_BLUETOOTH_NAME, null)
    val lastConnection = getLong(Constants.PREF_LAST_BLUETOOTH_CONNECTION, 0)
    
    return if (address != null && name != null) {
        BluetoothAudioDevice(
            address = address,
            name = name,
            connectionState = ConnectionState.DISCONNECTED,
            lastConnected = if (lastConnection > 0) lastConnection else null
        )
    } else {
        null
    }
}

fun SharedPreferences.saveAudioFile(audioFile: AudioFile) {
    edit()
        .putString(Constants.PREF_SELECTED_AUDIO_URI, audioFile.uri)
        .putString(Constants.PREF_SELECTED_AUDIO_NAME, audioFile.displayName)
        .apply()
}

fun SharedPreferences.getSelectedAudioFile(): AudioFile? {
    val uri = getString(Constants.PREF_SELECTED_AUDIO_URI, null)
    val name = getString(Constants.PREF_SELECTED_AUDIO_NAME, null)
    
    return if (uri != null && name != null) {
        AudioFile(
            uri = uri,
            displayName = name,
            duration = 0, // Will be populated when needed
            mimeType = "audio/*",
            size = 0
        )
    } else {
        null
    }
}

fun Long.toTimeString(): String {
    val hours = this / (1000 * 60 * 60)
    val minutes = (this % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (this % (1000 * 60)) / 1000
    
    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%d:%02d", minutes, seconds)
    }
}

fun Int.volumePercentToAudioManagerVolume(maxVolume: Int): Int {
    return (this * maxVolume) / 100
}