package com.rulyox.linechallengememo

import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUI()
        getMemoList()

    }

    private fun setUI() {

        main_button_add.setOnClickListener {

            val writeIntent = Intent(this@MainActivity, WriteActivity::class.java)
            startActivity(writeIntent)

        }

    }

    private fun getMemoList() {

        val appRepository = AppRepository(application)

        val memoList: List<Memo> = appRepository.getAllMemo()
        val memoNum: Int = appRepository.getAllMemo().size

        if(memoNum > 0) {

            main_text_empty.visibility = View.GONE

        } else {

            main_text_empty.visibility = View.VISIBLE

        }

        val testItem = Memo(null, "TITLE", "TEXT", "THUMB")
        appRepository.addMemo(testItem)

    }

}
