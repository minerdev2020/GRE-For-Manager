package com.minerdev.greformanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ImageFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 1;
    private final ImageListAdapter imageListAdapter = new ImageListAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_image, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.house_modify_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        imageListAdapter.setOnItemClickListener(new ImageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImageListAdapter.ViewHolder viewHolder, View view, int position) {
                imageListAdapter.removeItem(position);
                imageListAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(imageListAdapter);

        ImageButton imageButton = rootView.findViewById(R.id.house_modify_imageButton_add);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri == null) {
                    return;
                }

                imageListAdapter.addItem(selectedImageUri);
                imageListAdapter.notifyDataSetChanged();
            }
        }
    }
}