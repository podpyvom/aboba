package com.alarmapp.bluetoothalarm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.alarmapp.bluetoothalarm.presentation.viewmodel.BluetoothViewModel
import com.alarmapp.bluetoothalarm.presentation.viewmodel.MainViewModel
import com.alarmapp.bluetoothalarm.ui.screens.MainScreen
import com.alarmapp.bluetoothalarm.ui.theme.AndroidBluetoothAlarmTheme
import com.alarmapp.bluetoothalarm.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // Permissions granted, refresh Bluetooth state
            bluetoothViewModel?.refreshBluetoothState()
        }
    }
    
    private val exactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { 
        // Check if permission was granted
        checkExactAlarmPermission()
    }
    
    private val audioFilePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { mainViewModel?.selectAudioFile(it) }
    }
    
    private var bluetoothViewModel: BluetoothViewModel? = null
    private var mainViewModel: MainViewModel? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AndroidBluetoothAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(
                        onRequestBluetoothPermissions = { requestBluetoothPermissions() },
                        onRequestExactAlarmPermission = { requestExactAlarmPermission() },
                        onSelectAudioFile = { audioFilePickerLauncher.launch("audio/*") }
                    )
                }
            }
        }
        
        checkAndRequestPermissions()
    }
    
    @Composable
    private fun MainApp(
        onRequestBluetoothPermissions: () -> Unit,
        onRequestExactAlarmPermission: () -> Unit,
        onSelectAudioFile: () -> Unit
    ) {
        val mainViewModelInstance: MainViewModel = hiltViewModel()
        val bluetoothViewModelInstance: BluetoothViewModel = hiltViewModel()
        
        // Store references for permission callbacks
        mainViewModel = mainViewModelInstance
        bluetoothViewModel = bluetoothViewModelInstance
        
        val bluetoothState by bluetoothViewModelInstance.state.collectAsState()
        
        // Check permissions on composition
        LaunchedEffect(Unit) {
            if (!bluetoothState.hasPermissions) {
                onRequestBluetoothPermissions()
            }
        }
        
        MainScreen(
            onRequestBluetoothPermissions = onRequestBluetoothPermissions,
            onRequestExactAlarmPermission = onRequestExactAlarmPermission,
            onSelectAudioFile = onSelectAudioFile
        )
    }
    
    private fun checkAndRequestPermissions() {
        // Check Bluetooth permissions
        if (!PermissionUtils.hasBluetoothPermissions(this)) {
            requestBluetoothPermissions()
        }
        
        // Check exact alarm permission
        if (!PermissionUtils.hasExactAlarmPermission(this)) {
            requestExactAlarmPermission()
        }
    }
    
    private fun requestBluetoothPermissions() {
        val permissions = PermissionUtils.getRequiredBluetoothPermissions()
        bluetoothPermissionLauncher.launch(permissions)
    }
    
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:$packageName")
            }
            exactAlarmPermissionLauncher.launch(intent)
        }
    }
    
    private fun checkExactAlarmPermission() {
        // This will be called after returning from settings
        // You can add logic here to verify if permission was granted
    }
}