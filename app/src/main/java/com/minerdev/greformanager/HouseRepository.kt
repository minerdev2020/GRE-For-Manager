package com.minerdev.greformanager

import android.app.Application
import androidx.lifecycle.LiveData

class HouseRepository(application: Application) {
    private val houseDao: HouseDao?
    val all: LiveData<List<House>>
    val allSale: LiveData<List<House>>
    val allSold: LiveData<List<House>>

    val lastUpdatedAt: Long?
        get() = houseDao?.lastUpdatedAt

    operator fun get(id: Int): House? {
        return houseDao?.get(id)
    }

    fun insert(house: House) {
        GreDatabase.databaseWriteExecutor.execute { houseDao?.insert(house) }
    }

    fun insert(houses: List<House>) {
        GreDatabase.databaseWriteExecutor.execute { houseDao?.insert(houses) }
    }

    fun update(house: House) {
        GreDatabase.databaseWriteExecutor.execute { houseDao?.update(house) }
    }

    fun deleteAll() {
        GreDatabase.databaseWriteExecutor.execute { houseDao?.deleteAll() }
    }

    init {
        val db: GreDatabase? = GreDatabase.getDatabase(application)
        houseDao = db!!.houseDao()
        all = houseDao.all
        allSale = houseDao.allSale
        allSold = houseDao.allSold
    }
}