package com.rulyox.linechallengememo

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import androidx.recyclerview.widget.RecyclerView

import java.lang.ref.WeakReference

class ImageAdapter(private val drawableList: List<Drawable>, context: Context): RecyclerView.Adapter<ImageAdapter.CustomViewHolder?>() {

    private val contextWeakReference = WeakReference(context)

    inner class CustomViewHolder(view: View, context: Context): RecyclerView.ViewHolder(view) {

        private val parent: LinearLayout = view.findViewById(R.id.item_image_parent)
        val image: ImageView = view.findViewById(R.id.item_image_image)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {

        val context: Context = contextWeakReference.get()!!
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_image, viewGroup, false)

        return CustomViewHolder(view, context)

    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        val context: Context = contextWeakReference.get()!!

        viewholder.image.setImageDrawable(drawableList[position])

        // image view round corners
        val roundCorner = context.getDrawable(R.drawable.round_corner) as GradientDrawable
        viewholder.image.background = roundCorner
        viewholder.image.clipToOutline = true

    }

    override fun getItemCount(): Int { return drawableList.size }

}
