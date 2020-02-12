package com.rulyox.linechallengememo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo

import kotlinx.android.synthetic.main.activity_write.*

class WriteActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        setSupportActionBar(write_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()

    }

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

                val finishIntent = Intent()
                finishIntent.putExtra("refresh", true)
                setResult(Activity.RESULT_OK, finishIntent)
                finish()

                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUI() {

        write_navigation_image.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.write_menu_gallery -> { addImage("gallery") }
                R.id.write_menu_camera -> { addImage("camera") }
                R.id.write_menu_url -> { addImage("url") }
            }
            true
        }

    }

    private fun saveMemo() {

        val appRepository = AppRepository(application)

        val newMemo = Memo(null, write_edit_title.text.toString(), write_edit_text.text.toString(), null)
        appRepository.addMemo(newMemo)

        Toast.makeText(this@WriteActivity, R.string.write_saved, Toast.LENGTH_SHORT).show()

    }

    private fun addImage(type: String) {

    }

}
