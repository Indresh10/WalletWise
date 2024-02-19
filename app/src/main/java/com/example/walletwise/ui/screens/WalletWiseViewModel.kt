package com.example.walletwise.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.PreferenceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WalletWiseViewModel(private val preferenceRepository: PreferenceRepository) : ViewModel() {
    val name = preferenceRepository.name
    var uiState:NavUiState by mutableStateOf(NavUiState.LoadingState)
        private set
    init {
        loadStartScreen()
    }

    private fun loadStartScreen() {
        uiState = NavUiState.LoadingState
        viewModelScope.launch {
            val welcome = preferenceRepository.welcome.first()
            val pinEnabled = preferenceRepository.isPinEnabled.first()
            uiState = when {
                !welcome -> NavUiState.WelcomeState
                pinEnabled -> NavUiState.LockState
                !pinEnabled -> NavUiState.HomeState
                else -> NavUiState.LoadingState
            }
        }
    }

    fun resetToLockScreen() {
        uiState = NavUiState.LockState
    }
}

sealed interface NavUiState {
    data object LoadingState : NavUiState
    data object WelcomeState : NavUiState
    data object LockState : NavUiState
    data object HomeState : NavUiState
}
