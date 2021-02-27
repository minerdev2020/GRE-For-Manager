package com.minerdev.greformanager.custom.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minerdev.greformanager.R
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

    private var listener = OnItemClickListener()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_image, parent, false)
        return ViewHolder(itemView, listener)
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

    class ViewHolder(itemView: View, clickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textView: TextView = itemView.findViewById(R.id.tv_thumbnail)

        fun bind(image: Image, thumbnailPos: Int) {
            if (image.id != 0) {
                Glide.with(itemView).load(BASE_URL + "/" + image.url).into(imageView)

            } else {
                Glide.with(itemView).load(Uri.parse(image.localUri)).into(imageView)
            }

            if (bindingAdapterPosition == thumbnailPos) {
                image.thumbnail = 1
                textView.visibility = View.VISIBLE

            } else {
                image.thumbnail = 0
                textView.visibility = View.GONE
            }
        }

        init {
            val imageBtnDelete = itemView.findViewById<ImageButton>(R.id.image_btn_delete)
            imageBtnDelete.setOnClickListener {
                clickListener?.onDeleteButtonClick(this@ViewHolder, itemView, bindingAdapterPosition)
            }

            val imageBtnThumbnail = itemView.findViewById<ImageButton>(R.id.image_btn_thumbnail)
            imageBtnThumbnail.setOnClickListener {
                clickListener?.onThumbnailButtonClick(this@ViewHolder, itemView, bindingAdapterPosition)
            }

            val imageBtnUp = itemView.findViewById<ImageButton>(R.id.image_btn_up)
            imageBtnUp.setOnClickListener {
                clickListener?.onUpButtonClick(this@ViewHolder, itemView, bindingAdapterPosition)
            }

            val imageBtnDown = itemView.findViewById<ImageButton>(R.id.image_btn_down)
            imageBtnDown.setOnClickListener {
                clickListener?.onDownButtonClick(this@ViewHolder, itemView, bindingAdapterPosition)
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