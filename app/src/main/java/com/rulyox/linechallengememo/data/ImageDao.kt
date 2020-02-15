package com.rulyox.linechallengememo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {

    @Insert
    suspend fun insert(image: Image)

    @Query("SELECT image.file FROM image WHERE image.memo = :memoId ORDER BY image.id ASC")
    suspend fun selectImageByMemo(memoId: Int): List<String>

    @Query("SELECT image.file FROM image WHERE image.memo = :memoId ORDER BY image.id ASC LIMIT 1")
    suspend fun selectThumbnailByMemo(memoId: Int): String?

}
