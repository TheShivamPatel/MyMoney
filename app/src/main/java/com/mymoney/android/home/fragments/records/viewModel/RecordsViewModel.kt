package com.mymoney.android.home.fragments.records.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.roomDB.data.Transaction

class RecordsViewModel(private val repo: TransactionRepository) : ViewModel() {

    val allRecords: LiveData<List<Transaction>> = repo.getAllTransactions()

}

class RecordsViewModelProvider(private val repo: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordsViewModel(repo) as T
    }
}