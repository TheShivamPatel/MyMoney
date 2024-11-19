package com.mymoney.android.home.fragments.records.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.roomDB.data.Transaction
import com.mymoney.android.roomDB.data.TransactionWithDetails

class RecordsViewModel(private val repo: TransactionRepository) : ViewModel() {

    val allRecords: LiveData<List<Transaction>> = repo.getAllTransactions()

    val allRecordsWithDetails: LiveData<List<TransactionWithDetails>> = repo.getAllTransactionsWithDetails()

    fun getTotalIncome(): LiveData<Double?> {
        return repo.getTotalIncome()
    }

    fun getTotalExpense(): LiveData<Double?> {
        return repo.getTotalExpense()
    }
}

class RecordsViewModelProvider(private val repo: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordsViewModel(repo) as T
    }
}