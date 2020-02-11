package com.rulyox.linechallengememo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemoDao {

    @Query("SELECT * FROM memo")
    suspend fun getAll(): List<Memo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Memo)

}
