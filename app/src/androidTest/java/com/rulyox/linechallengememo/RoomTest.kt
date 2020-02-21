package com.rulyox.linechallengememo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rulyox.linechallengememo.data.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var memoDao: MemoDao
    private lateinit var imageDao: ImageDao

    @Before
    fun createDb() {

        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        memoDao = appDatabase.memoDao()
        imageDao = appDatabase.imageDao()

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun modifyDatabase() {

        val memo1 = Memo(null, "title 1", "text 1", System.currentTimeMillis())
        val memo2 = Memo(null, "title 2", "text 2", System.currentTimeMillis())
        val memo3 = Memo(null, "title 3", "text 3", System.currentTimeMillis())

        val image1 = Image(null, 1, "file 1")
        val image2 = Image(null, 1, "file 2")
        val image3 = Image(null, 1, "file 3")

        var memo: Memo
        var memoList: List<Memo>

        var imageList: List<Image>
        var thumbnail: String?

        runBlocking {

            // add memo
            memoDao.insert(memo1)
            memoDao.insert(memo2)
            memoDao.insert(memo3)

            // get all memo
            memoList = memoDao.selectAll()
            assertThat(memoList.size, equalTo(3))

            // get memo by id
            memo = memoDao.selectById(3)
            assertThat(memo.title, equalTo(memo3.title))

            // select by query
            memoList = memoDao.selectByQuery("%${"title"}%")
            assertThat(memoList.size, equalTo(3))

            // update memo
            memo = Memo(memo.id, memo.title, memo.text + " updated", memo.time)
            memoDao.update(memo)
            memo = memoDao.selectById(3)
            assertThat(memo.text, equalTo(memo3.text + " updated"))

            // delete memo
            memoDao.delete(memo)
            memoList = memoDao.selectAll()
            assertThat(memoList.size, equalTo(2))

            // add image
            imageDao.insert(image1)
            imageDao.insert(image2)
            imageDao.insert(image3)

            // get image by memo
            imageList = imageDao.selectImageByMemo(1)
            assertThat(imageList.size, equalTo(3))

            // get thumbnail by memo
            thumbnail = imageDao.selectThumbnailByMemo(1)
            assertThat(thumbnail, equalTo("file 1"))

            // delete image
            imageDao.delete(imageList[0])
            imageList = imageDao.selectImageByMemo(1)
            assertThat(imageList.size, equalTo(2))

        }

    }

}
