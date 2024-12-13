package com.mymoney.android.roomDB.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "mymoney_transactions_table",
)

data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String?,
    val time: String?,
    val amount: Double,
    val type: String,
    val from_account_id: Int?,
    val category_id: Int?,
    val to_account_id: Int?,
    val note: String?
) : Serializable

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

data class TransactionWithDetails(
    val transactionId: Int,
    val date: String?,
    val time: String?,
    val amount: Double,
    val type: String,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryType: String?,
    val fromAccountName: String?,
    val toAccountName: String?,
    val note: String?
)

data class CategoryExpenseSummary(
    val categoryName: String,
    val totalAmount: Double,
    val categoryIcon: String?
)

data class CategoryExpensePercentage(
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Double,
    val categoryIcon: String?
)

//foreignKeys = [
//ForeignKey(
//entity = Category::class,
//parentColumns = ["id"],
//childColumns = ["category_id"],
//onDelete = ForeignKey.CASCADE
//),
//ForeignKey(
//entity = Account::class,
//parentColumns = ["id"],
//childColumns = ["account_id"],
//onDelete = ForeignKey.CASCADE
//),
//ForeignKey(
//entity = Account::class,
//parentColumns = ["id"],
//childColumns = ["from_account_id"],
//onDelete = ForeignKey.CASCADE
//),
//ForeignKey(
//entity = Account::class,
//parentColumns = ["id"],
//childColumns = ["to_account_id"],
//onDelete = ForeignKey.CASCADE
//)
//]