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
    @Query("SELECT * FROM house")
    LiveData<List<HouseParcelableData>> getAll();

//    @Query("SELECT * FROM house WHERE :options")
//    List<HouseParcelableData> getHouses(String options);

    @Query("SELECT * FROM house WHERE id = :id")
    HouseParcelableData get(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HouseParcelableData... houses);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HouseParcelableData house);

    @Update
    void update(HouseParcelableData... houses);

    @Update
    void update(HouseParcelableData house);

    @Delete
    void delete(HouseParcelableData... houses);

    @Delete
    void delete(HouseParcelableData house);

    @Query("DELETE FROM house")
    void deleteAll();
}
