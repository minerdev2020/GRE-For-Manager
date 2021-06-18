package com.minerdev.greformanager.custom.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minerdev.greformanager.databinding.ItemImageBinding
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.Constants
import com.minerdev.greformanager.utils.Constants.BASE_URL
import com.minerdev.greformanager.utils.Constants.CREATE
import com.minerdev.greformanager.utils.Constants.DELETE
import com.minerdev.greformanager.utils.Constants.UPDATE
import java.util.*

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    var images = ArrayList<Image>()
    var deletedImages = ArrayList<Image>()
    var thumbnail = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, OnItemClickListener())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position], thumbnail)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun addItem(item: Uri) {
        images.add(addImage(item))
    }

    fun removeItem(position: Int) {
        if (images[position].state == UPDATE) {
            images[position].state = DELETE
            deletedImages.add(images[position])
        }

        images.removeAt(position)
    }

    private fun addImage(item: Uri): Image {
        val image = Image()
        image.position = images.size.toByte()
        image.thumbnail = 0
        image.localUri = item.toString()
        image.state = CREATE

        return image
    }

    class ViewHolder(private val binding: ItemImageBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imageBtnDelete.setOnClickListener {
                listener.onDeleteButtonClick(
                    this@ViewHolder,
                    binding.root,
                    bindingAdapterPosition
                )
            }

            binding.imageBtnThumbnail.setOnClickListener {
                listener.onThumbnailButtonClick(
                    this@ViewHolder,
                    binding.root,
                    bindingAdapterPosition
                )
            }

            binding.imageBtnUp.setOnClickListener {
                listener.onUpButtonClick(this@ViewHolder, binding.root, bindingAdapterPosition)
            }

            binding.imageBtnDown.setOnClickListener {
                listener.onDownButtonClick(this@ViewHolder, binding.root, bindingAdapterPosition)
            }
        }

        fun bind(image: Image, thumbnailPos: Int) {
            if (image.id != 0) {
                Glide.with(binding.root).load(BASE_URL + "/" + image.url).into(binding.imageView)

            } else {
                Glide.with(binding.root).load(Uri.parse(image.localUri)).into(binding.imageView)
            }

            if (bindingAdapterPosition == thumbnailPos) {
                image.thumbnail = 1
                binding.tvThumbnail.visibility = View.VISIBLE

            } else {
                image.thumbnail = 0
                binding.tvThumbnail.visibility = View.GONE
            }
        }

    }

    inner class OnItemClickListener {
        fun onDeleteButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            if (thumbnail == position) {
                thumbnail = 0

            } else if (thumbnail > position) {
                thumbnail--
            }

            removeItem(position)
            notifyItemRemoved(position)
        }

        fun onThumbnailButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            thumbnail = position
            notifyDataSetChanged()
        }

        fun onUpButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            if (position > 0) {
                Collections.swap(images, position - 1, position)
                Log.d(Constants.TAG, "onUpButtonClick : $images")

                notifyItemRangeChanged(position - 1, 2)

                if (thumbnail == position) {
                    thumbnail--

                } else if (thumbnail == position - 1) {
                    thumbnail++
                }
            }
        }

        fun onDownButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            if (position < images.size - 1) {
                Collections.swap(images, position, position + 1)
                Log.d(Constants.TAG, "onDownButtonClick : $images")

                notifyItemRangeChanged(position, 2)

                if (thumbnail == position) {
                    thumbnail++

                } else if (thumbnail == position + 1) {
                    thumbnail--
                }
            }
        }
    }
}