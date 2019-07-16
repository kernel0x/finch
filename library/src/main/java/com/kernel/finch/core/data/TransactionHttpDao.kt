package com.kernel.finch.core.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kernel.finch.core.data.models.TransactionHttpEntity
import com.kernel.finch.core.data.models.TransactionHttpEntity.Companion.TABLE_NAME

@Dao
interface TransactionHttpDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY requestDate DESC, id DESC")
    fun getAll(): LiveData<List<TransactionHttpEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE path LIKE '%' || :search ||'%' ORDER BY requestDate DESC, id DESC")
    fun getAll(search: String): LiveData<List<TransactionHttpEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE responseCode LIKE '%' || :responseCode ||'%' ORDER BY requestDate DESC, id DESC")
    fun getAll(responseCode: Int?): LiveData<List<TransactionHttpEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getById(id: Long): LiveData<TransactionHttpEntity>

    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("DELETE FROM $TABLE_NAME WHERE requestDate <= :threshold")
    fun deleteWhereRequestDate(threshold: Long)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll(): Int

    @Insert
    fun insert(entity: TransactionHttpEntity): Long

    @Update
    fun update(entity: TransactionHttpEntity): Int

    @Delete
    fun delete(entity: TransactionHttpEntity): Int

}