package com.kernel.finch.core.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity
import com.kernel.finch.common.loggers.data.models.NetworkLogEntity.Companion.TABLE_NAME

@Dao
internal interface NetworkLogDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY requestDate DESC, id DESC")
    fun getAll(): LiveData<List<NetworkLogEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE path LIKE '%' || :search ||'%' ORDER BY requestDate DESC, id DESC")
    fun getAll(search: String): LiveData<List<NetworkLogEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE responseCode LIKE '%' || :responseCode ||'%' ORDER BY requestDate DESC, id DESC")
    fun getAll(responseCode: Int?): LiveData<List<NetworkLogEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getById(id: Long): LiveData<NetworkLogEntity>

    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("DELETE FROM $TABLE_NAME WHERE requestDate <= :threshold")
    fun deleteWhereRequestDate(threshold: Long)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: NetworkLogEntity): Long

    @Update
    fun update(entity: NetworkLogEntity): Int

    @Delete
    fun delete(entity: NetworkLogEntity): Int
}
