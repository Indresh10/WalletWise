package com.example.walletwise.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val nameKey = stringPreferencesKey("name")
        private val passKey = stringPreferencesKey("pin")
        private val welcomeKey = booleanPreferencesKey("welcome")
        private val decimalKey = booleanPreferencesKey("decimal")
        private val pinEnabledKey = booleanPreferencesKey("pinEnabled")
    }

    suspend fun setName(name: String) {
        dataStore.edit {
            it[nameKey] = name
        }
    }

    suspend fun setPass(pass: String) {
        dataStore.edit {
            it[passKey] = pass
        }
    }

    suspend fun finishWelcome() {
        dataStore.edit {
            it[welcomeKey] = true
        }
    }

    suspend fun enableDecimal(decimal: Boolean) {
        dataStore.edit {
            it[decimalKey] = decimal
        }
    }

    suspend fun enablePin(enablePin:Boolean){
        dataStore.edit {
            it[pinEnabledKey] = enablePin
        }
    }

    val name: Flow<String> = dataStore.data.map {
        it[nameKey] ?: ""
    }

    val pass: Flow<String> = dataStore.data.map {
        it[passKey] ?: ""
    }

    val welcome: Flow<Boolean> = dataStore.data.map {
        it[welcomeKey] ?: false
    }

    val decimal: Flow<Boolean> = dataStore.data.map {
        it[decimalKey] ?: true
    }

    val isPinEnabled: Flow<Boolean> = dataStore.data.map {
        it[pinEnabledKey] ?: false
    }
}