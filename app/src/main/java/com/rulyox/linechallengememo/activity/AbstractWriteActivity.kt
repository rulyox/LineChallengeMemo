package com.rulyox.linechallengememo.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.test.espresso.idling.CountingIdlingResource
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.ImageAdapter
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

abstract class AbstractWriteActivity: AppCompatActivity() {

    protected var imgDrawableList: MutableList<Drawable> = mutableListOf()

    companion object {
        const val INTENT_GALLERY = 1
        const val INTENT_CAMERA = 2

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        val writeCountingIdlingResource: CountingIdlingResource = CountingIdlingResource("IDLE_RESOURCE_WRITE")
    }

    abstract fun initUI()

    abstract fun saveMemo()

    abstract fun clickImage(position: Int)

    abstract fun deleteImage(position: Int)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showCancelDialog()
                true
            }
            R.id.write_menu_save -> {
                saveMemo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        showCancelDialog()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == INTENT_GALLERY) { gotImageGallery(data!!) }
            else if(requestCode == INTENT_CAMERA) { gotImageCamera() }

        }

    }

    // start intent to choose image from gallery
    fun getImageGallery() {

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, INTENT_GALLERY)

    }

    // get intent result after choosing image from gallery
    private fun gotImageGallery(data: Intent) {

        val imgUri: Uri = data.data!!
        val imgStream: InputStream = contentResolver.openInputStream(imgUri)!!
        val imgDrawable: Drawable = Drawable.createFromStream(imgStream, imgUri.toString())
        imgStream.close()

        imgDrawableList.add(imgDrawable)
        updateImageRecycler()

    }

    // start intent to take a picture
    fun getImageCamera() {

        val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_camera.jpg")
        val imgUri: Uri = FileProvider.getUriForFile(this, "com.rulyox.linechallengememo.fileprovider", imgFile)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        startActivityForResult(cameraIntent, INTENT_CAMERA)

    }

    // get intent result after taking a picture
    private fun gotImageCamera() {

        val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_camera.jpg")

        if(imgFile.exists()) {

            val imgPath: String = imgFile.absolutePath
            val imgDrawable: Drawable = Drawable.createFromPath(imgPath)!!

            imgDrawableList.add(imgDrawable)
            updateImageRecycler()

            imgFile.delete()

        }

    }

    // get image from internet
    fun getImageUrl() {

        val dpi: Float = resources.displayMetrics.density

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.write_dialog_internet)

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins((20*dpi).toInt(), (10*dpi).toInt(), (20*dpi).toInt(), (10*dpi).toInt())
        val editText = EditText(this)
        editText.hint = resources.getText(R.string.write_dialog_url)
        editText.layoutParams = params
        editText.layoutParams = params
        container.addView(editText)
        alertDialogBuilder.setView(container)

        alertDialogBuilder.setPositiveButton(R.string.dialog_ok, ({ dialog, _ ->

            val url: String = editText.text.toString()

            // download image in new thread
            GlobalScope.launch {

                writeCountingIdlingResource.increment()

                try {

                    val inputStream = URL(url).content as InputStream
                    val imgDrawable: Drawable = Drawable.createFromStream(inputStream, url)

                    // update view in main thread
                    runOnUiThread {
                        imgDrawableList.add(imgDrawable)
                        updateImageRecycler()
                    }

                } catch(error: Exception) { // handle url or image error

                    runOnUiThread {
                        Toast.makeText(this@AbstractWriteActivity, R.string.error_url, Toast.LENGTH_SHORT).show()
                    }

                }

                writeCountingIdlingResource.decrement()

            }

            dialog.dismiss()

        }))
        alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, ({ dialog, _ -> dialog.dismiss() }))
        alertDialogBuilder.create().show()

    }

    fun updateImageRecycler() {

        if(imgDrawableList.size > 0) write_recycler_image.visibility = View.VISIBLE
        else write_recycler_image.visibility = View.GONE

        val imageAdapter = ImageAdapter(imgDrawableList, this)
        write_recycler_image.adapter = imageAdapter
        imageAdapter.notifyDataSetChanged()

    }

    // save image in memo to file
    fun saveDrawable(imgDrawable: Drawable, memoId: Int, index: Int): String {

        val imgName = "img_${memoId}_${index}"

        val imgBmp: Bitmap = (imgDrawable as BitmapDrawable).bitmap

        val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${imgName}.jpg")
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

        val thumbFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${imgName}_thumb.jpg")
        val thumbPath: String = thumbFile.absolutePath

        val thumbFileStream = FileOutputStream(thumbPath)
        resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, thumbFileStream)
        thumbFileStream.close()

        return imgName

    }

    private fun showCancelDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(R.string.write_dialog_cancel)
        alertDialogBuilder.setPositiveButton(R.string.dialog_yes, ({ dialog, _ ->

            dialog.dismiss()

            Toast.makeText(this, R.string.write_canceled, Toast.LENGTH_SHORT).show()

            finish()

        }))
        alertDialogBuilder.setNegativeButton(R.string.dialog_no, ({ dialog, _ -> dialog.dismiss() }))
        alertDialogBuilder.create().show()

    }

    // refresh after this activity finishes
    fun setRefresh() {

        val finishIntent = Intent()
        finishIntent.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, finishIntent)

    }

}
