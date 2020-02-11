package com.rulyox.linechallengememo

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo

import kotlinx.android.synthetic.main.activity_read.*

class ReadActivity: AppCompatActivity() {

    private var memoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        setSupportActionBar(read_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        memoId = intent.getIntExtra("id", -1)

        getMemoData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_read, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.read_menu_save -> {
                true
            }
            R.id.read_menu_delete -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getMemoData() {

        val appRepository = AppRepository(application)

        val memo: Memo = appRepository.getMemoById(memoId)

        read_edit_title.text = SpannableStringBuilder(memo.title)
        read_edit_text.text = SpannableStringBuilder(memo.text)

    }

}
