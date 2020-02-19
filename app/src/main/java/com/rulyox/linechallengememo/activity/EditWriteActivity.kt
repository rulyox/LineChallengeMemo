package com.rulyox.linechallengememo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Image
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File
import java.io.FileOutputStream

class EditWriteActivity: AbstractWriteActivity() {

    private var memoId: Int = -1
    private var existedImgList: MutableList<Image> = mutableListOf()
    private var deletedImgList: MutableList<Image> = mutableListOf()
    private lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        setSupportActionBar(write_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        memoId = intent.getIntExtra("memoId", -1)
        appRepository = AppRepository(application)

        initUI()
        getMemoData()

    }

    override fun initUI() {

        // label
        write_text_label.text = getString(R.string.write_label_edit)

        // navigation view
        write_navigation_image.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.write_menu_gallery -> { getImageGallery() }
                R.id.write_menu_camera -> { getImageCamera() }
                R.id.write_menu_url -> { }
            }
            true
        }

        // recycler view
        write_recycler_image.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    }

    private fun getMemoData() {

        val memo: Memo = appRepository.getMemoById(memoId)

        write_edit_title.text = SpannableStringBuilder(memo.title)
        write_edit_text.text = SpannableStringBuilder(memo.text)

        existedImgList = appRepository.getImageByMemo(memoId).toMutableList()

        if(existedImgList.isNotEmpty()) {

            // get drawables from files
            for(img in existedImgList) {

                val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${img.file}_thumb.jpg")

                if(imgFile.exists()) {

                    val imgPath: String = imgFile.absolutePath

                    val imgDrawable: Drawable = Drawable.createFromPath(imgPath)!!
                    imgDrawableList.add(imgDrawable)

                } else {

                    val imgDrawable: Drawable = ContextCompat.getDrawable(this@EditWriteActivity, R.drawable.img_not_found)!!
                    imgDrawableList.add(imgDrawable)

                }

            }

            updateRecycler()

        }

    }

    override fun clickImage(position: Int) {

        val alertDialogBuilder = AlertDialog.Builder(this@EditWriteActivity)
        alertDialogBuilder.setItems(arrayOf(getString(R.string.write_dialog_show), getString(
            R.string.write_dialog_delete
        ))) { dialog, id ->

            if (id == 0) { // show

                if(existedImgList.size > position) { // existing image

                    val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${existedImgList[position].file}.jpg")

                    if(imgFile.exists()) {

                        val imgPath: String = imgFile.absolutePath

                        val showIntent = Intent(this@EditWriteActivity, ShowImageActivity::class.java)
                        showIntent.putExtra("path", imgPath)
                        startActivity(showIntent)

                    } else Toast.makeText(this@EditWriteActivity, R.string.error_image_not_found, Toast.LENGTH_SHORT).show()

                } else { // newly added images

                    // image is currently not saved in storage. save temp image
                    val imgBmp: Bitmap = (imgDrawableList[position] as BitmapDrawable).bitmap

                    val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_show.jpg")
                    val imgPath: String = imgFile.absolutePath

                    val imgFileStream = FileOutputStream(imgPath)
                    imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, imgFileStream)
                    imgFileStream.close()

                    val showIntent = Intent(this@EditWriteActivity, ShowImageActivity::class.java)
                    showIntent.putExtra("path", imgPath)
                    showIntent.putExtra("temp", true)
                    startActivity(showIntent)

                    dialog.cancel()

                }

            } else if (id == 1) { // delete

                deleteImage(position)

                dialog.cancel()

            }

        }
        alertDialogBuilder.create().show()

    }

    override fun deleteImage(position: Int) {

        imgDrawableList.removeAt(position)

        // add to deletedImgList
        if(existedImgList.size > position) {
            deletedImgList.add(existedImgList[position])
            existedImgList.removeAt(position)
        }

        updateRecycler()

    }

    override fun saveMemo() {

        // save memo
        val editedMemo = Memo(memoId, write_edit_title.text.toString(), write_edit_text.text.toString(), System.currentTimeMillis())
        appRepository.updateMemo(editedMemo)

        // delete image files
        for(img in deletedImgList) {

            appRepository.deleteImage(img)

            val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${img.file}.jpg")
            if(imgFile.exists()) imgFile.delete()

            val thumbFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${img.file}_thumb.jpg")
            if(thumbFile.exists()) thumbFile.delete()

        }

        // save images
        for((index, imgDrawable) in imgDrawableList.withIndex()) {

            // image is already saved
            if(existedImgList.size > index) continue

            val imgName = saveDrawable(imgDrawable, memoId, index)
            val newImage = Image(null, memoId, imgName)
            appRepository.addImage(newImage)

        }

        Toast.makeText(this@EditWriteActivity, R.string.write_edited, Toast.LENGTH_SHORT).show()

    }

}
