package com.minerdev.greformanager

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private val imageList: MutableList<Image> = ArrayList()

    fun addImages(imageList: List<Image>) {
        this.imageList.clear()
        this.imageList.addAll(imageList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_image_slider, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imageList[position]
        val uri = Uri.parse(Constants.instance.DNS + "/storage/images/" + item.house_id + "/" + item.title)
        holder.setItem(uri)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.item_image_slider_imageView)

        fun setItem(uri: Uri) {
            Glide.with(itemView).load(uri).into(imageView)
        }
    }
}