package com.mymoney.android.home.fragments.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mymoney.android.home.fragments.account.repository.AccountsRepository
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.utils.DefaultAccounts
import com.mymoney.android.utils.DefaultCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsViewModel(private val repo: AccountsRepository) : ViewModel() {

    val allAccounts: LiveData<List<Account>> = repo.getAllAccounts()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val isAccountsEmpty = repo.isAccountsEmpty()
            if (isAccountsEmpty) {
                repo.insertAccountList(DefaultAccounts.getAccounts())
            }
        }
    }


    fun addAccount(account: Account) {
        viewModelScope.launch {
            repo.insertAccount(account)
        }
    }

    fun modifyAccount(account: Account) {
        viewModelScope.launch {
            repo.updateAccount(account)
        }
    }

    fun removeAccount(account: Account) {
        viewModelScope.launch {
            repo.deleteAccount(account)
        }
    }

    suspend fun getBalanceByAccountId(id: Int): Double? {
        return repo.getBalanceByAccountId(id)
    }

}

class AccountsViewModelProvider(private val repo: AccountsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountsViewModel(repo) as T
    }
}