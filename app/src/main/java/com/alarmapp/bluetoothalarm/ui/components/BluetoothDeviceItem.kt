package com.alarmapp.bluetoothalarm.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alarmapp.bluetoothalarm.data.models.BluetoothAudioDevice
import com.alarmapp.bluetoothalarm.data.models.ConnectionState

@Composable
fun BluetoothDeviceItem(
    device: BluetoothAudioDevice,
    isConnected: Boolean,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { 
            if (!isConnected && device.canConnect()) {
                onConnect()
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Bluetooth,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isConnected) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Column(
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = device.getDisplayName(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isConnected) FontWeight.Bold else FontWeight.Normal
                    )
                    
                    Text(
                        text = when (device.connectionState) {
                            ConnectionState.CONNECTED -> "Connected"
                            ConnectionState.CONNECTING -> "Connecting..."
                            ConnectionState.RECONNECTING -> "Reconnecting..."
                            ConnectionState.FAILED -> "Connection Failed"
                            ConnectionState.DISCONNECTED -> "Available"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when (device.connectionState) {
                            ConnectionState.CONNECTED -> MaterialTheme.colorScheme.primary
                            ConnectionState.CONNECTING, ConnectionState.RECONNECTING -> MaterialTheme.colorScheme.secondary
                            ConnectionState.FAILED -> MaterialTheme.colorScheme.error
                            ConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }
            }
            
            if (isConnected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Connected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}