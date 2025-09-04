package com.alarmapp.bluetoothalarm.domain.usecase

import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.domain.repository.BluetoothRepositoryInterface
import javax.inject.Inject

class ConnectBluetoothUseCase @Inject constructor(
    private val bluetoothRepository: BluetoothRepositoryInterface
) {
    
    fun isBluetoothAvailable(): Boolean {
        return bluetoothRepository.isBluetoothEnabled() && 
               bluetoothRepository.hasBluetoothPermissions()
    }
    
    fun startDeviceDiscovery(): Result<Unit> {
        return try {
            if (!bluetoothRepository.isBluetoothEnabled()) {
                return Result.failure(Exception("Bluetooth is not enabled"))
            }
            
            if (!bluetoothRepository.hasBluetoothPermissions()) {
                return Result.failure(Exception("Bluetooth permissions not granted"))
            }
            
            val success = bluetoothRepository.startDeviceDiscovery()
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to start device discovery"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun stopDeviceDiscovery() {
        bluetoothRepository.stopDeviceDiscovery()
    }
    
    fun connectToDevice(device: BluetoothAudioDevice): Result<Unit> {
        return try {
            if (!isBluetoothAvailable()) {
                return Result.failure(Exception("Bluetooth not available"))
            }
            
            val success = bluetoothRepository.connectToDevice(device.address)
            if (success) {
                bluetoothRepository.saveSelectedDevice(device)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to connect to device"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun disconnectFromDevice() {
        bluetoothRepository.disconnectFromDevice()
    }
    
    fun getPairedDevices(): List<BluetoothAudioDevice> {
        return if (isBluetoothAvailable()) {
            bluetoothRepository.getPairedDevices()
        } else {
            emptyList()
        }
    }
    
    fun getSelectedDevice(): BluetoothAudioDevice? {
        return bluetoothRepository.getSelectedDevice()
    }
}