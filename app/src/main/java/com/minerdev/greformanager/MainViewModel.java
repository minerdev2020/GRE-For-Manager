package com.minerdev.greformanager;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<House>> allHouses;
    private final HouseRepository repository;
    private final ImageRepository imageRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new HouseRepository(application);
        imageRepository = new ImageRepository(application);
        allHouses = repository.getAll();
    }

    public Long getLastUpdatedAt() {
        return repository.getLastUpdatedAt();
    }

    public LiveData<List<House>> getAll() {
        return allHouses;
    }

    public House get(int id) {
        return repository.get(id);
    }

    public void insert(House house) {
        repository.insert(house);
    }

    public void insert(House... houses) {
        repository.insert(houses);
    }

    public void update(House house) {
        repository.update(house);
    }

    public void deleteAll() {
        repository.deleteAll();
        imageRepository.deleteAll();
    }
}
