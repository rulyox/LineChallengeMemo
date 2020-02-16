package com.rulyox.linechallengememo.data

import android.app.Application
import kotlinx.coroutines.runBlocking

class AppRepository(application: Application) {

    private val database = AppDatabase.getInstance(application)
    private val memoDao = database.memoDao()
    private val imageDao = database.imageDao()

    fun getAllMemo(): List<Memo> = runBlocking { memoDao.selectAll() }

    fun getMemoById(id: Int): Memo = runBlocking { memoDao.selectById(id) }

    fun addMemo(memo: Memo): Long = runBlocking { memoDao.insert(memo) }

    fun deleteMemo(memo: Memo) = runBlocking { memoDao.delete(memo) }

    fun updateMemo(memo: Memo) = runBlocking { memoDao.update(memo) }

    fun addImage(image: Image) = runBlocking { imageDao.insert(image) }

    fun getImageByMemo(memoId: Int): List<String> = runBlocking { imageDao.selectImageByMemo(memoId) }

    fun getThumbnailByMemo(memoId: Int): String? = runBlocking { imageDao.selectThumbnailByMemo(memoId) }

}
