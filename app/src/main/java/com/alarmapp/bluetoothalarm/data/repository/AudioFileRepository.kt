package com.alarmapp.bluetoothalarm.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.alarmapp.bluetoothalarm.data.models.AudioFile
import com.alarmapp.bluetoothalarm.utils.getSelectedAudioFile
import com.alarmapp.bluetoothalarm.utils.getSharedPrefs
import com.alarmapp.bluetoothalarm.utils.saveAudioFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioFileRepository @Inject constructor(
    private val context: Context
) {
    
    fun getAudioFileFromUri(uri: Uri): AudioFile? {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.MIME_TYPE,
                    MediaStore.Audio.Media.SIZE
                ),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayName = cursor.getStringOrNull(MediaStore.Audio.Media.DISPLAY_NAME) ?: "Unknown"
                    val duration = cursor.getLongOrNull(MediaStore.Audio.Media.DURATION) ?: 0L
                    val mimeType = cursor.getStringOrNull(MediaStore.Audio.Media.MIME_TYPE) ?: "audio/*"
                    val size = cursor.getLongOrNull(MediaStore.Audio.Media.SIZE) ?: 0L
                    
                    AudioFile(
                        uri = uri.toString(),
                        displayName = displayName,
                        duration = duration,
                        mimeType = mimeType,
                        size = size
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun saveSelectedAudioFile(audioFile: AudioFile) {
        context.getSharedPrefs().saveAudioFile(audioFile)
    }
    
    fun getSelectedAudioFile(): AudioFile? {
        return context.getSharedPrefs().getSelectedAudioFile()
    }
    
    fun validateAudioFile(uri: Uri): Boolean {
        return try {
            val audioFile = getAudioFileFromUri(uri)
            audioFile?.isValidAudioFile() == true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun Cursor.getStringOrNull(columnName: String): String? {
        val columnIndex = getColumnIndex(columnName)
        return if (columnIndex != -1 && !isNull(columnIndex)) {
            getString(columnIndex)
        } else {
            null
        }
    }
    
    private fun Cursor.getLongOrNull(columnName: String): Long? {
        val columnIndex = getColumnIndex(columnName)
        return if (columnIndex != -1 && !isNull(columnIndex)) {
            getLong(columnIndex)
        } else {
            null
        }
    }
}