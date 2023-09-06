package com.example.walletwise.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(private val preferenceRepository: PreferenceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    decimal = preferenceRepository.decimal.first(),
                    name = preferenceRepository.name.first(),
                    pin = preferenceRepository.pass.first()
                )
            }
        }
    }

    fun updateDecimal(decimal: Boolean) {
        _uiState.update {
            it.copy(decimal = decimal)
        }
        viewModelScope.launch {
            preferenceRepository.enableDecimal(decimal)
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updatePin(pin: String) {
        _uiState.update {
            it.copy(pin = pin)
        }
    }

    fun saveName() {
        viewModelScope.launch {
            preferenceRepository.setName(uiState.value.name)
        }
    }

    fun savePin() {
        viewModelScope.launch {
            preferenceRepository.setPass(uiState.value.pin)
        }
    }
}

data class SettingUiState(val decimal: Boolean = true, val name: String = "", val pin: String = "")