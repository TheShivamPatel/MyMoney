package com.mymoney.android.home.fragments.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mymoney.android.home.fragments.account.repository.AccountsRepository
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.utils.DefaultAccounts
import com.mymoney.android.utils.DefaultCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsViewModel(private val repo: AccountsRepository, private val financeRepository: FinanceRepository) : ViewModel() {

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

    val totalIncome: LiveData<Double?> = financeRepository.getTotalIncome(TransactionType.INCOME.name)

    val totalExpense: LiveData<Double?> = financeRepository.getTotalExpense(TransactionType.EXPENSE.name)

    val totalBalance: LiveData<Double?> = financeRepository.getTotalBalance(TransactionType.INCOME.name, TransactionType.EXPENSE.name)

    fun saveAccount(account: Account) {
        viewModelScope.launch {
            repo.insertAccount(account)
        }
    }

    fun updateAccount(account: Account) {
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

class AccountsViewModelProvider(private val repo: AccountsRepository, private val financeRepository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountsViewModel(repo, financeRepository) as T
    }
}