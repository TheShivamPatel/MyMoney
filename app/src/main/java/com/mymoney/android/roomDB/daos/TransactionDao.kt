package com.mymoney.android.roomDB.daos

import androidx.lifecycle.LiveData
import com.mymoney.android.roomDB.data.Transaction
import androidx.room.*
import com.mymoney.android.roomDB.data.TransactionWithDetails

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
    fun getTransactionsByAccountTransfer(
        fromAccountId: Int,
        toAccountId: Int
    ): LiveData<List<Transaction>>

    @Query(
        """
        SELECT 
            t.id AS transactionId, 
            t.date, 
            t.time, 
            t.amount, 
            t.type, 
            c.name AS categoryName, 
            c.icon AS categoryIcon, 
            c.type AS categoryType, 
            a.name AS accountName, 
            fa.name AS fromAccountName, 
            ta.name AS toAccountName, 
            t.note
        FROM mymoney_transactions_table AS t
        LEFT JOIN mymoney_category_table AS c ON t.category_id = c.id
        LEFT JOIN mymoney_account_table AS a ON t.account_id = a.id
        LEFT JOIN mymoney_account_table AS fa ON t.from_account_id = fa.id
        LEFT JOIN mymoney_account_table AS ta ON t.to_account_id = ta.id
        ORDER BY t.date DESC, t.time DESC
    """
    )
    fun getAllTransactionsWithDetails(): LiveData<List<TransactionWithDetails>>

    @Query(
        """
        SELECT 
            t.id AS transactionId, 
            t.date, 
            t.time, 
            t.amount, 
            t.type, 
            c.name AS categoryName, 
            c.icon AS categoryIcon, 
            c.type AS categoryType, 
            a.name AS accountName, 
            fa.name AS fromAccountName, 
            ta.name AS toAccountName, 
            t.note
        FROM mymoney_transactions_table AS t
        LEFT JOIN mymoney_category_table AS c ON t.category_id = c.id
        LEFT JOIN mymoney_account_table AS a ON t.account_id = a.id
        LEFT JOIN mymoney_account_table AS fa ON t.from_account_id = fa.id
        LEFT JOIN mymoney_account_table AS ta ON t.to_account_id = ta.id
        WHERE (:types IS NULL OR t.type IN (:types))
        ORDER BY t.date DESC, t.time DESC
    """
    )
    fun getFilteredTransactionsWithDetails(
        types: List<String>?
    ): LiveData<List<TransactionWithDetails>>

    @Query("SELECT SUM(amount) FROM mymoney_transactions_table WHERE type = 'income'")
    fun getTotalIncome(): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM mymoney_transactions_table WHERE type = 'expense'")
    fun getTotalExpense(): LiveData<Double?>
}
