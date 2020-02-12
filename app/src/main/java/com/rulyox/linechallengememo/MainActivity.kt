package com.rulyox.linechallengememo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*

import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initUI()
        getMemoList()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val doRefresh = data?.getBooleanExtra("refresh", false)

        if(doRefresh != null && doRefresh) getMemoList()

    }

    private fun initUI() {

        // add button
        main_button_add.setOnClickListener {

            val writeIntent = Intent(this@MainActivity, WriteActivity::class.java)
            startActivityForResult(writeIntent, 1)

        }

        // recycler view
        main_recycler_memo.layoutManager = LinearLayoutManager(this)
        main_recycler_memo.addItemDecoration(DividerItemDecoration(main_recycler_memo.context, DividerItemDecoration.VERTICAL))

    }

    private fun getMemoList() {

        val appRepository = AppRepository(application)

        val memoList: List<Memo> = appRepository.getAllMemo()
        val memoNum: Int = appRepository.getAllMemo().size

        if(memoNum > 0) {

            main_text_empty.visibility = View.GONE
            main_recycler_memo.visibility = View.VISIBLE

        } else {

            main_text_empty.visibility = View.VISIBLE
            main_recycler_memo.visibility = View.GONE

        }

        // recycler view adapter
        val memoAdapter = MemoAdapter(memoList, this)
        main_recycler_memo.adapter = memoAdapter
        memoAdapter.notifyDataSetChanged()

    }

    // onclick recycler view item
    fun memoClicked(id: Int) {

        val readIntent = Intent(this@MainActivity, ReadActivity::class.java)
        readIntent.putExtra("id", id)
        startActivityForResult(readIntent, 2)

    }

}
