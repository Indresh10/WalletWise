package com.example.walletwise.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
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
}

sealed interface NavUiState{
    object LoadingState:NavUiState
    object WelcomeState:NavUiState
    object LockState:NavUiState
    object HomeState:NavUiState
}
