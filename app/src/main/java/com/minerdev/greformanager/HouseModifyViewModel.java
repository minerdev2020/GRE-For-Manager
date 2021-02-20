package com.minerdev.greformanager;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class HouseModifyViewModel extends ViewModel {
    private House house;
    private List<Uri> imageUris;
    private int thumbnail;

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
}
