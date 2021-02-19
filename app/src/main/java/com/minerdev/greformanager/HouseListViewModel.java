package com.minerdev.greformanager;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HouseListViewModel extends AndroidViewModel {
    private final LiveData<List<HouseParcelableData>> allHouses;
    private HouseRepository houseRepository;

    public HouseListViewModel(Application application) {
        super(application);
        houseRepository = new HouseRepository(application);
        allHouses = houseRepository.getAll();
    }

    public LiveData<List<HouseParcelableData>> getAll() {
        return allHouses;
    }

    public HouseParcelableData get(int id) {
        return houseRepository.get(id);
    }

    public void insert(HouseParcelableData house) {
        houseRepository.insert(house);
    }

    public void insertList(HouseParcelableData... houses) {
        houseRepository.insertList(houses);
    }

    public void update(HouseParcelableData house) {
        houseRepository.update(house);
    }

    public void deleteAll() {
        houseRepository.deleteAll();
    }
}
