package com.minerdev.greformanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minerdev.greformanager.Repository
import com.minerdev.greformanager.model.House

class MainViewModel : ViewModel() {
    val allSale: MutableLiveData<List<House>>
    val allSold: MutableLiveData<List<House>>
    val house: MutableLiveData<House>

    private val repository: Repository = Repository()

    init {
        allSale = repository.allSale
        allSold = repository.allSold
        house = repository.house
    }

    fun loadHouse(id: Int) {
        repository.loadHouse(id)
    }

    fun loadHouses() {
        repository.loadHouses()
    }
}