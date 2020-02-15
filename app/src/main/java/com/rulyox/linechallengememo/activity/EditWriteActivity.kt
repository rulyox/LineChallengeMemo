package com.rulyox.linechallengememo.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.SpannableStringBuilder
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.ImageAdapter
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File

class EditWriteActivity: AbstractWriteActivity() {

    private var memoId: Int = -1

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
            for(imgName in imgStringList) {

                val imgFile = File(imgDir, "${imgName}.jpg")

                if(imgFile.exists()) {

                    val imgPath: String = imgFile.absolutePath

                    val imgDrawable: Drawable = Drawable.createFromPath(imgPath)!!
                    imgDrawableList.add(imgDrawable)

                } else {

                }

            }

            // recycler view adapter
            val imageAdapter = ImageAdapter(imgDrawableList, this)
            write_recycler_image.adapter = imageAdapter
            imageAdapter.notifyDataSetChanged()

        }

    }

    override fun saveMemo() {}

    override fun deleteImage(position: Int) {

        imgDrawableList.removeAt(position)

        updateRecycler()

    }

}
