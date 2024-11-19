package com.mymoney.android.roomDB.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val category_id: Int?,
    val account_id: Int?,
    val from_account_id: Int?,
    val to_account_id: Int?,
    val note: String?
)

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
    val accountName: String?,
    val fromAccountName: String?,
    val toAccountName: String?,
    val note: String?
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