package com.mymoney.android.addEditRecord.repository

import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    fun getAllTransactions() = transactionDao.getAllTransactions()

    fun getAllTransactionsWithDetails() = transactionDao.getAllTransactionsWithDetails()

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

    suspend fun getTransactionById(transactionId: Int): Transaction? {
        return transactionDao.getTransactionById(transactionId)
    }

    suspend fun deleteTransaction(transactionId: Int) {
        withContext(Dispatchers.IO) {
            transactionDao.deleteTransaction(transactionId)
        }
    }

    suspend fun getCategoryNameById(id: Int): String {
        return transactionDao.getCategoryNameById(id)
    }

    suspend fun getAccountNameById(id: Int): String {
        return transactionDao.getAccountNameById(id)
    }

    suspend fun addAmountToAccount(id: Int, amount: Double) {
        return transactionDao.addAmountToAccount(id, amount)
    }

    suspend fun subtractAmountToAccount(id: Int, amount: Double) {
        return transactionDao.subtractAmountToAccount(id, amount)
    }
}
