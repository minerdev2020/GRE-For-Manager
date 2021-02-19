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
public interface ImageDao {
    @Query("SELECT * FROM image")
    LiveData<List<ImageParcelableData>> getAllImages();

//    @Query("SELECT * FROM image WHERE :options")
//    List<ImageParcelableData> getImages(String options);

    @Query("SELECT * FROM image WHERE house_id = :house_id")
    ImageParcelableData getImages(int house_id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ImageParcelableData... images);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ImageParcelableData image);

    @Update
    void update(ImageParcelableData... images);

    @Update
    void update(ImageParcelableData image);

    @Delete
    void delete(ImageParcelableData... images);

    @Delete
    void delete(ImageParcelableData image);

    @Query("DELETE FROM image")
    void deleteAll();
}
