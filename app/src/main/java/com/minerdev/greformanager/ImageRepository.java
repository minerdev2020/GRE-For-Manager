package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageRepository {
    private ImageDao imageDao;
    private LiveData<List<ImageParcelableData>> allImages;

    ImageRepository(Application application) {
        GreDatabase db = GreDatabase.getDatabase(application);
        imageDao = db.imageDao();
        allImages = imageDao.getAllImages();
    }

    LiveData<List<ImageParcelableData>> getAllImages() {
        return allImages;
    }

    void insert(ImageParcelableData image) {
        GreDatabase.databaseWriteExecutor.execute(() -> {
            imageDao.insert(image);
        });
    }
}
