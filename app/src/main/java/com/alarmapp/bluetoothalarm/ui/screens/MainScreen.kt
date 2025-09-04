package com.alarmapp.bluetoothalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alarmapp.bluetoothalarm.R
import com.alarmapp.bluetoothalarm.presentation.viewmodel.AlarmViewModel
import com.alarmapp.bluetoothalarm.presentation.viewmodel.BluetoothViewModel
import com.alarmapp.bluetoothalarm.presentation.viewmodel.MainViewModel
import com.alarmapp.bluetoothalarm.ui.components.AlarmSetupCard
import com.alarmapp.bluetoothalarm.ui.components.AudioFileCard
import com.alarmapp.bluetoothalarm.ui.components.BluetoothCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onRequestBluetoothPermissions: () -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    onSelectAudioFile: () -> Unit,
    alarmViewModel: AlarmViewModel = hiltViewModel(),
    bluetoothViewModel: BluetoothViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val alarmState by alarmViewModel.state.collectAsState()
    val bluetoothState by bluetoothViewModel.state.collectAsState()
    val selectedAudioFile by mainViewModel.selectedAudioFile.collectAsState()
    val mainError by mainViewModel.error.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle errors
    LaunchedEffect(alarmState.error) {
        alarmState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            alarmViewModel.clearError()
        }
    }
    
    LaunchedEffect(bluetoothState.error) {
        bluetoothState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            bluetoothViewModel.clearError()
        }
    }
    
    LaunchedEffect(mainError) {
        mainError?.let { error ->
            snackbarHostState.showSnackbar(error)
            mainViewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alarm Setup Section
            AlarmSetupCard(
                alarms = alarmState.alarms,
                isLoading = alarmState.isLoading,
                nextAlarmTime = alarmState.nextAlarmTime,
                onCreateAlarm = { hour, minute ->
                    alarmViewModel.createAlarm(hour, minute)
                },
                onToggleAlarm = { alarmId, enabled ->
                    alarmViewModel.toggleAlarm(alarmId, enabled)
                },
                onDeleteAlarm = { alarmId ->
                    alarmViewModel.deleteAlarm(alarmId)
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Bluetooth Setup Section
            BluetoothCard(
                bluetoothState = bluetoothState,
                onStartDiscovery = {
                    if (bluetoothState.hasPermissions) {
                        bluetoothViewModel.startDeviceDiscovery()
                    } else {
                        onRequestBluetoothPermissions()
                    }
                },
                onStopDiscovery = {
                    bluetoothViewModel.stopDeviceDiscovery()
                },
                onConnectDevice = { device ->
                    bluetoothViewModel.connectToDevice(device)
                },
                onDisconnectDevice = {
                    bluetoothViewModel.disconnectFromDevice()
                },
                onRefreshPermissions = {
                    bluetoothViewModel.refreshBluetoothState()
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Audio File Setup Section
            AudioFileCard(
                selectedAudioFile = selectedAudioFile,
                isLoading = mainViewModel.isLoading.collectAsState().value,
                onSelectAudioFile = onSelectAudioFile,
                onClearAudioFile = {
                    mainViewModel.clearSelectedAudioFile()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}