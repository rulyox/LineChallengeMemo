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
import com.rulyox.linechallengememo.data.AppRepository
import com.rulyox.linechallengememo.data.Memo
import java.io.File
import java.lang.ref.WeakReference

class MemoAdapter(private val memoList: List<Memo>, context: Context): RecyclerView.Adapter<MemoAdapter.CustomViewHolder?>() {

    private val contextWeakReference = WeakReference(context)

    inner class CustomViewHolder(view: View, val context: Context): RecyclerView.ViewHolder(view) {

        private val parent: LinearLayout = view.findViewById(R.id.item_parent)
        val title: TextView = view.findViewById(R.id.item_title)
        val text: TextView = view.findViewById(R.id.item_text)
        val thumb: ImageView = view.findViewById(R.id.item_thumb)

        init {
            parent.setOnClickListener { (context as MainActivity).memoClicked(memoList[adapterPosition].id!!) }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {

        val context: Context = contextWeakReference.get()!!
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_memo, viewGroup, false)

        return CustomViewHolder(view, context)

    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        val application: Application = viewholder.context.applicationContext as Application

        viewholder.title.text = memoList[position].title
        viewholder.text.text = memoList[position].text

        val appRepository = AppRepository(application)

        val thumbnail: String? = appRepository.getThumbnailByMemo(memoList[position].id!!)

        if(thumbnail != null) {

            val imgDir = viewholder.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imgFile = File(imgDir, "${thumbnail}_thumb.jpg")

            if(imgFile.exists()) {

                val imgPath: String = imgFile.absolutePath

                val thumbBmp: Bitmap = BitmapFactory.decodeFile(imgPath)
                viewholder.thumb.setImageBitmap(thumbBmp)

            } else {

            }

            // image view round corners
            val roundCorner = viewholder.context.getDrawable(R.drawable.round_corner) as GradientDrawable
            viewholder.thumb.background = roundCorner
            viewholder.thumb.clipToOutline = true

        } else viewholder.thumb.visibility = View.GONE


    }

    override fun getItemCount(): Int { return memoList.size }

}
