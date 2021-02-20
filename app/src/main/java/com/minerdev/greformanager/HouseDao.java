package com.minerdev.greformanager;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HouseDao {
    @Query("SELECT * FROM House")
    LiveData<List<House>> getAll();

//    @Query("SELECT * FROM house WHERE :options")
//    List<House> getHouses(String options);

    @Query("SELECT * FROM House WHERE id = :id")
    House get(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(House... houses);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(House house);

    @Update
    void update(House... houses);

    @Update
    void update(House house);

    @Delete
    void delete(House... houses);

    @Delete
    void delete(House house);

    @Query("DELETE FROM House")
    void deleteAll();
}
