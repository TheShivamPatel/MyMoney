package com.mymoney.android.roomDB.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mymoney.android.roomDB.daos.AccountDao
import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.data.Transaction

@Database(entities = [Transaction::class, Category::class, Account::class], version = 1)
abstract class MyMoneyDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao

    companion object {

        @Volatile
        private var INSTANCE: MyMoneyDatabase? = null

        fun getDatabase(context: Context): MyMoneyDatabase {
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MyMoneyDatabase::class.java,
                    "mymoneyDB"
                )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.execSQL("PRAGMA foreign_keys=ON;")
                        }
                    })
                    .build()
            }
            return INSTANCE as MyMoneyDatabase
        }
    }

}