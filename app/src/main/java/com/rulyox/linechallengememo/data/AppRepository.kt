package com.rulyox.linechallengememo.data

import android.app.Application
import kotlinx.coroutines.runBlocking

class AppRepository(application: Application) {

    private val appDatabase = AppDatabase.getInstance(application)
    private val memoDao = appDatabase.memoDao()
    private val imageDao = appDatabase.imageDao()

    fun getAllMemo(): List<Memo> = runBlocking { memoDao.selectAll() }

    fun getMemoById(id: Int): Memo = runBlocking { memoDao.selectById(id) }

    fun getMemoByQuery(query: String): List<Memo> = runBlocking { memoDao.selectByQuery("%${query}%") }

    fun addMemo(memo: Memo): Long = runBlocking { memoDao.insert(memo) }

    fun deleteMemo(memo: Memo) = runBlocking { memoDao.delete(memo) }

    fun updateMemo(memo: Memo) = runBlocking { memoDao.update(memo) }

    fun addImage(image: Image) = runBlocking { imageDao.insert(image) }

    fun getImageByMemo(memoId: Int): List<Image> = runBlocking { imageDao.selectImageByMemo(memoId) }

    fun getThumbnailByMemo(memoId: Int): String? = runBlocking { imageDao.selectThumbnailByMemo(memoId) }

    fun deleteImage(image: Image) = runBlocking { imageDao.delete(image) }

    fun closeDB() { appDatabase.close() }

}
