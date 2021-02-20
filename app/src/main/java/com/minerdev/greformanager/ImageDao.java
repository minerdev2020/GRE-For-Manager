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
    @Query("SELECT * FROM Image")
    LiveData<List<Image>> getAllImages();

//    @Query("SELECT * FROM image WHERE :options")
//    List<Image> getImages(String options);

    @Query("SELECT * FROM Image WHERE house_id = :house_id")
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
    void update(List<Image> images);

    @Update
    void update(Image... images);

    @Update
    void update(Image image);

    @Delete
    void delete(Image... images);

    @Delete
    void delete(Image image);

    @Query("DELETE FROM Image")
    void deleteAll();
}
