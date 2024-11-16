package com.mymoney.android.roomDB.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.data.Category

@Database(entities = [Category::class], version = 1)
abstract class MyMoneyDatabase : RoomDatabase(){

    abstract fun categoryDao() : CategoryDao

    companion object {

        @Volatile
        private var INSTANCE : MyMoneyDatabase? = null

        fun getDatabase(context: Context) : MyMoneyDatabase {
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MyMoneyDatabase::class.java,
                    "mymoneyDB"
                ).build()
            }
            return INSTANCE as MyMoneyDatabase
        }
    }

}