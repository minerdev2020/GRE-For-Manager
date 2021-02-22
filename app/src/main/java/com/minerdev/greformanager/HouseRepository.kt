package com.minerdev.greformanager

import android.app.Application
import androidx.lifecycle.LiveData

class HouseRepository(application: Application) {
    private val houseDao: HouseDao
    val allSale: LiveData<MutableList<House>>
    val allSold: LiveData<MutableList<House>>

    val lastUpdatedAt: Long?
        get() = houseDao.lastUpdatedAt

    operator fun get(id: Int): House? {
        return houseDao[id]
    }

    fun insert(house: House) {
        GreDatabase.databaseWriteExecutor.execute { houseDao.insert(house) }
    }

    fun insert(houses: List<House>) {
        GreDatabase.databaseWriteExecutor.execute { houseDao.insert(houses) }
    }

    fun update(house: House) {
        GreDatabase.databaseWriteExecutor.execute { houseDao.update(house) }
    }

    fun updateOrInsert(house: House) {
        GreDatabase.run {
            databaseWriteExecutor.execute {
                val result = houseDao[house.id]
                if (result == null) {
                    houseDao.insert(house)

                } else {
                    houseDao.update(house)
                }
            }
        }
    }

    fun deleteAll() {
        GreDatabase.databaseWriteExecutor.execute { houseDao.deleteAll() }
    }

    init {
        val db: GreDatabase? = GreDatabase.getDatabase(application)
        houseDao = db!!.houseDao()
        allSale = houseDao.allSale
        allSold = houseDao.allSold
    }
}