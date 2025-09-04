package com.alarmapp.bluetoothalarm.manager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager as SystemBluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.data.models.ConnectionState
import com.alarmapp.bluetoothalarm.utils.Constants
import com.alarmapp.bluetoothalarm.utils.PermissionUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothManager @Inject constructor(
    private val context: Context
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as SystemBluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    
    private val _discoveredDevices = MutableStateFlow<List<BluetoothAudioDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothAudioDevice>> = _discoveredDevices.asStateFlow()
    
    private val _connectedDevice = MutableStateFlow<BluetoothAudioDevice?>(null)
    val connectedDevice: StateFlow<BluetoothAudioDevice?> = _connectedDevice.asStateFlow()
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val _errorEvents = Channel<String>(Channel.BUFFERED)
    val errorEvents: Flow<String> = _errorEvents.receiveAsFlow()
    
    private val deviceFoundReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    handleDeviceFound(intent)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isScanning.value = false
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    handleDeviceConnected(intent)
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    handleDeviceDisconnected(intent)
                }
            }
        }
    }
    
    private var isReceiverRegistered = false
    
    init {
        registerBluetoothReceiver()
    }
    
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
    fun hasBluetoothPermissions(): Boolean {
        return PermissionUtils.hasBluetoothPermissions(context)
    }
    
    @SuppressLint("MissingPermission")
    fun startDeviceDiscovery(): Boolean {
        if (!hasBluetoothPermissions()) {
            sendError("Bluetooth permissions not granted")
            return false
        }
        
        if (!isBluetoothEnabled()) {
            sendError("Bluetooth is not enabled")
            return false
        }
        
        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }
            
            _discoveredDevices.value = emptyList()
            _isScanning.value = true
            
            return adapter.startDiscovery()
        }
        
        return false
    }
    
    @SuppressLint("MissingPermission")
    fun stopDeviceDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
        _isScanning.value = false
    }
    
    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothAudioDevice> {
        if (!hasBluetoothPermissions()) {
            return emptyList()
        }
        
        return bluetoothAdapter?.bondedDevices?.mapNotNull { device ->
            if (isAudioDevice(device)) {
                BluetoothAudioDevice(
                    address = device.address,
                    name = device.name ?: "Unknown Device",
                    connectionState = ConnectionState.DISCONNECTED,
                    lastConnected = null
                )
            } else null
        } ?: emptyList()
    }
    
    @SuppressLint("MissingPermission")
    fun connectToDevice(deviceAddress: String): Boolean {
        if (!hasBluetoothPermissions()) {
            sendError("Bluetooth permissions not granted")
            return false
        }
        
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        if (device == null) {
            sendError("Device not found")
            return false
        }
        
        _connectionState.value = ConnectionState.CONNECTING
        
        // Note: Actual connection implementation would use BluetoothA2dp or other profiles
        // This is a simplified version for the basic structure
        return true
    }
    
    @SuppressLint("MissingPermission")
    fun disconnectFromDevice() {
        _connectionState.value = ConnectionState.DISCONNECTED
        _connectedDevice.value = null
    }
    
    @SuppressLint("MissingPermission")
    private fun handleDeviceFound(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            handleDeviceFoundApi33(intent)
        } else {
            handleDeviceFoundLegacy(intent)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    private fun handleDeviceFoundApi33(intent: Intent) {
        val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
        
        device?.let { handleFoundDevice(it, rssi) }
    }
    
    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun handleDeviceFoundLegacy(intent: Intent) {
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
        
        device?.let { handleFoundDevice(it, rssi) }
    }
    
    @SuppressLint("MissingPermission")
    private fun handleFoundDevice(device: BluetoothDevice, rssi: Int) {
        if (isAudioDevice(device)) {
            val audioDevice = BluetoothAudioDevice(
                address = device.address,
                name = device.name ?: "Unknown Device",
                connectionState = ConnectionState.DISCONNECTED,
                lastConnected = null,
                rssi = rssi
            )
            
            val currentDevices = _discoveredDevices.value.toMutableList()
            if (currentDevices.none { it.address == audioDevice.address }) {
                currentDevices.add(audioDevice)
                _discoveredDevices.value = currentDevices
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun handleDeviceConnected(intent: Intent) {
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        }
        
        device?.let {
            if (isAudioDevice(it)) {
                val audioDevice = BluetoothAudioDevice(
                    address = it.address,
                    name = it.name ?: "Unknown Device",
                    connectionState = ConnectionState.CONNECTED,
                    lastConnected = System.currentTimeMillis()
                )
                _connectedDevice.value = audioDevice
                _connectionState.value = ConnectionState.CONNECTED
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun handleDeviceDisconnected(intent: Intent) {
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        }
        
        device?.let {
            if (_connectedDevice.value?.address == it.address) {
                _connectedDevice.value = null
                _connectionState.value = ConnectionState.DISCONNECTED
            }
        }
    }
    
    private fun isAudioDevice(device: BluetoothDevice): Boolean {
        return device.bluetoothClass?.majorDeviceClass == android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO
    }
    
    private fun registerBluetoothReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            context.registerReceiver(deviceFoundReceiver, filter)
            isReceiverRegistered = true
        }
    }
    
    private fun sendError(message: String) {
        _errorEvents.trySend(message)
    }
    
    fun cleanup() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(deviceFoundReceiver)
                isReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                // Receiver was not registered
            }
        }
        stopDeviceDiscovery()
    }
}