package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.sql.Timestamp;
import java.util.List;

public class HouseRepository {
    private HouseDao houseDao;
    private LiveData<List<House>> allHouses;

    public HouseRepository(Application application) {
        GreDatabase db = GreDatabase.getDatabase(application);
        houseDao = db.houseDao();
        allHouses = houseDao.getAll();
    }

    public LiveData<Timestamp> getLatestUpdatedAt() {
        return houseDao.getLatestUpdatedAt();
    }

    public LiveData<List<House>> getAll() {
        return allHouses;
    }

    public House get(int id) {
        return houseDao.get(id);
    }

    public void insert(House house) {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.insert(house));
    }

    public void insert(House... houses) {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.insert(houses));
    }

    public void update(House house) {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.update(house));
    }

    public void deleteAll() {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.deleteAll());
    }
}
