package com.rulyox.linechallengememo.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.MemoAdapter
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity: AppCompatActivity() {

    private lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(search_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appRepository = AppRepository(application)

        initUI()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUI() {

        search_button_search.setOnClickListener{ getMemoList(search_edit_query.text.toString()) }

        // recycler view
        search_recycler_memo.layoutManager = LinearLayoutManager(this)

    }

    private fun getMemoList(query: String) {

        val memoList: List<Memo> = appRepository.getMemoByQuery(query)

        // recycler view adapter
        val memoAdapter = MemoAdapter(memoList, this)
        search_recycler_memo.adapter = memoAdapter
        memoAdapter.notifyDataSetChanged()

    }

    // onclick recycler view memo
    fun clickMemo(id: Int) {

        val readIntent = Intent(this@SearchActivity, ReadActivity::class.java)
        readIntent.putExtra("id", id)
        startActivity(readIntent)

    }

}
