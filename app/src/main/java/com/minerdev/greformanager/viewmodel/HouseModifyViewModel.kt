package com.minerdev.greformanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.minerdev.greformanager.Repository
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image

class HouseModifyViewModel(application: Application) : AndroidViewModel(application) {
    val images: MutableLiveData<List<Image>>
    private val repository: Repository = Repository(application)

    init {
        images = repository.images
    }

    fun loadImages(houseId: Int) {
        repository.loadImages(houseId)
    }

    fun add(house: House, images: List<Image>) {
        repository.add(house, images)
    }

    fun modify(house: House, images: List<Image>) {
        repository.modify(house, images)
    }
}