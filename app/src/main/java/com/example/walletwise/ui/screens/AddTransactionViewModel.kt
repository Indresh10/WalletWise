package com.example.walletwise.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.WalletDataRepository
import com.example.walletwise.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val walletDataRepository: WalletDataRepository
) : ViewModel() {
    private val type: Boolean = savedStateHandle[AddTransactionDestination.typeArg]!!
    private val accId: Long = savedStateHandle[AddTransactionDestination.accIdArg]!!

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState = _uiState.asStateFlow()

    val tags = if (type) walletDataRepository.getIncomeTags().filterNotNull().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), emptyList()
    ) else walletDataRepository.getExpenseTags().filterNotNull().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), emptyList()
    )

    fun getType(): Boolean = type

    fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amount = amount)
        }
    }

    private fun updateDate(date: String) {
        _uiState.update {
            it.copy(date = date)
        }
    }

    fun validateAndUpdateDate(time: Long) {
        val date = Date(time)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        val dateString = sdf.format(date)
        if (dateString == sdf.format(today)) {
            updateDate("Today")
        } else {
            updateDate(dateString)
        }

    }

    fun updateNotes(notes: String) {
        _uiState.update {
            it.copy(notes = notes)
        }
    }

    fun validateTransaction(): Boolean =
        ((uiState.value.amount.toDoubleOrNull() ?: 0.0) > 0.0) and uiState.value.date.isNotEmpty()

    fun saveTransactions(tagId: Long) {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            val account = walletDataRepository.getAccountById(accId)
            if (type)
                account.curAmount += uiState.value.amount.toDoubleOrNull() ?: 0.0
            else
                account.curAmount -= uiState.value.amount.toDoubleOrNull() ?: 0.0

            if (account.curAmount < 0.0)
                account.curAmount = 0.0
            walletDataRepository.updateAccount(account)
            val notes: String? = uiState.value.notes?.let {
                it.ifEmpty { null }
            }
            walletDataRepository.insertTransaction(
                Transaction(
                    0,
                    accId,
                    tagId,
                    if (uiState.value.date == "Today") todayDate else uiState.value.date,
                    notes,
                    uiState.value.amount.toDoubleOrNull() ?: 0.0
                )
            )
        }
    }
}

data class AddTransactionUiState(
    val amount: String = "",
    val date: String = "Today",
    val notes: String? = null
)