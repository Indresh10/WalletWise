package com.example.walletwise.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.PreferenceRepository
import com.example.walletwise.data.WalletDataRepository
import com.example.walletwise.data.model.AccountType
import com.example.walletwise.data.model.TagType
import com.example.walletwise.data.model.TransactionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class BalanceScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val walletDataRepository: WalletDataRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState = _uiState.asStateFlow()

    private val key = savedStateHandle.get<String>(HistoryDestination.keyArg)
    private val value = savedStateHandle.get<Long>(HistoryDestination.valueArg)

    val showDecimal = preferenceRepository.decimal

    fun getKey() = key
    private fun getSubtitle() {
        viewModelScope.launch {
            val subtitle: String? = when (key) {
                "Account" -> value?.let { walletDataRepository.getAccountById(it).name }
                "Tag" -> value?.let { walletDataRepository.getTagById(it).name }
                else -> ""
            }
            val transactions = when (key) {
                "Account" -> value?.let {
                    walletDataRepository.getTransactionByAccount(it).filterNotNull().first()
                } ?: emptyList()

                "Tag" -> {
                    value?.let {
                        walletDataRepository.getTransactionByTag(it).filterNotNull().first()
                    } ?: emptyList()
                }

                "AccountType" -> {
                    value?.let {
                        walletDataRepository.getTransactionByAccountType(AccountType.values()[it.toInt()].name)
                            .filterNotNull().first()
                    } ?: emptyList()
                }

                "TagType" -> {
                    value?.let {
                        if (it != 2L)
                            walletDataRepository.getTransactionByTagType(it == 0L)
                                .filterNotNull().first()
                        else
                            walletDataRepository.getTransactions().filterNotNull().first()
                    } ?: emptyList()
                }

                "Time" -> {
                    val today = LocalDate.now()
                    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    when (value) {
                        0L -> updateDateRange(timeLayout = TimeLayout.Month,data = today.monthValue)
                        1L -> updateDateRange(timeLayout = TimeLayout.Year,data = today.year)
                        2L -> updateDateRange(timeLayout = TimeLayout.Range,
                            start = format.format(today),
                            end = format.format(today.plusDays(7))
                        )
                    }
                    emptyList()
                }

                else -> walletDataRepository.getTransactions().filterNotNull().first()
            }
            _uiState.update {
                it.copy(subtitle = subtitle ?: "")
            }
            calcAmount(transactions)
            getDatedTransactions(transactions)
        }
    }



    fun updateAccountType(accountType:AccountType){
        viewModelScope.launch {
            val transactions =
                walletDataRepository.getTransactionByAccountType(accountType.name).first()
            calcAmount(transactions)
            getDatedTransactions(transactions)
        }
    }

    fun updateTagType(tagType: TagType?){
        viewModelScope.launch {
            val transactions = if(tagType != null){
                walletDataRepository.getTransactionByTagType(tagType.type).first()
            }else{
                walletDataRepository.getTransactions().first()
            }
            calcAmount(transactions)
            getDatedTransactions(transactions)
        }
    }

    fun updateDateRange(timeLayout: TimeLayout=TimeLayout.Month, data:Int=0, start: String = "", end: String = "") {
        val today = LocalDate.now()
        lateinit var startDate: String
        lateinit var endDate: String
        when (timeLayout) {
            TimeLayout.Month -> {
                startDate = LocalDate.of(today.year, data, 1)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                endDate = YearMonth.of(today.year, data).atEndOfMonth()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }

            TimeLayout.Year -> {
                startDate = Year.of(data).atDay(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                endDate = YearMonth.of(data, Month.DECEMBER).atEndOfMonth()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }

            TimeLayout.Range -> {
                startDate =
                    LocalDate.parse(start).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                endDate = LocalDate.parse(end).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
        }
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            viewModelScope.launch {
                val transactions =
                    walletDataRepository.getTransactionsByDate(startDate, endDate).first()
                calcAmount(transactions)
                getDatedTransactions(transactions)
            }
        }
    }

    private fun calcAmount(transactions: List<TransactionData>) {
        var pAmount = 0.0
        var nAmount = 0.0
        transactions.forEach { data ->
            if (data.tag.type) {
                pAmount += data.transaction.amount
            } else {
                nAmount -= data.transaction.amount
            }
        }
        _uiState.update {
            it.copy(positiveAmt = pAmount, negativeAmt = nAmount)
        }
    }

    private fun getDatedTransactions(transactions: List<TransactionData>) {
        _uiState.update {
            it.copy(
                datedTransactions = transactions.groupBy { data ->
                    data.transaction.date
                })
        }
    }

    init {
        getSubtitle()
    }
}

data class BalanceUiState(
    val positiveAmt: Double = 0.0,
    val negativeAmt: Double = 0.0,
    val datedTransactions: Map<String, List<TransactionData>> = emptyMap(),
    val subtitle: String = ""
)

enum class TimeLayout {
    Month, Year, Range
}
