package com.example.walletwise.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class AppOfflineContainer(private val context: Context,private val dataStore: DataStore<Preferences>):AppContainer {
    override val walletDataRepository: WalletDataRepository by lazy {
        OfflineWalletDataRepository(WalletWiseDB.getInstance(context).dao())
    }
    override val preferenceRepository: PreferenceRepository
        get() = PreferenceRepository(dataStore)
}