package com.example.walletwise.ui.screens.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.WalletDataRepository
import com.example.walletwise.data.model.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddTagViewModel(
    savedStateHandle: SavedStateHandle,
    private val walletDataRepository: WalletDataRepository
) : ViewModel() {
    val type = savedStateHandle[AddTagDestination.typeArg] ?: false
    val tags = if (type) walletDataRepository.getIncomeTags().filterNotNull().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), emptyList()
    ) else walletDataRepository.getExpenseTags().filterNotNull().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), emptyList()
    )

    private val _uiState = MutableStateFlow(AddTagUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updateIcon(icon: Int) {
        _uiState.update {
            it.copy(icon = icon)
        }
    }

    fun validateTag(): Boolean = uiState.value.name.isNotEmpty()

    fun saveTag() {
        viewModelScope.launch {
            walletDataRepository.insertTag(Tag(0, uiState.value.name, type, uiState.value.icon))
        }
    }
}

data class AddTagUiState(
    val name: String = "",
    val icon: Int = 0
)