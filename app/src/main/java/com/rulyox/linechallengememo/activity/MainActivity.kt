package com.rulyox.linechallengememo.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.MemoAdapter
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_main.*

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

            val createIntent = Intent(this@MainActivity, CreateWriteActivity::class.java)
            startActivityForResult(createIntent, 1)

        }

        // recycler view
        main_recycler_memo.layoutManager = LinearLayoutManager(this)

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

    // onclick recycler view memo
    fun clickMemo(id: Int) {

        val readIntent = Intent(this@MainActivity, ReadActivity::class.java)
        readIntent.putExtra("id", id)
        startActivityForResult(readIntent, 2)

    }

}
