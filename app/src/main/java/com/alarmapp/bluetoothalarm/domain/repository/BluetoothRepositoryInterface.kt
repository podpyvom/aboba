package com.alarmapp.bluetoothalarm.domain.repository

import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.data.models.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepositoryInterface {
    fun getDiscoveredDevices(): StateFlow<List<BluetoothAudioDevice>>
    fun getConnectedDevice(): StateFlow<BluetoothAudioDevice?>
    fun getConnectionState(): StateFlow<ConnectionState>
    fun isScanning(): StateFlow<Boolean>
    fun getErrorEvents(): Flow<String>
    fun isBluetoothEnabled(): Boolean
    fun hasBluetoothPermissions(): Boolean
    fun startDeviceDiscovery(): Boolean
    fun stopDeviceDiscovery()
    fun getPairedDevices(): List<BluetoothAudioDevice>
    fun connectToDevice(deviceAddress: String): Boolean
    fun disconnectFromDevice()
    fun saveSelectedDevice(device: BluetoothAudioDevice)
    fun getSelectedDevice(): BluetoothAudioDevice?
    fun cleanup()
}