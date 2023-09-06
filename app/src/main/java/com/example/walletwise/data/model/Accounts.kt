package com.example.walletwise.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Accounts(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val type: String,
    @ColumnInfo(name="cur_amount")
    var curAmount: Double
)

enum class AccountType{
    Cash,Bank,Wallet
}
