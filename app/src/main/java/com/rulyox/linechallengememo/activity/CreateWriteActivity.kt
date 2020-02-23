package com.rulyox.linechallengememo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Image
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File
import java.io.FileOutputStream

class CreateWriteActivity: AbstractWriteActivity() {

    private lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        setSupportActionBar(write_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appRepository = AppRepository(application)

        initUI()

    }

    override fun initUI() {

        // label
        write_text_label.text = getString(R.string.write_label_create)

        // navigation view
        write_navigation_image.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.write_menu_gallery -> { getImageGallery() }
                R.id.write_menu_camera -> { getImageCamera() }
                R.id.write_menu_url -> { getImageUrl() }
            }
            true
        }

        // recycler view
        write_recycler_image.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    }

    // image recycler view listen onclick
    override fun clickImage(position: Int) {

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setItems(arrayOf(getString(R.string.write_dialog_show), getString(
            R.string.write_dialog_delete
        ))) { dialog, id ->

            if (id == 0) { // show

                // image is currently not saved in storage. save temp image
                val imgBmp: Bitmap = (imgDrawableList[position] as BitmapDrawable).bitmap

                val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_show.jpg")
                val imgPath: String = imgFile.absolutePath

                val imgFileStream = FileOutputStream(imgPath)
                imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, imgFileStream)
                imgFileStream.close()

                val showIntent = Intent(this, ShowImageActivity::class.java)
                showIntent.putExtra("path", imgPath)
                showIntent.putExtra("temp", true)
                startActivity(showIntent)

                dialog.cancel()

            } else if (id == 1) { // delete

                deleteImage(position)

                dialog.cancel()

            }

        }
        alertDialogBuilder.create().show()

    }

    override fun deleteImage(position: Int) {

        imgDrawableList.removeAt(position)
        updateImageRecycler()

    }

    override fun saveMemo() {

        val memoTitle: String = write_edit_title.text.toString()
        val memoText: String = write_edit_text.text.toString()

        // if empty
        if(memoTitle == "" && memoText == "" && imgDrawableList.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_memo, Toast.LENGTH_SHORT).show()
            return
        }

        val newMemo = Memo(null, memoTitle, memoText, System.currentTimeMillis())
        val newId: Int = appRepository.addMemo(newMemo).toInt()

        // save images
        for((index, imgDrawable) in imgDrawableList.withIndex()) {

            val imgName = saveDrawable(imgDrawable, newId, index)
            val newImage = Image(null, newId, imgName)
            appRepository.addImage(newImage)

        }

        Toast.makeText(this, R.string.write_saved, Toast.LENGTH_SHORT).show()

        setRefresh()
        finish()

    }

}
