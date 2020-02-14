package com.rulyox.linechallengememo

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import java.io.File

import kotlinx.android.synthetic.main.activity_read.*

import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo

class ReadActivity: AppCompatActivity() {

    private var memoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        setSupportActionBar(read_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        memoId = intent.getIntExtra("id", -1)

        initUI()
        getMemoData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_read, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.read_menu_edit -> {
                true
            }
            R.id.read_menu_delete -> {
                deleteMemo()
                finishAndRefresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUI() {

        // recycler view
        read_recycler_image.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    }

    private fun getMemoData() {

        val appRepository = AppRepository(application)

        val memo: Memo = appRepository.getMemoById(memoId)

        read_edit_title.text = memo.title
        read_edit_text.text = memo.text

        val imageList: List<String> = appRepository.getImageByMemo(memoId)

        if(imageList.isNotEmpty()) {

            read_recycler_image.visibility = View.VISIBLE

            val imgList: MutableList<Drawable> = mutableListOf()

            val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            // get drawables from files
            for(imgName in imageList) {

                val imgFile = File(imgDir, "${imgName}_thumb.jpg")

                if(imgFile.exists()) {

                    val imgPath: String = imgFile.absolutePath

                    val imgDrawable: Drawable = Drawable.createFromPath(imgPath)!!
                    imgList.add(imgDrawable)

                } else {

                }

            }

            // recycler view adapter
            val imageAdapter = ImageAdapter(imgList, this)
            read_recycler_image.adapter = imageAdapter
            imageAdapter.notifyDataSetChanged()

        }

    }

    private fun finishAndRefresh() {

        val finishIntent = Intent()
        finishIntent.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, finishIntent)

        finish()

    }

    private fun deleteMemo() {

        val appRepository = AppRepository(application)

        // delete images
        val imageList: List<String> = appRepository.getImageByMemo(memoId)

        val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        for(imgName in imageList) {

            val imgFile = File(imgDir, "${imgName}.jpg")
            if(imgFile.exists()) imgFile.delete()

            val thumbFile = File(imgDir, "${imgName}_thumb.jpg")
            if(thumbFile.exists()) thumbFile.delete()

        }

        val memo: Memo = appRepository.getMemoById(memoId)
        appRepository.deleteMemo(memo)

        Toast.makeText(this@ReadActivity, R.string.read_deleted, Toast.LENGTH_SHORT).show()

    }

}
