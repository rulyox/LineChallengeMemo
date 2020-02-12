package com.rulyox.linechallengememo

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import androidx.recyclerview.widget.RecyclerView

import java.lang.ref.WeakReference

class ImageAdapter(private val drawableList: List<Drawable>, context: Context): RecyclerView.Adapter<ImageAdapter.CustomViewHolder?>() {

    private val mContextWeakReference = WeakReference(context)

    inner class CustomViewHolder(view: View, context: Context): RecyclerView.ViewHolder(view) {

        private val parent: LinearLayout = view.findViewById(R.id.item_image_parent)
        val image: ImageView = view.findViewById(R.id.item_image_image)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {

        val context: Context = mContextWeakReference.get()!!
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_image, viewGroup, false)

        return CustomViewHolder(view, context)

    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        viewholder.image.setImageDrawable(drawableList[position])

    }

    override fun getItemCount(): Int { return drawableList.size }

}
