package com.minerdev.greformanager;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private ArrayList<Uri> items = new ArrayList<>();
    private OnItemClickListener listener;
    private View itemView;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        itemView = inflater.inflate(R.layout.image_item, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Uri item) {
        items.add(item);
    }

    public void setItems(ArrayList<Uri> items) {
        this.items = items;
    }

    public Uri getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Uri item) {
        items.set(position, item);
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    public interface OnItemClickListener {
        void onItemClick(ImageListAdapter.ViewHolder viewHolder, View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageButton imageButton_delete;
        private ImageButton imageButton_thumbnail;
        private TextView textView;

        private static boolean isSetThumbnail = false;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageItem_imageView);
            imageButton_delete = itemView.findViewById(R.id.imageItem_imageButton_delete);
            imageButton_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(ViewHolder.this, itemView, getAdapterPosition());
                }
            });

            imageButton_thumbnail = itemView.findViewById(R.id.imageItem_imageButton_thumbnail);
            imageButton_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setVisibility(textView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    isSetThumbnail = true;
                }
            });

            textView = itemView.findViewById(R.id.imageItem_textView_thumbnail);
            if (getAdapterPosition() == -1 && !isSetThumbnail) {
                textView.setVisibility(View.VISIBLE);
                isSetThumbnail = true;

            } else {
                textView.setVisibility(View.GONE);
            }
        }

        public void setItem(Uri uri) {
            imageView.setImageURI(uri);
        }
    }
}
