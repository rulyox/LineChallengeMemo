package com.rulyox.linechallengememo

import android.graphics.BitmapFactory
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_show_image.*

class ShowImageActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

        initImage()

    }

    private fun initImage() {

        val imgPath = intent.getStringExtra("path")
        val imgBmp = BitmapFactory.decodeFile(imgPath)
        show_image.setImageBitmap(imgBmp)

    }

}
