package com.rulyox.linechallengememo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Memo::class, Image::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun memoDao(): MemoDao
    abstract fun imageDao(): ImageDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "database.db")
                .build()
        }

    }

}
