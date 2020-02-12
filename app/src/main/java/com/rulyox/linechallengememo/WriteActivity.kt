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

    private var imageList: MutableList<Drawable> = mutableListOf()

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
                R.id.write_menu_gallery -> { addImage("gallery") }
                R.id.write_menu_camera -> { addImage("camera") }
                R.id.write_menu_url -> { addImage("url") }
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
            val imgPath = saveDrawable(imgDrawable, newId, index)
            val newImage = Image(null, newId, imgPath)
            appRepository.addImage(newImage)
        }

        Toast.makeText(this@WriteActivity, R.string.write_saved, Toast.LENGTH_SHORT).show()

    }

    private fun addImage(type: String) {

        when(type) {
            "gallery" -> { getImageGallery() }
        }

        write_recycler_image.visibility = View.VISIBLE

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

    private fun updateRecycler() {

        val imageAdapter = ImageAdapter(imageList, this)
        write_recycler_image.adapter = imageAdapter
        imageAdapter.notifyDataSetChanged()

    }

    private fun saveDrawable(imgDrawable: Drawable, memoId: Int, index: Int): String {

        val imgBmp: Bitmap = (imgDrawable as BitmapDrawable).bitmap

        val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgFile = File(imgDir, "image_${memoId}_${index}.jpg")
        val imgPath: String = imgFile.absolutePath

        val fileStream = FileOutputStream(imgPath)
        imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
        fileStream.close()

        return imgPath

    }

}
