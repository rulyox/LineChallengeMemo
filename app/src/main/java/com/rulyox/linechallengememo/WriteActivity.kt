package com.rulyox.linechallengememo

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

                val appRepository = AppRepository(application)
                val newMemo = Memo(null, write_edit_title.text.toString(), write_edit_text.text.toString(), null)
                appRepository.addMemo(newMemo)

                Toast.makeText(this@WriteActivity, R.string.write_saved, Toast.LENGTH_SHORT).show()

                finish()
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
