package com.mymoney.android.home.fragments.category.repository

import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.data.CategoryType

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllIncomeCategory() = categoryDao.getAllIncomeCategory(CategoryType.INCOME.name)

    fun getAllExpenseCategory() = categoryDao.getAllExpenseCategory(CategoryType.EXPENSE.name)

    suspend fun insertIncomeCategoryList(categoryList: List<Category>) {
        categoryDao.insertCategoryList(categoryList)
    }

    suspend fun insertExpenseCategoryList(categoryList: List<Category>) {
        categoryDao.insertCategoryList(categoryList)
    }

    suspend fun isIncomeCategoryEmpty(): Boolean {
        return categoryDao.getCategoryCount(CategoryType.INCOME.name) == 0
    }

    suspend fun isExpenseCategoryEmpty(): Boolean {
        return categoryDao.getCategoryCount(CategoryType.EXPENSE.name) == 0
    }

    suspend fun deleteCategoryFromList(category: Category) {
        categoryDao.deleteCategory(category)
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
}