package com.rulyox.linechallengememo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment

import androidx.appcompat.app.AppCompatActivity

import java.io.File

import kotlinx.android.synthetic.main.activity_show_image.*

class ShowImageActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

        initImage()

    }

    private fun initImage() {

        val imgPath: String? = intent.getStringExtra("path")
        val tempExist: Boolean = intent.getBooleanExtra("temp", false)

        val imgBmp = BitmapFactory.decodeFile(imgPath)
        show_image.setImageBitmap(imgBmp)

        // temp image exists if called by WriteActivity
        if(tempExist) deleteTempImage()

    }

    private fun deleteTempImage() {

        val imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgFile = File(imgDir, "temp.jpg")

        if(imgFile.exists()) imgFile.delete()

    }

}
