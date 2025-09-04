package com.alarmapp.bluetoothalarm.di

import android.content.Context
import androidx.room.Room
import com.alarmapp.bluetoothalarm.data.database.AlarmDao
import com.alarmapp.bluetoothalarm.data.database.AlarmDatabase
import com.alarmapp.bluetoothalarm.data.repository.AlarmRepository
import com.alarmapp.bluetoothalarm.data.repository.AudioFileRepository
import com.alarmapp.bluetoothalarm.data.repository.BluetoothRepository
import com.alarmapp.bluetoothalarm.domain.repository.AlarmRepositoryInterface
import com.alarmapp.bluetoothalarm.domain.repository.BluetoothRepositoryInterface
import com.alarmapp.bluetoothalarm.manager.AlarmScheduler
import com.alarmapp.bluetoothalarm.manager.AudioManager
import com.alarmapp.bluetoothalarm.manager.BluetoothManager
import com.alarmapp.bluetoothalarm.manager.VolumeProgressionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAlarmRepository(
        alarmRepository: AlarmRepository
    ): AlarmRepositoryInterface
    
    @Binds
    @Singleton
    abstract fun bindBluetoothRepository(
        bluetoothRepository: BluetoothRepository
    ): BluetoothRepositoryInterface
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAlarmDatabase(@ApplicationContext context: Context): AlarmDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AlarmDatabase::class.java,
            "alarm_database"
        ).fallbackToDestructiveMigration().build()
    }
    
    @Provides
    fun provideAlarmDao(database: AlarmDatabase): AlarmDao {
        return database.alarmDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {
    
    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager {
        return BluetoothManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
        return AudioManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmScheduler(context)
    }
    
    @Provides
    @Singleton
    fun provideVolumeProgressionManager(): VolumeProgressionManager {
        return VolumeProgressionManager()
    }
    
    @Provides
    @Singleton
    fun provideAudioFileRepository(@ApplicationContext context: Context): AudioFileRepository {
        return AudioFileRepository(context)
    }
}