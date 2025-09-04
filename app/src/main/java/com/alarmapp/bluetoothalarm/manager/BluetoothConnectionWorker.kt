package com.alarmapp.bluetoothalarm.manager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alarmapp.bluetoothalarm.domain.repository.BluetoothRepositoryInterface
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class BluetoothConnectionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bluetoothRepository: BluetoothRepositoryInterface
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val action = inputData.getString("action")
            val bluetoothDeviceAddress = inputData.getString("bluetooth_device_address")
            
            if (action == "connect_bluetooth" && bluetoothDeviceAddress != null) {
                // Attempt to connect to Bluetooth device 1 minute before alarm
                val success = bluetoothRepository.connectToDevice(bluetoothDeviceAddress)
                
                if (success) {
                    // Give time for connection to establish
                    delay(5000)
                    Result.success()
                } else {
                    // Try once more after a short delay
                    delay(2000)
                    val retrySuccess = bluetoothRepository.connectToDevice(bluetoothDeviceAddress)
                    if (retrySuccess) {
                        Result.success()
                    } else {
                        // Don't fail the work - alarm will still trigger with phone speaker
                        Result.success()
                    }
                }
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            // Don't fail the work - alarm will still trigger
            Result.success()
        }
    }
}