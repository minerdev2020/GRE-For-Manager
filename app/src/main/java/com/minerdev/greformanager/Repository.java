package com.minerdev.greformanager;

import android.net.Uri;

import java.util.ArrayList;

public class Repository {
    public HouseParcelableData house;
    public ArrayList<Uri> imageUris;

    private Repository() {

    }

    public static Repository getInstance() {
        return Repository.Holder.INSTANCE;
    }

    private static class Holder {
        public static final Repository INSTANCE = new Repository();
    }
}
