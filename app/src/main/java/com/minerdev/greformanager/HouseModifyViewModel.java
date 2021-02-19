package com.minerdev.greformanager;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class HouseModifyViewModel extends ViewModel {
    private HouseParcelableData house;
    private List<Uri> imageUris;

    public void setMode(String mode, HouseParcelableData house) {
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

    public HouseParcelableData getHouse() {
        return house;
    }

    public HouseParcelableData getNewHouse() {
        house = new HouseParcelableData();
        return house;
    }

    public List<Uri> getImageUris() {
        return imageUris;
    }
}
