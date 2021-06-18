package com.minerdev.greformanager.custom.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minerdev.greformanager.databinding.ItemHouseBinding
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.wrapper.HouseWrapper
import com.minerdev.greformanager.utils.AppHelper.getDiffTimeMsg
import com.minerdev.greformanager.utils.Constants.BASE_URL

class HouseListAdapter(diffCallback: DiffCallback) :
    ListAdapter<House, HouseListAdapter.ViewHolder>(diffCallback) {
    lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHouseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    operator fun get(position: Int): House {
        return getItem(position)
    }

    interface OnItemClickListener {
        fun onItemClick(viewHolder: ViewHolder, view: View, position: Int)
    }

    class ViewHolder(private val binding: ItemHouseBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.houseItemLayout.setOnClickListener {
                listener.onItemClick(this@ViewHolder, binding.root, bindingAdapterPosition)
            }
        }

        fun bind(house: House) {
            val houseWrapper = HouseWrapper(house)

            binding.tvPaymentType.text = houseWrapper.paymentType
            binding.tvPrice.text = houseWrapper.price
            binding.tvHouseInfo.text = houseWrapper.houseInfo
            binding.tvDescription.text = houseWrapper.detailInfo
            binding.tvUploadTime.text = getDiffTimeMsg(house.createdAt)

            val uri = Uri.parse(BASE_URL + "/" + house.thumbnail)
            Glide.with(binding.root).load(uri).into(binding.ivProfile)
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