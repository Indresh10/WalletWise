package com.example.walletwise.ui.screens.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.WalletDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val walletDataRepository: WalletDataRepository
) : ViewModel() {
    private val accId: Long = savedStateHandle[EditAccountDestination.accIdArg]!!

    private val _uiState = MutableStateFlow(AddAccountUiState())

    val uiState: StateFlow<AddAccountUiState> = _uiState.asStateFlow()
    fun updateAccount() {
        viewModelScope.launch {
            val account = walletDataRepository.getAccountById(accId)
            walletDataRepository.updateAccount(
                account.copy(name = _uiState.value.name, type = _uiState.value.type)
            )
        }
    }

    fun validateInput(): Boolean =
        uiState.value.name.isNotEmpty() and uiState.value.type.isNotEmpty()

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updateType(type: String) {
        _uiState.update {
            it.copy(type = type)
        }
    }

    init {
        viewModelScope.launch {
            val account = walletDataRepository.getAccountById(accId)
            updateName(account.name)
            updateType(account.type)
        }
    }
}