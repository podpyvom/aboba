package com.alarmapp.bluetoothalarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.util.Calendar

@Composable
fun TimePicker(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedHour by remember { 
        mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) 
    }
    var selectedMinute by remember { 
        mutableStateOf(Calendar.getInstance().get(Calendar.MINUTE)) 
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.padding(32.dp),
        title = {
            Text(
                text = "Set Alarm Time",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time Display
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = String.format("%02d:%02d", selectedHour, selectedMinute),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Hour Controls
                TimePickerRow(
                    label = "Hour",
                    value = selectedHour,
                    minValue = 0,
                    maxValue = 23,
                    onValueChange = { selectedHour = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Minute Controls
                TimePickerRow(
                    label = "Minute", 
                    value = selectedMinute,
                    minValue = 0,
                    maxValue = 59,
                    onValueChange = { selectedMinute = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onTimeSelected(selectedHour, selectedMinute) }
            ) {
                Text("Set Alarm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TimePickerRow(
    label: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    if (value > minValue) onValueChange(value - 1)
                    else onValueChange(maxValue)
                }
            ) {
                Text("-")
            }
            
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.Center
            )
            
            OutlinedButton(
                onClick = { 
                    if (value < maxValue) onValueChange(value + 1)
                    else onValueChange(minValue)
                }
            ) {
                Text("+")
            }
        }
    }
}