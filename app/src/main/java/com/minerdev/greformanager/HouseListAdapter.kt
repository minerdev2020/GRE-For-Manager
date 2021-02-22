package com.minerdev.greformanager

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HouseListAdapter(diffCallback: DiffCallback) : ListAdapter<House, HouseListAdapter.ViewHolder>(diffCallback) {
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_house, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }

    operator fun get(position: Int): House? {
        return getItem(position)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        listener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(viewHolder: ViewHolder, view: View, position: Int)
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        private val textView_payment: TextView = itemView.findViewById(R.id.houseItem_textView_payment_type)
        private val textView_price: TextView = itemView.findViewById(R.id.houseItem_textView_price)
        private val textView_house_info: TextView = itemView.findViewById(R.id.houseItem_textView_house_info)
        private val textView_description: TextView = itemView.findViewById(R.id.houseItem_textView_description)
        private val textView_upload_time: TextView = itemView.findViewById(R.id.houseItem_textView_upload_time)
        private val imageView_profile: ImageView = itemView.findViewById(R.id.houseItem_imageView_profile)

        fun bind(house: House) {
            val houseWrapper = HouseWrapper(house)

            textView_payment.text = houseWrapper.paymentType
            textView_price.text = houseWrapper.price
            textView_house_info.text = houseWrapper.houseInfo
            textView_description.text = houseWrapper.detailInfo
            textView_upload_time.text = AppHelper.instance.getDiffTimeMsg(house.created_at)

            val uri = Uri.parse(Constants.instance.DNS + "/storage/images/" + house.id + "/" + house.thumbnail)
            Glide.with(itemView).load(uri).into(imageView_profile)
        }

        init {
            val linearLayout = itemView.findViewById<LinearLayout>(R.id.houseItem_layout)
            linearLayout.setOnClickListener {
                clickListener?.onItemClick(this@ViewHolder, itemView, bindingAdapterPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<House>() {
        override fun areItemsTheSame(oldItem: House, newItem: House): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: House, newItem: House): Boolean {
            return oldItem == newItem
        }
    }
}