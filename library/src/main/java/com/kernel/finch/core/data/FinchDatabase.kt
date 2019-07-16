package com.kernel.finch.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.kernel.finch.core.data.models.TransactionHttpEntity

@Database(entities = [TransactionHttpEntity::class], version = 1, exportSchema = false)
abstract class FinchDatabase : RoomDatabase() {
    abstract fun transactionHttp(): TransactionHttpDao

    companion object {
        @Volatile
        private var INSTANCE: FinchDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): FinchDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        FinchDatabase::class.java,
                        "database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}