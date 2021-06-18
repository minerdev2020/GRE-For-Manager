package com.minerdev.greformanager.custom.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minerdev.greformanager.databinding.ItemImageSliderBinding
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.Constants.BASE_URL
import java.util.*

class ImageSliderAdapter : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {
    private val imageList: MutableList<Image> = ArrayList()

    fun addImages(imageList: List<Image>) {
        this.imageList.clear()
        this.imageList.addAll(imageList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imageList[position]
        val uri = Uri.parse(BASE_URL + "/" + item.url)
        holder.setItem(uri)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(private val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setItem(uri: Uri) {
            Glide.with(binding.root).load(uri).into(binding.imageView)
        }
    }
}