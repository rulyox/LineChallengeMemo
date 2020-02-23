package com.rulyox.linechallengememo.adapter

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rulyox.linechallengememo.R
import com.rulyox.linechallengememo.activity.MainActivity
import com.rulyox.linechallengememo.activity.SearchActivity
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import java.io.File
import java.util.*

class MemoAdapter(private val memoList: List<Memo>, private val context: Context): RecyclerView.Adapter<MemoAdapter.CustomViewHolder?>() {

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val parent: LinearLayout = view.findViewById(R.id.item_memo_parent)
        val title: TextView = view.findViewById(R.id.item_memo_title)
        val text: TextView = view.findViewById(R.id.item_memo_text)
        val time: TextView = view.findViewById(R.id.item_memo_time)
        val thumb: ImageView = view.findViewById(R.id.item_memo_thumb)

        init {
            if(context is MainActivity) parent.setOnClickListener{ context.clickMemo(memoList[adapterPosition].id!!) }
            else if(context is SearchActivity) parent.setOnClickListener{ context.clickMemo(memoList[adapterPosition].id!!) }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {

        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_memo, viewGroup, false)

        return  CustomViewHolder(view)

    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        val application: Application = context.applicationContext as Application
        val appRepository = AppRepository(application)

        viewholder.title.text = memoList[position].title
        viewholder.text.text = memoList[position].text

        // get timestamp from database time
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = memoList[position].time
        val timestamp = calendar.get(Calendar.YEAR).toString() + ". " +
                (if(calendar.get(Calendar.MONTH)+1 < 10) "0" + (calendar.get(Calendar.MONTH)+1).toString() else (calendar.get(Calendar.MONTH)+1).toString()) + ". " +
                (if(calendar.get(Calendar.DATE) < 10) "0" + calendar.get(Calendar.DATE).toString() else calendar.get(Calendar.DATE).toString()) + ". " +
                (if(calendar.get(Calendar.HOUR_OF_DAY) < 10) "0" + calendar.get(Calendar.HOUR_OF_DAY).toString() else calendar.get(Calendar.HOUR_OF_DAY).toString()) + ":" +
                (if(calendar.get(Calendar.MINUTE) < 10) "0" + calendar.get(Calendar.MINUTE).toString() else calendar.get(Calendar.MINUTE).toString())
        viewholder.time.text = timestamp

        val thumbnail: String? = appRepository.getThumbnailByMemo(memoList[position].id!!)

        if(thumbnail != null) {

            val imgDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imgFile = File(imgDir, "${thumbnail}_thumb.jpg")

            if(imgFile.exists()) {

                val imgPath: String = imgFile.absolutePath

                val thumbBmp: Bitmap = BitmapFactory.decodeFile(imgPath)
                viewholder.thumb.setImageBitmap(thumbBmp)

            } else {

                val thumbBmp: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_not_found)
                viewholder.thumb.setImageBitmap(thumbBmp)

            }

            // image view round corners
            val roundCorner = context.getDrawable(R.drawable.bg_round_corner) as GradientDrawable
            viewholder.thumb.background = roundCorner
            viewholder.thumb.clipToOutline = true

        } else viewholder.thumb.visibility = View.GONE


    }

    override fun getItemCount(): Int { return memoList.size }

}
