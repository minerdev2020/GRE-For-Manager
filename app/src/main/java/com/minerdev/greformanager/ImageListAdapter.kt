package com.minerdev.greformanager

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    var items: MutableList<Uri?>? = ArrayList()
    private var listener = OnItemClickListener()
    var thumbnail = 0
    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        listener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_image, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(items!![position], thumbnail)
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    fun addItem(item: Uri?) {
        items!!.add(item)
    }

    fun getItem(position: Int): Uri? {
        return items!![position]
    }

    fun setItem(position: Int, item: Uri?) {
        items!![position] = item
    }

    fun removeItem(position: Int) {
        items!!.removeAt(position)
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView
        private val textView: TextView
        fun setItem(uri: Uri?, thumbnailPos: Int) {
            Glide.with(itemView).load(uri).into(imageView)
            if (adapterPosition == thumbnailPos) {
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
        }

        init {
            imageView = itemView.findViewById(R.id.imageItem_imageView)
            textView = itemView.findViewById(R.id.imageItem_textView_thumbnail)
            val imageButton_delete = itemView.findViewById<ImageButton>(R.id.imageItem_imageButton_delete)
            imageButton_delete.setOnClickListener { v: View? ->
                clickListener?.onDeleteButtonClick(this@ViewHolder, itemView, adapterPosition)
            }
            val imageButton_thumbnail = itemView.findViewById<ImageButton>(R.id.imageItem_imageButton_thumbnail)
            imageButton_thumbnail.setOnClickListener { v: View? ->
                clickListener?.onThumbnailButtonClick(this@ViewHolder, itemView, adapterPosition)
            }
            val imageButton_up = itemView.findViewById<ImageButton>(R.id.imageItem_imageButton_up)
            imageButton_up.setOnClickListener { v: View? ->
                clickListener?.onUpButtonClick(this@ViewHolder, itemView, adapterPosition)
            }
            val imageButton_down = itemView.findViewById<ImageButton>(R.id.imageItem_imageButton_down)
            imageButton_down.setOnClickListener { v: View? ->
                clickListener?.onDownButtonClick(this@ViewHolder, itemView, adapterPosition)
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
                Collections.swap(items, position - 1, position)
                notifyDataSetChanged()
                if (thumbnail == position) {
                    thumbnail--
                } else if (thumbnail == position - 1) {
                    thumbnail++
                }
            }
        }

        fun onDownButtonClick(viewHolder: ViewHolder?, view: View?, position: Int) {
            if (position < items!!.size - 1) {
                Collections.swap(items, position, position + 1)
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