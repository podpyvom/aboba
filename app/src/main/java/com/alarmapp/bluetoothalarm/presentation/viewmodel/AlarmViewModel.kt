package com.alarmapp.bluetoothalarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarmapp.bluetoothalarm.data.models.Alarm
import com.alarmapp.bluetoothalarm.domain.repository.AlarmRepositoryInterface
import com.alarmapp.bluetoothalarm.domain.usecase.SetAlarmUseCase
import com.alarmapp.bluetoothalarm.presentation.state.AlarmState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepositoryInterface,
    private val setAlarmUseCase: SetAlarmUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(AlarmState())
    val state: StateFlow<AlarmState> = _state.asStateFlow()
    
    init {
        loadAlarms()
    }
    
    private fun loadAlarms() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            alarmRepository.getAllAlarms().collectLatest { alarms ->
                _state.value = _state.value.copy(
                    alarms = alarms,
                    isLoading = false,
                    nextAlarmTime = getNextAlarmTime(alarms)
                )
            }
        }
    }
    
    fun createAlarm(hour: Int, minute: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val newAlarm = Alarm(
                hour = hour,
                minute = minute,
                isEnabled = true,
                audioFilePath = null,
                audioFileName = null,
                bluetoothDeviceAddress = null,
                bluetoothDeviceName = null
            )
            
            val result = setAlarmUseCase(newAlarm)
            result.onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isAlarmSet = true,
                    error = null
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }
    
    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = setAlarmUseCase(alarm)
            result.onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }
    
    fun toggleAlarm(alarmId: Long, enabled: Boolean) {
        viewModelScope.launch {
            val result = setAlarmUseCase.toggleAlarm(alarmId, enabled)
            result.onFailure { exception ->
                _state.value = _state.value.copy(error = exception.message)
            }
        }
    }
    
    fun deleteAlarm(alarmId: Long) {
        viewModelScope.launch {
            val result = setAlarmUseCase.deleteAlarm(alarmId)
            result.onFailure { exception ->
                _state.value = _state.value.copy(error = exception.message)
            }
        }
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    private fun getNextAlarmTime(alarms: List<Alarm>): Long? {
        val enabledAlarms = alarms.filter { it.isEnabled }
        return enabledAlarms.minOfOrNull { it.getNextAlarmTimeMillis() }
    }
}