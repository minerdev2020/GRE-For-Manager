package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageRepository {
    private ImageDao imageDao;
    private LiveData<List<Image>> allImages;

    public ImageRepository(Application application) {
        GreDatabase db = GreDatabase.getDatabase(application);
        imageDao = db.imageDao();
        allImages = imageDao.getAllImages();
    }

    public LiveData<List<Image>> getAllImages() {
        return allImages;
    }

    public LiveData<List<Image>> getImages(int house_id) {
        return imageDao.getImages(house_id);
    }

    public void insert(Image image) {
        GreDatabase.databaseWriteExecutor.execute(() -> imageDao.insert(image));
    }

    public void insert(Image... images) {
        GreDatabase.databaseWriteExecutor.execute(() -> imageDao.insert(images));
    }

    public void update(Image image) {
        GreDatabase.databaseWriteExecutor.execute(() -> imageDao.update(image));
    }

    public void update(Image... images) {
        GreDatabase.databaseWriteExecutor.execute(() -> imageDao.update(images));
    }

    public void updateOrInsert(Image image) {
        GreDatabase.databaseWriteExecutor.execute(() -> {
            Image result = imageDao.getImage(image.house_id, image.position);
            if (result == null) {
                imageDao.insert(image);

            } else {
                imageDao.update(image);
            }
        });
    }

    public void deleteAll() {
        GreDatabase.databaseWriteExecutor.execute(() -> imageDao.deleteAll());
    }
}
