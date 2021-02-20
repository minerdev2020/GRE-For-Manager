package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class ImageViewModel extends AndroidViewModel {
    private ImageRepository repository;

    public ImageViewModel(Application application) {
        super(application);
        repository = new ImageRepository(application);
    }

    public void insert(Image image) {
        repository.insert(image);
    }

    public void insert(Image... images) {
        repository.insert(images);
    }

    public void update(Image image) {
        repository.update(image);
    }

    public void update(Image... images) {
        repository.update(images);
    }

    public void updateOrInsert(Image image) {
        repository.updateOrInsert(image);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
