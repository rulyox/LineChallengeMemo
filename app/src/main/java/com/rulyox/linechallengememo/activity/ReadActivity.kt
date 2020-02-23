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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.ImageAdapter
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Image
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_read.*
import java.io.File
import java.util.*

class ReadActivity: AppCompatActivity() {

    private var memoId: Int = -1
    private var imgList: List<Image> = listOf()
    private lateinit var appRepository: AppRepository

    companion object {
        const val INTENT_EDIT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        setSupportActionBar(read_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        memoId = intent.getIntExtra("id", -1)
        appRepository = AppRepository(application)

        initUI()
        getMemoData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_read, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.read_menu_edit -> {
                val editIntent = Intent(this, EditWriteActivity::class.java)
                editIntent.putExtra("memoId", memoId)
                startActivityForResult(editIntent, INTENT_EDIT)
                true
            }
            R.id.read_menu_delete -> {
                deleteMemo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == INTENT_EDIT) {

                val doRefresh = data?.getBooleanExtra("refresh", false)

                if(doRefresh != null && doRefresh) {
                    getMemoData()
                    setRefresh()
                }

            }

        }

    }

    private fun initUI() {

        // recycler view
        read_recycler_image.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    }

    private fun getMemoData() {

        val memo: Memo = appRepository.getMemoById(memoId)

        read_text_title.text = memo.title
        read_text_text.text = memo.text

        // get timestamp from database time
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = memo.time
        val timestamp = calendar.get(Calendar.YEAR).toString() + ". " +
                (if(calendar.get(Calendar.MONTH)+1 < 10) "0" + (calendar.get(Calendar.MONTH)+1).toString() else (calendar.get(Calendar.MONTH)+1).toString()) + ". " +
                (if(calendar.get(Calendar.DATE) < 10) "0" + calendar.get(Calendar.DATE).toString() else calendar.get(Calendar.DATE).toString()) + ". " +
                (if(calendar.get(Calendar.HOUR_OF_DAY) < 10) "0" + calendar.get(Calendar.HOUR_OF_DAY).toString() else calendar.get(Calendar.HOUR_OF_DAY).toString()) + ":" +
                (if(calendar.get(Calendar.MINUTE) < 10) "0" + calendar.get(Calendar.MINUTE).toString() else calendar.get(Calendar.MINUTE).toString())
        read_text_label.text = timestamp

        imgList = appRepository.getImageByMemo(memoId)

        if(imgList.isNotEmpty()) {

            read_recycler_image.visibility = View.VISIBLE

            val imgDrawableList: MutableList<Drawable> = mutableListOf()

            // get drawables from files
            for(img in imgList) {

                val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${img.file}_thumb.jpg")

                if(imgFile.exists()) {

                    val imgPath: String = imgFile.absolutePath

                    val imgDrawable: Drawable = Drawable.createFromPath(imgPath)!!
                    imgDrawableList.add(imgDrawable)

                } else {

                    val imgDrawable: Drawable = ContextCompat.getDrawable(this, R.drawable.img_not_found)!!
                    imgDrawableList.add(imgDrawable)

                }

            }

            // recycler view adapter
            val imageAdapter = ImageAdapter(imgDrawableList, this)
            read_recycler_image.adapter = imageAdapter
            imageAdapter.notifyDataSetChanged()

        } else read_recycler_image.visibility = View.GONE

    }

    private fun deleteMemo() {

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(R.string.read_dialog_delete)
        alertDialogBuilder.setPositiveButton(R.string.dialog_yes, ({ dialog, _ ->

            // delete images
            for(img in imgList) {

                val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${img.file}.jpg")
                if(imgFile.exists()) imgFile.delete()

                val thumbFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${img.file}_thumb.jpg")
                if(thumbFile.exists()) thumbFile.delete()

            }

            val memo: Memo = appRepository.getMemoById(memoId)
            appRepository.deleteMemo(memo)

            dialog.dismiss()

            Toast.makeText(this, R.string.read_deleted, Toast.LENGTH_SHORT).show()

            setRefresh()
            finish()

        }))
        alertDialogBuilder.setNegativeButton(R.string.dialog_no, ({ dialog, _ -> dialog.dismiss() }))
        alertDialogBuilder.create().show()

    }

    // image recycler view listen onclick
    fun clickImage(position: Int) {

        val imgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${imgList[position].file}.jpg")

        if(imgFile.exists()) {

            val imgPath: String = imgFile.absolutePath

            val showIntent = Intent(this, ShowImageActivity::class.java)
            showIntent.putExtra("path", imgPath)
            startActivity(showIntent)

        } else Toast.makeText(this, R.string.error_image_not_found, Toast.LENGTH_SHORT).show()

    }

    // refresh after this activity finishes
    private fun setRefresh() {

        val finishIntent = Intent()
        finishIntent.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, finishIntent)

    }

}
