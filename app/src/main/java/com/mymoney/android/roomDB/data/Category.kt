package com.mymoney.android.roomDB.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mymoney_category_table")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name: String,
    val icon: String,
    val type: String,
)

enum class CategoryType {
    INCOME,
    EXPENSE
}
