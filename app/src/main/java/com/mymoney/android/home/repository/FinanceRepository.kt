package com.mymoney.android.home.repository

import com.mymoney.android.roomDB.daos.TransactionDao

class FinanceRepository(
    private val transactionDao: TransactionDao
) {

    fun getTotalIncome(type: String) = transactionDao.getTotalByType(type)

    fun getTotalExpense(type: String) = transactionDao.getTotalByType(type)

    fun getTotalBalance(incomeType: String, expenseType: String) = transactionDao.getTotalBalance(incomeType, expenseType)

}
