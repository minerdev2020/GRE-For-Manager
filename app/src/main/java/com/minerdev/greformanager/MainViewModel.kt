package com.minerdev.greformanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val allSale: LiveData<List<House>>?
    val allSold: LiveData<List<House>>?

    private val repository: HouseRepository = HouseRepository(application)
    private val imageRepository: ImageRepository = ImageRepository(application)

    val lastUpdatedAt: Long
        get() = repository.lastUpdatedAt ?: 0

    operator fun get(id: Int): House? {
        return repository[id]
    }

    fun insert(house: House) {
        repository.insert(house)
    }

    fun insert(houses: List<House>) {
        repository.insert(houses)
    }

    fun update(house: House) {
        repository.update(house)
    }

    fun deleteAll() {
        repository.deleteAll()
        imageRepository.deleteAll()
    }

    init {
        allSale = repository.allSale
        allSold = repository.allSold
    }
}