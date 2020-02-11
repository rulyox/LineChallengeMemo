package com.rulyox.linechallengememo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.rulyox.linechallengememo.data.AppDatabase
import com.rulyox.linechallengememo.data.Memo

class MainActivity : AppCompatActivity() {

    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appDatabase = AppDatabase.getInstance(this)

        setUI()

        GlobalScope.launch(Dispatchers.Main) {

            val testItem = Memo(null, "title", "text", "thumb")

            appDatabase!!.memoDao().insert(testItem)

            val testList: List<Memo> = appDatabase!!.memoDao().getAll()

            Toast.makeText(this@MainActivity, testList[0].text, Toast.LENGTH_LONG).show()

        }

    }

    private fun setUI() {

        main_button_add.setOnClickListener {

            val writeIntent = Intent(this@MainActivity, WriteActivity::class.java)
            startActivity(writeIntent)

        }

    }

}
