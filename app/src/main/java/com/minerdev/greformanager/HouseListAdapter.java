package com.minerdev.greformanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HouseListAdapter extends RecyclerView.Adapter<HouseListAdapter.ViewHolder> {
    private ArrayList<House> items = new ArrayList<>();
    private OnHouseItemClickListener listener;
    private View itemView;

    public void setOnItemClickListener(OnHouseItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        itemView = inflater.inflate(R.layout.house_item, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        House item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(House item) {
        items.add(item);
    }

    public void setItems(ArrayList<House> items) {
        this.items = items;
    }

    public House getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, House item) {
        items.set(position, item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_payment;
        private TextView textView_price;
        private TextView textView_house_info;
        private TextView textView_description;
        private ImageView imageView_profile;

        public ViewHolder(@NonNull View itemView, final OnHouseItemClickListener clickListener) {
            super(itemView);

            textView_payment = itemView.findViewById(R.id.houseItem_textView_payment_type);
            textView_price = itemView.findViewById(R.id.houseItem_textView_price);
            textView_house_info = itemView.findViewById(R.id.houseItem_textView_house_info);
            textView_description = itemView.findViewById(R.id.houseItem_textView_description);
            imageView_profile = itemView.findViewById(R.id.houseItem_imageView_profile);

            LinearLayout linearLayout = itemView.findViewById(R.id.houseItem_layout);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onItemClick(ViewHolder.this, itemView, getAdapterPosition());
                    }
                }
            });

        }

        public void setItem(House House) {
            textView_payment.setText(House.getPaymentType().getName());
            textView_price.setText(House.getPrice());
            textView_house_info.setText(House.getHouseType().getName());
            textView_description.setText(House.getAddress());
            imageView_profile.setImageResource(R.drawable.house);
        }
    }
}
