package com.rulyox.linechallengememo.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MemoDao {

    @Query("SELECT * FROM memo")
    fun getAll(): List<Memo>

}
