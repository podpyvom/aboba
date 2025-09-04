package com.alarmapp.bluetoothalarm.data.models

import android.net.Uri

data class AudioFile(
    val uri: String,
    val displayName: String,
    val duration: Long,
    val mimeType: String,
    val size: Long
) {
    fun getFormattedDuration(): String {
        val minutes = duration / (1000 * 60)
        val seconds = (duration / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }
    
    fun isValidAudioFile(): Boolean {
        return mimeType.startsWith("audio/") && duration > 0
    }
    
    fun getUriObject(): Uri {
        return Uri.parse(uri)
    }
}

enum class AudioFileFormat {
    MP3, WAV, M4A, OGG, UNKNOWN;
    
    companion object {
        fun fromMimeType(mimeType: String): AudioFileFormat {
            return when {
                mimeType.contains("mp3") -> MP3
                mimeType.contains("wav") -> WAV
                mimeType.contains("m4a") || mimeType.contains("mp4") -> M4A
                mimeType.contains("ogg") -> OGG
                else -> UNKNOWN
            }
        }
    }
}