package com.example.walletwise.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.Update
import com.example.walletwise.data.model.Accounts
import com.example.walletwise.data.model.Balance
import com.example.walletwise.data.model.Tag
import com.example.walletwise.data.model.Transaction
import com.example.walletwise.data.model.TransactionData
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletWiseDao {
    //Accounts
    @Insert
    suspend fun insertAccount(accounts: Accounts) :Long
    
    @Update
    suspend fun updateAccount(accounts: Accounts)

    @Delete
    suspend fun deleteAccount(accounts: Accounts)

    //Tag
    @Insert
    suspend fun insertTag(tag: Tag)

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    //Transaction
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // Queries

    @Query("select type,sum(cur_amount) as total from accounts group by type")
    fun getBalance(): Flow<List<Balance>>

    @Query("select * from accounts")
    fun getAccounts(): Flow<List<Accounts>>

    @Query("select * from accounts where id = :id")
    suspend fun getAccountById(id:Long) : Accounts

    @Query("select * from tag where name like :name")
    suspend fun getTagByName(name:String): Tag

    @Query("select * from tag where id = :id")
    suspend fun getTagById(id:Long):Tag

    @Query("select * from tag where type = 1")
    fun getIncomeTags(): Flow<List<Tag>>

    @Query("select * from tag where type = 0")
    fun getExpenseTags(): Flow<List<Tag>>

    @Query("select count(*) from tag")
    suspend fun getTagsCount() : Int

    @androidx.room.Transaction
    @Query("select * from transactions order by date desc")
    fun getTransactions() : Flow<List<TransactionData>>

    @androidx.room.Transaction
    @Query("select * from transactions where acc_id = :accId order by date desc")
    fun getTransactionByAccount(accId:Long) : Flow<List<TransactionData>>

    @androidx.room.Transaction
    @Query("select * from transactions where tag_id = :tagId order by date desc")
    fun getTransactionByTag(tagId:Long) : Flow<List<TransactionData>>

    @androidx.room.Transaction
    @Query("select t.* from transactions as t inner join accounts as a on acc_id=a.id where a.type = :type order by t.date desc")
    fun getTransactionByAccountType(type:String) : Flow<List<TransactionData>>

    @androidx.room.Transaction
    @Query("select t.* from transactions as t inner join tag on tag_id= tag.id where tag.type = :type order by t.date desc")
    fun getTransactionByTagType(type:Int) : Flow<List<TransactionData>>

    @androidx.room.Transaction
    @Query("select * from transactions where date between :start and :end order by date desc")
    fun getTransactionsByDate(start:String,end:String) : Flow<List<TransactionData>>
}