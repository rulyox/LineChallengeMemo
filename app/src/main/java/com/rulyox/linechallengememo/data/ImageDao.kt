package com.rulyox.linechallengememo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {

    @Insert
    suspend fun insert(image: Image)

    @Query("SELECT image.file FROM image WHERE image.memo = :memoId ORDER BY image.id ASC")
    suspend fun selectImageByMemo(memoId: Int): List<String>

}
