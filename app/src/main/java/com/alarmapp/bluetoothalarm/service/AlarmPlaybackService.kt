package com.alarmapp.bluetoothalarm.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.alarmapp.bluetoothalarm.manager.AudioManager
import com.alarmapp.bluetoothalarm.manager.BluetoothManager
import com.alarmapp.bluetoothalarm.manager.VolumeProgressionManager
import com.alarmapp.bluetoothalarm.utils.Constants
import com.alarmapp.bluetoothalarm.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmPlaybackService : Service() {
    
    @Inject
    lateinit var audioManager: AudioManager
    
    @Inject
    lateinit var bluetoothManager: BluetoothManager
    
    @Inject
    lateinit var volumeProgressionManager: VolumeProgressionManager
    
    private var serviceJob: Job? = null
    private var alarmId: Long = -1
    
    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannels(this)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleAlarmIntent(it) }
        return START_NOT_STICKY
    }
    
    private fun handleAlarmIntent(intent: Intent) {
        alarmId = intent.getLongExtra("alarm_id", -1L)
        val hour = intent.getIntExtra("alarm_hour", 0)
        val minute = intent.getIntExtra("alarm_minute", 0)
        val audioFilePath = intent.getStringExtra("audio_file_path")
        val bluetoothDeviceAddress = intent.getStringExtra("bluetooth_device_address")
        
        if (alarmId == -1L) {
            stopSelf()
            return
        }
        
        startForegroundService()
        startAlarmPlayback(audioFilePath, bluetoothDeviceAddress)
    }
    
    private fun startForegroundService() {
        val notification = NotificationUtils.createAlarmNotification(this).build()
        startForeground(Constants.ALARM_PLAYBACK_SERVICE_ID, notification)
    }
    
    private fun startAlarmPlayback(audioFilePath: String?, bluetoothDeviceAddress: String?) {
        serviceJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                // Try to connect to Bluetooth device if specified
                if (bluetoothDeviceAddress != null && bluetoothManager.isBluetoothEnabled()) {
                    bluetoothManager.connectToDevice(bluetoothDeviceAddress)
                    // Give some time for Bluetooth connection
                    delay(3000)
                }
                
                // Determine audio source
                val audioUri = if (!audioFilePath.isNullOrEmpty()) {
                    Uri.parse(audioFilePath)
                } else {
                    // Use default alarm sound
                    android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
                }
                
                // Start audio playback with volume progression
                val success = audioManager.playAudio(audioUri, enableVolumeProgression = true)
                
                if (!success) {
                    // Fallback to default alarm sound
                    audioManager.playAudio(
                        android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI,
                        enableVolumeProgression = true
                    )
                }
                
                // Auto-stop after maximum duration
                delay(Constants.DEFAULT_ALARM_DURATION_MS)
                stopAlarmPlayback()
                
            } catch (e: Exception) {
                stopAlarmPlayback()
            }
        }
    }
    
    private fun stopAlarmPlayback() {
        serviceJob?.cancel()
        audioManager.stopAudio()
        volumeProgressionManager.stopVolumeProgression()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopAlarmPlayback()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        const val ACTION_STOP_ALARM = "stop_alarm"
    }
}