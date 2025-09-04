package com.alarmapp.bluetoothalarm.domain.repository

import com.alarmapp.bluetoothalarm.data.models.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepositoryInterface {
    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun getAlarmById(id: Long): Alarm?
    suspend fun getEnabledAlarms(): List<Alarm>
    suspend fun insertAlarm(alarm: Alarm): Long
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)
    suspend fun deleteAlarmById(id: Long)
    suspend fun updateAlarmEnabled(id: Long, enabled: Boolean)
    suspend fun deleteAllAlarms()
}