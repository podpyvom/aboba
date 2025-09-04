package com.alarmapp.bluetoothalarm.presentation.state

import com.alarmapp.bluetoothalarm.data.models.Alarm

data class AlarmState(
    val alarms: List<Alarm> = emptyList(),
    val currentAlarm: Alarm? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAlarmSet: Boolean = false,
    val nextAlarmTime: Long? = null
) {
    val hasAlarms: Boolean get() = alarms.isNotEmpty()
    val hasEnabledAlarms: Boolean get() = alarms.any { it.isEnabled }
    val enabledAlarmsCount: Int get() = alarms.count { it.isEnabled }
}