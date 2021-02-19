package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private final LiveData<List<ImageParcelableData>> allImages;
    private ImageRepository repository;

    public ImageViewModel(Application application) {
        super(application);
        repository = new ImageRepository(application);
        allImages = repository.getAllImages();
    }

    LiveData<List<ImageParcelableData>> getAllImages() {
        return allImages;
    }

    public void insert(ImageParcelableData house) {
        repository.insert(house);
    }
}
