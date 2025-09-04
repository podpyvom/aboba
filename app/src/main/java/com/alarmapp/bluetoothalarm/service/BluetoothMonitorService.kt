package com.alarmapp.bluetoothalarm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.alarmapp.bluetoothalarm.manager.BluetoothManager
import com.alarmapp.bluetoothalarm.utils.Constants
import com.alarmapp.bluetoothalarm.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothMonitorService : Service() {
    
    @Inject
    lateinit var bluetoothManager: BluetoothManager
    
    private var monitoringJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannels(this)
        startForegroundService()
        startBluetoothMonitoring()
    }
    
    private fun startForegroundService() {
        val notification = NotificationUtils.createBluetoothServiceNotification(this).build()
        startForeground(Constants.BLUETOOTH_MONITOR_SERVICE_ID, notification)
    }
    
    private fun startBluetoothMonitoring() {
        monitoringJob = serviceScope.launch {
            bluetoothManager.connectionState.collectLatest { connectionState ->
                when (connectionState) {
                    com.alarmapp.bluetoothalarm.data.models.ConnectionState.CONNECTED -> {
                        // Monitor connection health
                        monitorConnectionHealth()
                    }
                    com.alarmapp.bluetoothalarm.data.models.ConnectionState.DISCONNECTED -> {
                        // Attempt reconnection if there's a preferred device
                        attemptReconnection()
                    }
                    else -> {
                        // Handle other states
                    }
                }
            }
        }
    }
    
    private suspend fun monitorConnectionHealth() {
        while (bluetoothManager.connectionState.value == com.alarmapp.bluetoothalarm.data.models.ConnectionState.CONNECTED) {
            delay(Constants.BLUETOOTH_RECONNECTION_DELAY_MS)
            // Check if connection is still healthy
            // This is a simplified version - real implementation would ping the device
        }
    }
    
    private suspend fun attemptReconnection() {
        if (!bluetoothManager.isBluetoothEnabled() || !bluetoothManager.hasBluetoothPermissions()) {
            return
        }
        
        // Get preferred device from preferences
        val selectedDevice = bluetoothManager.connectedDevice.value
        if (selectedDevice != null) {
            var retryCount = 0
            while (retryCount < Constants.BLUETOOTH_MAX_RETRY_ATTEMPTS) {
                delay(Constants.BLUETOOTH_RECONNECTION_DELAY_MS)
                
                if (bluetoothManager.connectToDevice(selectedDevice.address)) {
                    break
                }
                
                retryCount++
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Restart service if killed
    }
    
    override fun onDestroy() {
        super.onDestroy()
        monitoringJob?.cancel()
        serviceScope.cancel()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}