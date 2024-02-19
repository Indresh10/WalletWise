package com.example.walletwise.ui.screens.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LockScreenViewModel(private val preferenceRepository: PreferenceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LockUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(validPin = preferenceRepository.pass.first())
            }
        }
    }

    fun updatePin(pass: String) {
        _uiState.update {
            it.copy(pin = pass)
        }
    }
}

data class LockUiState(val pin: String = "", val validPin: String = "")