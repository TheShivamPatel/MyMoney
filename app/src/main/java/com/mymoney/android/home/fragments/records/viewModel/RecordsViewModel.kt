package com.mymoney.android.home.fragments.records.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.data.AvailableFilters
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterType
import com.mymoney.android.roomDB.data.CategoryExpenseSummary
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.roomDB.data.TransactionWithDetails
import kotlinx.coroutines.launch

class RecordsViewModel(private val repo: TransactionRepository, private val financeRepository: FinanceRepository) : ViewModel() {

    val allTotalExpensesByCategory : LiveData<List<CategoryExpenseSummary>> = repo.getTotalByCategory(TransactionType.EXPENSE.name)

    val totalIncome: LiveData<Double?> = financeRepository.getTotalIncome(TransactionType.INCOME.name)

    val totalExpense: LiveData<Double?> = financeRepository.getTotalExpense(TransactionType.EXPENSE.name)

    private val filterList = AvailableFilters.getAvailableFilters()

    val allRecords = MutableLiveData<List<TransactionWithDetails>>()
    private val _unfilteredRecords = mutableListOf<TransactionWithDetails>()
    val unfilteredRecords: List<TransactionWithDetails> get() = _unfilteredRecords

    private var selectedCategories = emptyList<String>()

    init {
        observeRecords()
    }

    fun observeRecords() {
        repo.getAllTransactionsWithDetails().observeForever { response ->
            response?.let {
                _unfilteredRecords.clear()
                _unfilteredRecords.addAll(it)
                allRecords.postValue(it)
            }
        }
    }

    fun applyFilters() {

        selectedCategories = filterList.getSubFilterNamesByType(1)
        var filteredList = unfilteredRecords
        if (selectedCategories.isNotEmpty()) {
            filteredList = filteredList.filter { it.type in selectedCategories }
        }
        allRecords.postValue(filteredList)
    }

    private fun List<FilterType>.getSubFilterNamesByType(type: Int): List<String> {
        return this.firstOrNull { it.id == type }
            ?.subFilters
            ?.filter { it.isSelected }
            ?.map { it.name }
            ?: listOf()
    }
}

class RecordsViewModelProvider(private val repo: TransactionRepository, private val financeRepository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordsViewModel(repo, financeRepository) as T
    }
}