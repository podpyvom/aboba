package com.alarmapp.bluetoothalarm.domain.usecase

import com.alarmapp.bluetoothalarm.data.models.Alarm
import com.alarmapp.bluetoothalarm.domain.repository.AlarmRepositoryInterface
import com.alarmapp.bluetoothalarm.manager.AlarmScheduler
import javax.inject.Inject

class SetAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepositoryInterface,
    private val alarmScheduler: AlarmScheduler
) {
    
    suspend operator fun invoke(alarm: Alarm): Result<Long> {
        return try {
            // Insert or update the alarm in the database
            val alarmId = if (alarm.id == 0L) {
                alarmRepository.insertAlarm(alarm)
            } else {
                alarmRepository.updateAlarm(alarm)
                alarm.id
            }
            
            // Cancel existing alarm scheduling if any
            alarmScheduler.cancelAlarm(alarmId)
            
            // Schedule the alarm if it's enabled
            if (alarm.isEnabled) {
                val updatedAlarm = alarm.copy(id = alarmId)
                val scheduled = alarmScheduler.scheduleAlarm(updatedAlarm)
                if (!scheduled) {
                    return Result.failure(Exception("Failed to schedule alarm"))
                }
            }
            
            Result.success(alarmId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleAlarm(alarmId: Long, enabled: Boolean): Result<Unit> {
        return try {
            alarmRepository.updateAlarmEnabled(alarmId, enabled)
            
            if (enabled) {
                val alarm = alarmRepository.getAlarmById(alarmId)
                if (alarm != null) {
                    alarmScheduler.scheduleAlarm(alarm.copy(isEnabled = true))
                }
            } else {
                alarmScheduler.cancelAlarm(alarmId)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAlarm(alarmId: Long): Result<Unit> {
        return try {
            alarmScheduler.cancelAlarm(alarmId)
            alarmRepository.deleteAlarmById(alarmId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun rescheduleAllAlarms(): Result<Unit> {
        return try {
            val enabledAlarms = alarmRepository.getEnabledAlarms()
            alarmScheduler.rescheduleAllAlarms(enabledAlarms)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}