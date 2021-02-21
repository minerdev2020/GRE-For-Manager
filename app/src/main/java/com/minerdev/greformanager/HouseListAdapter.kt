package com.minerdev.greformanager

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HouseListAdapter(diffCallback: DiffCallback) : PagingDataAdapter<House, HouseListAdapter.ViewHolder>(diffCallback) {
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_house, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.setItem(item!!)
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
        private val textView_payment: TextView
        private val textView_price: TextView
        private val textView_house_info: TextView
        private val textView_description: TextView
        private val textView_upload_time: TextView
        private val imageView_profile: ImageView

        fun setItem(house: House) {
            val houseWrapper = HouseWrapper(house)

            textView_payment.text = houseWrapper.paymentType
            textView_price.text = houseWrapper.price
            textView_house_info.text = houseWrapper.houseInfo
            textView_description.text = houseWrapper.detailInfo
            textView_upload_time.setText(AppHelper.instance.getDiffTimeMsg(house.created_at))

            val uri = Uri.parse(Constants.instance.DNS + "/storage/images/" + house.id + "/" + house.thumbnail)
            Glide.with(itemView).load(uri).into(imageView_profile)
        }

        init {
            textView_payment = itemView.findViewById(R.id.houseItem_textView_payment_type)
            textView_price = itemView.findViewById(R.id.houseItem_textView_price)
            textView_house_info = itemView.findViewById(R.id.houseItem_textView_house_info)
            textView_description = itemView.findViewById(R.id.houseItem_textView_description)
            textView_upload_time = itemView.findViewById(R.id.houseItem_textView_upload_time)
            imageView_profile = itemView.findViewById(R.id.houseItem_imageView_profile)
            val linearLayout = itemView.findViewById<LinearLayout>(R.id.houseItem_layout)
            linearLayout.setOnClickListener { v: View? ->
                clickListener?.onItemClick(this@ViewHolder, itemView, adapterPosition)
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