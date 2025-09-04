package com.alarmapp.bluetoothalarm.domain.usecase

import android.net.Uri
import com.alarmapp.bluetoothalarm.data.models.AudioFile
import com.alarmapp.bluetoothalarm.data.repository.AudioFileRepository
import javax.inject.Inject

class SelectAudioFileUseCase @Inject constructor(
    private val audioFileRepository: AudioFileRepository
) {
    
    fun selectAudioFile(uri: Uri): Result<AudioFile> {
        return try {
            val audioFile = audioFileRepository.getAudioFileFromUri(uri)
            
            if (audioFile == null) {
                return Result.failure(Exception("Failed to read audio file"))
            }
            
            if (!audioFile.isValidAudioFile()) {
                return Result.failure(Exception("Invalid audio file format"))
            }
            
            audioFileRepository.saveSelectedAudioFile(audioFile)
            Result.success(audioFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getSelectedAudioFile(): AudioFile? {
        return audioFileRepository.getSelectedAudioFile()
    }
    
    fun validateAudioFile(uri: Uri): Boolean {
        return audioFileRepository.validateAudioFile(uri)
    }
    
    fun clearSelectedAudioFile() {
        // Implementation would clear the saved audio file from preferences
        // For now, we'll leave this as a placeholder
    }
}