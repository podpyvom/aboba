package com.alarmapp.bluetoothalarm.data.repository

import com.alarmapp.bluetoothalarm.data.database.AlarmDao
import com.alarmapp.bluetoothalarm.data.models.Alarm
import com.alarmapp.bluetoothalarm.domain.repository.AlarmRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepositoryInterface {
    
    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms()
    }
    
    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getAlarmById(id)
    }
    
    override suspend fun getEnabledAlarms(): List<Alarm> {
        return alarmDao.getEnabledAlarms()
    }
    
    override suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm)
    }
    
    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }
    
    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }
    
    override suspend fun deleteAlarmById(id: Long) {
        alarmDao.deleteAlarmById(id)
    }
    
    override suspend fun updateAlarmEnabled(id: Long, enabled: Boolean) {
        alarmDao.updateAlarmEnabled(id, enabled)
    }
    
    override suspend fun deleteAllAlarms() {
        alarmDao.deleteAllAlarms()
    }
}