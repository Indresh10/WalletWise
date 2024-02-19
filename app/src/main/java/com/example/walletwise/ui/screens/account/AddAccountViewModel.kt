package com.example.walletwise.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.WalletDataRepository
import com.example.walletwise.data.model.Accounts
import com.example.walletwise.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddAccountViewModel(private val walletDataRepository: WalletDataRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddAccountUiState())

    val uiState: StateFlow<AddAccountUiState> = _uiState.asStateFlow()
    fun saveAccount() {
        viewModelScope.launch {
            val accId = walletDataRepository.insertAccount(
                Accounts(
                    0,
                    uiState.value.name,
                    uiState.value.type,
                    uiState.value.amount.toDoubleOrNull() ?: 0.0
                )
            )
            val tag = walletDataRepository.getTagByName("Capital")
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            walletDataRepository.insertTransaction(Transaction(0,accId,tag.id,sdf.format(Date()),null,uiState.value.amount.toDoubleOrNull() ?: 0.0))
        }
    }

    fun validateInput(): Boolean =
        uiState.value.name.isNotEmpty() and uiState.value.type.isNotEmpty() and ((uiState.value.amount.toDoubleOrNull()
            ?: 0.0) > 0.0)

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

    fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amount = amount)
        }
    }
}

data class AddAccountUiState(val name: String = "", val type: String = "", val amount: String = "")