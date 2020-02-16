package com.rulyox.linechallengememo.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rulyox.linechallengememo.R
import kotlinx.android.synthetic.main.activity_show_image.*
import java.io.File

class ShowImageActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

        initImage()

    }

    private fun initImage() {

        val imgPath: String = intent.getStringExtra("path")!!
        val tempExist: Boolean = intent.getBooleanExtra("temp", false)

        val imgBmp = BitmapFactory.decodeFile(imgPath)
        show_image.setImageBitmap(imgBmp)

        // temp image exists if called by WriteActivity
        if(tempExist) {

            val imgFile = File(imgPath)

            if(imgFile.exists()) imgFile.delete()

        }

    }

}
