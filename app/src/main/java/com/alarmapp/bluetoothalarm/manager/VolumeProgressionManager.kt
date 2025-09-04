package com.alarmapp.bluetoothalarm.manager

import com.alarmapp.bluetoothalarm.utils.Constants
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
class VolumeProgressionManager @Inject constructor() {
    
    private val _currentVolumePercent = MutableStateFlow(Constants.INITIAL_VOLUME_PERCENT)
    val currentVolumePercent: StateFlow<Int> = _currentVolumePercent.asStateFlow()
    
    private val _isProgressionActive = MutableStateFlow(false)
    val isProgressionActive: StateFlow<Boolean> = _isProgressionActive.asStateFlow()
    
    private var progressionJob: Job? = null
    
    fun startVolumeProgression(
        onVolumeChange: (Int) -> Unit,
        onProgressionComplete: () -> Unit = {}
    ) {
        stopVolumeProgression()
        
        _isProgressionActive.value = true
        _currentVolumePercent.value = Constants.INITIAL_VOLUME_PERCENT
        
        progressionJob = CoroutineScope(Dispatchers.Main).launch {
            var currentVolume = Constants.INITIAL_VOLUME_PERCENT
            
            // Set initial volume
            onVolumeChange(currentVolume)
            _currentVolumePercent.value = currentVolume
            
            while (currentVolume < Constants.MAX_VOLUME_PERCENT && _isProgressionActive.value) {
                delay(Constants.VOLUME_INCREMENT_INTERVAL_MS)
                
                if (_isProgressionActive.value) {
                    currentVolume = (currentVolume + Constants.VOLUME_INCREMENT_PERCENT)
                        .coerceAtMost(Constants.MAX_VOLUME_PERCENT)
                    
                    onVolumeChange(currentVolume)
                    _currentVolumePercent.value = currentVolume
                }
            }
            
            // Ensure we reach max volume if progression completed naturally
            if (_isProgressionActive.value && currentVolume >= Constants.MAX_VOLUME_PERCENT) {
                onVolumeChange(Constants.MAX_VOLUME_PERCENT)
                _currentVolumePercent.value = Constants.MAX_VOLUME_PERCENT
                onProgressionComplete()
            }
            
            _isProgressionActive.value = false
        }
    }
    
    fun stopVolumeProgression() {
        _isProgressionActive.value = false
        progressionJob?.cancel()
        progressionJob = null
    }
    
    fun setVolume(volumePercent: Int, onVolumeChange: (Int) -> Unit) {
        val clampedVolume = volumePercent.coerceIn(0, 100)
        _currentVolumePercent.value = clampedVolume
        onVolumeChange(clampedVolume)
    }
    
    fun getProgressionDurationMs(): Long {
        val volumeRange = Constants.MAX_VOLUME_PERCENT - Constants.INITIAL_VOLUME_PERCENT
        val increments = volumeRange / Constants.VOLUME_INCREMENT_PERCENT
        return increments * Constants.VOLUME_INCREMENT_INTERVAL_MS
    }
    
    fun getProgressionPercentage(): Float {
        val currentVolume = _currentVolumePercent.value
        val totalRange = Constants.MAX_VOLUME_PERCENT - Constants.INITIAL_VOLUME_PERCENT
        val currentProgress = currentVolume - Constants.INITIAL_VOLUME_PERCENT
        
        return if (totalRange > 0) {
            (currentProgress.toFloat() / totalRange.toFloat() * 100f).coerceIn(0f, 100f)
        } else {
            100f
        }
    }
    
    fun cleanup() {
        stopVolumeProgression()
    }
}