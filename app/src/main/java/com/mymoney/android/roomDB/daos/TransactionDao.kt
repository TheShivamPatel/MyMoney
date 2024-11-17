package com.mymoney.android.roomDB.daos
import androidx.lifecycle.LiveData
import com.mymoney.android.roomDB.data.Transaction
import androidx.room.*

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM mymoney_transactions_table")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM mymoney_transactions_table WHERE account_id = :accountId")
    fun getTransactionsByAccountId(accountId: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM mymoney_transactions_table WHERE category_id = :categoryId")
    fun getTransactionsByCategoryId(categoryId: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM mymoney_transactions_table WHERE from_account_id = :fromAccountId AND to_account_id = :toAccountId")
    fun getTransactionsByAccountTransfer(fromAccountId: Int, toAccountId: Int): LiveData<List<Transaction>>
}
