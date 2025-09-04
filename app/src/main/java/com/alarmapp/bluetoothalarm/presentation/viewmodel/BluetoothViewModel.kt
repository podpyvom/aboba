package com.alarmapp.bluetoothalarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.domain.repository.BluetoothRepositoryInterface
import com.alarmapp.bluetoothalarm.domain.usecase.ConnectBluetoothUseCase
import com.alarmapp.bluetoothalarm.presentation.state.BluetoothState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepositoryInterface,
    private val connectBluetoothUseCase: ConnectBluetoothUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(BluetoothState())
    val state: StateFlow<BluetoothState> = _state.asStateFlow()
    
    init {
        initializeBluetoothState()
        observeBluetoothEvents()
    }
    
    private fun initializeBluetoothState() {
        _state.value = _state.value.copy(
            isBluetoothEnabled = bluetoothRepository.isBluetoothEnabled(),
            hasPermissions = bluetoothRepository.hasBluetoothPermissions(),
            selectedDevice = bluetoothRepository.getSelectedDevice()
        )
        
        // Load paired devices
        val pairedDevices = connectBluetoothUseCase.getPairedDevices()
        _state.value = _state.value.copy(discoveredDevices = pairedDevices)
    }
    
    private fun observeBluetoothEvents() {
        viewModelScope.launch {
            bluetoothRepository.getDiscoveredDevices().collectLatest { devices ->
                _state.value = _state.value.copy(discoveredDevices = devices)
            }
        }
        
        viewModelScope.launch {
            bluetoothRepository.getConnectedDevice().collectLatest { device ->
                _state.value = _state.value.copy(connectedDevice = device)
            }
        }
        
        viewModelScope.launch {
            bluetoothRepository.getConnectionState().collectLatest { connectionState ->
                _state.value = _state.value.copy(connectionState = connectionState)
            }
        }
        
        viewModelScope.launch {
            bluetoothRepository.isScanning().collectLatest { isScanning ->
                _state.value = _state.value.copy(isScanning = isScanning)
            }
        }
        
        viewModelScope.launch {
            bluetoothRepository.getErrorEvents().collectLatest { error ->
                _state.value = _state.value.copy(error = error)
            }
        }
    }
    
    fun startDeviceDiscovery() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        
        val result = connectBluetoothUseCase.startDeviceDiscovery()
        result.onSuccess {
            _state.value = _state.value.copy(isLoading = false)
        }.onFailure { exception ->
            _state.value = _state.value.copy(
                isLoading = false,
                error = exception.message
            )
        }
    }
    
    fun stopDeviceDiscovery() {
        connectBluetoothUseCase.stopDeviceDiscovery()
    }
    
    fun connectToDevice(device: BluetoothAudioDevice) {
        _state.value = _state.value.copy(isLoading = true, error = null, selectedDevice = device)
        
        val result = connectBluetoothUseCase.connectToDevice(device)
        result.onSuccess {
            _state.value = _state.value.copy(isLoading = false)
        }.onFailure { exception ->
            _state.value = _state.value.copy(
                isLoading = false,
                error = exception.message
            )
        }
    }
    
    fun disconnectFromDevice() {
        connectBluetoothUseCase.disconnectFromDevice()
        _state.value = _state.value.copy(selectedDevice = null)
    }
    
    fun refreshBluetoothState() {
        _state.value = _state.value.copy(
            isBluetoothEnabled = bluetoothRepository.isBluetoothEnabled(),
            hasPermissions = bluetoothRepository.hasBluetoothPermissions()
        )
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        bluetoothRepository.cleanup()
    }
}