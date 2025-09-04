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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alarmapp.bluetoothalarm.R
import com.alarmapp.bluetoothalarm.data.models.Alarm
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlarmSetupCard(
    alarms: List<Alarm>,
    isLoading: Boolean,
    nextAlarmTime: Long?,
    onCreateAlarm: (hour: Int, minute: Int) -> Unit,
    onToggleAlarm: (alarmId: Long, enabled: Boolean) -> Unit,
    onDeleteAlarm: (alarmId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
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
                    text = stringResource(R.string.alarm_setup),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                FloatingActionButton(
                    onClick = { showTimePicker = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Alarm")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Next alarm info
            nextAlarmTime?.let { time ->
                val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                Text(
                    text = "Next alarm: ${formatter.format(Date(time))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Alarms list
            if (alarms.isEmpty()) {
                Text(
                    text = "No alarms set",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alarms) { alarm ->
                        AlarmItem(
                            alarm = alarm,
                            onToggle = { enabled -> onToggleAlarm(alarm.id, enabled) },
                            onDelete = { onDeleteAlarm(alarm.id) }
                        )
                    }
                }
            }
        }
    }
    
    if (showTimePicker) {
        TimePicker(
            onTimeSelected = { hour, minute ->
                onCreateAlarm(hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
private fun AlarmItem(
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = alarm.getFormattedTime(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (alarm.audioFileName != null) {
                    Text(
                        text = alarm.audioFileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete alarm",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}