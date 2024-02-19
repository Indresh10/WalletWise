package com.example.walletwise.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.walletwise.data.model.Accounts
import com.example.walletwise.data.model.Tag
import com.example.walletwise.data.model.Transaction

@Database(
    entities = [Accounts::class, Tag::class, Transaction::class],
    version = 3,
    exportSchema = false
)
abstract class WalletWiseDB : RoomDatabase() {
    abstract fun dao(): WalletWiseDao

    companion object {
        @Volatile
        private var Instance: WalletWiseDB? = null

        fun getInstance(context: Context): WalletWiseDB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WalletWiseDB::class.java, "wallet_wise_database.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

    // migration 1 to 2 added icon in tag attribute
}