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
import java.util.Collections;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private ArrayList<Uri> items = new ArrayList<>();
    private OnItemClickListener listener = new OnItemClickListener();
    private int thumbnailPos = 0;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.image_item, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(items.get(position), thumbnailPos);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ImageButton imageButton_delete;
        private ImageButton imageButton_thumbnail;
        private ImageButton imageButton_up;
        private ImageButton imageButton_down;


        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageItem_imageView);
            textView = itemView.findViewById(R.id.imageItem_textView_thumbnail);

            imageButton_delete = itemView.findViewById(R.id.imageItem_imageButton_delete);
            imageButton_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onDeleteButtonClick(ViewHolder.this, itemView, getAdapterPosition());
                    }
                }
            });

            imageButton_thumbnail = itemView.findViewById(R.id.imageItem_imageButton_thumbnail);
            imageButton_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onThumbnailButtonClick(ViewHolder.this, itemView, getAdapterPosition());
                    }
                }
            });

            imageButton_up = itemView.findViewById(R.id.imageItem_imageButton_up);
            imageButton_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onUpButtonClick(ViewHolder.this, itemView, getAdapterPosition());
                    }
                }
            });

            imageButton_down = itemView.findViewById(R.id.imageItem_imageButton_down);
            imageButton_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onDownButtonClick(ViewHolder.this, itemView, getAdapterPosition());
                    }
                }
            });
        }

        public void setItem(Uri uri, int thumbnailPos) {
            imageView.setImageURI(uri);

            if (getAdapterPosition() == thumbnailPos) {
                textView.setVisibility(View.VISIBLE);

            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    public class OnItemClickListener {
        public void onDeleteButtonClick(ImageListAdapter.ViewHolder viewHolder, View view, int position) {
            if (thumbnailPos == position) {
                thumbnailPos = 0;

            } else if (thumbnailPos > position) {
                thumbnailPos--;
            }

            removeItem(position);
            notifyDataSetChanged();
        }

        public void onThumbnailButtonClick(ImageListAdapter.ViewHolder viewHolder, View view, int position) {
            thumbnailPos = position;
            notifyDataSetChanged();
        }

        public void onUpButtonClick(ImageListAdapter.ViewHolder viewHolder, View view, int position) {
            if (position > 0) {
                Collections.swap(items, position - 1, position);
                notifyDataSetChanged();

                if (thumbnailPos == position) {
                    thumbnailPos--;

                } else if (thumbnailPos == position - 1) {
                    thumbnailPos++;
                }
            }
        }

        public void onDownButtonClick(ImageListAdapter.ViewHolder viewHolder, View view, int position) {
            if (position < items.size() - 1) {
                Collections.swap(items, position, position + 1);
                notifyDataSetChanged();

                if (thumbnailPos == position) {
                    thumbnailPos++;

                } else if (thumbnailPos == position + 1) {
                    thumbnailPos--;
                }
            }
        }
    }
}
