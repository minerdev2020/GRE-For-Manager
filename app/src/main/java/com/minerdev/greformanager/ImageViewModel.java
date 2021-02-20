package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.sql.Timestamp;
import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private ImageRepository repository;

    public ImageViewModel(Application application) {
        super(application);
        repository = new ImageRepository(application);
    }

    public LiveData<Timestamp> getLatestUpdatedAt(int house_id) {
        return repository.getLatestUpdatedAt(house_id);
    }

    public LiveData<List<Image>> getOrderByPosition(int house_id) {
        return repository.getOrderByPosition(house_id);
    }

    public void insert(Image image) {
        repository.insert(image);
    }

    public void insert(Image... images) {
        repository.insert(images);
    }

    public void deleteAll(int house_id) {
        repository.deleteAll(house_id);
    }
}
