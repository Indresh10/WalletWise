package com.example.walletwise.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        Accounts::class,
        arrayOf("id"),
        arrayOf("acc_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    ), ForeignKey(
        Tag::class,
        arrayOf("id"),
        arrayOf("tag_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "acc_id", index = true)
    val accId: Long,
    @ColumnInfo(name = "tag_id",index = true)
    val tagId: Long,
    val date: String,
    val notes: String?,
    val amount: Double
)
