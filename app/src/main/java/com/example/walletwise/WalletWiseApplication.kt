package com.example.walletwise

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.walletwise.data.AppContainer
import com.example.walletwise.data.AppOfflineContainer

const val PREFERENCES_NAME = "wallet_wise_preferences"
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(PREFERENCES_NAME)

class WalletWiseApplication:Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppOfflineContainer(applicationContext,dataStore)
    }
}