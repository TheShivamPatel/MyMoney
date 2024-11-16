package com.mymoney.android.roomDB.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mymoney_account_table")
data class Account (
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name: String,
    val balance: Double,
)