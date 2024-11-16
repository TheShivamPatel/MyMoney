package com.mymoney.android.home.fragments.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mymoney.android.home.fragments.account.repository.AccountsRepository
import com.mymoney.android.home.fragments.category.repository.CategoryRepository
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.utils.DefaultCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(private val repo: CategoryRepository) : ViewModel() {

    val incomeCategory: LiveData<List<Category>> = repo.getAllIncomeCategory()
    val expenseCategory: LiveData<List<Category>> = repo.getAllExpenseCategory()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val isIncomeEmpty = repo.isIncomeCategoryEmpty()
            if (isIncomeEmpty) {
                repo.insertIncomeCategoryList(DefaultCategories.getIncomeCategories())
            }

            val isExpenseEmpty = repo.isExpenseCategoryEmpty()
            if (isExpenseEmpty) {
                repo.insertExpenseCategoryList(DefaultCategories.getExpenseCategories())
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repo.deleteCategoryFromList(category)
        }
    }


}

class CategoryViewModelProvider(private val repo: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(repo) as T
    }
}