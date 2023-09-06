package com.example.walletwise.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionData(
    @Relation(Accounts::class,"acc_id","id")
    val accounts: Accounts,
    @Relation(Tag::class,"tag_id","id")
    val tag: Tag,
    @Embedded
    val transaction: Transaction
)
