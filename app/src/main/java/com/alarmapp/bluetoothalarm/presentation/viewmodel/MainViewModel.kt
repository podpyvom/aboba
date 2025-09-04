package com.alarmapp.bluetoothalarm.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarmapp.bluetoothalarm.data.models.AudioFile
import com.alarmapp.bluetoothalarm.domain.usecase.SelectAudioFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val selectAudioFileUseCase: SelectAudioFileUseCase
) : ViewModel() {
    
    private val _selectedAudioFile = MutableStateFlow<AudioFile?>(null)
    val selectedAudioFile: StateFlow<AudioFile?> = _selectedAudioFile.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadSelectedAudioFile()
    }
    
    private fun loadSelectedAudioFile() {
        _selectedAudioFile.value = selectAudioFileUseCase.getSelectedAudioFile()
    }
    
    fun selectAudioFile(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = selectAudioFileUseCase.selectAudioFile(uri)
            result.onSuccess { audioFile ->
                _selectedAudioFile.value = audioFile
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
        }
    }
    
    fun clearSelectedAudioFile() {
        _selectedAudioFile.value = null
        selectAudioFileUseCase.clearSelectedAudioFile()
    }
    
    fun clearError() {
        _error.value = null
    }
}