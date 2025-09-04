package com.alarmapp.bluetoothalarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alarmapp.bluetoothalarm.R
import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.data.models.ConnectionState
import com.alarmapp.bluetoothalarm.presentation.state.BluetoothState

@Composable
fun BluetoothCard(
    bluetoothState: BluetoothState,
    onStartDiscovery: () -> Unit,
    onStopDiscovery: () -> Unit,
    onConnectDevice: (BluetoothAudioDevice) -> Unit,
    onDisconnectDevice: () -> Unit,
    onRefreshPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.bluetooth_setup),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onRefreshPermissions) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bluetooth Status
            BluetoothStatus(bluetoothState = bluetoothState)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            if (!bluetoothState.isBluetoothEnabled) {
                Button(
                    onClick = { /* Open Bluetooth settings */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.enable_bluetooth))
                }
            } else if (!bluetoothState.hasPermissions) {
                Button(
                    onClick = onRefreshPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (bluetoothState.isScanning) {
                        OutlinedButton(
                            onClick = onStopDiscovery,
                            modifier = Modifier.weight(1f)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(16.dp).height(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop")
                        }
                    } else {
                        Button(
                            onClick = onStartDiscovery,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.scan_devices))
                        }
                    }
                    
                    if (bluetoothState.isConnected) {
                        OutlinedButton(
                            onClick = onDisconnectDevice,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.disconnect))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Device List
            if (bluetoothState.hasDevices) {
                Text(
                    text = "Available Devices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bluetoothState.discoveredDevices) { device ->
                        BluetoothDeviceItem(
                            device = device,
                            isConnected = bluetoothState.connectedDevice?.address == device.address,
                            onConnect = { onConnectDevice(device) }
                        )
                    }
                }
            } else if (!bluetoothState.isScanning && bluetoothState.canScan) {
                Text(
                    text = stringResource(R.string.no_devices_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun BluetoothStatus(bluetoothState: BluetoothState) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (bluetoothState.isBluetoothEnabled) 
                Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
            contentDescription = null,
            tint = if (bluetoothState.isBluetoothEnabled) 
                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = if (bluetoothState.isBluetoothEnabled) "Bluetooth Enabled" else "Bluetooth Disabled",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            bluetoothState.connectedDevice?.let { device ->
                Text(
                    text = "Connected to ${device.getDisplayName()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}