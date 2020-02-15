package com.rulyox.linechallengememo.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.ImageAdapter
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

abstract class AbstractWriteActivity: AppCompatActivity() {

    protected var imgDrawableList: MutableList<Drawable> = mutableListOf()

    abstract fun initUI()

    abstract fun saveMemo()

    abstract fun deleteImage(position: Int)

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

    fun getImageGallery() {

        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"

        startActivityForResult(pickIntent, 1)

    }

    private fun gotImageGallery(data: Intent) {

        val imgUri: Uri = data.data!!
        val imgStream: InputStream = contentResolver.openInputStream(imgUri)!!
        val imgDrawable: Drawable = Drawable.createFromStream(imgStream, imgUri.toString())
        imgStream.close()

        imgDrawableList.add(imgDrawable)

        updateRecycler()

    }

    fun updateRecycler() {

        if(imgDrawableList.size > 0) write_recycler_image.visibility = View.VISIBLE
        else write_recycler_image.visibility = View.GONE

        val imageAdapter = ImageAdapter(imgDrawableList, this)
        write_recycler_image.adapter = imageAdapter
        imageAdapter.notifyDataSetChanged()

    }

    fun imageClicked(position: Int) {

        val alertDialogBuilder = AlertDialog.Builder(this@AbstractWriteActivity)
        alertDialogBuilder.setItems(arrayOf(getString(R.string.write_dialog_show), getString(
            R.string.write_dialog_delete
        ))) { dialog, id ->

            if (id == 0) { // show

                // image is currently not saved in storage. save temp image
                val imgBmp: Bitmap = (imgDrawableList[position] as BitmapDrawable).bitmap

                val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val imgFile = File(imgDir, "temp.jpg")
                val imgPath: String = imgFile.absolutePath

                val imgFileStream = FileOutputStream(imgPath)
                imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, imgFileStream)
                imgFileStream.close()

                val showIntent = Intent(this@AbstractWriteActivity, ShowImageActivity::class.java)
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

    fun saveDrawable(imgDrawable: Drawable, memoId: Int, index: Int): String {

        val imgName = "img_${memoId}_${index}"

        val imgBmp: Bitmap = (imgDrawable as BitmapDrawable).bitmap

        val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgFile = File(imgDir, "${imgName}.jpg")
        val imgPath: String = imgFile.absolutePath

        val imgFileStream = FileOutputStream(imgPath)
        imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, imgFileStream)
        imgFileStream.close()

        // create thumbnail
        val thumbSize = 300

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

        val thumbFile = File(imgDir, "${imgName}_thumb.jpg")
        val thumbPath: String = thumbFile.absolutePath

        val thumbFileStream = FileOutputStream(thumbPath)
        resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, thumbFileStream)
        thumbFileStream.close()

        return imgName

    }

    private fun finishAndRefresh() {

        val finishIntent = Intent()
        finishIntent.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, finishIntent)

        finish()

    }

}
