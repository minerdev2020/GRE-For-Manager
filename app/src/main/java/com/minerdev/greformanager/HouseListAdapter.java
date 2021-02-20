package com.minerdev.greformanager;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.sql.Date;

public class HouseListAdapter extends ListAdapter<House, HouseListAdapter.ViewHolder> {
    private OnItemClickListener listener;

    public HouseListAdapter(DiffUtil.ItemCallback<House> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_house, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        House item = getItem(position);
        holder.setItem(item);
    }

    public House get(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(HouseListAdapter.ViewHolder viewHolder, View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView_payment;
        private final TextView textView_price;
        private final TextView textView_house_info;
        private final TextView textView_description;
        private final TextView textView_upload_time;
        private final ImageView imageView_profile;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            textView_payment = itemView.findViewById(R.id.houseItem_textView_payment_type);
            textView_price = itemView.findViewById(R.id.houseItem_textView_price);
            textView_house_info = itemView.findViewById(R.id.houseItem_textView_house_info);
            textView_description = itemView.findViewById(R.id.houseItem_textView_description);
            textView_upload_time = itemView.findViewById(R.id.houseItem_textView_upload_time);
            imageView_profile = itemView.findViewById(R.id.houseItem_imageView_profile);

            LinearLayout linearLayout = itemView.findViewById(R.id.houseItem_layout);
            linearLayout.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(ViewHolder.this, itemView, getAdapterPosition());
                }
            });
        }

        public void setItem(House house) {
            HouseWrapper houseWrapper = new HouseWrapper(house);

            textView_payment.setText(houseWrapper.getPaymentType());
            textView_price.setText(houseWrapper.getPrice());
            textView_house_info.setText(houseWrapper.getHouseInfo());
            textView_description.setText(houseWrapper.getDetailInfo());
            Date date = new Date(house.created_at);
            textView_upload_time.setText(date.toString());

            Uri uri = Uri.parse(Constants.getInstance().DNS + "/storage/images/" + house.id + "/" + house.thumbnail);
            Glide.with(itemView).load(uri).into(imageView_profile);
        }
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<House> {
        @Override
        public boolean areItemsTheSame(@NonNull House oldItem, @NonNull House newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull House oldItem, @NonNull House newItem) {
            return oldItem.id == newItem.id;
        }
    }
}
