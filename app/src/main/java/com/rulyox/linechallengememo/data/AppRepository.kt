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

    fun updateMemoThumbnail(id: Int, thumbnail: String?) = runBlocking { memoDao.updateThumbnail(id, thumbnail) }

    fun addImage(image: Image) = runBlocking { imageDao.insert(image) }

    fun getImageByMemo(memoId: Int) = runBlocking { imageDao.selectImageByMemo(memoId) }

}
