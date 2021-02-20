package com.minerdev.greformanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class ImageFragment extends Fragment implements OnSaveDataListener {
    private static final int GALLERY_REQUEST_CODE = 1;
    private final ImageListAdapter imageListAdapter = new ImageListAdapter();
    private HouseModifyViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_image, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(HouseModifyViewModel.class);

        RecyclerView recyclerView = rootView.findViewById(R.id.house_modify_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(imageListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        ImageButton imageButton = rootView.findViewById(R.id.house_modify_imageButton_add);
        imageButton.setOnClickListener(v -> {
            int permissionChecked = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionChecked != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);

            } else {
                if (imageListAdapter.getItemCount() < 9) {
                    showAlbum();

                } else {
                    Toast.makeText(getContext(), "사진은 최대 9장까지 업로드가 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri == null) {
                    return;
                }

                String path = AppHelper.getInstance().getPathFromUri(getContext(), selectedImageUri);
                File file = new File(path);
                if (!file.exists() || !file.canRead()) {
                    Toast.makeText(getContext(), "사진이 존재 하지않거나 읽을 수 없습니다!", Toast.LENGTH_SHORT).show();

                } else if (file.length() > Constants.getInstance().FILE_MAX_SIZE) {
                    Toast.makeText(getContext(), "사진의 크기는 10MB 보다 작아야 합니다!", Toast.LENGTH_SHORT).show();

                } else {
                    imageListAdapter.addItem(selectedImageUri);
                    imageListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAlbum();

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("앱 권한");
                    alertDialog.setMessage("해당 앱의 원활한 기능을 이용하시려면 애플리케이션 정보>권한에서 '저장공간' 권한을 허용해 주십시오.");
                    alertDialog.setPositiveButton("권한설정", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getContext().getPackageName()));
                        startActivity(intent);
                        dialog.cancel();
                    });
                    alertDialog.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
                    alertDialog.show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean checkData() {
        return imageListAdapter.getItemCount() > 0;
    }

    @Override
    public void saveData() {
        viewModel.setImageUris(imageListAdapter.getItems());
        viewModel.setThumbnail(imageListAdapter.getThumbnail());
    }

    public void initData() {
        imageListAdapter.setItems(viewModel.getImageUris());
        imageListAdapter.setThumbnail(viewModel.getThumbnail());
    }

    public void showAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }
}