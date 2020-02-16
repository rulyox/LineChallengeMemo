package com.rulyox.linechallengememo.activity

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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.ImageAdapter
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_read.*
import java.io.File

class ReadActivity: AppCompatActivity() {

    private var memoId: Int = -1
    private var imgStringList: List<String> = listOf()

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
                val editIntent = Intent(this@ReadActivity, EditWriteActivity::class.java)
                editIntent.putExtra("memoId", memoId)
                startActivity(editIntent)
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

        imgStringList = appRepository.getImageByMemo(memoId)

        if(imgStringList.isNotEmpty()) {

            read_recycler_image.visibility = View.VISIBLE

            val imgDrawableList: MutableList<Drawable> = mutableListOf()

            val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            // get drawables from files
            for(imgName in imgStringList) {

                val imgFile = File(imgDir, "${imgName}_thumb.jpg")

                if(imgFile.exists()) {

                    val imgPath: String = imgFile.absolutePath

                    val imgDrawable: Drawable = Drawable.createFromPath(imgPath)!!
                    imgDrawableList.add(imgDrawable)

                } else {

                    val imgDrawable: Drawable = ContextCompat.getDrawable(this@ReadActivity, R.drawable.img_not_found)!!
                    imgDrawableList.add(imgDrawable)

                }

            }

            // recycler view adapter
            val imageAdapter = ImageAdapter(imgDrawableList, this)
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
        val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        for(imgName in imgStringList) {

            val imgFile = File(imgDir, "${imgName}.jpg")
            if(imgFile.exists()) imgFile.delete()

            val thumbFile = File(imgDir, "${imgName}_thumb.jpg")
            if(thumbFile.exists()) thumbFile.delete()

        }

        val memo: Memo = appRepository.getMemoById(memoId)
        appRepository.deleteMemo(memo)

        Toast.makeText(this@ReadActivity, R.string.read_deleted, Toast.LENGTH_SHORT).show()

    }

    fun imageClicked(position: Int) {

        val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgFile = File(imgDir, "${imgStringList[position]}.jpg")

        if(imgFile.exists()) {

            val imgPath: String = imgFile.absolutePath

            val showIntent = Intent(this@ReadActivity, ShowImageActivity::class.java)
            showIntent.putExtra("path", imgPath)
            startActivity(showIntent)

        } else Toast.makeText(this@ReadActivity, R.string.error_image_not_found, Toast.LENGTH_SHORT).show()

    }

}
