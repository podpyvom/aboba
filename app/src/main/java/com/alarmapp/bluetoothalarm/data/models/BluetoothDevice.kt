package com.alarmapp.bluetoothalarm.data.models

data class BluetoothAudioDevice(
    val address: String,
    val name: String,
    val connectionState: ConnectionState,
    val lastConnected: Long?,
    val isPreferred: Boolean = false,
    val rssi: Int? = null,
    val batteryLevel: Int? = null
) {
    fun getDisplayName(): String {
        return if (name.isBlank()) "Unknown Device" else name
    }
    
    fun isConnected(): Boolean {
        return connectionState == ConnectionState.CONNECTED
    }
    
    fun canConnect(): Boolean {
        return connectionState == ConnectionState.DISCONNECTED || 
               connectionState == ConnectionState.FAILED
    }
}

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING, 
    CONNECTED,
    FAILED,
    RECONNECTING
}

data class BluetoothScanResult(
    val device: BluetoothAudioDevice,
    val timestamp: Long = System.currentTimeMillis()
)