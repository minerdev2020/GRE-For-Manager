package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HouseRepository {
    private HouseDao houseDao;
    private LiveData<List<HouseParcelableData>> allHouses;

    public HouseRepository(Application application) {
        GreDatabase db = GreDatabase.getDatabase(application);
        houseDao = db.houseDao();
        allHouses = houseDao.getAll();
    }

    public LiveData<List<HouseParcelableData>> getAll() {
        return allHouses;
    }

    public HouseParcelableData get(int id) {
        return houseDao.get(id);
    }

    public void insert(HouseParcelableData house) {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.insert(house));
    }

    public void insertList(HouseParcelableData... houses) {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.insert(houses));
    }

    public void update(HouseParcelableData house) {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.update(house));
    }

    public void deleteAll() {
        GreDatabase.databaseWriteExecutor.execute(() -> houseDao.deleteAll());
    }
}
