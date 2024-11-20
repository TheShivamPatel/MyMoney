package com.mymoney.android.home.fragments.records.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.roomDB.data.CategoryExpenseSummary
import com.mymoney.android.roomDB.data.Transaction
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.roomDB.data.TransactionWithDetails

class RecordsViewModel(private val repo: TransactionRepository, private val financeRepository: FinanceRepository) : ViewModel() {

    val allRecords: LiveData<List<Transaction>> = repo.getAllTransactions()

    val allRecordsWithDetails: LiveData<List<TransactionWithDetails>> = repo.getAllTransactionsWithDetails()

    val allRecordsByTypeWithDetails: LiveData<List<TransactionWithDetails>> = repo.getAllTransactionsByTypeWithDetails(TransactionType.EXPENSE.name)

    val allTotalExpensesByCategory : LiveData<List<CategoryExpenseSummary>> = repo.getTotalByCategory(TransactionType.EXPENSE.name)

    val totalIncome: LiveData<Double?> = financeRepository.getTotalIncome(TransactionType.INCOME.name)

    val totalExpense: LiveData<Double?> = financeRepository.getTotalExpense(TransactionType.EXPENSE.name)

}

class RecordsViewModelProvider(private val repo: TransactionRepository, private val financeRepository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordsViewModel(repo, financeRepository) as T
    }
}