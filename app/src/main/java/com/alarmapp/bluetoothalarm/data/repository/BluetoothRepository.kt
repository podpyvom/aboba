package com.alarmapp.bluetoothalarm.data.repository

import android.content.Context
import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.data.models.ConnectionState
import com.alarmapp.bluetoothalarm.domain.repository.BluetoothRepositoryInterface
import com.alarmapp.bluetoothalarm.manager.BluetoothManager
import com.alarmapp.bluetoothalarm.utils.getSelectedBluetoothDevice
import com.alarmapp.bluetoothalarm.utils.getSharedPrefs
import com.alarmapp.bluetoothalarm.utils.saveBluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothRepository @Inject constructor(
    private val context: Context,
    private val bluetoothManager: BluetoothManager
) : BluetoothRepositoryInterface {
    
    override fun getDiscoveredDevices(): StateFlow<List<BluetoothAudioDevice>> {
        return bluetoothManager.discoveredDevices
    }
    
    override fun getConnectedDevice(): StateFlow<BluetoothAudioDevice?> {
        return bluetoothManager.connectedDevice
    }
    
    override fun getConnectionState(): StateFlow<ConnectionState> {
        return bluetoothManager.connectionState
    }
    
    override fun isScanning(): StateFlow<Boolean> {
        return bluetoothManager.isScanning
    }
    
    override fun getErrorEvents(): Flow<String> {
        return bluetoothManager.errorEvents
    }
    
    override fun isBluetoothEnabled(): Boolean {
        return bluetoothManager.isBluetoothEnabled()
    }
    
    override fun hasBluetoothPermissions(): Boolean {
        return bluetoothManager.hasBluetoothPermissions()
    }
    
    override fun startDeviceDiscovery(): Boolean {
        return bluetoothManager.startDeviceDiscovery()
    }
    
    override fun stopDeviceDiscovery() {
        bluetoothManager.stopDeviceDiscovery()
    }
    
    override fun getPairedDevices(): List<BluetoothAudioDevice> {
        return bluetoothManager.getPairedDevices()
    }
    
    override fun connectToDevice(deviceAddress: String): Boolean {
        return bluetoothManager.connectToDevice(deviceAddress)
    }
    
    override fun disconnectFromDevice() {
        bluetoothManager.disconnectFromDevice()
    }
    
    override fun saveSelectedDevice(device: BluetoothAudioDevice) {
        context.getSharedPrefs().saveBluetoothDevice(device)
    }
    
    override fun getSelectedDevice(): BluetoothAudioDevice? {
        return context.getSharedPrefs().getSelectedBluetoothDevice()
    }
    
    override fun cleanup() {
        bluetoothManager.cleanup()
    }
}