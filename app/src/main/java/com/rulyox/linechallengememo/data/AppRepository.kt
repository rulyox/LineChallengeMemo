package com.rulyox.linechallengememo.data

import android.app.Application

import kotlinx.coroutines.runBlocking

class AppRepository(application: Application) {

    private val database = AppDatabase.getInstance(application)
    private val memoDao = database.memoDao()

    fun getAllMemo(): List<Memo> = runBlocking { memoDao.selectAll() }

    fun getMemoById(id: Int): Memo = runBlocking { memoDao.selectById(id) }

    fun addMemo(memo: Memo) = runBlocking { memoDao.insert(memo) }

}
