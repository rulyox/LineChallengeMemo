package com.rulyox.linechallengememo

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView

import java.lang.ref.WeakReference

class ImageAdapter(private val drawableList: List<Drawable>, context: Context): RecyclerView.Adapter<ImageAdapter.CustomViewHolder?>() {

    private val contextWeakReference = WeakReference(context)

    inner class CustomViewHolder(view: View, val context: Context): RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.item_image_image)

        init {
            if(context is ReadActivity) image.setOnClickListener{ context.imageClicked(adapterPosition) }
            else if(context is WriteActivity) image.setOnClickListener{ context.imageClicked(adapterPosition) }
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {

        val context: Context = contextWeakReference.get()!!
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_image, viewGroup, false)

        return CustomViewHolder(view, context)

    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        viewholder.image.setImageDrawable(drawableList[position])

        // image view round corners
        val roundCorner = viewholder.context.getDrawable(R.drawable.round_corner) as GradientDrawable
        viewholder.image.background = roundCorner
        viewholder.image.clipToOutline = true

    }

    override fun getItemCount(): Int { return drawableList.size }

}
