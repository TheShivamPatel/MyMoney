package com.mymoney.android.roomDB.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mymoney.android.roomDB.data.Category

@Dao
interface CategoryDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category) : Long

    @Delete
    suspend fun deleteCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryList(categoryList: List<Category>)

    @Query("SELECT * FROM mymoney_category_table WHERE type = :type")
    fun getAllIncomeCategory(type: String) : LiveData<List<Category>>

    @Query("SELECT * FROM mymoney_category_table WHERE type = :type")
    fun getAllExpenseCategory(type: String) : LiveData<List<Category>>

    @Query("SELECT COUNT(*) FROM mymoney_category_table WHERE type = :type")
    suspend fun getCategoryCount(type: String): Int
}