package com.mymoney.android.roomDB.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mymoney.android.roomDB.data.Account

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account): Long

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("SELECT * FROM mymoney_account_table")
    fun getAllAccounts(): LiveData<List<Account>>

    @Query("SELECT * FROM mymoney_account_table WHERE id = :id")
    fun getAccountById(id: Int): LiveData<Account>

    @Query("SELECT balance FROM mymoney_account_table WHERE id = :id")
    suspend fun getBalanceByAccountId(id: Int): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountList(accountList: List<Account>)

    @Query("SELECT COUNT(*) FROM mymoney_account_table")
    suspend fun getAccountCount(): Int
}
