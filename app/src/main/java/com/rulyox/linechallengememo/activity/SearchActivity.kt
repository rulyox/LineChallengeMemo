package com.rulyox.linechallengememo.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.adapter.MemoAdapter
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity: AppCompatActivity() {

    private lateinit var appRepository: AppRepository
    private var searchQuery: String = ""

    companion object {
        const val INTENT_READ = 1
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {

            val doRefresh = data?.getBooleanExtra("refresh", false)

            if(doRefresh != null && doRefresh) {
                getMemoList(searchQuery)
                setRefresh()
            }

        }

    }

    private fun initUI() {

        search_button_search.setOnClickListener{

            searchQuery = search_edit_query.text.toString()

            if(searchQuery != "") getMemoList(searchQuery)
            else Toast.makeText(this, R.string.error_empty_query, Toast.LENGTH_SHORT).show()

        }

        // recycler view
        search_recycler_memo.layoutManager = LinearLayoutManager(this)

    }

    private fun getMemoList(query: String) {

        val memoList: List<Memo> = appRepository.getMemoByQuery(query)

        if(memoList.isNotEmpty()) {

            search_text_empty.visibility = View.GONE
            search_recycler_memo.visibility = View.VISIBLE

            // recycler view adapter
            val memoAdapter = MemoAdapter(memoList, this)
            search_recycler_memo.adapter = memoAdapter
            memoAdapter.notifyDataSetChanged()

        } else {

            search_text_empty.visibility = View.VISIBLE
            search_recycler_memo.visibility = View.GONE

        }

    }

    // onclick recycler view memo
    fun clickMemo(id: Int) {

        val readIntent = Intent(this, ReadActivity::class.java)
        readIntent.putExtra("id", id)
        startActivityForResult(readIntent, INTENT_READ)

    }

    private fun setRefresh() {

        val finishIntent = Intent()
        finishIntent.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, finishIntent)

    }

}
