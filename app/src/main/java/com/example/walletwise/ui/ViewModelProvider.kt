package com.example.walletwise.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.walletwise.WalletWiseApplication
import com.example.walletwise.ui.screens.AccountScreenViewModel
import com.example.walletwise.ui.screens.AddAccountViewModel
import com.example.walletwise.ui.screens.AddTagViewModel
import com.example.walletwise.ui.screens.AddTransactionViewModel
import com.example.walletwise.ui.screens.BalanceScreenViewModel
import com.example.walletwise.ui.screens.LockScreenViewModel
import com.example.walletwise.ui.screens.SettingViewModel
import com.example.walletwise.ui.screens.WalletWiseViewModel
import com.example.walletwise.ui.screens.WelcomeScreenViewModel

object ViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            AccountScreenViewModel(walletWiseApplication().appContainer.walletDataRepository,walletWiseApplication().appContainer.preferenceRepository)
        }
        initializer {
            AddAccountViewModel(walletWiseApplication().appContainer.walletDataRepository)
        }
        initializer {
            AddTransactionViewModel(
                this.createSavedStateHandle(),
                walletWiseApplication().appContainer.walletDataRepository
            )
        }
        initializer {
            AddTagViewModel(
                this.createSavedStateHandle(),
                walletWiseApplication().appContainer.walletDataRepository
            )
        }
        initializer {
            BalanceScreenViewModel(
                this.createSavedStateHandle(),
                walletWiseApplication().appContainer.walletDataRepository,
                walletWiseApplication().appContainer.preferenceRepository
            )
        }
        initializer {
            WelcomeScreenViewModel(walletWiseApplication().appContainer.preferenceRepository)
        }
        initializer {
            WalletWiseViewModel(walletWiseApplication().appContainer.preferenceRepository)
        }
        initializer {
            LockScreenViewModel(walletWiseApplication().appContainer.preferenceRepository)
        }
        initializer {
            SettingViewModel(walletWiseApplication().appContainer.preferenceRepository)
        }
    }
}

fun CreationExtras.walletWiseApplication(): WalletWiseApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WalletWiseApplication)
