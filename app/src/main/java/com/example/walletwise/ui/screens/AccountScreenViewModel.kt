package com.example.walletwise.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwise.data.PreferenceRepository
import com.example.walletwise.data.WalletDataRepository
import com.example.walletwise.data.model.ImageType
import com.example.walletwise.data.model.Tag
import com.example.walletwise.data.model.TagType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountScreenViewModel(
    private val walletDataRepository: WalletDataRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    val balances = walletDataRepository.getBalance().filterNotNull().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(1000L),
        emptyList()
    )
    val accounts = walletDataRepository.getAccounts().filterNotNull().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(1000L), emptyList()
    )

    val showDecimal = preferenceRepository.decimal

    init {
        addTags()
    }

    private fun addTags() {
        viewModelScope.launch {
            if (walletDataRepository.getTagsCount() == 0) {
                ImageType.values().forEach {
                    walletDataRepository.insertTag(
                        Tag(
                            0,
                            it.name,
                            if (it.name != "Salary") TagType.Expense.type else TagType.Income.type,
                            it.ordinal
                        )
                    )
                }
                walletDataRepository.insertTag(
                    Tag(
                        0,
                        "Other",
                        TagType.Income.type,
                        ImageType.Other.ordinal
                    )
                )

                walletDataRepository.insertTag(
                    Tag(
                        0,
                        "Capital",
                        TagType.Income.type,
                        ImageType.Salary.ordinal
                    )
                )
            }
        }
    }
}
