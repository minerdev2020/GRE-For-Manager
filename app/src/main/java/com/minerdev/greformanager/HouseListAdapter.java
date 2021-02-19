package com.minerdev.greformanager;

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

public class HouseListAdapter extends ListAdapter<HouseParcelableData, HouseListAdapter.ViewHolder> {
    private OnItemClickListener listener;

    public HouseListAdapter(DiffUtil.ItemCallback<HouseParcelableData> diffCallback) {
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
        HouseParcelableData item = getItem(position);
        holder.setItem(item);
    }

    public HouseParcelableData get(int position) {
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
        private final ImageView imageView_profile;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            textView_payment = itemView.findViewById(R.id.houseItem_textView_payment_type);
            textView_price = itemView.findViewById(R.id.houseItem_textView_price);
            textView_house_info = itemView.findViewById(R.id.houseItem_textView_house_info);
            textView_description = itemView.findViewById(R.id.houseItem_textView_description);
            imageView_profile = itemView.findViewById(R.id.houseItem_imageView_profile);

            LinearLayout linearLayout = itemView.findViewById(R.id.houseItem_layout);
            linearLayout.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(ViewHolder.this, itemView, getAdapterPosition());
                }
            });
        }

        public void setItem(HouseParcelableData house) {
            House house1 = new House(house);

            textView_payment.setText(house1.getPaymentType());
            textView_price.setText(house1.getPrice());
            textView_house_info.setText(house1.getHouseInfo());
            textView_description.setText(house1.getDetailInfo());
            imageView_profile.setImageResource(R.drawable.house);
        }
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<HouseParcelableData> {
        @Override
        public boolean areItemsTheSame(@NonNull HouseParcelableData oldItem, @NonNull HouseParcelableData newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull HouseParcelableData oldItem, @NonNull HouseParcelableData newItem) {
            return oldItem.id == newItem.id;
        }
    }
}
