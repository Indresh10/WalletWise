package com.example.walletwise.data

import com.example.walletwise.data.model.Accounts
import com.example.walletwise.data.model.Balance
import com.example.walletwise.data.model.Tag
import com.example.walletwise.data.model.Transaction
import com.example.walletwise.data.model.TransactionData
import kotlinx.coroutines.flow.Flow

class OfflineWalletDataRepository(private val walletWiseDao: WalletWiseDao) : WalletDataRepository {
    override suspend fun insertAccount(accounts: Accounts): Long =
        walletWiseDao.insertAccount(accounts)

    override suspend fun updateAccount(accounts: Accounts) = walletWiseDao.updateAccount(accounts)

    override suspend fun deleteAccount(accounts: Accounts) = walletWiseDao.deleteAccount(accounts)

    override suspend fun insertTag(tag: Tag) = walletWiseDao.insertTag(tag)

    override suspend fun updateTag(tag: Tag) = walletWiseDao.updateTag(tag)

    override suspend fun deleteTag(tag: Tag) = walletWiseDao.deleteTag(tag)

    override suspend fun insertTransaction(transaction: Transaction) =
        walletWiseDao.insertTransaction(transaction)

    override suspend fun updateTransaction(transaction: Transaction) =
        walletWiseDao.updateTransaction(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) =
        walletWiseDao.deleteTransaction(transaction)

    override fun getBalance(): Flow<List<Balance>> = walletWiseDao.getBalance()

    override fun getAccounts(): Flow<List<Accounts>> = walletWiseDao.getAccounts()

    override suspend fun getAccountById(id: Long): Accounts = walletWiseDao.getAccountById(id)

    override suspend fun getTagByName(name: String): Tag = walletWiseDao.getTagByName(name)

    override suspend fun getTagById(id: Long): Tag = walletWiseDao.getTagById(id)

    override fun getIncomeTags(): Flow<List<Tag>> = walletWiseDao.getIncomeTags()

    override fun getExpenseTags(): Flow<List<Tag>> = walletWiseDao.getExpenseTags()

    override suspend fun getTagsCount(): Int = walletWiseDao.getTagsCount()

    override fun getTransactions(): Flow<List<TransactionData>> = walletWiseDao.getTransactions()

    override fun getTransactionByAccount(accId: Long): Flow<List<TransactionData>> =
        walletWiseDao.getTransactionByAccount(accId)

    override fun getTransactionByTag(tagId: Long): Flow<List<TransactionData>> =
        walletWiseDao.getTransactionByTag(tagId)

    override fun getTransactionByAccountType(type: String): Flow<List<TransactionData>> =
        walletWiseDao.getTransactionByAccountType(type)

    override fun getTransactionByTagType(type: Boolean): Flow<List<TransactionData>> =
        walletWiseDao.getTransactionByTagType(if (type) 1 else 0)

    override fun getTransactionsByDate(start:String,end:String) : Flow<List<TransactionData>> =
        walletWiseDao.getTransactionsByDate(start, end)
}