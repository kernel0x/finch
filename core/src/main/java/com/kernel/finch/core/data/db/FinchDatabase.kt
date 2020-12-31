package com.kernel.finch.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity.Companion.TABLE_NAME

@Database(entities = [NetworkLogEntity::class], version = 1, exportSchema = false)
internal abstract class FinchDatabase : RoomDatabase() {

    abstract fun networkLog(): NetworkLogDao

    companion object {

        @Volatile
        private var instance: FinchDatabase? = null

        fun getInstance(context: Context): FinchDatabase =
            instance ?: synchronized(this) {
                val newInstance = instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FinchDatabase::class.java,
                    TABLE_NAME
                ).build().also { instance = it }
                newInstance
            }
    }
}
