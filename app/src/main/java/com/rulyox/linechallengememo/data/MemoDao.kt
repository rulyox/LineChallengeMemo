package com.rulyox.linechallengememo.data

import androidx.room.*

@Dao
interface MemoDao {

    @Query("SELECT * FROM memo")
    suspend fun selectAll(): List<Memo>

    @Query("SELECT * FROM memo WHERE id = :id")
    suspend fun selectById(id: Int): Memo

    @Insert
    suspend fun insert(memo: Memo): Long

    @Delete
    suspend fun delete(memo: Memo)

    @Query("UPDATE memo SET thumbnail = :thumbnail WHERE id = :id")
    suspend fun updateThumbnail(id: Int, thumbnail: String?)

}
