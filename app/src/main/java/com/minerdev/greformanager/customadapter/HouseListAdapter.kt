package com.minerdev.greformanager.customadapter

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
import com.minerdev.greformanager.R
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.utils.AppHelper.getDiffTimeMsg
import com.minerdev.greformanager.utils.Constants.BASE_URL
import com.minerdev.greformanager.wrapper.HouseWrapper

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
        private val tvPaymentType: TextView = itemView.findViewById(R.id.tv_payment_type)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvHouseInfo: TextView = itemView.findViewById(R.id.tv_house_info)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        private val tvUploadTime: TextView = itemView.findViewById(R.id.tv_upload_time)
        private val ivProfile: ImageView = itemView.findViewById(R.id.iv_profile)

        fun bind(house: House) {
            val houseWrapper = HouseWrapper(house)

            tvPaymentType.text = houseWrapper.paymentType
            tvPrice.text = houseWrapper.price
            tvHouseInfo.text = houseWrapper.houseInfo
            tvDescription.text = houseWrapper.detailInfo
            tvUploadTime.text = getDiffTimeMsg(house.createdAt)

            val uri = Uri.parse(BASE_URL + "/" + house.thumbnail)
            Glide.with(itemView).load(uri).into(ivProfile)
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