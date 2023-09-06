package com.example.walletwise.data

import androidx.room.Query
import com.example.walletwise.data.model.Accounts
import com.example.walletwise.data.model.Balance
import com.example.walletwise.data.model.Tag
import com.example.walletwise.data.model.Transaction
import com.example.walletwise.data.model.TransactionData
import kotlinx.coroutines.flow.Flow

interface WalletDataRepository {
    //Accounts
    suspend fun insertAccount(accounts: Accounts) : Long

    suspend fun updateAccount(accounts: Accounts)

    suspend fun deleteAccount(accounts: Accounts)

    //Tag
    suspend fun insertTag(tag: Tag)

    suspend fun updateTag(tag: Tag)

    suspend fun deleteTag(tag: Tag)

    //Transaction
    suspend fun insertTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    // Queries

    fun getBalance(): Flow<List<Balance>>

    fun getAccounts(): Flow<List<Accounts>>

    suspend fun getAccountById(id:Long):Accounts

    suspend fun getTagByName(name:String): Tag

    suspend fun getTagById(id:Long):Tag

    fun getIncomeTags(): Flow<List<Tag>>

    fun getExpenseTags(): Flow<List<Tag>>

    suspend fun getTagsCount(): Int

    fun getTransactions() : Flow<List<TransactionData>>

    fun getTransactionByAccount(accId:Long) : Flow<List<TransactionData>>

    fun getTransactionByTag(tagId:Long) : Flow<List<TransactionData>>

    fun getTransactionByAccountType(type:String) : Flow<List<TransactionData>>

    fun getTransactionByTagType(type:Boolean) : Flow<List<TransactionData>>

    fun getTransactionsByDate(start:String,end:String) : Flow<List<TransactionData>>
}