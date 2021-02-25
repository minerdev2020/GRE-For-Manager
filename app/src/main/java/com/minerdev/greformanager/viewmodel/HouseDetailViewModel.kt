package com.minerdev.greformanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.minerdev.greformanager.Repository
import com.minerdev.greformanager.model.Image
import org.json.JSONObject

class HouseDetailViewModel(application: Application) : AndroidViewModel(application) {
    val images: MutableLiveData<List<Image>>
    private val repository: Repository = Repository(application)

    init {
        images = repository.images
    }

    fun loadImages(houseId: Int) {
        repository.loadImages(houseId)
    }

    fun modify(id: Int, data: JSONObject) {
        repository.modify(id, data)
    }
}