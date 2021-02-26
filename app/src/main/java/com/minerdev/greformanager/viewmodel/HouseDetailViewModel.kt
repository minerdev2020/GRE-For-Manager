package com.minerdev.greformanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minerdev.greformanager.Repository
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image

class HouseDetailViewModel : ViewModel() {
    val house: MutableLiveData<House>
    val images: MutableLiveData<List<Image>>

    private val repository: Repository = Repository()

    init {
        house = repository.house
        images = repository.allImages
    }

    fun loadHouse(houseId: Int) {
        repository.loadHouse(houseId)
    }

    fun loadImages(houseId: Int) {
        repository.loadImages(houseId)
    }

    fun modifyHouseState(id: Int, state: Byte) {
        repository.modifyHouseState(id, state)
    }
}