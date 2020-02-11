package com.rulyox.linechallengememo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import java.lang.ref.WeakReference

import com.rulyox.linechallengememo.data.Memo

class MemoAdapter(private val list: List<Memo>, context: Context): RecyclerView.Adapter<MemoAdapter.CustomViewHolder?>() {

    private val mContextWeakReference = WeakReference(context)

    inner class CustomViewHolder(view: View, context: Context): RecyclerView.ViewHolder(view) {

        private val parent: LinearLayout = view.findViewById(R.id.item_parent)
        val title: TextView = view.findViewById(R.id.item_title)
        val text: TextView = view.findViewById(R.id.item_text)
        val thumb: ImageView = view.findViewById(R.id.item_thumb)

        init {
            parent.setOnClickListener { (context as MainActivity).clicked(adapterPosition) }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {

        val context: Context = mContextWeakReference.get()!!
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_memo, viewGroup, false)

        return CustomViewHolder(view, context)

    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        viewholder.title.text = list[position].title
        viewholder.text.text = list[position].text

    }

    override fun getItemCount(): Int { return list.size }

}
