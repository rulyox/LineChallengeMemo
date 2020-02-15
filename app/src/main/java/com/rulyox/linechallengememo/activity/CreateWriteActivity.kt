package com.rulyox.linechallengememo.activity

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Image
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_write.*

class CreateWriteActivity: AbstractWriteActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        setSupportActionBar(write_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()

    }

    override fun initUI() {

        // label
        write_text_label.text = getString(R.string.write_label_create)

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

    override fun saveMemo() {

        val appRepository = AppRepository(application)

        // save memo
        val newMemo = Memo(null, write_edit_title.text.toString(), write_edit_text.text.toString())
        val newId: Int = appRepository.addMemo(newMemo).toInt()

        // save images
        for((index, imgDrawable) in imgDrawableList.withIndex()) {

            val imgName = saveDrawable(imgDrawable, newId, index)
            val newImage = Image(null, newId, imgName)
            appRepository.addImage(newImage)

        }

        Toast.makeText(this@CreateWriteActivity,
            R.string.write_saved, Toast.LENGTH_SHORT).show()

    }

    override fun deleteImage(position: Int) {

        imgDrawableList.removeAt(position)

        updateRecycler()

    }

}
