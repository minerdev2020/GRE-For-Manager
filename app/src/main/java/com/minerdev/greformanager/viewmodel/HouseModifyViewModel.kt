package com.minerdev.greformanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minerdev.greformanager.Repository
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image

class HouseModifyViewModel : ViewModel() {
    val images: MutableLiveData<List<Image>>
    private val repository: Repository = Repository()

    init {
        images = repository.allImages
    }

    fun loadImages(houseId: Int) {
        repository.loadImages(houseId)
    }

    fun add(house: House, images: List<Image>, onResponse: () -> Unit) {
        repository.add(house, images, onResponse)
    }

    fun modify(house: House, images: List<Image>, onResponse: () -> Unit) {
        repository.modify(house, images, onResponse)
    }
}