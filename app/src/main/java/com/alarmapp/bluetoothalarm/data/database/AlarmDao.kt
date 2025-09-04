package com.alarmapp.bluetoothalarm.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alarmapp.bluetoothalarm.data.models.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    
    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    fun getAllAlarms(): Flow<List<Alarm>>
    
    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): Alarm?
    
    @Query("SELECT * FROM alarms WHERE isEnabled = 1")
    suspend fun getEnabledAlarms(): List<Alarm>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm): Long
    
    @Update
    suspend fun updateAlarm(alarm: Alarm)
    
    @Delete
    suspend fun deleteAlarm(alarm: Alarm)
    
    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarmById(id: Long)
    
    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun updateAlarmEnabled(id: Long, enabled: Boolean)
    
    @Query("DELETE FROM alarms")
    suspend fun deleteAllAlarms()
}