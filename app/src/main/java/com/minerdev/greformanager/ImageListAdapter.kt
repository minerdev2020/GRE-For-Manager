package com.minerdev.greformanager

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    var imageUris = ArrayList<Uri>()
    var images = ArrayList<Image>()
    private var listener = OnItemClickListener()
    var thumbnail = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_image, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageUris[position], thumbnail)
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    fun addItem(context: Context?, item: Uri) {
        context?.let {
            images.add(addImage(it, item))
            imageUris.add(item)
        }
    }

    fun removeItem(position: Int) {
        images.removeAt(position)
        imageUris.removeAt(position)
    }

    private fun addImage(context: Context, uri: Uri): Image {
        val image = Image()
        val file = File(AppHelper.instance.getPathFromUri(context, uri))
        val fileName: String = file.name
        val fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1)
        image.title = System.currentTimeMillis().toString() + "." + fileExtension
        image.position = imageUris.size.toByte()

        if (image.position == thumbnail.toByte()) {
            image.thumbnail = 1

        } else {
            image.thumbnail = 0
        }

        return image
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textView: TextView = itemView.findViewById(R.id.tv_thumbnail)

        fun bind(uri: Uri?, thumbnailPos: Int) {
            Glide.with(itemView).load(uri).into(imageView)

            if (bindingAdapterPosition == thumbnailPos) {
                textView.visibility = View.VISIBLE

            } else {
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
            notifyDataSetChanged()
        }

        fun onThumbnailButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            thumbnail = position
            notifyDataSetChanged()
        }

        fun onUpButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            if (position > 0) {
                Collections.swap(imageUris, position - 1, position)
                Collections.swap(images, position - 1, position)
                notifyDataSetChanged()

                if (thumbnail == position) {
                    thumbnail--

                } else if (thumbnail == position - 1) {
                    thumbnail++
                }
            }
        }

        fun onDownButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            if (position < imageUris.size - 1) {
                Collections.swap(imageUris, position, position + 1)
                Collections.swap(images, position, position + 1)
                notifyDataSetChanged()

                if (thumbnail == position) {
                    thumbnail++

                } else if (thumbnail == position + 1) {
                    thumbnail--
                }
            }
        }
    }
}