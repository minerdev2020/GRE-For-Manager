package com.minerdev.greformanager

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ImageDao {
    @Query("SELECT updated_at FROM Image WHERE house_id = :house_id ORDER BY updated_at DESC")
    fun getLastUpdatedAt(house_id: Int): Long?

    @get:Query("SELECT * FROM Image")
    val allImages: LiveData<List<Image>>

    @Query("SELECT * FROM Image WHERE house_id = :house_id ORDER BY position")
    fun getImages(house_id: Int): LiveData<List<Image>>

    @Query("SELECT * FROM Image WHERE house_id = :house_id and position = :position")
    fun getImage(house_id: Int, position: Int): Image?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(images: List<Image>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(image: Image)

    @Update
    fun update(image: Image)

    @Query("DELETE FROM Image WHERE house_id = :house_id")
    fun deleteAll(house_id: Int)

    @Query("DELETE FROM Image")
    fun deleteAll()
}