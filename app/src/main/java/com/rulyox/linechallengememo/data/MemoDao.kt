package com.rulyox.linechallengememo.data

import androidx.room.*

@Dao
interface MemoDao {

    @Query("SELECT * FROM memo ORDER BY memo.time DESC")
    suspend fun selectAll(): List<Memo>

    @Query("SELECT * FROM memo WHERE id = :id")
    suspend fun selectById(id: Int): Memo

    @Insert
    suspend fun insert(memo: Memo): Long

    @Delete
    suspend fun delete(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

}
