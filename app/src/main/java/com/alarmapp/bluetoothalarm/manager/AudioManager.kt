package com.alarmapp.bluetoothalarm.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager as SystemAudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import com.alarmapp.bluetoothalarm.utils.Constants
import com.alarmapp.bluetoothalarm.utils.volumePercentToAudioManagerVolume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    private val context: Context
) {
    private val systemAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as SystemAudioManager
    private var mediaPlayer: MediaPlayer? = null
    private var volumeProgressionJob: Job? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentVolume = MutableStateFlow(Constants.INITIAL_VOLUME_PERCENT)
    val currentVolume: StateFlow<Int> = _currentVolume.asStateFlow()
    
    private val audioFocusChangeListener = SystemAudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            SystemAudioManager.AUDIOFOCUS_LOSS -> {
                stopAudio()
            }
            SystemAudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pauseAudio()
            }
            SystemAudioManager.AUDIOFOCUS_GAIN -> {
                resumeAudio()
            }
            SystemAudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower volume temporarily
                setVolume(_currentVolume.value / 2)
            }
        }
    }
    
    fun playAudio(audioUri: Uri, enableVolumeProgression: Boolean = true): Boolean {
        try {
            // Request audio focus
            if (!requestAudioFocus()) {
                return false
            }
            
            // Release any existing MediaPlayer
            releaseMediaPlayer()
            
            // Create new MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, audioUri)
                
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                
                isLooping = true
                
                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                    
                    // Set initial volume
                    setVolume(Constants.INITIAL_VOLUME_PERCENT)
                    
                    // Start volume progression if enabled
                    if (enableVolumeProgression) {
                        startVolumeProgression()
                    }
                }
                
                setOnErrorListener { _, what, extra ->
                    stopAudio()
                    false
                }
                
                prepareAsync()
            }
            
            return true
        } catch (e: Exception) {
            stopAudio()
            return false
        }
    }
    
    fun stopAudio() {
        volumeProgressionJob?.cancel()
        releaseMediaPlayer()
        abandonAudioFocus()
        _isPlaying.value = false
        _currentVolume.value = Constants.INITIAL_VOLUME_PERCENT
    }
    
    fun pauseAudio() {
        mediaPlayer?.pause()
        volumeProgressionJob?.cancel()
        _isPlaying.value = false
    }
    
    fun resumeAudio() {
        mediaPlayer?.start()
        _isPlaying.value = true
        startVolumeProgression()
    }
    
    fun setVolume(volumePercent: Int) {
        val clampedVolume = volumePercent.coerceIn(0, 100)
        _currentVolume.value = clampedVolume
        
        val maxVolume = systemAudioManager.getStreamMaxVolume(SystemAudioManager.STREAM_ALARM)
        val targetVolume = clampedVolume.volumePercentToAudioManagerVolume(maxVolume)
        
        systemAudioManager.setStreamVolume(
            SystemAudioManager.STREAM_ALARM,
            targetVolume,
            0
        )
    }
    
    private fun startVolumeProgression() {
        volumeProgressionJob?.cancel()
        volumeProgressionJob = CoroutineScope(Dispatchers.Main).launch {
            var currentVolumePercent = Constants.INITIAL_VOLUME_PERCENT
            
            while (currentVolumePercent < Constants.MAX_VOLUME_PERCENT && _isPlaying.value) {
                setVolume(currentVolumePercent)
                delay(Constants.VOLUME_INCREMENT_INTERVAL_MS)
                currentVolumePercent += Constants.VOLUME_INCREMENT_PERCENT
            }
            
            // Ensure we reach max volume
            if (_isPlaying.value) {
                setVolume(Constants.MAX_VOLUME_PERCENT)
            }
        }
    }
    
    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = AudioFocusRequest.Builder(SystemAudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            
            audioFocusRequest = focusRequest
            systemAudioManager.requestAudioFocus(focusRequest) == SystemAudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            systemAudioManager.requestAudioFocus(
                audioFocusChangeListener,
                SystemAudioManager.STREAM_ALARM,
                SystemAudioManager.AUDIOFOCUS_GAIN
            ) == SystemAudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }
    
    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { request ->
                systemAudioManager.abandonAudioFocusRequest(request)
            }
        } else {
            @Suppress("DEPRECATION")
            systemAudioManager.abandonAudioFocus(audioFocusChangeListener)
        }
        audioFocusRequest = null
    }
    
    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
    
    fun cleanup() {
        stopAudio()
    }
    
    fun getCurrentStreamVolume(): Int {
        val currentVolume = systemAudioManager.getStreamVolume(SystemAudioManager.STREAM_ALARM)
        val maxVolume = systemAudioManager.getStreamMaxVolume(SystemAudioManager.STREAM_ALARM)
        return (currentVolume * 100) / maxVolume
    }
    
    fun isBluetoothAudioConnected(): Boolean {
        return systemAudioManager.isBluetoothA2dpOn
    }
}