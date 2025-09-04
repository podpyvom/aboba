package com.alarmapp.bluetoothalarm.presentation.state

import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.data.models.ConnectionState

data class BluetoothState(
    val isBluetoothEnabled: Boolean = false,
    val isScanning: Boolean = false,
    val discoveredDevices: List<BluetoothAudioDevice> = emptyList(),
    val connectedDevice: BluetoothAudioDevice? = null,
    val selectedDevice: BluetoothAudioDevice? = null,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val hasPermissions: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
) {
    val hasDevices: Boolean get() = discoveredDevices.isNotEmpty()
    val isConnected: Boolean get() = connectionState == ConnectionState.CONNECTED
    val canScan: Boolean get() = isBluetoothEnabled && hasPermissions && !isScanning
    val canConnect: Boolean get() = isBluetoothEnabled && hasPermissions && selectedDevice != null
}