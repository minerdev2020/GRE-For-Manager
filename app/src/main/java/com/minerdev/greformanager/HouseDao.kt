package com.minerdev.greformanager

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HouseDao {
    @get:Query("SELECT updated_at FROM House ORDER BY updated_at DESC")
    val lastUpdatedAt: Long?

    @get:Query("SELECT * FROM House")
    val all: LiveData<List<House>>

    @get:Query("SELECT * FROM House WHERE state = 0")
    val allSale: LiveData<List<House>>

    @get:Query("SELECT * FROM House WHERE state = 1")
    val allSold: LiveData<List<House>>

    @Query("SELECT * FROM House WHERE id = :id")
    operator fun get(id: Int): House?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(house: List<House>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(house: House)

    @Update
    fun update(house: House)

    @Delete
    fun delete(house: House)

    @Query("DELETE FROM House")
    fun deleteAll()
}