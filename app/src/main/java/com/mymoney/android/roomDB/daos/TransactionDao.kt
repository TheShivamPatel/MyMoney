package com.mymoney.android.roomDB.daos

import androidx.lifecycle.LiveData
import com.mymoney.android.roomDB.data.Transaction
import androidx.room.*
import com.mymoney.android.roomDB.data.CategoryExpenseSummary
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

    @Query("SELECT SUM(amount) FROM mymoney_transactions_table WHERE type = :type")
    fun getTotalByType(type: String): LiveData<Double?>

    @Query("SELECT (SELECT COALESCE(SUM(amount), 0) FROM mymoney_transactions_table WHERE type = :incomeType) - (SELECT COALESCE(SUM(amount), 0) FROM mymoney_transactions_table WHERE type = :expenseType)")
    fun getTotalBalance(incomeType: String, expenseType: String): LiveData<Double?>

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
    """
    )
    fun getAllTransactionsWithDetails(): LiveData<List<TransactionWithDetails>>


    @Query(
        """
    SELECT 
        c.name AS categoryName,
        c.icon AS categoryIcon,
        SUM(t.amount) AS totalAmount
    FROM mymoney_transactions_table AS t
    LEFT JOIN mymoney_category_table AS c ON t.category_id = c.id
    WHERE t.type = :type
    GROUP BY c.name, c.icon
    ORDER BY totalAmount DESC
    """
    )
    fun getTotalByCategory(type: String): LiveData<List<CategoryExpenseSummary>>


}
