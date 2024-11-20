package com.mymoney.android.addEditRecord.repository

import androidx.lifecycle.LiveData
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAllTransactions() = transactionDao.getAllTransactions()

    fun getAllTransactionsWithDetails() = transactionDao.getAllTransactionsWithDetails()

    fun getAllTransactionsByTypeWithDetails(type: String) = transactionDao.getAllTransactionsByTypeWithDetails(type)

    fun getTotalByCategory(type: String) = transactionDao.getTotalByCategory(type)

    fun getTotalIncome(): LiveData<Double?> {
        return transactionDao.getTotalIncome()
    }

    fun getTotalExpense(): LiveData<Double?> {
        return transactionDao.getTotalExpense()
    }

    suspend fun insertTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction)
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            transactionDao.updateTransaction(transaction)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            transactionDao.deleteTransaction(transaction)
        }
    }
}
