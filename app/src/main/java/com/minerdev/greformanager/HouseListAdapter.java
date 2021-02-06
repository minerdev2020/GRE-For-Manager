package com.minerdev.greformanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HouseListAdapter extends RecyclerView.Adapter<HouseListAdapter.ViewHolder> {
    ArrayList<House.SerializedData> items = new ArrayList<>();
    OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
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
        House.SerializedData item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(House.SerializedData item) {
        items.add(item);
    }

    public void setItems(ArrayList<House.SerializedData> items) {
        this.items = items;
    }

    public House.SerializedData getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, House.SerializedData item) {
        items.set(position, item);
    }

    public interface OnItemClickListener {
        void onItemClick(HouseListAdapter.ViewHolder viewHolder, View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView_payment;
        final TextView textView_price;
        final TextView textView_house_info;
        final TextView textView_description;
        final ImageView imageView_profile;

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

        public void setItem(House.SerializedData house) {
            textView_payment.setText(Constants.getInstance().PAYMENT_TYPE.get(house.houseType).get(house.paymentType));
            textView_price.setText(String.valueOf(house.price));
            textView_house_info.setText(Constants.getInstance().HOUSE_TYPE.get(house.houseType));
            textView_description.setText(house.detailInfo);
            imageView_profile.setImageResource(R.drawable.house);
        }
    }
}
