package com.rulyox.linechallengememo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.net.Uri
import android.os.Environment
import android.graphics.drawable.BitmapDrawable

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

import kotlinx.android.synthetic.main.activity_write.*

import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Image
import com.rulyox.linechallengememo.data.Memo

class WriteActivity: AppCompatActivity() {

    private val imageList: MutableList<Drawable> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        setSupportActionBar(write_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.write_menu_save -> {
                saveMemo()
                finishAndRefresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == 1) { gotImageGallery(data!!) }

        }

    }

    private fun initUI() {

        write_navigation_image.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.write_menu_gallery -> { getImageGallery() }
                R.id.write_menu_camera -> { }
                R.id.write_menu_url -> { }
            }
            true
        }

        // recycler view
        write_recycler_image.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    }

    private fun finishAndRefresh() {

        val finishIntent = Intent()
        finishIntent.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, finishIntent)

        finish()

    }

    private fun saveMemo() {

        val appRepository = AppRepository(application)

        // save memo
        val newMemo = Memo(null, write_edit_title.text.toString(), write_edit_text.text.toString(), null)
        val newId: Int = appRepository.addMemo(newMemo).toInt()

        // save images
        for((index, imgDrawable) in imageList.withIndex()) {

            val imgName = saveDrawable(imgDrawable, newId, index)
            val newImage = Image(null, newId, imgName)
            appRepository.addImage(newImage)

        }

        // create thumbnail
        if(imageList.size > 0) {

            val thumbName = "image_thumbnail_${newId}.jpg"
            val thumbSize = 300

            val imgBmp: Bitmap = (imageList[0] as BitmapDrawable).bitmap

            var imgWidth: Int = imgBmp.width
            var imgHeight: Int = imgBmp.height

            if(imgWidth > imgHeight) {
                imgWidth = (imgWidth.toFloat() / imgHeight * thumbSize).toInt()
                imgHeight = thumbSize
            } else {
                imgHeight = (imgHeight.toFloat() / imgWidth * thumbSize).toInt()
                imgWidth = thumbSize
            }

            val resizedBmp: Bitmap = Bitmap.createScaledBitmap(imgBmp, imgWidth, imgHeight, false)

            val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imgFile = File(imgDir, thumbName)
            val imgPath: String = imgFile.absolutePath

            val fileStream = FileOutputStream(imgPath)
            resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
            fileStream.close()

            appRepository.updateMemoThumbnail(newId, thumbName)

        }

        Toast.makeText(this@WriteActivity, R.string.write_saved, Toast.LENGTH_SHORT).show()

    }

    private fun saveDrawable(imgDrawable: Drawable, memoId: Int, index: Int): String {

        val imgName = "image_${memoId}_${index}.jpg"

        val imgBmp: Bitmap = (imgDrawable as BitmapDrawable).bitmap

        val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgFile = File(imgDir, imgName)
        val imgPath: String = imgFile.absolutePath

        val fileStream = FileOutputStream(imgPath)
        imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
        fileStream.close()

        return imgName

    }

    private fun updateRecycler() {

        if(imageList.size > 0) write_recycler_image.visibility = View.VISIBLE
        else write_recycler_image.visibility = View.GONE

        val imageAdapter = ImageAdapter(imageList, this)
        write_recycler_image.adapter = imageAdapter
        imageAdapter.notifyDataSetChanged()

    }

    private fun getImageGallery() {

        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"

        startActivityForResult(pickIntent, 1)

    }

    private fun gotImageGallery(data: Intent) {

        val imgUri: Uri = data.data!!
        val imgStream: InputStream = contentResolver.openInputStream(imgUri)!!
        val imgDrawable: Drawable = Drawable.createFromStream(imgStream, imgUri.toString())
        imageList.add(imgDrawable)
        imgStream.close()

        updateRecycler()

    }

}
