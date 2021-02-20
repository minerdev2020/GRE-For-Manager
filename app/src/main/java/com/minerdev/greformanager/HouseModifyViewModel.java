package com.minerdev.greformanager;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HouseModifyViewModel extends ViewModel {
    private House house;
    private List<Uri> imageUris;
    private List<Image> images;
    private int thumbnail;
    private String thumbnailTitle;

    public void setMode(String mode, House house) {
        if (mode.equals("add")) {
            this.house = null;
            this.imageUris = new ArrayList<>();

        } else if (mode.equals("modify") && house != null) {
            this.house = house;
            this.imageUris = new ArrayList<>();

        } else {
            this.house = null;
            this.imageUris = null;
        }
    }

    public House getHouse() {
        return house;
    }

    public House getNewHouse() {
        house = new House();
        return house;
    }

    public List<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailTitle() {
        return thumbnailTitle;
    }

    public List<Image> getImages() {
        return images;
    }

    public void saveImages(Context context) {
        images = new ArrayList<>();
        int position = 0;
        for (Uri uri : imageUris) {
            Image image = new Image();

            File file = new File(AppHelper.getInstance().getPathFromUri(context, uri));
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            image.title = System.currentTimeMillis() + "." + fileExtension;
            image.position = (byte) position;
            image.house_id = house.id;

            if (position == thumbnail) {
                image.thumbnail = 1;
                thumbnailTitle = image.title;

            } else {
                image.thumbnail = 0;
            }

            images.add(image);

            position++;
        }
    }
}
