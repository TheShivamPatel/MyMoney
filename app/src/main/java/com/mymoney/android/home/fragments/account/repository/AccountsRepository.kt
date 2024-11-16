package com.mymoney.android.home.fragments.account.repository

import com.mymoney.android.roomDB.daos.AccountDao
import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.data.CategoryType

class AccountsRepository(private val accountDao: AccountDao) {

    fun getAllAccounts() = accountDao.getAllAccounts()

    suspend fun insertAccount(account: Account) = accountDao.insertAccount(account)

    suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)

    suspend fun deleteAccount(account: Account) = accountDao.deleteAccount(account)

    fun getAccountById(id: Int) = accountDao.getAccountById(id)

    suspend fun getBalanceByAccountId(id: Int): Double? {
        return accountDao.getBalanceByAccountId(id)
    }

    suspend fun isAccountsEmpty(): Boolean {
        return accountDao.getAccountCount() == 0
    }

    suspend fun insertAccountList(accountList: List<Account>) {
        accountDao.insertAccountList(accountList)
    }

}