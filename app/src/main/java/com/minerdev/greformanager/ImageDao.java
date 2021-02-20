package com.minerdev.greformanager;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.sql.Timestamp;
import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT updated_at FROM Image WHERE house_id = :house_id ORDER BY updated_at DESC")
    LiveData<Timestamp> getLatestUpdatedAt(int house_id);

    @Query("SELECT * FROM Image")
    LiveData<List<Image>> getAllImages();

    @Query("SELECT * FROM Image WHERE house_id = :house_id ORDER BY position")
    LiveData<List<Image>> getImages(int house_id);

    @Query("SELECT * FROM Image WHERE house_id = :house_id and position = :position")
    Image getImage(int house_id, int position);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Image> images);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Image... images);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Image image);

    @Update
    void update(Image image);

    @Query("DELETE FROM Image WHERE house_id = :house_id")
    void deleteAll(int house_id);

    @Query("DELETE FROM Image")
    void deleteAll();
}
