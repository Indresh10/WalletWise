package com.example.walletwise.data

interface AppContainer {
    val walletDataRepository:WalletDataRepository
    val preferenceRepository:PreferenceRepository
}