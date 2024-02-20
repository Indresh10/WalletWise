package com.example.walletwise.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.walletwise.WalletWiseApplication
import com.example.walletwise.ui.screens.WalletWiseViewModel
import com.example.walletwise.ui.screens.account.AccountScreenViewModel
import com.example.walletwise.ui.screens.account.AddAccountViewModel
import com.example.walletwise.ui.screens.account.EditAccountViewModel
import com.example.walletwise.ui.screens.extras.SettingViewModel
import com.example.walletwise.ui.screens.start.LockScreenViewModel
import com.example.walletwise.ui.screens.start.WelcomeScreenViewModel
import com.example.walletwise.ui.screens.transaction.AddTagViewModel
import com.example.walletwise.ui.screens.transaction.AddTransactionViewModel
import com.example.walletwise.ui.screens.transaction.BalanceScreenViewModel

object ViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            AccountScreenViewModel(
                walletWiseApplication().appContainer.walletDataRepository,
                walletWiseApplication().appContainer.preferenceRepository
            )
        }
        initializer {
            AddAccountViewModel(walletWiseApplication().appContainer.walletDataRepository)
        }
        initializer {
            EditAccountViewModel(
                this.createSavedStateHandle(),
                walletWiseApplication().appContainer.walletDataRepository
            )
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
