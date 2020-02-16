package com.rulyox.linechallengememo.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.ImageAdapter
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File
import java.io.FileOutputStream

class EditWriteActivity: AbstractWriteActivity() {

    private var memoId: Int = -1
    private var imgNotFoundList: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        setSupportActionBar(write_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
                R.id.write_menu_camera -> { }
                R.id.write_menu_url -> { }
            }
            true
        }

        // recycler view
        write_recycler_image.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    }

    private fun getMemoData() {

        memoId = intent.getIntExtra("memoId", -1)

        val appRepository = AppRepository(application)

        val memo: Memo = appRepository.getMemoById(memoId)

        write_edit_title.text = SpannableStringBuilder(memo.title)
        write_edit_text.text = SpannableStringBuilder(memo.text)

        val imgStringList: List<String>  = appRepository.getImageByMemo(memoId)

        if(imgStringList.isNotEmpty()) {

            write_recycler_image.visibility = View.VISIBLE

            val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            // get drawables from files
            for((index, imgName) in imgStringList.withIndex()) {

                val imgFile = File(imgDir, "${imgName}.jpg")

                if(imgFile.exists()) {

                    val imgPath: String = imgFile.absolutePath

                    val imgDrawable: Drawable = Drawable.createFromPath(imgPath)!!
                    imgDrawableList.add(imgDrawable)

                } else {

                    imgNotFoundList.add(index)

                    val imgDrawable: Drawable = ContextCompat.getDrawable(this@EditWriteActivity, R.drawable.img_not_found)!!
                    imgDrawableList.add(imgDrawable)

                }

            }

            // recycler view adapter
            val imageAdapter = ImageAdapter(imgDrawableList, this)
            write_recycler_image.adapter = imageAdapter
            imageAdapter.notifyDataSetChanged()

        }

    }

    override fun clickImage(position: Int) {

        val alertDialogBuilder = AlertDialog.Builder(this@EditWriteActivity)
        alertDialogBuilder.setItems(arrayOf(getString(R.string.write_dialog_show), getString(
            R.string.write_dialog_delete
        ))) { dialog, id ->

            if (id == 0) { // show

                if(!imgNotFoundList.contains(position)) {

                    // image is currently not saved in storage. save temp image
                    val imgBmp: Bitmap = (imgDrawableList[position] as BitmapDrawable).bitmap

                    val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val imgFile = File(imgDir, "temp.jpg")
                    val imgPath: String = imgFile.absolutePath

                    val imgFileStream = FileOutputStream(imgPath)
                    imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, imgFileStream)
                    imgFileStream.close()

                    val showIntent = Intent(this@EditWriteActivity, ShowImageActivity::class.java)
                    showIntent.putExtra("path", imgPath)
                    showIntent.putExtra("temp", true)
                    startActivity(showIntent)

                    dialog.cancel()

                } else Toast.makeText(this@EditWriteActivity, R.string.error_image_not_found, Toast.LENGTH_SHORT).show()

            } else if (id == 1) { // delete

                deleteImage(position)

                dialog.cancel()

            }

        }
        alertDialogBuilder.create().show()

    }

    override fun deleteImage(position: Int) {

        imgDrawableList.removeAt(position)

        // fix imgNotFoundList
        if(imgNotFoundList.contains(position)) {

            imgNotFoundList.remove(position)

            for((imgIndex, imgPosition) in imgNotFoundList.withIndex())
                if(imgPosition > position) imgNotFoundList[imgIndex]--

        }

        updateRecycler()

    }

    override fun saveMemo() {

        val appRepository = AppRepository(application)

        // save memo
        val editedMemo = Memo(memoId, write_edit_title.text.toString(), write_edit_text.text.toString())
        appRepository.updateMemo(editedMemo)

        Toast.makeText(this@EditWriteActivity, R.string.write_edited, Toast.LENGTH_SHORT).show()

        TODO("Handle image changes")

    }

}
