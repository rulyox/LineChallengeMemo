package com.rulyox.linechallengememo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemoDao {

    @Query("SELECT * FROM memo")
    suspend fun selectAll(): List<Memo>

    @Query("SELECT * FROM memo WHERE id = :id")
    suspend fun selectById(id: Int): Memo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Memo)

}
